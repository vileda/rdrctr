package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.*;
import cc.vileda.rdrctr.NotFoundException;
import cc.vileda.rdrctr.redirecter.control.RedirectsControl;
import cc.vileda.rdrctr.redirecter.entity.KnownRedirectEvent;
import cc.vileda.rdrctr.redirecter.entity.Redirect;
import cc.vileda.rdrctr.redirecter.entity.UnknownRedirectEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.logging.Logger;

@Stateless
@Interceptors(LogInterceptor.class)
@Path("/")
@Api(value = "/", description = "Redirect")
public class RedirectsResource {
    @Inject
    HttpServletRequest request;

    @Inject
    Redirects redirects;

    @Inject
    Logger logger;

    @Inject
    RedirectsControl redirectsHelper;

    @Inject
    Event<KnownRedirectEvent> knownRedirectEventEvent;

    @Inject
    Event<UnknownRedirectEvent> unknownRedirectEventEvent;

    @GET
    @Path("/{path: ^(?:(?!rdrctr).)*$}")
    @Produces(MediaType.WILDCARD)
    @ApiOperation(value = "redirect the domain", notes = "Redirect")
    public Response doRedirect(@PathParam("path") @ApiParam(name = "path") String path) throws NotFoundException {
        String host = request.getHeader("Host");
        if(host != null && !"favicon.ico".equals(path)) {
            Response redirectResponse = redirectsHelper.getRedirectByHost(host, path);
            if (redirectResponse != null) {
                return redirectResponse;
            }
        }

        logger.info("redirect for " + host + "/" + path + " not found.");
        unknownRedirectEventEvent.fire(new UnknownRedirectEvent(request));

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @ApiOperation(value = "create a redirect", notes = "Create a Redirect Object")
    public Response createRedirect(Redirect redirect) {
        Redirect newRedirect = redirects.saveOrUpdate(redirect);
        return Response.created(URI.create("/rdrctr/" + newRedirect.getId())).build();
    }

    @GET
    @Path("rdrctr/{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRedirect(@PathParam("id") long id) {
        Redirect redirect = redirects.find(id);
        if(redirect == null) {
            return Response.noContent().build();
        }
        return Response.ok().entity(redirect.asJsonObject()).build();
    }
}
