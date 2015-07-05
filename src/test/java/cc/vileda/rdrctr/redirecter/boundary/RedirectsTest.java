package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.*;
import cc.vileda.rdrctr.redirecter.control.RedirectsControl;
import cc.vileda.rdrctr.redirecter.entity.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RedirectsTest {
    private static final String TESTHOST_FROM_ONE = "testhost.de";
    private static final String TESTHOST_TO_ONE = "http://testhost2.de/";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(RedirecterLoggerProducer.class)
                .addClass(NotFoundException.class)
                .addClass(NotFoundExceptionMapper.class)
                .addClass(JAXRSConfiguration.class)
                .addClass(LogInterceptor.class)
                .addClasses(Redirect.class)
                .addClasses(Redirect_.class)
                .addClasses(RedirectLog.class)
                .addClasses(RedirectLog_.class)
                .addClasses(UnknownRedirectEvent.class)
                .addClasses(KnownRedirectEvent.class)
                .addClasses(Redirects.class)
                .addClasses(RedirectsControl.class)
                .addClass(RedirectsResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
    }

    @Inject
    Redirects redirects;

    @Test
    public void testLogRedirect() throws Exception {
        assertNotNull(redirects);
        Redirect redirect = new Redirect("foo", "bar");

        redirect = redirects.saveOrUpdate(redirect);
        assertNotNull(redirect);
        assertNotNull(redirect.getId());

        RedirectLog redirectLog = redirects.logRedirect(redirect, "ref", TESTHOST_FROM_ONE, TESTHOST_TO_ONE, "");

        assertNotNull(redirectLog.getId());
        assertNotNull(redirectLog.getRedirect());
        assertEquals(redirectLog.getRedirect().getId(), redirect.getId());
    }
}