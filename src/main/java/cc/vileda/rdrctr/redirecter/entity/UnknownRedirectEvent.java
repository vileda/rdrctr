package cc.vileda.rdrctr.redirecter.entity;

import javax.servlet.http.HttpServletRequest;

public class UnknownRedirectEvent {
    private final HttpServletRequest request;

    public UnknownRedirectEvent(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
