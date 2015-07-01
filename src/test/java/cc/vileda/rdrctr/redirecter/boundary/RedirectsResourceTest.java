package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.redirecter.control.RedirectsHelper;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RedirectsResourceTest {

    @Test
    public void testExtractSubdomain() throws Exception {
        String subdomain = RedirectsHelper.extractSubdomain("sub.domain.tld");
        assertThat(subdomain, is("sub."));
    }
}