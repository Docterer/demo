package com.jojo.lottery;

import java.util.Random;

/**
 * <p>
 * ��Ʊ���ֶ��������ǰѴ󲿷��˵�Ǯ����С�����ˣ�����һ��Ĳ���ͬ����Ʊû���κμ��ɿ��ԣ�<br/>
 * �Ե÷ǳ����š�
 * </p>
 * <p>
 * ��Ʊ�Ĺ����൱�򵥣��󲿷ֶ��Ǹ������롱��ע�������롱��һ�����֣�����һ���Ĺ�����<br/>
 * ����˫ɫ��Ϊ����7��������33����λ������05 10 17 19 29 32 12����������ѡ����������룬<br/>
 * ��ô�Ϳ��Ի��ϼ���Ǯȥ�����������ע������Ͷ����ʽ������һ�����������ء��ĵط���<br/>
 * �����˵���ע��Ǯ������������ط��������Խ�࣬�������Ǯ��Խ�ࡣÿ�ܶ����ġ��գ��ٷ�<br/>
 * ���������7��������33����λ�����������ע�ĺ�����ٷ�������ɵĺ���һ�£���ϲ������<br/>
 * ���������ȥ�ˣ���������500W�Ľ���
 * </p>
 * <p>
 * �ǲ�Ʊ����Ҫ��ô׬Ǯ�أ���ʵ�ϣ���Ʊ��������ѽ�����Ľ���ͳͳ�͸�Ѻ�к�����ˣ�����<br/>
 * ����ĳ�ֱ�����һ���֣���ЩǮ���ǻ����ġ����ԣ����Ʊ����׬�����������ֻҪ��һ��������ô<br/>
 * ��������Ǯ���ˣ���ɳ����������������Ϊ�ɹۡ�
 * 
 * @author jojo
 *
 */
public class ChinaWelfareLottery {

	/**
	 * ˫ɫ��
	 * 
	 * @return
	 */
	public static String colorBall() {
		// ��������
		int[] redSeed = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33 };
		// ��������
		int[] blueSeed = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
		Random random = new Random();
		StringBuffer stringBuffer = new StringBuffer();

		// ��ȡ�������
		stringBuffer.append("����");
		for (int i = 0; i < 6; i++) {
			stringBuffer.append(redSeed[Math.abs(random.nextInt()) % redSeed.length]);
			stringBuffer.append(" ");
		}
		// ��ȡ�������
		stringBuffer.append("����");
		stringBuffer.append(blueSeed[Math.abs(random.nextInt()) % blueSeed.length]);

		return stringBuffer.toString();
	}

	public static void main(String[] args) {
		System.out.println(colorBall());
	}
}
