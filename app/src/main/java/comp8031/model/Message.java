package comp8031.model;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class Message {

    private ArrayList<String> transactionElements;
    private String transactionId;
    private String transactionTime;
    private boolean indTransactionSuccess;

    public ArrayList<String> getTransactionElements() {
        return transactionElements;
    }

    public void setTransactionElements(ArrayList<String> transactionElements) {
        this.transactionElements = transactionElements;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public boolean getTransactionSuccess() {
        return  indTransactionSuccess;
    }

    public void setTransactionSuccess(boolean success) {
        this.indTransactionSuccess = success;
    }

    public Message() {
        DateTimeFormatter customFormatte = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm:ss");
        transactionTime = customFormatte.format(LocalTime.now());
        transactionId = UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return String.format("This is transaction %s", getTransactionId().toString());
    }
}
