package nl.keienberg.twinfield.adapter.client.impl;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 *  WebServiceTemplate that logs exceptions thrown, including the URI the request was sent to.
 *  Furthermore supports retries of connection attempts.
 */
public class LoggingWebServiceTemplate extends WebServiceTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingWebServiceTemplate.class);
    private int maxConnectionRetries;

    @Override
    public <T> T sendAndReceive(String uriString,
                                WebServiceMessageCallback requestCallback,
                                WebServiceMessageExtractor<T> responseExtractor)
    {
        return trySendAndReceive(0, uriString, requestCallback, responseExtractor);
    }

    private <T> T trySendAndReceive(int retryCount, String uriString, WebServiceMessageCallback requestCallback,
                                    WebServiceMessageExtractor<T> responseExtractor)
    {
        try {
            T response = super.sendAndReceive(uriString, requestCallback, responseExtractor);
            if (retryCount > 0 && LOG.isInfoEnabled()) {
                String msg = String.format("Call to %s succeeded on retry: %s", uriString, retryCount);
                LOG.info(msg);
            }
            return response;
        } catch (WebServiceIOException e) {
            // the connection or read timed out, the exception's stack trace is probably
            // not that interesting...
            Throwable cause = e.getCause();
            if (cause instanceof ConnectTimeoutException
                    && maxConnectionRetries > retryCount)
            {
                // unable to connect, but we have not reached max retry attempts
                int nextTry = retryCount + 1;
                if (LOG.isDebugEnabled()) {
                    String msg = String.format("Failed to connect to %s on try: %s, retrying (max retries: %s)",
                            uriString, nextTry, maxConnectionRetries);
                    LOG.debug(msg);
                }
                return trySendAndReceive(nextTry, uriString, requestCallback, responseExtractor);
            }
            logException("Communication error calling webservice at: %s: %s", uriString, e);
            throw e;
        } catch (RuntimeException e) {
            // always log stack trace, because this is not something we anticipate
            LOG.error("Exception calling webservice at: " + uriString, e);
            throw e;
        }
    }

    private void logException(String msg, String uriString, RuntimeException e) {
        String logEntry;
        if (maxConnectionRetries == 0) {
            logEntry = String.format(msg, uriString, e.toString());
        } else {
            logEntry = String.format(msg + " (after %s retries)", uriString, e.toString(), maxConnectionRetries);
        }
        LOG.error(logEntry);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Debug information about the exception:", e);
        }
    }

    /**
     * @param aMaxRetries the maximum number of retries for connections.
     */
    public void setMaxConnectionRetries(int aMaxRetries) {
        maxConnectionRetries = aMaxRetries;
    }

    /**
     * @return the the maximum number of retries before we report we can't connect.
     */
    public int getMaxConnectionRetries() {
        return maxConnectionRetries;
    }
}
