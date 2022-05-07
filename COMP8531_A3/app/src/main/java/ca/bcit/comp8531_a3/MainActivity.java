package ca.bcit.comp8531_a3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.Inet4Address;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected;

    private EditText etIpAddress;
    private ToggleButton btnConnect;
    private Button btnStartTransaction;
    private TextView tvTransactions;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpAddress = findViewById(R.id.et_ip_address);
        btnConnect = findViewById(R.id.btn_connect);
        btnStartTransaction = findViewById(R.id.btn_start_transaction);
        tvTransactions = findViewById(R.id.tv_transaction_text);
        tvLog = findViewById(R.id.tv_log_text);

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

        btnStartTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginTransaction();
            }
        });


    }

    private void connect(InetAddress serverIp) {
        //TODO - Set up websockets connection
    }

    private void disconnect() {
        //TODO - Tear down websockets connection
    }

    private void beginTransaction() {
        //TODO -
    }

    private void setTransactionSuccessful() {
        //TODO -
    }

    private void endTransaction() {
        //TODO -
    }

}