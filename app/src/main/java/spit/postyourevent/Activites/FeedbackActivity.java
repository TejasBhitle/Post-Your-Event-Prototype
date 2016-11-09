package spit.postyourevent.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import spit.postyourevent.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar)findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void sendFeedback(View v){

        RadioGroup rg= (RadioGroup)findViewById(R.id.radioGroup);
        int selected= rg.getCheckedRadioButtonId();
        RadioButton selectedButton=(RadioButton)findViewById(selected);
        String pref=selectedButton.getText().toString();
        EditText feed=(EditText)findViewById(R.id.feed);
        String ans=feed.getText().toString();
        String[] to={"postyourevent.spit@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "Feedback: "+pref+"\n\n"+ans);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
