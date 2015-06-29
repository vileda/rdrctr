package cc.vileda.rdrctr.redirecter.boundary;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class RedirectsResourceTest {

    @Test
    public void testExtractSubdomain() throws Exception {
        String subdomain = RedirectsResource.extractSubdomain("sub.domain.tld");
        assertThat(subdomain, is("sub."));
    }
}