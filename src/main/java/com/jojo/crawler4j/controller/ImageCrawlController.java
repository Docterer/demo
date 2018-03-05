
package com.jojo.crawler4j.controller;

import com.jojo.crawler4j.ImageCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class ImageCrawlController {

    public static void main(String[] args) throws Exception {

        String rootFolder = "D:\\Workspace\\test\\crawler4j";
        int numberOfCrawlers = 7;
        String storageFolder = "D:\\Workspace\\test\\crawler4j\\image";

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(rootFolder);

        config.setIncludeBinaryContentInCrawling(true);

        String[] crawlDomains = {"http://www.ishuhui.com/cartoon/book/11"};

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        for (String domain : crawlDomains) {
            controller.addSeed(domain);
        }

        ImageCrawler.configure(crawlDomains, storageFolder);

        controller.start(ImageCrawler.class, numberOfCrawlers);
    }
}