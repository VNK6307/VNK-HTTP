package vnk.http.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

public class RequestLine {
    private final String method;
    private final String path;
    private final String version;


    public RequestLine(BufferedInputStream in) throws IOException {
        String[] requestLine = parseRequestLine(in);
        this.method = requestLine[0];
        this.path = requestLine[1];
        this.version = requestLine[2];

    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    private String[] parseRequestLine(BufferedInputStream in) throws IOException {
        final var limit = 4096;

        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);

        String[] requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            return null;
        }

        return requestLine;
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}