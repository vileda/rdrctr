package cc.vileda.rdrctr.redirecter.control;

import cc.vileda.rdrctr.redirecter.boundary.Redirects;
import cc.vileda.rdrctr.redirecter.entity.Redirect;
import cc.vileda.rdrctr.redirecter.entity.RedirectEvent;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class RedirectsHelper {
    @Inject
    HttpServletRequest request;

    @Inject
    Redirects redirects;

    @Inject
    Logger logger;

    @Inject
    Event<RedirectEvent> redirectEvent;

    public static String extractSubdomain(String hostHeader) {
        Pattern tld = Pattern.compile("^(.*)\\..*\\..*$");
        Matcher matcher = tld.matcher(hostHeader);

        return matcher.matches() ? (matcher.group(1) + ".") : "";
    }

    private String buildLocation(Redirect redirect, String subdomain, String path) {
        String location = request.getScheme()
                + "://"
                + subdomain
                + redirect.getToHost()
                + "/" + path;

        return location.replace(redirect.getFromHost(), "");
    }

    private Response redirectTo(Redirect redirect, String path) {
        String hostHeader = request.getHeader("Host");
        String subdomain = extractSubdomain(hostHeader);
        String location = buildLocation(redirect, subdomain, path);

        redirects.incrementViewCount(redirect);

        logger.info("redirect " + hostHeader + " -> " + location + " the " + redirect.getViewCount() + "th time.");

        return Response
                .temporaryRedirect(URI.create(location))
                .build();
    }

    public Response getRedirectByHost(String host, String path) {
        String subdomain = RedirectsHelper.extractSubdomain(host);
        String fromHost = subdomain.length() > 0 ? host.replace(subdomain, "") : host;

        Optional<Redirect> redirect = redirects.findByFromHost(fromHost, host);

        if (redirect.isPresent()) return redirectTo(redirect.get(), path);

        return null;
    }

    public void logRequestToDatabase(@Observes RedirectEvent event) {
        String referer = event.getRequest().getHeader("Referer");
        String fromHost = event.getRequest().getHeader("Host");
        String toHost = event.getToHost();
        String ip = event.getRequest().getRemoteAddr();

        redirects.logRedirect(referer, fromHost, toHost, ip);
    }
}
