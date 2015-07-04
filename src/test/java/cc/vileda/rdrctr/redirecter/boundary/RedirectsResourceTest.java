package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.JAXRSConfiguration;
import cc.vileda.rdrctr.LogInterceptor;
import cc.vileda.rdrctr.RedirecterLoggerProducer;
import cc.vileda.rdrctr.redirecter.control.RedirectsHelper;
import cc.vileda.rdrctr.redirecter.entity.*;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static cc.vileda.rdrctr.redirecter.boundary.RedirecterTestHelper.*;
import static cc.vileda.rdrctr.redirecter.control.RedirectsHelper.extractSubdomain;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class RedirectsResourceTest {

    private static final String TESTHOST_FROM_ONE = "testhost.de";
    private static final String TESTHOST_FROM_TWO = "testfoobar.de";
    private static final String TESTHOST_TO_ONE = "http://testhost2.de/";
    private static final String TESTHOST_TO_TWO = "http://foobar.de/";

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(RedirecterLoggerProducer.class)
                .addClass(JAXRSConfiguration.class)
                .addClass(LogInterceptor.class)
                .addClasses(Redirect.class)
                .addClasses(Redirect_.class)
                .addClasses(RedirectLog.class)
                .addClasses(RedirectLog_.class)
                .addClasses(UnknownRedirectEvent.class)
                .addClasses(KnownRedirectEvent.class)
                .addClasses(RedirectsHelper.class)
                .addClasses(Redirects.class)
                .addClass(RedirectsResource.class)
                .addAsWebResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
    }

    @BeforeClass
    public static void initResteasyClient() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @ArquillianResource
    private static URL base;

    @Test
    public void redirectWithKnownHost() throws Exception {
        assertRedirectTo(base, TESTHOST_FROM_ONE, TESTHOST_TO_ONE);
        assertRedirectTo(base, TESTHOST_FROM_TWO, TESTHOST_TO_TWO);
    }

    @Test
    public void redirectWithKnownHostAndPath() throws Exception {
        assertRedirectTo(base, "/foobar", "testhost.de", "http://testhost2.de/foobar");
        assertRedirectTo(base, "/foobar/baz", "testhost.de", "http://testhost2.de/foobar/baz");
    }

    @Test
    public void redirectWithKnownHostAndSubdomain() throws Exception {
        assertRedirectTo(base, "www.testhost.de", "http://www.testhost2.de/");
    }

    @Test
    public void redirectWithKnownHostSubdomainAndPath() throws Exception {
        assertRedirectTo(base, "/foobar/baz", "foo.www.testhost.de", "http://foo.www.testhost2.de/foobar/baz");
    }

    @Test
    public void redirectWithUnknownHost() throws Exception {
        Response response = createWebTarget(base).request().header("Host", "unknown").get();
        assertNotNull(response);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void createNewRedirect() throws Exception {
        String from = getTestHost();
        String to = getTestHost();

        Redirect redirect = new Redirect(from, to);
        Response response = createRedirectByPost(createWebTarget(base), redirect);

        assertNotNull(response);
        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        URI newRedirectUri = response.getLocation();
        assertNotNull(newRedirectUri);

        JsonObject newRedirect = getRedirect(createClient(), newRedirectUri);
        assertNotNull(newRedirect);
        JsonObject redirectJson = redirect.asJsonObject();

        assertThat(newRedirect.getString("toHost"),    is(redirectJson.getString("toHost")));
        assertThat(newRedirect.getString("fromHost"),  is(redirectJson.getString("fromHost")));
        assertThat(newRedirect.getString("createdAt"), is(redirectJson.getString("createdAt")));
    }

    @Test
    public void getUnknownRedirectReturns404() throws URISyntaxException {
        Response response = createWebTarget(base).path("/rdrctr/0").request().get();
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void incrementViewCountOnRedirect() throws URISyntaxException {
        String from = getTestHost();
        String to = getTestHost();
        to = to.replace(extractSubdomain(to), "");
        Response response = createRedirectByPost(createWebTarget(base), from, to);
        assertRedirectTo(base, from, "http://" + extractSubdomain(from) + to + "/");
        JsonObject redirect = getRedirect(createClient(), response.getLocation());
        assertThat(redirect.getInt("viewCount"), is(1));
    }

    @Test
    public void doNotRedirectOnFavicon() throws URISyntaxException {
        Response response = createWebTarget(base).path("/favicon.ico").request().header("Host", TESTHOST_FROM_ONE).get();
        assertNotNull(response);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void testExtractSubdomain() throws Exception {
        String subdomain = RedirectsHelper.extractSubdomain("sub.domain.tld");
        assertThat(subdomain, is("sub."));
    }
}