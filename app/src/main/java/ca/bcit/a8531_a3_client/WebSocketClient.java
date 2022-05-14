package ca.bcit.a8531_a3_client;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import comp8031.model.Message;
import comp8031.model.MessageDecoder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private final String WS_TAG = "WebSocketClient";

    private final MessageDecoder decoder;
    private final MainActivity activity;
    private final ArrayList<String> processedMessages;

    public WebSocketClient(MainActivity mainActivity){
        super();
        activity = mainActivity;
        decoder = new MessageDecoder();
        processedMessages = new ArrayList<>();
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        Log.d(WS_TAG, "WS connection opening");
        final String message = response.message();
        if (response.message().contains("Switching Protocols")) {
            return;
        }
        activity.runOnUiThread(new LogTask(activity, message));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        try {
            Message incomingMessage = decoder.decode(text);
            Log.d(WS_TAG, "Received message ID " + incomingMessage.getTransactionId() + " | " + incomingMessage.getTransactionSuccess());

            // Outgoing messages echo back, don't process the same message twice
            if (processedMessages.contains(incomingMessage.getTransactionId())) {
                return;
            }

            // Stop processing a completed transaction
            String complete = ((TextView) activity.findViewById(R.id.tv_log_text)).getText().toString();
            if (complete.contains(incomingMessage.getTransactionId())) {
                return;
            }

            String pending = ((TextView) activity.findViewById(R.id.tv_transaction_text)).getText().toString();
            // Remote receiving initial message
            if (!pending.contains(incomingMessage.getTransactionId())) {
                activity.runOnUiThread(new LogTask(activity, incomingMessage.getTransactionId()));
                processedMessages.add(incomingMessage.getTransactionId());
            }
            // Origin receiving an echo
            if (pending.contains(incomingMessage.getTransactionId()) && incomingMessage.getTransactionSuccess() == false) {
                return;
            }

            if (incomingMessage.getTransactionSuccess()) {
                // Case A: Receiving confirmation from remote
                // Ack successful receipt in MainActivity and end if done
                activity.numberOfSuccesses += 1;
                Log.d(WS_TAG, "Received an ACK, numberOfSuccesses = " + activity.numberOfSuccesses);
                if (activity.endTransaction()) {
                    processedMessages.add(incomingMessage.getTransactionId());
                }
            } else {
                // Case B: Receiving instruction from remote
                // completeRemoteTransaction() writes to DB and commits
                // setTransactionSuccessful sends back confirmation
                if (activity.completeRemoteTransaction(incomingMessage.getTransactionElements())) {
                    incomingMessage.setTransactionSuccess(true);
                    activity.setTransactionSuccessful(incomingMessage);
                    Log.d(WS_TAG, "Reported " + incomingMessage.getTransactionId() + " successful");
                }
            }

            return;
        } catch (Exception e) {
            Log.e(WS_TAG, "Error handling incoming messages");
            Log.e(WS_TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        throw new NotImplementedException("Don't need this");
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d(WS_TAG, "WS connection closing");
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {
        Log.e(WS_TAG, "WS Error");
        Log.e(WS_TAG, t.getLocalizedMessage());
    }
}
