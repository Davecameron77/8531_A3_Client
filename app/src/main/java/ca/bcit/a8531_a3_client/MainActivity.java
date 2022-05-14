package ca.bcit.a8531_a3_client;

import android.os.Bundle;
import android.util.Log;
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

import ca.bcit.a8531_a3_client.Database.DbHelper;
import comp8031.model.Message;
import comp8031.model.MessageEncoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected;
    private OkHttpClient client;
    private WebSocketClient wsClient;
    private WebSocket ws;
    private DbHelper dbHelper;

    private final int NUMBER_OF_REMOTE = 2;
    private boolean transactionInProgress = false;
    protected int numberOfSuccesses;

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
        wsClient = new WebSocketClient(this);
        dbHelper = new DbHelper(getApplicationContext());
        numberOfSuccesses = 0;

        tvLog.setText("");
        tvTransactions.setText("");

        // Connect button listener
        btnConnect.setChecked(isConnected);
        btnConnect.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                try {
                    String serverIp = etIpAddress.getText().toString();
                    serverIp = serverIp.isEmpty() ? "10.0.2.2" : serverIp;
                    InetAddress remoteIp = Inet4Address.getByName(serverIp);
                    connect(remoteIp);

                } catch (Exception ex) {
                    isConnected = false;
                    Toast.makeText(getBaseContext(), "Error connecting", Toast.LENGTH_LONG).show();
                    Log.e("Error", "Error: " + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
            } else {
                disconnect();
                isConnected = false;
            }
        });

        // Transaction button listener
        btnStartTransaction.setOnClickListener(view -> beginLocalTransaction(generateEntries()));

    }

    /**
     * Connects to WebSocket server
     * @param serverIp IP Address of remote server
     */
    private void connect(InetAddress serverIp) {

        // This will default to ws://10.0.2.2/bcit/websocket/device if the user enters nothing
        Request request = new Request.Builder().url("ws://" + serverIp.toString() + ":8080/bcit/websocket/device").build();
        WebSocketClient wsClient = new WebSocketClient(this);
        ws = client.newWebSocket(request, wsClient);

        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        isConnected = true;
    }

    /**
     * Disconnects from WebSocket server
     */
    private void disconnect() {
        ws.close(1000, "Client closing connection");
        isConnected = false;
    }

    /**
     * Starts a new transaction
     */
    protected void beginLocalTransaction(ArrayList<String> entries) {
        if (!isConnected) {
            Toast.makeText(this, "Error: Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (transactionInProgress) {
            Toast.makeText(this, "Error: Transaction already in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        transactionInProgress = true;

        // Start local insert
        dbHelper.insert(entries);

        // Create and encode message
        Message beginMessage = new Message();
        beginMessage.setTransactionElements(entries);

        runOnUiThread(new TransactionTask(this, beginMessage.getTransactionId()));

        String serializedMessage = "";
        try {
            MessageEncoder encoder = new MessageEncoder();
            serializedMessage = encoder.encode(beginMessage);
        } catch (Exception ex) {
            Log.e("Message Encoder", "Error encoding message with id " + beginMessage.getTransactionId());
        }

        // Broadcast the transaction
        if (!serializedMessage.isEmpty()) {
            ws.send(serializedMessage);
        }
    }
                /**
     * Intended to be run by remote nodes, will insert and commit in the same call
     * @param entries Entries to insert
     * @return true on success
     */
    protected boolean completeRemoteTransaction(ArrayList<String> entries) {
        dbHelper.insert(entries);

        return dbHelper.commitTransaction();
    }

    /**
     * Broadcasts transaction success by returning the message
     * message.transactionSuccess should == true
     * @param completeMessage The message to broadcast back
     */
    protected void setTransactionSuccessful(Message completeMessage) throws InterruptedException {
        MessageEncoder encoder = new MessageEncoder();
        String serializedMessage = encoder.encode(completeMessage);
        long sleepTime = (long) (Math.random() * 100);
        Thread.sleep(sleepTime);
        ws.send(serializedMessage);
    }

    /**
     * Finalizes a transaction after the correct amount of ACKs
     */
    protected boolean endTransaction() {
        if (numberOfSuccesses == NUMBER_OF_REMOTE) {
            if (dbHelper.commitTransaction()) {
                numberOfSuccesses = 0;
                transactionInProgress = false;
                runOnUiThread(new UpdateTask(this));

                return true;
            }
        }
        return false;
    }

    /**
     * Generates boilerplate for transactions
     * @return ArrayList<String> of dummy entries
     */
    protected ArrayList<String> generateEntries() {
        // Make a list of dummy entries to insert
        int numberOfEntries = 10;
        ArrayList<String> entries = new ArrayList<>();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build();

        for (int i=0; i<=numberOfEntries; i++) {
            entries.add(generator.generate(20));
        }

        return entries;
    }

}

