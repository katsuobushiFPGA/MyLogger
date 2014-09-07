package jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Logger {

    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://www.google.co.jp").get();
        System.out.println(document.html());
    }
}