package ca.bcit.a8531_a3_client;

import android.util.Log;

import javax.websocket.DecodeException;

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

    private MessageEncoder encoder;
    private MessageDecoder decoder;
    private MainActivity activity;

    public WebSocketClient(MainActivity mainActivity){
        super();
        activity = mainActivity;
        encoder = new MessageEncoder();
        decoder = new MessageDecoder();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        final String message = response.message();
        activity.runOnUiThread(new LogTask(activity, message));
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            Message incomingMessage = decoder.decode(text);
            activity.runOnUiThread(new LogTask(activity, incomingMessage.getContent()));
        } catch (DecodeException e) {

        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d(WS_TAG, "WS connection closing");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(WS_TAG, "WS Error");
        Log.e(WS_TAG, t.getLocalizedMessage());
    }
}
