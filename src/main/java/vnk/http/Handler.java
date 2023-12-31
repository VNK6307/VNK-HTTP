package vnk.http;

import vnk.http.model.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Handler {

    public static void handlerGet(BufferedOutputStream out, Request request) throws IOException {
        System.out.println(request.getQueryParams());

        final Path filePath = Path.of(".", "public", request.getPath());
        final String mimeType = Files.probeContentType(filePath);

        if (request.getPath().equals("/classic.html")) {
            handleClassicHtmlRequest(out, filePath, mimeType);
        } else {
            handleRegularRequest(out, filePath, mimeType);
        }
        out.close();

    }

    public static void handlerPost(BufferedOutputStream out, Request request) throws IOException {// ToDo нужен весь запрос. Либо ссылка на его обработку


        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();

    }

    private static void handleClassicHtmlRequest(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        String template = Files.readString(filePath);
        String content = template.replace("{time}", LocalDateTime.now().toString());
        sendResponse(out, mimeType, content.getBytes());
    }

    private static void handleRegularRequest(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        sendResponse(out, mimeType, Files.readAllBytes(filePath));
    }

    private static void sendResponse(BufferedOutputStream out, String mimeType, byte[] content) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(content);
        out.flush();
    }
}
