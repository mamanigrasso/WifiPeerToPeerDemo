package at.aau.wifipeertopeerdemo;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Marco on 4/17/2018.
 */

public class SendHelloAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context appContext;
    private WifiP2pDevice receiver;

    public SendHelloAsyncTask(@NonNull Context applicationContext, WifiP2pDevice deviceToSendTo) {
        this.appContext = applicationContext.getApplicationContext();
        this.receiver = deviceToSendTo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String host = receiver.deviceAddress;
        int port = 8888;
        Socket socket = new Socket();
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.writeUTF("HELLO");
        } catch (IOException e) {
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return null;
    }
}
