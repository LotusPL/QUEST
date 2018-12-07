package wsb.quest;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.button;
import static android.R.id.button3;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProximityManager proximityManager;
    private Database db;

    TextToSpeech textToSpeech;
    private String message="";
    private String place = "";

    private HashMap beaconMap = new HashMap();
    public ArrayList<String> beacons = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        /*
        ##################################################################################
        Defining KontaktIO API
        ##################################################################################
         */

        KontaktSDK.initialize("kYgqBKBXvxmQLRONfZsvPUDShWHHocRM");
        Log.i("Sample","Tekst");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());


        /*
        ##################################################################################
         */

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button b = (Button) findViewById(R.id.button3);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), ShowBeacons.class);
                myIntent.putExtra("beacon",beacons);
                startActivity(myIntent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {
                if(iBeacon.getUniqueId().toString().length() > 0){
                    Log.i("Test", "Discovered beacon: " + iBeacon.getUniqueId());
                    Log.i("Test", "Distance: " + iBeacon.getDistance() + " m");
                    Log.i("Test", "Battery level: " + iBeacon.getBatteryPower() + " %");
                    Log.i("Test", "------Kolejny beacon------");
                    addBeacons(iBeacon.getUniqueId().toString());
                    Log.i("Test", "Lista beacon'ów: " + String.valueOf(beacons));
                    Log.i("Test", "------Koniec listy beacon'ów------");

                    String temp = checkInDatabase(iBeacon.getUniqueId());
                    Log.i("Sample", "Temp: " + temp);



                }

            }
            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                //Beacons updated
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                //Beacon lost

                Log.i("Sample", "We lost this beacon: " + iBeacon.getUniqueId());
            }

        };
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.i("Sample", "Eddystone discovered: " + eddystone.toString());
            }
        };
    }
    public void addBeacons(String name) {
        boolean exist = false;
        for (int i = 0; i < beacons.size(); i++) {
            if (beacons.get(i).equals(name)) {
                exist = true;
            }
        }
        if (!exist) {
            beacons.add(name);
        }

    }

    public void removeBeacons(String name) {
        boolean exist = false;
        for (int i = 0; i < beacons.size(); i++) {
            if (beacons.get(i).equals(name)) {
                exist = true;
            }
        }
        if (exist) {
            beacons.remove(name);
        }
    }
    public String checkInDatabase(String value){
        FirebaseDatabase databaseFire = FirebaseDatabase.getInstance();
        Log.i("Sample", "DB Call: value: " + value);
        DatabaseReference dbRef = databaseFire.getReference(value);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Sample", "dataSnapshot: " + dataSnapshot.getValue(String.class));
                message = dataSnapshot.getValue(String.class);
                Log.i("Sample", "message: " + message);
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return message;
    }

}
