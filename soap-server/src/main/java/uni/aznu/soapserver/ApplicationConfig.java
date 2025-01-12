package uni.aznu.soapserver;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class ApplicationConfig {
    @Bean
    public ServletRegistrationBean<CXFServlet> dispatcherServlet(){
        return new ServletRegistrationBean<>(new CXFServlet(), "/soap-api/*");
    }

    @Bean
    @Primary
    public DispatcherServletPath dispatcherServletPath(){return () -> "";}

    @Bean
    public LoggingFeature loggingFeature(){
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Bean(name=Bus.DEFAULT_BUS_ID)
    public SpringBus springBus(LoggingFeature loggingFeature){
        SpringBus bus = new SpringBus();
        bus.getFeatures().add(loggingFeature);
        return bus;
    }

    @Bean
    public Endpoint endpoint(Bus bus, VisitService service){
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/visit");
        return endpoint;

    }
}
