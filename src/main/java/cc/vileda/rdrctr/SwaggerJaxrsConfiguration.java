package cc.vileda.rdrctr;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.DefaultJaxrsConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "SwaggerJaxrsConfiguration", loadOnStartup = 2)
public class SwaggerJaxrsConfiguration extends DefaultJaxrsConfig {

    @Override
    public void init(ServletConfig servletConfig) {
        try {
            servletConfig.getServletContext().setInitParameter("resteasy.scan", "true");
            super.init(servletConfig);
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion("1.0.2");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setHost("localhost:8080");
            beanConfig.setBasePath("/rdrctr");
            beanConfig.setResourcePackage("cc.vileda.rdrctr");
            beanConfig.setScan(true);
        } catch (ServletException e) {
            System.out.println(e.getMessage());
        }
    }
}
