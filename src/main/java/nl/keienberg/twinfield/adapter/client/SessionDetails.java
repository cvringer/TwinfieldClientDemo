package nl.keienberg.twinfield.adapter.client;

/**
 * Pojo to store session details.
 */
public class SessionDetails {
    private String logonResult;
    private String sessionId;
    private String nextAction;
    private String cluster;
    private String message;

    /**
     * Gets logon result.
     *
     * @return the logon result
     */
    public String getLogonResult() {
        return logonResult;
    }

    /**
     * Sets logon result.
     *
     * @param logonResult the logon result
     */
    public void setLogonResult(String logonResult) {
        this.logonResult = logonResult;
    }

    /**
     * Gets next action.
     *
     * @return the next action
     */
    public String getNextAction() {
        return nextAction;
    }

    /**
     * Sets next action.
     *
     * @param nextAction the next action
     */
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    /**
     * Gets session id.
     *
     * @return the session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets session id.
     *
     * @param sessionId the session id
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets cluster.
     *
     * @return the cluster
     */
    public String getCluster() {
        return cluster;
    }

    /**
     * Sets cluster.
     *
     * @param cluster the cluster
     */
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
