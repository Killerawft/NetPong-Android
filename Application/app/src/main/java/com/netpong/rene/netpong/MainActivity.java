package com.netpong.rene.netpong;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
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
    private static SensorManager sensorService;
    private Sensor sensor;

    String recieveString;
    String IpStart; //Anfang der IP. Aus dem Wifi heraus genommen
    String GameIPStr; //Gesamte IP Adresse. IpStart + vom Benutzer eingegeben
    String OwnIpStr; //Eigene IP als String
    int ConPlayer = 0; //Die Zahl des verbundenen Spielers. 0 = Nicht verbunden; 1 = Spieler 1; 2 = Spieler 2


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bCon1 = (Button) findViewById(R.id.bCon1);
        final Button bCon2 = (Button) findViewById(R.id.bCon2);
        final Button bDisc = (Button) findViewById(R.id.bDisc);
        final EditText etName = (EditText) findViewById(R.id.etName);

        bDisc.setEnabled(false);
        ConPlayer = 0;

        //Eigene IP Adresse auslesen
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int OwnIp = wifiInfo.getIpAddress();

        //Den Anfang der IP Adresse vorgeben.
        //IpStart = String.format("%d.%d.%d.", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff));
        //etIPStart.setText(IpStart);

        //eigenen IP String setzten
        OwnIpStr = String.format("%d.%d.%d.%d", (OwnIp & 0xff), (OwnIp >> 8 & 0xff),(OwnIp >> 16 & 0xff), (OwnIp >> 24 & 0xff));

        //Sensor Manager initialisieren
        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor != null) {
            sensorService.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "GYROSCOPE Sensor not found", Toast.LENGTH_LONG).show();
            finish();
        }


        bCon1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Spieler 1 verbinden

                Button bCon1 = (Button) findViewById(R.id.bCon1);
                Button bCon2 = (Button) findViewById(R.id.bCon2);
                Button bDisc = (Button) findViewById(R.id.bDisc);
                EditText etIP = (EditText) findViewById(R.id.etIP);

                Context ToastContext = getApplicationContext();
                CharSequence ToastText = "";
                int ToastDuration = Toast.LENGTH_SHORT;

                GameIPStr = etIP.getText().toString();

                if (GameIPStr == "" || ConPlayer != 0)
                    return;

                connectPlayer(etName.getText().toString(), 1);

                while(recieveString == null) //Auf füllen des Strings warten
                {

                }
                if (recieveString.startsWith("NP_OK")) {
                    bCon1.setEnabled(false);
                    bCon2.setEnabled(false);
                    ToastText = "Spieler 1 verbunden";
                    ConPlayer = 1;
                    bDisc.setEnabled(true);
                } else if (recieveString.startsWith("NP_NOK")) {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Spieler 1 bereits vorhanden";
                } else if (recieveString.startsWith("Error Rec")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Fehler beim Empfang";
                } else if (recieveString.startsWith("Error Send")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Fehler beim Senden";
                } else {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Unerwarteter Fehler";
                }

                Toast.makeText(ToastContext, ToastText, ToastDuration).show();
                recieveString = null; //String löschen, damit der nächste mit while auf füllen des Strings warten kann

        }
        });

        bCon2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Spieler 2 Verbinden

                Button bCon1 = (Button) findViewById(R.id.bCon1);
                Button bCon2 = (Button) findViewById(R.id.bCon2);
                Button bDisc = (Button) findViewById(R.id.bDisc);
                EditText etIP = (EditText) findViewById(R.id.etIP);

                Context ToastContext = getApplicationContext();
                CharSequence ToastText = "";
                int ToastDuration = Toast.LENGTH_SHORT;

                GameIPStr = etIP.getText().toString();

                if (GameIPStr == "" || ConPlayer != 0)
                    return;

                connectPlayer(etName.getText().toString(), 2);

                while(recieveString == null) //Auf füllen des Strings warten
                {

                }
                if (recieveString.startsWith("NP_OK")) {
                    bCon1.setEnabled(false);
                    bCon2.setEnabled(false);
                    ToastText = "Spieler 2 verbunden";
                    ConPlayer = 2;
                    bDisc.setEnabled(true);
                } else if (recieveString.startsWith("NP_NOK")) {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Spieler 2 bereits vorhanden";
                } else if (recieveString.startsWith("Error Rec")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Fehler beim Empfang";
                } else if (recieveString.startsWith("Error Send")){
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Fehler beim Senden";
                } else {
                    bCon1.setEnabled(true);
                    bCon2.setEnabled(true);
                    ToastText =  "Unerwarteter Fehler";
                }

                Toast.makeText(ToastContext, ToastText, ToastDuration).show();
                recieveString = null; //String löschen, damit der nächste mit while auf füllen des Strings warten kann

            }
        });


        bDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendDisconnect();
                while (ConPlayer != 0) //Auf Disconnect wareten
                {

                }

                bCon1.setEnabled(true);
                bCon2.setEnabled(true);
                bDisc.setEnabled(false);

                recieveString = null;

                Toast.makeText(getApplicationContext(), "Verbindung getrennt", Toast.LENGTH_SHORT).show();
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


    public void connectPlayer(final String Name, final int PlayerNr)
    {
        new Thread(new Runnable()  //Als eigenen Thread
        {
            @Override
            public void run() {

                int GamePort = 2222;

                byte[] send_bytes;
                String send_data;

                byte[] receiveData = new byte[10];

                int timeout = 3000;

                send_data = "N" + Integer.toString(PlayerNr - 1) + ";" + Name + ";" + OwnIpStr;
                send_bytes = send_data.getBytes();


                try {
                    InetAddress IPAddress =  InetAddress.getByName(GameIPStr);
                    DatagramPacket send_packet = new DatagramPacket(send_bytes, send_bytes.length, IPAddress, GamePort);

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


    public void sendmove(final float SensVal)
    {
        new Thread(new Runnable()  //Als eigenen Thread
        {
            @Override
            public void run() {

                int GamePort = 2222;

                byte[] send_bytes;
                String send_data;

                int timeout = 3000;

                send_data = "S" + Integer.toString(ConPlayer - 1) + ";" + "M";

                if (SensVal < (-3.0)) //Nach Links geneigt
                {
                    send_data += "L";
                    //Toast.makeText(getApplicationContext(), "Links", Toast.LENGTH_SHORT).show();
                }
                else if (SensVal > 3.0) //Nach Rechts geneigt
                {
                    send_data += "R";
                    //Toast.makeText(getApplicationContext(), "Rechts", Toast.LENGTH_SHORT).show();
                }
                else
                    return;    //Wenn in der Mitte, nichts senden

                send_bytes = send_data.getBytes();

                try {
                    InetAddress IPAddress = InetAddress.getByName(GameIPStr);
                    DatagramPacket send_packet = new DatagramPacket(send_bytes, send_bytes.length, IPAddress, GamePort);

                    DatagramSocket client_socket = new DatagramSocket(GamePort);

                    client_socket.setSoTimeout(timeout);
                    client_socket.send(send_packet);

                    client_socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public void SendDisconnect()
    {
        new Thread(new Runnable()  //Als eigenen Thread
        {
            @Override
            public void run() {

                final Button bCon1 = (Button) findViewById(R.id.bCon1);
                final Button bCon2 = (Button) findViewById(R.id.bCon2);
                final Button bDisc = (Button) findViewById(R.id.bDisc);

                int GamePort = 2222;

                byte[] send_bytes;
                String send_data;

                int timeout = 3000;

                send_data = "D" + Integer.toString(ConPlayer - 1);

                send_bytes = send_data.getBytes();

                try {
                    InetAddress IPAddress = InetAddress.getByName(GameIPStr);
                    DatagramPacket send_packet = new DatagramPacket(send_bytes, send_bytes.length, IPAddress, GamePort);

                    DatagramSocket client_socket = new DatagramSocket(GamePort);

                    client_socket.setSoTimeout(timeout);
                    client_socket.send(send_packet);

                    client_socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }

                ConPlayer = 0;

            }
        }).start();
    }




    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        private long lastUpdate;
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (System.currentTimeMillis() - lastUpdate > 1 && ConPlayer > 0) { //Alle 10 ms
                TextView tvSensData = (TextView) findViewById(R.id.tvSensData);

                sendmove(event.values[1]);
                //Toast.makeText(MainActivity.this, String.format("Neigung Y: %f", event.values[1]), Toast.LENGTH_SHORT).show();
                tvSensData.setText(String.format("Sensor Data: %f", event.values[1]));
                lastUpdate = System.currentTimeMillis();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensor != null) {
            sensorService.unregisterListener(mySensorEventListener);
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
