package ca.bcit.a8531_a3_client;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

import comp8031.model.Message;
import comp8031.model.MessageDecoder;
import comp8031.model.MessageEncoder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private final String WS_TAG = "WebSocketClient";

    private final MessageEncoder encoder;
    private final MessageDecoder decoder;
    private final MainActivity activity;
    private final ArrayList<UUID> processedMessages;

    public WebSocketClient(MainActivity mainActivity){
        super();
        activity = mainActivity;
        encoder = new MessageEncoder();
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
            activity.runOnUiThread(new LogTask(activity, incomingMessage.getTransactionId().toString()));

            // Don't process the same message twice
            if (processedMessages.contains(incomingMessage.getTransactionId())) {
                return;
            } else {
                processedMessages.add(incomingMessage.getTransactionId());
            }

            if (incomingMessage.getTransactionSuccess()) {
                // Case A: Receiving confirmation from remote
                // Ack successful receipt in MainActivity
                activity.numberOfSuccesses += 1;
            } else {
                // Case B: Receiving instruction from remote
                // completeRemoteTransaction() writes to DB and commits
                // setTransactionSuccessful sends back confirmation
                if (activity.completeRemoteTransaction(incomingMessage.getTransactionElements())) {
                    incomingMessage.setTransactionSuccess(true);
                    activity.setTransactionSuccessful(incomingMessage);
                }
            }
        } catch (Exception e) {
            Log.e(WS_TAG, "Error handling incoming messages");
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {

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
