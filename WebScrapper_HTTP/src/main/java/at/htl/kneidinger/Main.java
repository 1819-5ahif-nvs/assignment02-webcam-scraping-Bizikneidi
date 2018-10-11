package at.htl.kneidinger;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
class JavaHTTPServer implements Runnable {
    // Client Connection via Socket Class
    private Socket connect;

    JavaHTTPServer(Socket c) {
        connect = c;
    }

    @Override
    public void run() {
        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            String site = buildWebsite();
            // we support only GET and HEAD methods, we check
            // send HTTP Headers
            out.println("HTTP/1.1 200 OK");
            out.println("Server: Java HTTP Server from SSaurel : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: text/html");
            out.println("Content-length: " + site.length());
            out.println(); // blank line between headers and content, very important !
            out.println(site);
            out.flush(); // flush character output stream buffer
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
    }

    private String buildWebsite() {
        return "<!DOCTYPE html>" +
                "<html>\n " +
                "<body>\n" +
                "<video id='MyVideo' width='100%' controls preload='auto' autobuffer='' muted=''>\n" +
                "<source src='" + scrapTheWeb() + "' type='video/mp4' />\n" +
                "<div>Hansi</div>\n" +
                "</video>\n" +
                "</body>\n" +
                "</html>";
    }


    private String scrapTheWeb() {
        try {
            Document document = Jsoup.connect("https://webtv.feratel.com/webtv/?cam=5132&design=v3&c0=0&c2=1&lg=en&s=0")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .get();
            Element videoElement = document.getElementById("fer_video");
            return videoElement.select("source").first().attr("src");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);
    private static final int PORT = 3030;

    public static void main(String[] args) throws IOException {
        logger.addAppender(new FileAppender(new SimpleLayout(), "./links.log"));

        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
}
