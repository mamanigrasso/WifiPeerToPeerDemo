package at.aau.wifipeertopeerdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Marco on 4/17/2018.
 */

public class TextServerAsyncTask extends AsyncTask<Void, Void, String> {

    private MainActivity activity;

    public TextServerAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = null;
            Socket client = null;
            try {
                serverSocket = new ServerSocket(8888);
                client = serverSocket.accept();
            } catch (IOException e) {
                Log.e("sheesh", e.getMessage());
            }

            if (client == null)
                return null;

            /**
             * If this code is reached, a client has connected and transferred data
             */

            DataInputStream DIS = new DataInputStream(client.getInputStream());
            String text = DIS.readUTF();
            serverSocket.close();
            return text;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Show text.
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            activity.makeText(result);
        }
    }

}
