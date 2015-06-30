package cc.vileda.rdrctr.redirecter.control;

import cc.vileda.rdrctr.redirecter.boundary.Redirects;
import cc.vileda.rdrctr.redirecter.entity.Redirect;

import javax.ejb.Stateless;
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

    public static String extractSubdomain(String hostHeader) {
        Pattern tld = Pattern.compile("^(.*)\\..*\\..*$");
        Matcher matcher = tld.matcher(hostHeader);

        return matcher.matches() ? (matcher.group(1) + ".") : "";
    }

    public String buildLocation(Redirect redirect, String subdomain, String path) {
        String location = request.getScheme()
                + "://"
                + subdomain
                + redirect.getToHost()
                + "/" + path;

        return location.replace(redirect.getFromHost(), "");
    }

    public Response redirectTo(Redirect redirect, String path) {
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
}
