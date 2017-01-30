package com.netpong.rene.netpong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private SensorManager sensorManager;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button bCon1 = (Button) findViewById(R.id.bCon1);
        final Button bCon2 = (Button) findViewById(R.id.bCon2);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etIPEnd = (EditText) findViewById(R.id.etIPEnd);
        final TextView etIPStart = (TextView) findViewById(R.id.etIPStart);

        //Eigene IP Adresse auslesen
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int OwnIp = wifiInfo.getIpAddress();
        //Den Anfang der IP Adresse vorgeben.
        etIPStart.setText(String.format("%d.%d.%d.", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff)));


        //Sensor Manager initialisieren
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        bCon1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String GameIPStr;

                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int OwnIp = wifiInfo.getIpAddress();

                GameIPStr = String.format("%d.%d.%d.%d", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff), Integer.parseInt(etIPEnd.getText().toString()));

                try {
                    InetAddress GameIP = InetAddress.getByName(GameIPStr);
                    if (connectPlayer(etName.getText().toString(), 1, GameIP))
                        bCon2.setEnabled(false);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        });

        bCon2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String GameIPStr;

                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int OwnIp = wifiInfo.getIpAddress();

                GameIPStr = String.format("%d.%d.%d.%d", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff), (Integer.parseInt(etIPEnd.getText().toString()) >> 24 & 0xff));

                try {
                    InetAddress GameIP = InetAddress.getByName(GameIPStr);
                    if (connectPlayer(etName.getText().toString(), 2, GameIP))
                        bCon1.setEnabled(false);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ConPlay1(View view) {

    }

    public void ConPlay2(View view) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE && System.currentTimeMillis() - lastUpdate > 3000) { //Nur alle 10ms ausführen
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        Toast.makeText(this, String.format("Neigung Y: %d", event.values[1]), Toast.LENGTH_SHORT);
    }


    public boolean connectPlayer(String Name, int PlayerNr, InetAddress IPAddress) throws IOException {

        int GamePort = 2222;

        byte[] send_bytes;
        String send_data;
        byte[] receiveData = null;
        String recieveString;

        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int OwnIp = wifiInfo.getIpAddress();

        String OwnIpStr = String.format("%d.%d.%d.%d", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff), (OwnIp >> 24 & 0xff));

        DatagramSocket client_socket = new DatagramSocket();

        send_data = "N" + Integer.toString(PlayerNr) + ";" + Name + ";" + OwnIpStr;

        send_bytes = send_data.getBytes();

        DatagramPacket send_packet = new DatagramPacket(send_bytes, send_data.length(), IPAddress, GamePort);
        client_socket.send(send_packet);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        client_socket.receive(receivePacket);

        client_socket.close();

        recieveString = new String(receivePacket.getData());
        //System.out.println("FROM SERVER:" + modifiedSentence);
        if (recieveString == "NP_OK") {
            recieveString = null;
            return true;
        }
        else {
            recieveString = null;
            return false;
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

