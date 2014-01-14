package nl.keienberg.twinfield.adapter.client;

import com.twinfield.session.Logon;

/**
 * Generic WebService Client.
 */
public interface TwinfieldWebServiceSession {

    /**
     * Sets default uri.
     *
     * @param uri the uri
     */
    void setDefaultUri(final String uri);

    /**
     * Abandon session.
     *
     * @param sessionDetails the session details
     * @return the boolean true if the abandon succeeded.
     */
    boolean abandon(final SessionDetails sessionDetails);

    /**
     * Create session.
     *
     * @param logon the logon
     * @return the session details
     */
    SessionDetails create(final Logon logon);

}
