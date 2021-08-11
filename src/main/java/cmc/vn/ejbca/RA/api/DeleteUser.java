package cmc.vn.ejbca.RA.api;

public class DeleteUser {
    String userName;
    int reason;
    boolean decision;

    public DeleteUser(String userName, int reason, boolean decision) {
        this.userName = userName;
        this.reason = reason;
        this.decision = decision;
    }

    public String getUserName() {
        return userName;
    }

    public int getReason() {
        return reason;
    }

    public boolean isDecision() {
        return decision;
    }
}
