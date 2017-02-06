package spit.postyourevent.Activites;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import spit.postyourevent.Constants;
import spit.postyourevent.Database.UserData;
import spit.postyourevent.R;


public class SetUserInfo extends AppCompatActivity {

    EditText nameEditText,emailId_EditText,contact_EditText,branch_EditText,year_EditText,rollNo_EditText;
    Button saveButton;
    String name,emailid,contactno,rollno,branch,year;
    Spinner year_Spinner, branch_Spinner;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Toolbar toolbar;

    private DatabaseReference root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user_layout);

        toolbar =(Toolbar)findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("User Info");

        /*instantiation*/
        nameEditText = (EditText)findViewById(R.id.setUser_name);
        emailId_EditText = (EditText)findViewById(R.id.setUser_emailid);
        contact_EditText = (EditText)findViewById(R.id.setUser_contact);
        //branch_EditText = (EditText)findViewById(R.id.setUser_branch);
        //year_EditText = (EditText)findViewById(R.id.setUser_year);
        year_Spinner =(Spinner)findViewById(R.id.inputYear);
        branch_Spinner =(Spinner)findViewById(R.id.inputBranch);
        rollNo_EditText =(EditText)findViewById(R.id.setUser_rollno);
        saveButton = (Button)findViewById(R.id.setUser_saveButton);

        year_Spinner.setSelection(0);
        /*Shared Prefs*/
        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);

        if(sharedPreferences != null){
            nameEditText.setText(sharedPreferences.getString(Constants.USER_NAME,""));
            emailId_EditText.setText(sharedPreferences.getString(Constants.USER_EMAIL,""));
            contact_EditText.setText(sharedPreferences.getString(Constants.USER_CONTACT,""));
            year_Spinner.setSelection(getIndexOfYear(sharedPreferences.getString(Constants.USER_YEAR,"")));
            branch_Spinner.setSelection(getIndexOfBranch(sharedPreferences.getString(Constants.USER_BRANCH,"")));
            rollNo_EditText.setText(sharedPreferences.getString(Constants.USER_ROLL_NO,""));
        }
        else{
            /*Set editable*/
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }

    private int getIndexOfYear(String string){
        switch (string){
            case "FE":return 1;
            case "SE":return 2;
            case "TE":return 3;
            case "BE":return 4;
        }
        return 0;
    }

    private int getIndexOfBranch(String string){
        switch (string){
            case "COMPS":return 1;
            case "IT":return 2;
            case "EXTC":return 3;
            case "ETRX":return 4;
        }
        return 0;
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save ?")
                .setMessage("Confirm to Save")
                .setPositiveButton("Yes, Do It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveButtonPressed();
                    }
                })
                .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dp nothing
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void onSaveButtonPressed() {
        name=nameEditText.getText().toString();
        emailid =emailId_EditText.getText().toString();
        contactno = contact_EditText.getText().toString();
        branch = branch_Spinner.getSelectedItem().toString();
        year = year_Spinner.getSelectedItem().toString();
        rollno = rollNo_EditText.getText().toString();

        if(name.equals("") || emailid.equals("") || contactno.equals("") || rollno.equals("") ||
                year.equals("Choose Year") || branch.equals("Choose Branch")){
            Toast.makeText(getApplicationContext(),"Please Enter All Details",Toast.LENGTH_SHORT).show();

        }
        else {
            editor = sharedPreferences.edit();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String user_uid;
            if(user!= null) {
                user_uid= user.getUid();
                Log.e("SetUserInfo","user uid added to shared prefs");

                editor.putString(Constants.USER_NAME, name);
                editor.putString(Constants.USER_EMAIL, emailid);
            editor.putString(Constants.USER_CONTACT, contactno);
            editor.putString(Constants.USER_BRANCH, branch);
            editor.putString(Constants.USER_YEAR, year);
            editor.putString(Constants.USER_ROLL_NO, rollno);
            editor.putString(Constants.USER_UID,user_uid);
            /*Also Add the rest props*/

            editor.apply();
            uploadUserToFireBase();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Sign in First ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadUserToFireBase(){
        UserData userData = new UserData(name,emailid,branch,year,contactno,rollno);

        HashMap<String,Object> usermap = userData.getHashMap();

        root = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userId;
        if (user!= null){ userId = user.getUid();
            DatabaseReference userRef = root.child(Constants.USER);
            userRef.child(userId).setValue(usermap);
        }
        else Toast.makeText(getApplication(),"Please Sign in first",Toast.LENGTH_SHORT).show();

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
