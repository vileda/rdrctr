package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.redirecter.entity.Redirect;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;

import static cc.vileda.rdrctr.redirecter.boundary.RedirecterTestHelper.assertRedirectTo;
import static cc.vileda.rdrctr.redirecter.boundary.RedirectsResource.extractSubdomain;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unused")
public class RedirectsResourceTest {

    public static final String TESTHOST_FROM_ONE = "testhost.de";
    public static final String TESTHOST_FROM_TWO = "testfoobar.de";
    public static final String TESTHOST_TO_ONE = "http://testhost2.de/";
    public static final String TESTHOST_TO_TWO = "http://foobar.de/";
    private Client client;
    private WebTarget target;

    @Before
    public void before() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        client = ClientBuilder.newClient()
                .register(JsonProcessingFeature.class)
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .property(JsonGenerator.PRETTY_PRINTING, true);
        target = client.target("http://localhost:8080/");
    }

    @Test
    public void testRedirectWithKnownHost() throws Exception {
        assertRedirectTo(target, TESTHOST_FROM_ONE, TESTHOST_TO_ONE);
        assertRedirectTo(target, TESTHOST_FROM_TWO, TESTHOST_TO_TWO);
    }

    @Test
    public void testRedirectWithKnownHostAndPath() throws Exception {
        assertRedirectTo(target.path("/foobar"), "testhost.de", "http://testhost2.de/foobar");
        assertRedirectTo(target.path("/foobar/baz"), "testhost.de", "http://testhost2.de/foobar/baz");
    }

    @Test
    public void testRedirectWithKnownHostAndSubdomain() throws Exception {
        assertRedirectTo(target, "www.testhost.de", "http://www.testhost2.de/");
    }

    @Test
    public void testRedirectWithKnownHostSubdomainAndPath() throws Exception {
        assertRedirectTo(target.path("/foobar/baz"),
                "foo.www.testhost.de", "http://foo.www.testhost2.de/foobar/baz");
    }

    @Test
    public void testRedirectWithUnknownHost() throws Exception {
        Response response = target.request().header("Host", "unknown").get();
        assertNotNull(response);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void testCreateNewRedirect() throws Exception {
        String from = getTestHost();
        String to = getTestHost();

        Redirect redirect = new Redirect(from, to);
        Response response = createRedirectByPost(redirect);

        assertNotNull(response);
        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        URI newRedirectUri = response.getLocation();
        assertNotNull(newRedirectUri);

        JsonObject newRedirect = getRedirect(newRedirectUri);
        assertNotNull(newRedirect);
        JsonObject redirectJson = redirect.asJsonObject();

        assertThat(redirectJson.getString("toHost"), is(newRedirect.getString("toHost")));
        assertThat(redirectJson.getString("fromHost"), is(newRedirect.getString("fromHost")));
        assertThat(redirectJson.getString("createdAt"), is(newRedirect.getString("createdAt")));
    }

    @Test
    public void testGetUnknownRedirectReturns404() {
        Response response = target.path("/rdrctr/0").request().get();
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void testIncrementViewCountOnRedirect() {
        String from = getTestHost();
        String to = getTestHost();
        to = to.replace(extractSubdomain(to), "");
        Response response = createRedirectByPost(from, to);
        assertRedirectTo(target, from, "http://" + extractSubdomain(from) + to + "/");
        JsonObject redirect = getRedirect(response.getLocation());
        assertThat(redirect.getInt("viewCount"), is(1));
    }

    private String getTestHost() {
        return ("test"+ UUID.randomUUID().toString() + ".de").replace('-', '.');
    }

    private JsonObject getRedirect(URI newRedirectUri) {
        return client.target(newRedirectUri)
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE).get(JsonObject.class);
    }

    private Response createRedirectByPost(String from, String to) {
        return createRedirectByPost(new Redirect(from, to));
    }

    private Response createRedirectByPost(Redirect redirect) {
        Entity<JsonObject> redirectEntity = Entity.json(redirect.asJsonObject());
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(redirectEntity);
    }

}