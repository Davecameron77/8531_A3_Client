package comp8031.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Message {

    private ArrayList<String> transactionElements;
    private UUID transactionId;
    private Instant transactionTime;
    private boolean indTransactionSuccess;

    public ArrayList<String> getTransactionElements() {
        return transactionElements;
    }

    public void setTransactionElements(ArrayList<String> transactionElements) {
        this.transactionElements = transactionElements;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Instant transactionTime) {
        this.transactionTime = transactionTime;
    }

    public boolean getTransactionSuccess() {
        return  indTransactionSuccess;
    }

    public void setTransactionSuccess(boolean success) {
        this.indTransactionSuccess = success;
    }

    public Message() {
        transactionId = UUID.randomUUID();
        transactionTime = Instant.now();
    }

    @Override
    public String toString() {
        return String.format("This is transaction %s", getTransactionId().toString());
    }
}
