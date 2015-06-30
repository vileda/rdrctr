package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.LogInterceptor;
import cc.vileda.rdrctr.redirecter.entity.Redirect;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
@Interceptors(LogInterceptor.class)
@Path("/")
public class RedirectsResource {
    @Inject
    HttpServletRequest request;

    @Inject
    Redirects redirects;

    @Inject
    Logger logger;

    @GET
    @Path("/{path : ^(?:(?!rdrctr).)*$}")
    public Response doRedirect(@PathParam("path") String path) {
        String host = request.getHeader("Host");
        if(host != null && !"favicon.ico".equals(path)) {
            Response redirectResponse = getRedirectByHost(host, path);
            if (redirectResponse != null) return redirectResponse;
        }

        logger.info("redirect for " + host + "/" + path + " not found.");

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces("application/json")
    public Response createRedirect(Redirect redirect) {
        Redirect newRedirect = redirects.saveOrUpdate(redirect);
        return Response.created(URI.create("/rdrctr/" + newRedirect.getId())).build();
    }

    @GET
    @Path("rdrctr/{id : \\d+}")
    @Produces("application/json")
    public Response getRedirect(@PathParam("id") long id) {
        Redirect redirect = redirects.find(id);
        if(redirect == null) {
            return Response.noContent().build();
        }
        return Response.ok().entity(redirect.asJsonObject()).build();
    }

    private Response getRedirectByHost(String host, String path) {
        String subdomain = extractSubdomain(host);
        String fromHost = subdomain.length() > 0 ? host.replace(subdomain, "") : host;

        Optional<Redirect> redirect = redirects.findByFromHost(fromHost, host);

        if (redirect.isPresent()) return redirectTo(redirect.get(), path);

        return null;
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

    private String buildLocation(Redirect redirect, String subdomain, String path) {
        String location = request.getScheme()
                + "://"
                + subdomain
                + redirect.getToHost()
                + "/" + path;

        return location.replace(redirect.getFromHost(), "");
    }

    public static String extractSubdomain(String hostHeader) {
        Pattern tld = Pattern.compile("^(.*)\\..*\\..*$");
        Matcher matcher = tld.matcher(hostHeader);

        return matcher.matches() ? (matcher.group(1) + ".") : "";
    }
}
