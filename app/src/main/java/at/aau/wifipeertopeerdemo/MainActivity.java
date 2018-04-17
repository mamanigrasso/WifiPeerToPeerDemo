package at.aau.wifipeertopeerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    Button btnDiscoverPeers, btnSendHello;
    WifiP2pDevice[] devices = null;
    WifiP2pDevice connectedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        btnDiscoverPeers = findViewById(R.id.btnSearchPeers);
        btnDiscoverPeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });

        btnSendHello = findViewById(R.id.btnSendHello);
        btnSendHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHelloToConnectedDevice();
            }
        });
        btnSendHello.setEnabled(false);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                makeText("success");
            }

            @Override
            public void onFailure(int reasonCode) {
                makeText("failure");
            }
        });
    }

    /**
     * Shows a text in form of a toast.
     * @param text String
     */
    public void makeText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        CharSequence[] devicesChar = new CharSequence[]{};

        if (wifiP2pDeviceList.getDeviceList().size() > 0) {
            devices = (WifiP2pDevice[]) wifiP2pDeviceList.getDeviceList().toArray();

            for (int i = 0; i < devices.length; i++) {
                devicesChar[i] = devices[i].toString();
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Click to connect");
            alertDialogBuilder.setItems(devicesChar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i > 0 && i < devices.length) {
                        connectToPeer(devices[i]);
                    }
                }
            });
            alertDialogBuilder.show();
        }
    }

    private void connectToPeer(@NonNull final WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                connectedDevice = device;
                btnSendHello.setEnabled(true);
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                connectedDevice = null;
                btnSendHello.setEnabled(false);
            }
        });
    }

    private void sendHelloToConnectedDevice() {
        if (connectedDevice == null)
            return;

        SendHelloAsyncTask task = new SendHelloAsyncTask(this, connectedDevice);
        task.execute();
    }
}
