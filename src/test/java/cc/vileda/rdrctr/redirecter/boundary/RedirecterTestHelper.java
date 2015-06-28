package cc.vileda.rdrctr.redirecter.boundary;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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

    public static void assertRedirectTo(WebTarget client, String from, String to) {
        Response response = client.request().header("Host", from).get();
        assertIsRedirect(response, from, to);
        assertThat(from + " redirects to " + to,
                response.getHeaderString("Location"),
                is(to));
    }
}
