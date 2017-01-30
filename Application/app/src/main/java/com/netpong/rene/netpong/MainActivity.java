package com.netpong.rene.netpong;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
    String recieveString;
    String IpStart; //Anfang der IP. Aus dem Wifi heraus genommen
    String GameIPStr; //Gesamte IP Adresse. IpStart + vom Benutzer eingegeben

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
        IpStart = String.format("%d.%d.%d.", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff));
        etIPStart.setText(IpStart);

        //Sensor Manager initialisieren
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        bCon1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button bCon1 = (Button) findViewById(R.id.bCon1);
                Button bCon2 = (Button) findViewById(R.id.bCon2);

                GameIPStr = String.format(IpStart + "%d", Integer.parseInt(etIPEnd.getText().toString()));

                try {
                    InetAddress GameIP = InetAddress.getByName(GameIPStr);
                    connectPlayer(etName.getText().toString(), 1, GameIP);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Verbinde...", Toast.LENGTH_SHORT);

                while(recieveString == null) //Auf füllen des Strings warten
                {

                }
                if (recieveString.startsWith("NP_OK")) {
                    bCon1.setEnabled(false);
                    bCon2.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Spieler 1 verbunden", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("NP_NOK")) {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Spieler 1 bereits vorhanden", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("Error Rec")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Fehler beim Empfang", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("Error Send")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Fehler beim Senden", Toast.LENGTH_SHORT);
                } else {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Unerwarteter Fehler", Toast.LENGTH_SHORT);
                }

        }
        });

        bCon2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Button bCon1 = (Button) findViewById(R.id.bCon1);
                Button bCon2 = (Button) findViewById(R.id.bCon2);

                GameIPStr = String.format(IpStart + "%d", Integer.parseInt(etIPEnd.getText().toString()));

                try{
                    InetAddress GameIP = InetAddress.getByName(GameIPStr);
                    connectPlayer(etName.getText().toString(), 2, GameIP);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Verbinde...", Toast.LENGTH_SHORT);

                while(recieveString == null) //Auf füllen des Strings warten
                {

                }

                if (recieveString.startsWith("NP_OK")) {
                    bCon1.setEnabled(false);
                    bCon2.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Spieler 2 verbunden", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("NP_NOK")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Spieler 2 bereits vorhanden", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("Error Rec")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Fehler beim Empfang", Toast.LENGTH_SHORT);
                } else if (recieveString.startsWith("Error Send")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Fehler beim Senden", Toast.LENGTH_SHORT);
                } else {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Unerwarteter Fehler", Toast.LENGTH_SHORT);
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


    public void connectPlayer(final String Name, final int PlayerNr, final InetAddress IPAddress)
    {
        new Thread(new Runnable()  //Als eigenen Thread
        {
            @Override
            public void run() {

                final Button bCon1 = (Button) findViewById(R.id.bCon1);
                final Button bCon2 = (Button) findViewById(R.id.bCon2);

                int GamePort = 2222;

                byte[] send_bytes;
                String send_data;

                byte[] receiveData = new byte[10];

                int timeout = 3000;

                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int OwnIp = wifiInfo.getIpAddress();

                String OwnIpStr = String.format("%d.%d.%d.%d", (OwnIp & 0xff), (OwnIp >> 8 & 0xff), (OwnIp >> 16 & 0xff), (OwnIp >> 24 & 0xff));

                send_data = "N" + Integer.toString(PlayerNr - 1) + ";" + Name + ";" + OwnIpStr;

                send_bytes = send_data.getBytes();

                DatagramPacket send_packet = new DatagramPacket(send_bytes, send_bytes.length, IPAddress, GamePort);

                try {
                    DatagramSocket client_socket = new DatagramSocket(GamePort);

                    client_socket.setSoTimeout(timeout);
                    client_socket.send(send_packet);

                    client_socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    recieveString = "Error Send";
                }



                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    DatagramSocket client_socket = new DatagramSocket(GamePort);

                    client_socket.setSoTimeout(timeout);
                    client_socket.receive(receivePacket);

                    client_socket.close();
                    recieveString = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    recieveString = "Error Rec";
                }
            }
        }).start();
    }

/*
    public void sendmovement(final int PlayerNr, final InetAddress IPAddress)
    {
        new Thread(new Runnable()  //Als eigenen Thread
        {
            @Override
            public void run() {



            }).start();
        }
    }
*/








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

