package org.chaiware.download16c;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Download16c {
    private static final Logger log = LoggerFactory.getLogger(Download16c.class);
    private static int downloaded = 0;
    private static int skipped = 0;
    private static final Map<Integer, Integer> yearToFailedDownloads = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Instant startTime = Instant.now();
        Document pageContent = Jsoup.connect("https://github.com/sixteencolors/sixteencolors-archive").get();
        log.info ("PageTitle: {}", pageContent.title());
        Elements yearLinks = pageContent.body().getElementsByClass("js-navigation-open Link--primary");
        try {
            Thread.sleep(50 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Browsing by year
        for (Element currentYear: yearLinks) {
            String year = currentYear.attr("title");
            try {
                Integer.parseInt(year);
            } catch (NumberFormatException nfe) {
                log.warn("Skipping: {} as it seems not to be a valid year folder", year);
                continue;
            }

            String yearUrl = "https://github.com" + currentYear.attr("href");
            createFolder(year);
            log.info ("\n\n################ {} ################", year);

            pageContent = Jsoup.connect(yearUrl).get();
            Elements packsLinks = pageContent.body().getElementsByClass("js-navigation-open Link--primary");
            for (Element currentPack: packsLinks) {
                String packUrl = "https://raw.githubusercontent.com" + currentPack.attr("href").replace("blob/", "");
                String fileName = packUrl.split("/")[packUrl.split("/").length - 1];
                String targetFilename = "./" + year + "/" + fileName;
                if (!new File(targetFilename).exists()) {
                    downloadFile(packUrl, targetFilename, year);
                    downloaded++;
                } else {
                    log.warn("File: {} exists, skipping...", targetFilename);
                    skipped++;
                }
            }
        }

        generateReport(startTime, Instant.now());
    }

    private static void createFolder(String folderName) {
        File theDir = new File("./" + folderName);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
    }

    private static void downloadFile(String urlStr, String dest, String year) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(urlStr).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(dest)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            log.info("File: {} Downloaded Successfully", urlStr);
        } catch (IOException e) {
            String msg = "\n\nException while downloading: " + urlStr;
            log.error(msg, e);
            Integer currentFails = yearToFailedDownloads.get(Integer.parseInt(year));
            yearToFailedDownloads.put(Integer.valueOf(year), currentFails != null ? currentFails + 1 : 1);
        }
    }

    private static void generateReport(Instant begin, Instant timeNow) {
        log.info("\n\n################# REPORT #################");
        log.info("Downloaded: {}", downloaded);
        log.info("Skipped: {}", skipped);

        for (Map.Entry<Integer, Integer> entry: yearToFailedDownloads.entrySet()) {
            log.info("Year: {}, FailedDownloads: {}", entry.getKey(), entry.getValue());
        }

        log.info("Duration: {}", Duration.between(begin, timeNow).toString().replace("PT", "").replace("H", ":").replace("M", ":").replace("S", ""));
    }
}
