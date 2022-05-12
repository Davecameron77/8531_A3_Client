package ca.bcit.a8531_a3_client;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.text.RandomStringGenerator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected;

    private EditText etIpAddress;
    private ToggleButton btnConnect;
    private Button btnStartTransaction;
    private TextView tvTransactions;
    private TextView tvLog;

    TableLayout table_layout_data;
    EditText et_contents;
    Button btnAddRow;
    ProgressDialog PD;
    SQLController sqlcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Server connection
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

        // SQLite data
        et_contents = (EditText) findViewById(R.id.et_contents_data);
        btnAddRow = (Button) findViewById(R.id.btn_add_row_data);
        table_layout_data = (TableLayout) findViewById(R.id.tableLayoutData);

        sqlcon = new SQLController(this);

        btnAddRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new MyAsync().execute();

            }
        });


        BuildTable();
    }

    private void connect(InetAddress serverIp) {
        //TODO - Set up websockets connection

        isConnected = true;
    }

    private void disconnect() {
        //TODO - Tear down websockets connection

        isConnected = false;
    }

    private void beginTransaction() {
        // Make a list of dummy entries to insert
        int numberOfEntries = 10;
        ArrayList<String> entries = new ArrayList<>();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build();

        for (int i = 0; i <= numberOfEntries; i++) {
            entries.add(generator.generate(20));
        }

        DbTransaction<String> proposedTransaction = new DbTransaction<>(entries);

        //TODO - Broadcast this over net
    }

    private void setTransactionSuccessful() {
        //TODO - Implementation of this
        return;
    }

    private void endTransaction() {
        //TODO - Implementation of this
        return;
    }


    private class MyAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            /*
            Get the text data and insert to database
             */
            //TODO: Using transaction instead
            String contents = et_contents.getText().toString();

            // inserting data
            sqlcon.open();
            sqlcon.insert(contents);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            table_layout_data.removeAllViews();

            PD = new ProgressDialog(MainActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            BuildTable();
            PD.dismiss();
        }
    }

    private void BuildTable() {

        sqlcon.open();
        Cursor c = sqlcon.readEntry();

        int rows = c.getCount();
        int cols = c.getColumnCount();

        c.moveToFirst();

        // outer for loop
        for (int i = 0; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 0; j < cols; j++) {

                TextView tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(18);
                tv.setPadding(0, 5, 0, 5);

                tv.setText(c.getString(j));

                row.addView(tv);
            }
            c.moveToNext();
            table_layout_data.addView(row);
        }
        sqlcon.close();
    }


}
