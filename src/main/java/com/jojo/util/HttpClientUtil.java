package com.jojo.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	/************************ ������ *****************************/

	/**
	 * ����ɹ�
	 */
	public static final int OK = 200;

	public static final int NOT_MODIFIED = 304;

	public static final int BAD_REQUEST = 400;

	public static final int NOT_AUTHORIZED = 401;

	public static final int FORBIDDEN = 403;

	public static final int NOT_FOUND = 404;

	public static final int NOT_ACCEPTABLE = 406;

	public static final int INTERNAL_SERVER_ERROR = 500;

	public static final int OTHER_IO_EXCEPTION = 498;

	public static final int READ_TIMEOUT = 499;

	public static final int BAD_GATEWAY = 502;

	public static final int SERVICE_UNAVAILABLE = 503;

	public static final int TIME_OUT = 60000;

	public static final String SOCKET_TIMEOUT = "http.socket.timeout";

	public static final String COLLECTION_TIMEOUT = "http.connection.timeout";

	public static final String COLLECTION_MANAGER_TIMEOUT = "http.connection-manager.timeout";

	/****************************** ���ö��� ******************************/
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * ���ӳع�����
	 */
	private static PoolingHttpClientConnectionManager clientConnectionManager;

	private static List<String> monitorHostList = new ArrayList<String>();

	/**
	 * ��ʼ�����ӳع�����
	 */
	static {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
		registryBuilder.register("ssl", SSLConnectionSocketFactory.getSocketFactory());

		clientConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder.build());
		// ���������
		clientConnectionManager.setMaxTotal(200);
		// ÿ��·�ɵĻ���������
		clientConnectionManager.setDefaultMaxPerRoute(20);
	}
	
	/**
	 * ��ȡHttpClient�������ӳع������ṩ
	 * @param timeout TODO
	 * 
	 * @return
	 */
	private static HttpClient getHttpClient(int timeout) {
		HttpClientBuilder clientBuilder = HttpClients.custom();
		// �����ӳػ�ȡ
		clientBuilder.setConnectionManager(clientConnectionManager);
		// ���ӳ�ʱ���ã�Ĭ��10��
		if(timeout == 0) timeout = 10;
		Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectionRequestTimeout(timeout * 1000);
		configBuilder.setConnectTimeout(timeout * 1000);
		configBuilder.setSocketTimeout(timeout * 1000);
		clientBuilder.setDefaultRequestConfig(configBuilder.build());
		
		// �������Դ�������3��
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// ����������ݵȵģ����ٴγ���
				if (!(request instanceof HttpEntityEnclosingRequest)) { return true; }
				// ������������������ӣ���ô������
				if (exception instanceof NoHttpResponseException) { return true; }
				// ����Ѿ�������5�Σ��ͷ���
				if (executionCount >= 3) { return false; }
				// ��Ҫ����SSL�����쳣
				if (exception instanceof SSLHandshakeException) { return false; }
				// ��ʱ
				if (exception instanceof InterruptedIOException) { return false; }
				// Ŀ����������ɴ�
				if (exception instanceof UnknownHostException) { return false; }
				// ���ӱ��ܾ�
				if (exception instanceof ConnectTimeoutException) { return false; }
				// ssl�����쳣
				if (exception instanceof SSLException) { return false; }
				// ��������������������ԣ���ʵ��� return д��������true����ͺ��ˣ�����Щ�쳣�͵�ѧϰ
				return false;
			}
		};
		clientBuilder.setRetryHandler(httpRequestRetryHandler);
		
		return clientBuilder.build();
	}

	public static HttpResult doPostContent(String url, String content, String contentType) {
		HttpResult httpResult = new HttpResult();
		String result = null;
		HttpPost post = new HttpPost(url);
		try {

			if (StringUtils.isNotEmpty(content)) {
				post.setEntity(new StringEntity(content, "UTF-8"));
			}

			// ����Header
			if (StringUtils.isNotEmpty(contentType)) {
				post.setHeader("Content-Type", contentType);
			}
			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);

			Map<String, String> headerMap = new HashMap<String, String>();
			for (Header header : response.getAllHeaders()) {
				headerMap.put(header.getName(), header.getValue());
			}
			httpResult.setHeaderMap(headerMap);

			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}
			if (resEntity != null) {
				String respBody = EntityUtils.toString(resEntity);
				try {
					result = respBody;
				} catch (Exception e) {
					logger.error("+++++==> respBody:" + respBody + " <==+++++", e);
				}
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("+++++==> doPost:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
		}
		httpResult.setBody(result);
		return httpResult;
	}

	@SuppressWarnings("unused")
	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication credentials were missing or incorrect.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned by the Search API when an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "Something is broken.  Please post to the group so the liushijie can investigate.";
			break;
		case BAD_GATEWAY:
			cause = "image server is down or being upgraded.";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: img servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}

	/**
	 * ����URL����get�����ȡ����
	 *
	 * @param url
	 * @return
	 */
	public static HttpResult doGet(String url, Map<String, String> headerMap, int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null;
		HttpGet get = new HttpGet(url);
		String host = null;
		try {
			host = new URL(url).getHost();
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					get.setHeader(entry.getKey(), entry.getValue());
				}
			}

			HttpResponse response = getHttpClient(0).execute(get);
			logger.info("response:" + response);
			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString error<==+++++", e);
				}
				httpResult.setBody(result);
			}
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				get.abort();
				return httpResult;
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("++++ doGet:" + url + " ++++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			get.releaseConnection();
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}
		return httpResult;
	}

	/**
	 * ����URL����get�����ȡ����
	 *
	 * @param url
	 * @return
	 */
	public static HttpResult doGetWithParams(String url, Map<String, String> paramsMap, int... timeout) {
		if (paramsMap != null && paramsMap.size() > 0) {
			url += "?";
			for (Map.Entry<String, String> m : paramsMap.entrySet()) {
				url += m.getKey() + "=" + m.getValue() + "&";
			}
			System.out.println(url);
			url = url.substring(0, url.length() - 1);
			System.out.println(url);
		}
		return doGet(url, null, timeout);
		// HttpResult httpResult = new HttpResult();
		// long currentTime = System.currentTimeMillis();
		// String result = null;
		// HttpGet get = new HttpGet(url);
		// String host = null;
		// try {
		// host = new URL(url).getHost();
		//
		// HttpResponse response = getHttpClient(timeout).execute(get);
		// HttpEntity resEntity = response.getEntity();
		// int statusCode = response.getStatusLine().getStatusCode();
		// httpResult.setCode(statusCode);
		// if (statusCode != OK) {
		// logger.error("+++++==>statusCode:["+statusCode+"],url:"+url+" <==+++++");
		// get.abort();
		// return httpResult;
		// }
		// if (resEntity != null) {
		// String respBody = EntityUtils.toString(resEntity);
		// try {
		// result = respBody;
		// } catch (Exception e) {
		// logger.error("+++++==> respBody:" + respBody + " <==+++++",e);
		// }
		// }
		// }
		// catch(SocketTimeoutException e)
		// {
		// httpResult.setCode(READ_TIMEOUT);
		// return httpResult;
		// } catch (IOException e) {
		// logger.error("++++ doGet:" + url + " ++++++", e);
		// httpResult.setCode(OTHER_IO_EXCEPTION);
		// return httpResult;
		// } finally {
		// get.releaseConnection();
		// if(monitorHostList.contains(host)){
		// logger.warn("============>["+url+"]"+" use
		// time:["+(System.currentTimeMillis()-currentTime)+" ms]<============");
		// }
		// }
		// httpResult.setBody(result);
		// return httpResult;
	}

	/**
	 * ����URL����get�����ȡ����
	 *
	 * @param url
	 * @return
	 */
	public static HttpResult doGet(String url, int... timeout) {
		return doGet(url, null, timeout);
		// HttpResult httpResult = new HttpResult();
		// long currentTime = System.currentTimeMillis();
		// String result = null;
		// HttpGet get = new HttpGet(url);
		// String host = null;
		// HttpClient client = null;
		// HttpResponse response = null;
		// try {
		// host = new URL(url).getHost();
		// client = getHttpClient(timeout);
		// response = client.execute(get);
		// HttpEntity resEntity = response.getEntity();
		// int statusCode = response.getStatusLine().getStatusCode();
		// httpResult.setCode(statusCode);
		// if (statusCode != OK) {
		// logger.error("+++++==>statusCode:["+statusCode+"],url:"+url+" <==+++++");
		// get.abort();
		// return httpResult;
		// }
		// if (resEntity != null) {
		// try {
		// result = EntityUtils.toString(resEntity);
		// } catch (Exception e) {
		// logger.error("+++++==> respBody:" + result + " <==+++++",e);
		// }
		// resEntity = null;
		// }
		// }
		// catch(SocketTimeoutException e)
		// {
		// httpResult.setCode(READ_TIMEOUT);
		// return httpResult;
		// }catch (IOException e) {
		// logger.error("++++ doGet:" + url + " ++++++", e);
		// httpResult.setCode(OTHER_IO_EXCEPTION);
		// return httpResult;
		// } finally {
		// get.releaseConnection();
		// response=null;
		// client=null;
		// if(monitorHostList.contains(host)){
		// logger.warn("============>["+url+"]"+" use
		// time:["+(System.currentTimeMillis()-currentTime)+" ms]<============");
		// }
		// }
		// httpResult.setBody(result);
		// return httpResult;
	}

	public static HttpResult doPostCharSet(String url, Map<String, String> paramsMap, String charSet, int... timeout) {
		return doPostCharSet(url, paramsMap, null, charSet, timeout);
	}

	/**
	 * ����URL����post�����ȡ����,֧�ִ��ַ���
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public static HttpResult doPostCharSet(String url, Map<String, String> paramsMap, Map<String, String> headerMap,
			String charSet, int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null;
		HttpPost post = new HttpPost(url);
		String host = null;
		try {
			host = new URL(url).getHost();

			// post.addHeader("Cookie", "JSESSIONID=DCD7BFE23126E4B69ABCC415A6D688AF;
			// Path=/");
			charSet = charSet == null || charSet.trim().equals("") ? "UTF-8" : charSet;
			if (paramsMap != null && paramsMap.size() > 0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> m : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				}
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, charSet);
				post.setEntity(reqEntity);
			}

			// ����Header
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString <==+++++", e);
				}
				httpResult.setBody(result);
			}
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPost:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}
		return httpResult;
	}

	public static HttpResult doPost(String url, Map<String, String> paramsMap, int... timeout) {
		return doPost(url, paramsMap, null, timeout);
	}

	/**
	 * ����URL����post�����ȡ����
	 *
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public static HttpResult doPost(String url, Map<String, String> paramsMap, Map<String, String> headerMap,
			int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null;
		HttpPost post = new HttpPost(url);
		String host = null;
		try {
			host = new URL(url).getHost();

			// post.addHeader("Cookie", "JSESSIONID=DCD7BFE23126E4B69ABCC415A6D688AF;
			// Path=/");

			if (paramsMap != null && paramsMap.size() > 0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> m : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				}
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
				post.setEntity(reqEntity);
			}

			// ����Header
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString <==+++++", e);
				}
				httpResult.setBody(result);
			}
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPost:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}
		return httpResult;
	}

	/**
	 * ����URL����post�����ȡ����
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public static HttpResult doPostGetCookie(String url, Map<String, String> paramsMap, int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null;
		HttpPost post = new HttpPost(url);
		String host = null;
		try {
			host = new URL(url).getHost();
			if (paramsMap != null && paramsMap.size() > 0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> m : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				}
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
				post.setEntity(reqEntity);
			}
			HttpResponse response = getHttpClient(0).execute(post);
			Header[] cookie = response.getHeaders("Set-Cookie");

			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}
			if (cookie != null) {
				result = cookie[0].toString();
				result = result.split("Set-Cookie: ")[1];
			}

		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPost:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}
		httpResult.setBody(result);
		return httpResult;
	}

	public static HttpResult doPostUseCookie(String url, Map<String, String> paramsMap, String cookie, int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null;
		HttpPost post = new HttpPost(url);
		String host = null;
		try {
			host = new URL(url).getHost();
			post.addHeader("Cookie", cookie);
			if (paramsMap != null && paramsMap.size() > 0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> m : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				}
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
				post.setEntity(reqEntity);
			}
			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}
			if (resEntity != null) {
				String respBody = EntityUtils.toString(resEntity);
				try {
					result = respBody;
				} catch (Exception e) {
					logger.error("+++++==> respBody:" + respBody + " <==+++++", e);
				}
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPost:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}
		httpResult.setBody(result);
		return httpResult;
	}

	/**
	 * ���ʷ���
	 * 
	 * @param url
	 *            ��ַ
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static HttpResult doPostXml(String url, String xml, int... timeout) {
		return doPostXml(url, "UTF-8", xml, null, timeout);
	}

	public static HttpResult doPostXml(String url, String xml, Map<String, String> headerMap, int... timeout) {
		return doPostXml(url, "UTF-8", xml, headerMap, timeout);
	}

	public static HttpResult doPostXml(String url, String charSet, String xml, int... timeout) {
		return doPostXml(url, charSet, xml, null, timeout);
	}

	public static HttpResult doPostXml(String url, String charSet, String xml, Map<String, String> headerMap,
			int... timeout) {
		HttpResult httpResult = new HttpResult();
		long currentTime = System.currentTimeMillis();
		String result = null, host = null;
		HttpPost post = new HttpPost(url);
		// Ȼ���Soap����������ӵ�PostMethod��
		byte[] b = null;
		InputStream is = null;
		HttpResponse response = null;
		HttpClient httpClient = null;
		try {
			host = new URL(url).getHost();
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}
			charSet = charSet == null ? "UTF-8" : charSet;
			b = xml.getBytes(charSet);
			is = new ByteArrayInputStream(b, 0, b.length);
			HttpEntity reqEntity = new InputStreamEntity(is, b.length,
					ContentType.create(ContentType.TEXT_XML.getMimeType(), Charset.forName(charSet)));
			post.setEntity(reqEntity);
			httpClient = getHttpClient(0);
			response = httpClient.execute(post);
			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
					httpResult.setBody(result);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString error <==+++++", e);
				}
			}
			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}

		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (Exception e) {
			logger.error("+++++==> doPostXml:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			response = null;
			httpClient = null;
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("HttpClientUtil doPostXml error", e);
				}
			}
			if (monitorHostList.contains(host)) {
				logger.warn("============>[" + url + "]" + " use time:[" + (System.currentTimeMillis() - currentTime)
						+ " ms]<============");
			}
		}

		return httpResult;
	}

	/**
	 * ����URL����post�����ȡ���� Rest
	 * 
	 * @param url
	 * @return
	 */
	public static HttpResult doPostJson(String url, String json, int... timeout) {
		HttpResult httpResult = new HttpResult();
		String result = null;
		HttpPost post = new HttpPost(url);
		try {
			post.addHeader("content-type", "application/json");
			if (StringUtils.isNotEmpty(json)) {
				StringEntity myEntity = new StringEntity(json, "UTF-8");
				post.setEntity(myEntity);
			}
			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			Map<String, String> headerMap = new HashMap<String, String>();
			for (Header header : response.getAllHeaders()) {
				headerMap.put(header.getName(), header.getValue());
			}
			httpResult.setHeaderMap(headerMap);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString <==+++++", e);
				}
				httpResult.setBody(result);
			}

			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}

		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPostJson:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
		}
		return httpResult;
	}

	public static HttpResult doPostJson(String url, String json, String charset, int... timeout) {
		HttpResult httpResult = new HttpResult();
		String result = null;
		HttpPost post = new HttpPost(url);
		try {
			post.addHeader("content-type", "application/json");
			if (StringUtils.isNotEmpty(json)) {
				StringEntity myEntity = new StringEntity(json, charset);
				post.setEntity(myEntity);
			}
			HttpResponse response = getHttpClient(0).execute(post);

			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			Map<String, String> headerMap = new HashMap<String, String>();
			for (Header header : response.getAllHeaders()) {
				headerMap.put(header.getName(), header.getValue());
			}
			httpResult.setHeaderMap(headerMap);
			if (resEntity != null) {
				try {
					result = EntityUtils.toString(resEntity);
				} catch (Exception e) {
					logger.error("+++++==> EntityUtils.toString <==+++++", e);
				}
				httpResult.setBody(result);
			}

			if (statusCode != OK) {
				logger.error("+++++==>statusCode:[" + statusCode + "],url:" + url + " <==+++++");
				post.abort();
				return httpResult;
			}

		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> doPostJson:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
		}
		return httpResult;
	}

	/**
	 * ͼƬ�ļ�
	 */
	public static final String FILE_TYPE_IMAGE = "1";
	/**
	 * ��Ƶ�ļ�
	 */
	public static final String FILE_TYPE_VEDIO = "2";
	/**
	 * ��Ƶ�ļ�
	 */
	public static final String FILE_TYPE_AUDIO = "3";
	/**
	 * ��װ�ļ�
	 */
	public static final String FILE_TYPE_SETUP = "4";
	/**
	 * ѹ���ļ�
	 */
	public static final String FILE_TYPE_COMPRESS = "5";

	/**
	 * �����ļ����ļ�������
	 * 
	 * @param filetype
	 *            �ļ����� 1ͼƬ 2��Ƶ 3��Ƶ 4��װ�ļ� 5�����Զ���
	 * @param data
	 * @param typelimit
	 *            �ļ����� ��д������ļ����ͺ�׺,����򶺺Ÿ���
	 * @param sizelimit
	 *            �ļ���С ��λbyte,Ĭ��������
	 * @return
	 */
	public static HttpResult uploadToFileStore(String url, byte[] data, String fileName, String filetype,
			String typelimit, String sizelimit, int... timeout) {
		HttpResult httpResult = new HttpResult();
		String result = null;
		if (data == null || data.length <= 0) {
			return null;
		}
		File tmpFile = null;
		HttpPost post = new HttpPost(url);
		try {
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null,
					Charset.forName("UTF-8"));
			// 1ͼƬ 2��Ƶ 3��Ƶ 4��װ�ļ� 5�����Զ���
			if (StringUtils.isNotEmpty(filetype))
				reqEntity.addPart("filetype", new StringBody(filetype));
			// ��д������ļ����ͺ�׺,����򶺺Ÿ���
			if (StringUtils.isNotEmpty(typelimit))
				reqEntity.addPart("typelimit", new StringBody(typelimit));
			// ��λbyte,Ĭ��������
			if (StringUtils.isNotEmpty(sizelimit))
				reqEntity.addPart("sizelimit", new StringBody(sizelimit));
			if (StringUtils.isEmpty(fileName)) {
				fileName = "tmp.jpeg";
			}
			tmpFile = getFileFromBytes(data, "/tmp/" + fileName);
			if (tmpFile != null && tmpFile.length() > 0)
				reqEntity.addPart("file", new FileBody(tmpFile));

			post.setEntity(reqEntity);

			HttpResponse response = getHttpClient(0).execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			httpResult.setCode(statusCode);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				String respBody = EntityUtils.toString(resEntity);
				try {
					result = respBody;
				} catch (Exception e) {
					logger.error("+++++==> respBody:" + respBody + " <==+++++", e);
				}
			}
		} catch (SocketTimeoutException e) {
			httpResult.setCode(READ_TIMEOUT);
			return httpResult;
		} catch (IOException e) {
			logger.error("+++++==> uploadToFileStore:" + url + " <==+++++", e);
			httpResult.setCode(OTHER_IO_EXCEPTION);
			return httpResult;
		} finally {
			post.releaseConnection();
			if (tmpFile != null) {
				tmpFile.delete();
			}
		}
		httpResult.setBody(result);
		return httpResult;
	}

	/**
	 * ��ת�ļ�
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	private static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = null;
		if (null == b || StringUtils.isEmpty(outputFile))
			return null;

		BufferedOutputStream stream = null;
		try {
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			logger.error("~~~", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("~~~~", e);
				}
			}
		}
		return ret;
	}

	public static String generateUrl(Map<String, String> params) {
		StringBuffer geturl = new StringBuffer("");
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			try {
				geturl.append(key + "=" + URLEncoder.encode(value, "utf-8") + "&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return geturl.toString();
	}

	
}

class HttpResult {
	public void setCode(int statusCode) {
		// TODO Auto-generated method stub
	}

	public void setBody(String result) {
		// TODO Auto-generated method stub
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		// TODO Auto-generated method stub
	}
}