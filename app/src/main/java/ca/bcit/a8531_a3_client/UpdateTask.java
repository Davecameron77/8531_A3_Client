package ca.bcit.a8531_a3_client;

import android.widget.TextView;

public class UpdateTask implements Runnable {
    MainActivity activity;

    public UpdateTask(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public void run() {
        String pendingTransaction = ((TextView) activity.findViewById(R.id.tv_transaction_text)).getText().toString();
        ((TextView) activity.findViewById(R.id.tv_transaction_text)).setText(null);
        ((TextView) activity.findViewById(R.id.tv_log_text)).append("\n" + pendingTransaction);
    }
}
