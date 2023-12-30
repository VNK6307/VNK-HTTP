package vnk.http;

import vnk.http.model.RequestLine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    public static final String GET = "GET";
    public static final String POST = "POST";

    private final List<String> validPaths;
    private static final int THREAD_POOL_SIZE = 64;
    private final ExecutorService threadPool;

    public Server(List<String> validPaths) {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.validPaths = validPaths;
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.execute(() -> {
                        handleConnection(socket);
                    });
                } catch (IOException e) {
                    LOG.warning(e.getMessage());
                }
            }
        } catch (IOException e) {
            LOG.warning(e.getMessage());
        }
    }// start

    private void handleConnection(Socket socket) {
        String thread = Thread.currentThread().getName();
        System.out.println("Connection to thread " + thread + " active");
        try (
                final var in = new BufferedInputStream(socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            callHandler(in, out);
            // ToDo вызвать соответствующий метод. Излишне???


        } catch (IOException e) {
            LOG.warning(e.getMessage());
        }
    }

    private void callHandler(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        final RequestLine requestLine = new RequestLine(in);
        System.out.println(requestLine.getPath());
        if (!validPaths.contains(requestLine.getPath())) {
            sendNotFoundResponse(out);
            return;
        }

        final String method = requestLine.getMethod();

        switch (method) {
            case GET:
                Handler.handlerGet(out, requestLine);
                break;
            case POST:
                Handler.handlerPost(out, requestLine);
                break;
            default:
                badRequest(out);
        }

    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
        out.close();
    }

    private void sendNotFoundResponse(BufferedOutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.flush();
    }
}
