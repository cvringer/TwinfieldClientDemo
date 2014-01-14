package nl.keienberg.twinfield.adapter.client.impl;

import com.twinfield.session.Abandon;
import com.twinfield.session.AbandonResponse;
import com.twinfield.session.Header;
import com.twinfield.session.Logon;
import com.twinfield.session.LogonResponse;
import nl.keienberg.twinfield.adapter.client.SessionDetails;
import nl.keienberg.twinfield.adapter.client.TwinfieldWebServiceSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;

/**
 *
 * Generic WebServiceClient Implementation.
 */
@ManagedResource
public class TwinfieldWebServiceSessionImpl extends BaseTwinfieldWebService implements TwinfieldWebServiceSession {
    /**
     * Logon url.
     */
    public static final String SOAP_ACTION_LOGON = "http://www.twinfield.com/Logon";
    /**
     * Abandon url.
     */
    public static final String SOAP_ACTION_ABANDON = "http://www.twinfield.com/Abandon";

    private static final Logger LOG = LoggerFactory.getLogger(TwinfieldWebServiceSessionImpl.class);

    @Override
    public boolean abandon(SessionDetails sessionDetails) {
        try {
            WebServiceMessageCallback requestCallback
                    = createRequestCallbackWithSessionId(SOAP_ACTION_ABANDON, sessionDetails.getSessionId());
            AbandonResponse response
                    = (AbandonResponse) getWebServiceTemplate().marshalSendAndReceive(new Abandon(), requestCallback);
            return (null != response);
        } catch (Exception e) {
            LOG.warn("Error abandoning Twinfield session. Cause: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public SessionDetails create(final Logon logon) {
        SessionDetails sessionDetails = null;
        try {
            Source source = createLogonSource(logon);
            sessionDetails = send(source);
        } catch (Exception e) {
            LOG.error("Error creating Twinfield session. Cause: {}", e.getMessage());
        }
        return sessionDetails;
    }

    private SessionDetails send(final Source source) throws TransformerConfigurationException {
        final SessionDetails sessionDetails = new SessionDetails();
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();

        WebServiceMessageCallback requestCallBack = getWebServiceMessageCallback(source, transformer);
        WebServiceMessageExtractor responseCallBack = getWebServiceMessageExtractor(sessionDetails);

        getWebServiceTemplate().sendAndReceive(requestCallBack, responseCallBack);
        return sessionDetails;
    }

    WebServiceMessageExtractor getWebServiceMessageExtractor(final SessionDetails sessionDetails) {
        return new WebServiceMessageExtractor() {
                @Override
                public Object extractData(WebServiceMessage message) throws IOException, TransformerException {
                    if (message instanceof SoapMessage) {
                        SoapMessage soapMessage = (SoapMessage) message;
                        sessionDetails.setSessionId(getSessionId(soapMessage));
                        sessionDetails.setMessage(String.format("Got session %s", sessionDetails.getSessionId()));

                        LogonResponse resp = getLogonResponse(soapMessage);
                        sessionDetails.setCluster(resp.getCluster());
                        sessionDetails.setLogonResult(resp.getLogonResult().value());
                        sessionDetails.setNextAction(resp.getNextAction().value());
                    }
                    return message;
                }
            };
    }

    WebServiceMessageCallback getWebServiceMessageCallback(final Source source, final Transformer transformer) {
        return new WebServiceMessageCallback() {
                @Override
                public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
                    transformer.transform(source, message.getPayloadResult());
                    if (message instanceof SoapMessage) {
                        SoapMessage soapMessage = (SoapMessage) message;
                        soapMessage.setSoapAction(SOAP_ACTION_LOGON);
                    }
                }
            };
    }

    private Source createLogonSource(final Logon logon) throws IOException {
        StringResult result = new StringResult();
        getWebServiceTemplate().getMarshaller().marshal(logon, result);
        return new StringSource(result.toString());
    }

    private String getSessionId(final SoapMessage message) throws IOException {
        SoapHeaderElement element = message.getSoapHeader().examineAllHeaderElements().next();
        return ((Header) getWebServiceTemplate().getUnmarshaller().unmarshal(element.getSource())).getSessionID();
    }

    private LogonResponse getLogonResponse(final SoapMessage message) throws IOException {
        return (LogonResponse) getWebServiceTemplate().getUnmarshaller().unmarshal(message.getPayloadSource());
    }
}
