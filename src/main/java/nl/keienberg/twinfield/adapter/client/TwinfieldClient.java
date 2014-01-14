package nl.keienberg.twinfield.adapter.client;

import com.twinfield.session.Logon;
import com.twinfield.session.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Manages a Twinfield Session
 */
@Component
@PropertySource({"classpath:twinfield.properties"})
public class TwinfieldClient {
    @Value("${session.user}")
    private String user;
    @Value("${session.password}")
    private String password;
    @Value("${session.organisation}")
    private String organisation;
    @Value("${session.login.url}")
    private String url;
    @Value("${session.postfix}")
    private String sessionPostfix;

    @Value("${request.postfix}")
    private String requestPostfix;
    @Value("${finder.postfix}")
    private String finderPostfix;

    @Autowired
    private TwinfieldWebServiceSession twinfieldWebServiceSession;

    /**
     * Create session details.
     *
     * @return the session details
     */
    public SessionDetails createSession() {
        Logon logon = new ObjectFactory().createLogon();
        logon.setUser(user);
        logon.setPassword(password);
        logon.setOrganisation(organisation);
        twinfieldWebServiceSession.setDefaultUri(url + sessionPostfix);
        return twinfieldWebServiceSession.create(logon);
    }

    /**
     * Abandon session.
     *
     * @param details the details
     * @return the boolean
     */
    public boolean abandonSession(SessionDetails details) {
        twinfieldWebServiceSession.setDefaultUri(details.getCluster() + sessionPostfix);
        return twinfieldWebServiceSession.abandon(details);
    }
}
