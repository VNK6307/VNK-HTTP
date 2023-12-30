package vnk.http;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        int port = 9999;

        List<String> l = List.of("/index.html",
                "/spring.svg",
                "/spring.png",
                "/resources.html",
                "/styles.css",
                "/app.js",
                "/links.html",
                "/forms.html",
                "/classic.html",
                "/events.html",
                "/events.js");
        Server server = new Server(l);
        server.start(port);
    }
}
