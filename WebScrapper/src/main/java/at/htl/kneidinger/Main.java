package at.htl.kneidinger;

import org.apache.log4j.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main {
    private static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws InterruptedException, IOException {
        logger.addAppender(new FileAppender(new SimpleLayout(), "./links.log"));
        for (; ; ) {
            scrapTheWeb();
            Thread.sleep(60000);
        }
    }

    private static void scrapTheWeb() {
        try {
            Document document = Jsoup.connect("https://webtv.feratel.com/webtv/?cam=5132&design=v3&c0=0&c2=1&lg=en&s=0")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .get();
            Element videoElement = document.getElementById("fer_video");
            logger.log(Level.INFO, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " " + videoElement.select("source").first().attr("src"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
