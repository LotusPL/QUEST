package wsb.quest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ShowBeacons extends AppCompatActivity {
    private HashMap beaconMap = new HashMap();
    private ArrayList<String> beacons = new ArrayList<String>();
    private Database db = new Database();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_beacons);
        Intent intent = getIntent();
        beacons = (ArrayList<String>) intent.getSerializableExtra("beacon");
        for(int i = 0; i < beacons.size(); i++){
            //beaconMap.put(beacons.get(i),db.checkInDatabase(beacons.get(i)));
            beacons.set(i,beacons.get(i));
            Log.i("Sample", "ShowBeacons Activity ! " + beacons.get(i));
            Log.i("Sample", "Map size: " + beaconMap.size());
        }
        ListView beaconList = (ListView)findViewById(R.id.beaconListView);
        //TODO
        ArrayAdapter<String> beaconAdapter = new ArrayAdapter<String>(this,R.layout.row,beacons);
        beaconList.setAdapter(beaconAdapter);
        beaconList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ShowBeacons.this,"Działa",Toast.LENGTH_LONG);

            }
        });
    }
}
