package com.example.dekube.ips;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CalculationActivity extends AppCompatActivity {

    ArrayAdapter<String> listAdapter;
    Button connectNew;
    ListView listView;

    int bluetooth;
    int wlan;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> deviceArray;

    ArrayList<String> pairedDevices;
    ArrayAdapter<String> adapter;

    Calculator calc;

    int rssi;

    IntentFilter filter;
    BroadcastReceiver receiver;

    private static final String B_DEV_1 = "Level Box Slim (F970)";
    private static final String B_DEV_2 = "GT-I9195";
    private static final String B_DEV_3 = "Acer Aspire V";

    private static final String W_DEV_1 = "Speedport W724V Typ C";
    private static final String W_DEV_2 = "AVM Fritz!WLAN Reapeater 310";
    private static final String W_DEV_3 = "TP-Link WLAN Repeater TL-WA850RE";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(), "no bt detected",Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if(!btAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.EXTRA_SCAN_MODE);
                startActivityForResult(intent, 1);
            }

            checkForComp();
            checkForTechnology();
            loadSensors(wlan, bluetooth);
            receiveDistance();



        }
    }

    private void receiveDistance() { 
        if ( bluetooth == 1)
            startGetRssiBluetooth();
        
        if (wlan == 1)
            startGetRssiWlan();
    }

    private void startGetRssiWlan() {
    }

    private void loadSensors(int wlan, int bluetooth) {
        if (bluetooth == 1){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        listAdapter.add(device.getName() + "\n" + device.getAddress());
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    }
                    else{
                        rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                        Toast.makeText(getApplicationContext(), "RSSI: " + rssi, Toast.LENGTH_LONG);
                    }
                }
            };

            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(receiver,filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth anmachen", Toast.LENGTH_LONG);
            finish();
        }
    }

    Intent intent = getIntent();

    private void checkForTechnology(){
        Intent myIntent = getIntent();
        bluetooth = myIntent.getIntExtra("bluetooth", 0);
        wlan = myIntent.getIntExtra("wlan", 0);
    }

    public void startGetRssiBluetooth(){
        btAdapter.startDiscovery();
    }

    public void checkForComp() {
        final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>" ))
                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(CalculationActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }


    /**String message = intent.getStingExtra(StartActivity.EXTRA_MESSAGE);

     TextView textView = (TextView) findViewById(R.id.textView);
     textView.setText(message);
     **/}
