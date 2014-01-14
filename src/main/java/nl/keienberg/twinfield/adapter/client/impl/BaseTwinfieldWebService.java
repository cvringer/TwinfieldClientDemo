package nl.keienberg.twinfield.adapter.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * The type Base winfield web service.
 */
public class BaseTwinfieldWebService extends WebServiceGatewaySupport {
    private static final Logger LOG = LoggerFactory.getLogger(BaseTwinfieldWebService.class);

    /**
     * TheTwinfield namespace.
     */
    public static final String QNAME_NAMESPACE_URI = "http://www.twinfield.com/";
    /**
     * The Twinfield prefix.
     */
    public static final String QNAME_PREFIX = "twin";
    /**
     * The Header tag.
     */
    public static final String HEADER_TAG = "Header";
    /**
     * The SessionId tag.
     */
    public static final String SESSION_ID_TAG = "SessionID";


    /**
     * Creates new.
     */
    public BaseTwinfieldWebService() {
        setWebServiceTemplate(new LoggingWebServiceTemplate());
    }

    /**
     * Sets the maximum number of retries if connection to web service fails.
     *
     * @param maxRetryCount number of retries (total number of connection attempts is one more
     *                      than this number)
     */
    @ManagedAttribute(description = "Number of times to retry creating a connection before an exception is thrown")
    public void setMaxConnectionRetry(int maxRetryCount) {
        LoggingWebServiceTemplate loggingTemplate = getLoggingWebServiceTemplate();
        loggingTemplate.setMaxConnectionRetries(maxRetryCount);
    }

    /**
     * Gets the maximum number of retries if connection to web service fails.
     *
     * @return number of retries configured (total number of connection attempts is one more
     *         than this number)
     */
    @ManagedAttribute
    public int getMaxConnectionRetry() {
        LoggingWebServiceTemplate loggingTemplate = getLoggingWebServiceTemplate();
        return loggingTemplate.getMaxConnectionRetries();
    }

    LoggingWebServiceTemplate getLoggingWebServiceTemplate() {
        WebServiceTemplate template = getWebServiceTemplate();
        if (!(template instanceof LoggingWebServiceTemplate)) {
            throw new IllegalStateException(
                    "This property can only accessed with a LoggingWebServiceTemplate, not with: "
                            + template);
        }
        return (LoggingWebServiceTemplate) template;
    }

    WebServiceMessageCallback createRequestCallback(final String soapAction) {
        WebServiceMessageCallback callBack = new WebServiceMessageCallback() {
            @Override
            public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
                if (message instanceof SoapMessage) {
                    SoapMessage soapMessage = (SoapMessage) message;
                    soapMessage.setSoapAction(soapAction);
                }
            }
        };
        return callBack;
    }

    WebServiceMessageCallback createRequestCallbackWithSessionId(
                                final String soapAction, final String sessionId)
    {
        WebServiceMessageCallback callBack = new WebServiceMessageCallback() {
            @Override
            public void doWithMessage(WebServiceMessage message) throws IOException {
                if (message instanceof SoapMessage) {
                    SaajSoapMessage soapMessage = (SaajSoapMessage) message;
                    soapMessage.setSoapAction(soapAction);
                    addHeader(soapMessage, sessionId);
                }
            }
        };
        return callBack;
    }

    SaajSoapMessage addHeader(SaajSoapMessage saajSoapMessage, final String sessionId) {
        try {
            SOAPHeader soapHeader = saajSoapMessage.getSaajMessage().getSOAPPart().getEnvelope().getHeader();

            Name headerElement = saajSoapMessage.getSaajMessage().getSOAPPart().getEnvelope()
                                    .createName(HEADER_TAG, QNAME_PREFIX, QNAME_NAMESPACE_URI);
            SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(headerElement);

            SOAPElement idToken = soapHeaderElement.addChildElement(SESSION_ID_TAG, QNAME_PREFIX);
            idToken.setTextContent(sessionId);
        } catch (SOAPException e) {
            LOG.error("Error adding Twinfield Header. Cause: {}", e.getMessage());
        }
        return saajSoapMessage;
    }
}
