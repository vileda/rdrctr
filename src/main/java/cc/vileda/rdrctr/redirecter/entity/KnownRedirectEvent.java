package cc.vileda.rdrctr.redirecter.entity;

import javax.servlet.http.HttpServletRequest;

public class KnownRedirectEvent {
    private final HttpServletRequest request;
    private final Redirect redirect;

    public KnownRedirectEvent(HttpServletRequest request, Redirect redirect) {
        this.request = request;
        this.redirect = redirect;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Redirect getRedirect() {
        return redirect;
    }
}
