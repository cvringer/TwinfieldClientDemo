package nl.keienberg.twinfield.adapter;

import junit.framework.Assert;
import nl.keienberg.twinfield.adapter.client.SessionDetails;
import nl.keienberg.twinfield.adapter.client.TwinfieldClient;
import nl.keienberg.twinfield.adapter.conf.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TwinfieldClientTest {

    @Autowired
    private TwinfieldClient twinfieldClient;

    @Test
    public void testSetupAndAbandonSession() {
        SessionDetails sessionDetails = twinfieldClient.createSession();

        Assert.assertNotNull(sessionDetails.getSessionId());

        twinfieldClient.abandonSession(sessionDetails);
    }
}
