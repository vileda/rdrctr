package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.redirecter.entity.Redirect;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.jboss.arquillian.test.api.ArquillianResource;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

class RedirecterTestHelper {
    private static void assertIsRedirect(Response response, String from, String to) {
        assertNotNull(response);
        assertThat(from + " redirects to " + to,
                response.getStatus(),
                is(Response.Status.TEMPORARY_REDIRECT.getStatusCode()));
    }

    public static void assertRedirectTo(URL base, String from, String to) throws URISyntaxException {
        assertRedirectTo(base, "", from, to);
    }

    public static void assertRedirectTo(URL base, String path, String from, String to) throws URISyntaxException {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        WebTarget target = createWebTarget(base, path);
        Response response = target.request().header("Host", from).get();
        assertIsRedirect(response, from, to);
        assertThat(from + " redirects to " + to,
                response.getHeaderString("Location"),
                is(to));
    }

    public static WebTarget createWebTarget(URL base) throws URISyntaxException {
        WebTarget target = createClient().target(base.toURI());
        return target;
    }

    public static WebTarget createWebTarget(URL base, String path) throws URISyntaxException {
        WebTarget target = createClient().target(base.toURI());
        return target.path(path);
    }

    public static Client createClient() {
        return ClientBuilder.newClient()
                    .register(JsonProcessingFeature.class)
                    .property(ClientProperties.FOLLOW_REDIRECTS, false)
                    .property(JsonGenerator.PRETTY_PRINTING, true);
    }

    public static String getTestHost() {
        return ("test"+ UUID.randomUUID().toString() + ".de").replace('-', '.');
    }

    public static JsonObject getRedirect(Client client, URI newRedirectUri) {
        return client.target(newRedirectUri)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE).get(JsonObject.class);
    }

    public static Response createRedirectByPost(WebTarget target, String from, String to) {
        return createRedirectByPost(target, new Redirect(from, to));
    }

    public static Response createRedirectByPost(WebTarget target, Redirect redirect) {
        Entity<JsonObject> redirectEntity = Entity.json(redirect.asJsonObject());
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(redirectEntity);
    }
}
