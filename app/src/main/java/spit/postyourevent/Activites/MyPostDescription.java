package spit.postyourevent.Activites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import spit.postyourevent.Constants;
import spit.postyourevent.Database.EventData;
import spit.postyourevent.R;


public class MyPostDescription extends AppCompatActivity {

    EditText venue,description,time;
    Button saveButton;

    private DatabaseReference root;
    private SharedPreferences sharedPrefs;
    String key,eventkey,username;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypost_description_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        sharedPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
        Bundle event_data = getIntent().getExtras();

        setTitle(event_data.getString(Constants.EVENT_NAME));

        venue = (EditText)findViewById(R.id.event_venue);
        venue.setText(event_data.getString(Constants.EVENT_VENUE));

        time = (EditText)findViewById(R.id.event_time);
        time.setText(event_data.getString(Constants.EVENT_TIME));

        description = (EditText)findViewById(R.id.event_description);
        description.setText(event_data.getString(Constants.EVENT_DESCRIPTION));

        username =event_data.getString(Constants.EVENT_USER);

        saveButton = (Button)findViewById(R.id.mypost_description_saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Yet To Code",Toast.LENGTH_SHORT).show();
                //onPostButtonClicked();
            }
        });
    }

    private void onPostButtonClicked() {
        String s_name = username;
        String s_description = description.getText().toString();
        String s_eventTime = time.getText().toString();
        String s_venue = venue.getText().toString();

        if(s_name.matches("") || s_description.matches("") || s_eventTime.matches("") || s_venue.matches("") ){
            Toast.makeText(getApplicationContext(),"Please Enter All information",Toast.LENGTH_SHORT).show();
        }
        else{

            String user_emailid = sharedPrefs.getString(Constants.USER_EMAIL,"");
            if(user_emailid.matches("")){
                Toast.makeText(getApplicationContext(),"Enter User information",Toast.LENGTH_SHORT).show();
            }
            else{
                try{
                    postThisEvent(new EventData(s_name,s_description,s_eventTime,s_venue,user_emailid));
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

        }

    }

    private void postThisEvent(final EventData eventData) {
        try {
            //Toast.makeText(getApplicationContext(),"Yet to Code",Toast.LENGTH_SHORT).show();
            HashMap<String,Object> eventHashmap = eventData.getHashMap();

            root = FirebaseDatabase.getInstance().getReference();

            //String eventKey = root.child(Constants.EVENT).push().getKey();

            HashMap<String,Object> updateHashMap = new HashMap<>();


            key = getUpdateKey(root,eventData);
            if(key != null){

                updateHashMap.put("/"+Constants.EVENT+"/" + key,eventHashmap);
                root.setValue( updateHashMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError!= null){
                            Toast.makeText(getApplicationContext(),"Error: "+databaseError,Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (Exception e){e.printStackTrace();}



        //finish();
    }

    private String getUpdateKey(DatabaseReference reference,final EventData eventData){
        try{
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String name = (String) snapshot.child(Constants.EVENT_NAME).getValue();
                        String user = (String) snapshot.child(Constants.EVENT_USER).getValue();

                        if(name.matches(eventData.getName())&& user.matches(eventData.getUserName())){
                            eventkey = snapshot.getKey();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return eventkey;

        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
