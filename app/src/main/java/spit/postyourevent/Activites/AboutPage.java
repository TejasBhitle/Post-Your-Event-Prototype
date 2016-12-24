package spit.postyourevent.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import spit.postyourevent.R;

/**
 * Created by DELL on 19/10/2016.
 */

public class AboutPage extends AppCompatActivity {

    private Toolbar toolbar;
    Button github;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_about_screen);
        setTitle("About");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        github =(Button)findViewById(R.id.github);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Github();
            }
        });
    }
    public void Github(){
        Uri uri = Uri.parse(getResources().getString(R.string.github));
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }
}
