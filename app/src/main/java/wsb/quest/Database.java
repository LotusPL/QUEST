package wsb.quest;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Karol on 07.12.2018.
 */

public class Database {
    String message;





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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return message;
    }
}
