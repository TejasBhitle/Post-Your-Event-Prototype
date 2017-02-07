package spit.postyourevent.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import spit.postyourevent.R;

/**
 * Created by DELL on 07/02/2017.
 */

public class DeveloperActivity extends AppCompatActivity {


    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void Bhitle(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/tejas-bhitle-ab6597126?authType=NAME_SEARCH&authToken=IlL2&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CentityType%3AentityHistoryName%2CclickedEntityId%3Amynetwork_521005674%2Cidx%3A0"));
        startActivity(browserIntent);
    }

    public void Bhave(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/aditya-bhave-235765128?authType=NAME_SEARCH&authToken=wbck&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A527408393%2CauthType%3ANAME_SEARCH%2Cidx%3A1-1-1%2CtarId%3A1477592122403%2Ctas%3AA"));
        startActivity(browserIntent);
    }
    public void Desai(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/aditya-desai?authType=NAME_SEARCH&authToken=BnFO&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A521201294%2CauthType%3ANAME_SEARCH%2Cidx%3A1-5-5%2CtarId%3A1477591788459%2Ctas%3AA"));
        startActivity(browserIntent);
    }
    public void Rutvij(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/rutvij-mehta-778b65126?authType=NAME_SEARCH&authToken=o17C&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A522432524%2CauthType%3ANAME_SEARCH%2Cidx%3A1-2-2%2CtarId%3A1477591934336%2Ctas%3AR"));
        startActivity(browserIntent);
    }
    public void Shlok(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/shlok-gujar-a30101128?authType=NAME_SEARCH&authToken=mphh&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A525785220%2CauthType%3ANAME_SEARCH%2Cidx%3A1-2-2%2CtarId%3A1477591971259%2Ctas%3AS"));
        startActivity(browserIntent);
    }
    public void Sushmen(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/sushmen-chaudhari-a2a962130?authType=NAME_SEARCH&authToken=9ubE&locale=en_US&trk=tyah&trkInfo=clickedVertical%3Amynetwork%2CclickedEntityId%3A539845954%2CauthType%3ANAME_SEARCH%2Cidx%3A1-1-1%2CtarId%3A1477642653614%2Ctas%3ASushmen%20"));
        startActivity(browserIntent);
        //Toast.makeText(getApplicationContext(),"Not on LinkedIn Yet",Toast.LENGTH_SHORT).show();
    }
}
