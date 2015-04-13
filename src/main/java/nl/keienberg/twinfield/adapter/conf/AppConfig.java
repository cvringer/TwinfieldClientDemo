package nl.keienberg.twinfield.adapter.conf;

import nl.keienberg.twinfield.adapter.client.TwinfieldWebServiceSession;
import nl.keienberg.twinfield.adapter.client.impl.TwinfieldWebServiceSessionImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;


/**
 * Configuration.
 */
@Configuration
@ComponentScan("nl.keienberg.twinfield.adapter")
public class AppConfig {
    /**
     * The JAXB context for session requests.
     */
    public static final String JAXB_SESSION_CONTEXT = "com.twinfield.session";

    /**
     * SoapMessageFactory Bean.
     * @return A SoapMessageFactory.
     */
    @Bean
    SoapMessageFactory soapMessageFactory() {
        SaajSoapMessageFactory soapMessageFactory = new SaajSoapMessageFactory();
        soapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
        return soapMessageFactory;

    }

    /**
     * Get a WebserviceTemplate.
     * @return A WebserviceTemplate.
     */
    @Bean
    WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMessageSender(new HttpComponentsMessageSender());
        return webServiceTemplate;
    }

    /**
     * Gets the Twinfield Webservice session.
     * @return the client to use.
     */
    @Bean(name = "twinfieldWebServiceSession")
    TwinfieldWebServiceSession twinfieldWebServiceSession() {
        TwinfieldWebServiceSessionImpl impl = new TwinfieldWebServiceSessionImpl();
        impl.setMessageFactory(soapMessageFactory());
        Jaxb2Marshaller marshaller = sessionMarshaller();
        impl.setMarshaller(marshaller);
        impl.setUnmarshaller(marshaller);
        return impl;
    }

    /**
     * Get the jaxb2Marshaller for marshalling and unmarshalling session context.
     * @return the jaxb2Marshaller.
     */
    @Bean
    Jaxb2Marshaller sessionMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(JAXB_SESSION_CONTEXT);
        return marshaller;
    }

    /**
     * Get the FreeMarkerConfigurationFactoryBean.
     * @return the FreeMarkerConfigurationFactoryBean
     */
    @Bean
    FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean() {
        FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean
                = new FreeMarkerConfigurationFactoryBean();
        freeMarkerConfigurationFactoryBean.setTemplateLoaderPath("classpath:/templates/");
        freeMarkerConfigurationFactoryBean.setDefaultEncoding("UTF-8");
        return freeMarkerConfigurationFactoryBean;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
