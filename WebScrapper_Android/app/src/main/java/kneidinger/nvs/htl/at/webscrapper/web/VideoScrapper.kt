package kneidinger.nvs.htl.at.webscrapper.web

import org.jsoup.Jsoup
import java.io.IOException

object VideoScrapper {
    fun scrapTheWeb(): String {
        return try {
            Jsoup.connect("https://webtv.feratel.com/webtv/?cam=5132&design=v3&c0=0&c2=1&lg=en&s=0")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .get()
                    .getElementById("fer_video")
                    .select("source")
                    .first()
                    .attr("src")
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}