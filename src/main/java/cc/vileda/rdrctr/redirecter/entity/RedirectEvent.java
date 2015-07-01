package cc.vileda.rdrctr.redirecter.entity;

import javax.servlet.http.HttpServletRequest;

public class RedirectEvent {
    private final HttpServletRequest request;
    private final String toHost;

    public RedirectEvent(HttpServletRequest request, String toHost) {
        this.request = request;
        this.toHost = toHost;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getToHost() {
        return toHost;
    }
}
