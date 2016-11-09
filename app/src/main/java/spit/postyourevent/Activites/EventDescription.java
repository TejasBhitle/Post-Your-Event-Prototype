package spit.postyourevent.Activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import spit.postyourevent.Constants;
import spit.postyourevent.Database.UserData;
import spit.postyourevent.R;


public class EventDescription extends AppCompatActivity {

    TextView venue,description,time,username;
    Button findOnMap,moreInfoButton;

    String s_username;
    DatabaseReference userReference,root;
    ValueEventListener listener;

    UserData userdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        root = FirebaseDatabase.getInstance().getReference();

        Bundle event_data = getIntent().getExtras();


        setTitle(event_data.getString(Constants.EVENT_NAME));

        venue = (TextView)findViewById(R.id.event_venue);
        final String venueString =event_data.getString(Constants.EVENT_VENUE);
        venue.setText(venueString);

        time = (TextView)findViewById(R.id.event_time);
        time.setText(event_data.getString(Constants.EVENT_TIME));

        description = (TextView)findViewById(R.id.event_description);
        description.setText(event_data.getString(Constants.EVENT_DESCRIPTION));

        username = (TextView)findViewById(R.id.event_username);
        s_username = event_data.getString(Constants.EVENT_USER);
        username.setText(s_username);
        FetchUserDataTask task = new FetchUserDataTask();
        task.execute();


        findOnMap = (Button)findViewById(R.id.findOnMapButton);
        findOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri showmap = Uri.parse("geo:0,0?q="+Uri.encode(venueString));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,showmap);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        moreInfoButton = (Button)findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreInfo();
            }
        });

    }

    private void showMoreInfo() {
        if(userdata == null){
            Toast.makeText(getApplicationContext(),"UserData was not fetched",Toast.LENGTH_SHORT).show();
        }
        else{
            String message="Name: "+userdata.getName()+"\n"+
                            "Email: "+ userdata.getEmail_id()+"\n"+
                            "Contact: "+userdata.getContact_no()+"\n"+
                            "Year: " + userdata.getYear()+"\n"+
                            "Branch: " + userdata.getBranch()+"\n";

            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescription.this);
            builder.setTitle("UserInfo")
                    .setMessage(message)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private class FetchUserDataTask extends AsyncTask<String,Void,UserData>{
        @Override
        protected UserData doInBackground(String... params) {
            Log.i("DoINBackground","user Data beginning");

            try{
                userReference = root.child(Constants.USER).child(s_username);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = (String)dataSnapshot.child(Constants.USER_NAME).getValue();
                        String usercontact = (String)dataSnapshot.child(Constants.USER_CONTACT).getValue();
                        String useremail = (String)dataSnapshot.child(Constants.USER_EMAIL).getValue();
                        String userbranch = (String)dataSnapshot.child(Constants.USER_BRANCH).getValue();
                        String userrollno = (String)dataSnapshot.child(Constants.USER_ROLL_NO).getValue();
                        String useryear = (String)dataSnapshot.child(Constants.USER_YEAR).getValue();
                        userdata= new UserData(username,useremail,userbranch,useryear,usercontact,userrollno);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"User Not Found",Toast.LENGTH_SHORT).show();
            }

            return userdata;
        }

        @Override
        protected void onPostExecute(UserData userData) {
            super.onPostExecute(userData);
        }
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
