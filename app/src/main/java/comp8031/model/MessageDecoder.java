package comp8031.model;

import com.google.gson.Gson;

public class MessageDecoder {

    private static Gson gson = new Gson();

    public Message decode(String s) {
        return gson.fromJson(s, Message.class);
    }

    public boolean willDecode(String s) {
        return (s != null);
    }

    public void init(Object endpointConfig) {
        // Custom initialization logic
    }

    public void destroy() {
        // Close resources
    }
}
