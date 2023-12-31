package vnk.http.model;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String path;
    private Map<String, String> queryParams;

    public Request(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = new HashMap<>(queryParams);
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        return queryParams.getOrDefault(name, "");
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public String toString() {
        return "Request{" +
                "path='" + path + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}