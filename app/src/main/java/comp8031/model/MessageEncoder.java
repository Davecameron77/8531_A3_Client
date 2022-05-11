package comp8031.model;

import com.google.gson.Gson;

public class MessageEncoder {

    private static Gson gson = new Gson();

    public String encode(Message message) {
        return gson.toJson(message);
    }

    public void init(Object endpointConfig) {
        // Custom initialization logic
    }

    public void destroy() {
        // Close resources
    }
}
