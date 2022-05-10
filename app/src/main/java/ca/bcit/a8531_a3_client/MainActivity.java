package ca.bcit.a8531_a3_client;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.text.RandomStringGenerator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.websocket.EncodeException;

import ca.bcit.a8531_a3_client.Database.DbHelper;
import comp8031.model.Message;
import comp8031.model.MessageEncoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected;
    private OkHttpClient client;
    private WebSocket ws;
    private DbHelper dbHelper;

    private EditText etIpAddress;
    private ToggleButton btnConnect;
    private Button btnStartTransaction;
    private TextView tvTransactions;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpAddress         = findViewById(R.id.et_ip_address);
        btnConnect          = findViewById(R.id.btn_connect);
        btnStartTransaction = findViewById(R.id.btn_start_transaction);
        tvTransactions      = findViewById(R.id.tv_transaction_text);
        tvLog               = findViewById(R.id.tv_log_text);

        client = new OkHttpClient();
        dbHelper = new DbHelper(getApplicationContext());

        // Connect button listener
        btnConnect.setChecked(isConnected);
        btnConnect.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                try {
                    String serverIp = etIpAddress.getText().toString();
                    InetAddress remoteIp = Inet4Address.getByName(serverIp);
                    connect(remoteIp);
                } catch (Exception ex) {
                    isConnected = false;
                    Toast.makeText(getBaseContext(), "Error connecting", Toast.LENGTH_LONG).show();
                }
            } else {
                disconnect();
                isConnected = false;
            }
        });

        // Transaction button listener
        btnStartTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginLocalTransaction();
            }
        });

    }

    /**
     * Connects to WebSocket server
     * @param serverIp
     */
    private void connect(InetAddress serverIp) {
        Request request = new Request.Builder().url("ws://" + serverIp).build();
        ws = client.newWebSocket(request, new WebSocketClient(this));
        isConnected = true;
    }

    /**
     * Disconnects from WebSocket server
     */
    private void disconnect() {
        ws.close(0, "Client closing connection");
        isConnected = false;
    }

    /**
     * Starts a new transaction
     */
    private void beginLocalTransaction() {
        // Make a list of dummy entries to insert
        int numberOfEntries = 10;
        ArrayList<String> entries = new ArrayList<>();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build();

        for (int i=0; i<=numberOfEntries; i++) {
            entries.add(generator.generate(20));
        }

        // Start local insert
        dbHelper.insert(entries);

        // Create and encode message
        Message beginMessage = new Message();
        beginMessage.setTransactionElements(entries);
        String serializedMessage = "";
        try {
            MessageEncoder encoder = new MessageEncoder();
            serializedMessage = encoder.encode(beginMessage);
        } catch (EncodeException ex) {
            Log.e("Message Encoder", "Error encoding message with id " + beginMessage.getTransactionId().toString());
        }

        // Broadcast the transaction
        if (!serializedMessage.isEmpty()) {
            ws.send(serializedMessage);
            tvLog.append("\nProposing transaction with ID " + beginMessage.getTransactionId().toString());
        }
    }

    private void setTransactionSuccessful() {
        //TODO - Implementation of this
    }

    private void endTransaction() {
        //TODO - Implementation of this
    }
}

