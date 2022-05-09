package ca.bcit.a8531_a3_client;

import android.widget.TextView;

public class LogTask implements Runnable {
    String logMessage;
    MainActivity activity;

    public LogTask(MainActivity mainActivity, String message) {
        logMessage = message;
        activity = mainActivity;
    }

    public void run() {
        ((TextView) activity.findViewById(R.id.tv_log_text)).append("\n" + logMessage);
    }
}
