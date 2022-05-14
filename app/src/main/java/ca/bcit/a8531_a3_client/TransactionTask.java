package ca.bcit.a8531_a3_client;

import android.widget.TextView;

public class TransactionTask implements Runnable {

    String transactionMessage;
    MainActivity activity;

    public TransactionTask(MainActivity mainActivity, String message) {
        transactionMessage = message;
        activity = mainActivity;
    }

    public void run() {
        ((TextView) activity.findViewById(R.id.tv_transaction_text)).append("\nTransaction ID: " + transactionMessage);
    }
}
