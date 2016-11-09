package spit.postyourevent.Activites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.util.Date;
import java.util.HashMap;

import spit.postyourevent.Constants;
import spit.postyourevent.Database.EventData;
import spit.postyourevent.R;

/**
 * Created by Tejas on 06/10/2016.
 */

public class AddEvent extends AppCompatActivity {

    private Button postEventButton,pickerButton;
    private EditText nameEdittext,descriptionEdittext,venueEdittext;
    private TextView timeTextView;

    private DatabaseReference root;
    private SharedPreferences sharedPrefs;

    private Toolbar toolbar;
    private SwitchDateTimeDialogFragment dateTimeFragment;

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_layout);

        /*Instantiation*/
        postEventButton = (Button)findViewById(R.id.postEventButton);
        nameEdittext = (EditText)findViewById(R.id.eventName_editText);
        descriptionEdittext = (EditText)findViewById(R.id.eventDescription_editText);
        timeTextView = (TextView) findViewById(R.id.eventTime_textView);
        venueEdittext = (EditText)findViewById(R.id.eventVenue_editText);
        pickerButton = (Button)findViewById(R.id.datetime_picker_Button);

        sharedPrefs = getSharedPreferences("prefs",MODE_PRIVATE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Event");

        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(R.string.positive_button_datetime_picker),
                    getString(R.string.negative_button_datetime_picker)
            );
        }
        dateTimeFragment.setHour(0);
        dateTimeFragment.setMinute(0);

        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                timeTextView.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        postEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostEventButtonClicked();
            }
        });


        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.show(getSupportFragmentManager(),TAG_DATETIME_FRAGMENT);
            }
        });
    }

    private void onPostEventButtonClicked() {

        String name = nameEdittext.getText().toString();
        String description = descriptionEdittext.getText().toString();
        String eventTime = timeTextView.getText().toString();
        String venue = venueEdittext.getText().toString();

         /*Carry out validation
        * If all Okay then call to post event*/
        if(name.matches("") || description.matches("") || eventTime.matches("") || venue.matches("") ){
            Toast.makeText(getApplicationContext(),"Please Enter All information",Toast.LENGTH_SHORT).show();
        }
        else{

            String user_emailid = sharedPrefs.getString(Constants.USER_EMAIL,"");
            if(user_emailid.matches("")){
                Toast.makeText(getApplicationContext(),"Enter User information",Toast.LENGTH_SHORT).show();
            }
            else
                postThisEvent(new EventData(name,description,eventTime,venue,user_emailid));

        }

    }

    private void postThisEvent(EventData eventData) {

        HashMap<String,Object> eventHashmap = eventData.getHashMap();

        root = FirebaseDatabase.getInstance().getReference();

        String eventKey = root.child(Constants.EVENT).push().getKey();

        HashMap<String,Object> updateHashMap = new HashMap<>();

        updateHashMap.put("/"+Constants.EVENT+"/" + eventKey,eventHashmap);

        root.updateChildren(updateHashMap, new DatabaseReference.CompletionListener() {
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

        finish();

    }
}
