package spit.postyourevent.Activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import spit.postyourevent.Constants;
import spit.postyourevent.CustomAdapter;
import spit.postyourevent.Database.EventData;
import spit.postyourevent.R;
import spit.postyourevent.RecyclerItemClickListener;


public class MyPostsActivity extends AppCompatActivity {

    DatabaseReference root;
    SharedPreferences sharedPrefs;
    String username;


    private Toolbar toolbar;
    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter Myadapter;
    private RecyclerView.LayoutManager MylayoutManager;

    private ValueEventListener valueEventListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypost_event_layout);

        toolbar =(Toolbar)findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Posts");

        root = FirebaseDatabase.getInstance().getReference();

        sharedPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
        username = sharedPrefs.getString(Constants.USER_EMAIL,"").replace("@gmail.com","");
        myrecyclerView = (RecyclerView)findViewById(R.id.mypost_recyclerView);

        MylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager(MylayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mypost_refreshView);


        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshData

                if(isConnected(getApplicationContext())){
                    refreshData();
                }
                else{
                    Toast.makeText(getApplication(),"No Connection",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        /*Initial*/
        if(isConnected(getBaseContext()))
            refreshData();
        else{
            Toast.makeText(getApplication(),"No Connection",Toast.LENGTH_SHORT).show();
        }


    }

    public void refreshData(){
        Log.i("refreshData","Refreshing Data.......................");
        swipeRefreshLayout.setRefreshing(true);
        FetchUserPostDataTask task = new FetchUserPostDataTask();
        task.execute();
    }

    private void updateUI(final ArrayList<EventData> arrayList) {

        swipeRefreshLayout.setRefreshing(false);
        Collections.reverse(arrayList);
        Myadapter = new CustomAdapter(getApplicationContext(),arrayList);
        myrecyclerView.setAdapter(Myadapter);
        myrecyclerView.scrollToPosition(0);



        myrecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnClickItemInterface() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),"Edit Feature Yet to be implemented",Toast.LENGTH_SHORT).show();
                        /*EventData ed = arrayList.get(position);
                        Intent i = new Intent();
                        i.setClass(MyPostsActivity.this, MyPostDescription.class);
                        i.putExtra(Constants.EVENT_NAME, ed.getName());
                        i.putExtra(Constants.EVENT_VENUE,ed.getVenue());
                        i.putExtra(Constants.EVENT_TIME,ed.geteventTime());
                        i.putExtra(Constants.EVENT_DESCRIPTION,ed.getDescription());
                        i.putExtra(Constants.EVENT_USER,ed.getUserName());
                        startActivity(i);*/
                    }
                }));
    }

    public boolean isConnected(Context context){
        if(context == null){
            return true;
        }
        else{
            ConnectivityManager manager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return (networkInfo!=null && networkInfo.isConnected());
        }
    }

    private class FetchUserPostDataTask extends AsyncTask<Void,Void,ArrayList<EventData>> {

        @Override
        protected ArrayList<EventData> doInBackground(Void... params) {

            final ArrayList<EventData> userPostArrayList = new ArrayList<>();

            DatabaseReference eventRef = root.child(Constants.EVENT);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userPostArrayList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String name = (String) snapshot.child(Constants.EVENT_NAME).getValue();
                        String descrip = (String) snapshot.child(Constants.EVENT_DESCRIPTION).getValue();
                        String time = (String) snapshot.child(Constants.EVENT_TIME).getValue();
                        String venue = (String) snapshot.child(Constants.EVENT_VENUE).getValue();
                        String user = (String) snapshot.child(Constants.EVENT_USER).getValue();
                        String user_uid =(String) snapshot.child(Constants.USER_UID).getValue();

                        String this_user_id= sharedPrefs.getString(Constants.USER_UID,"");
                        if (this_user_id.matches(user_uid)){
                            EventData eventData = new EventData(name,descrip,time,venue,user,user_uid);
                            userPostArrayList.add(eventData);
                        }


                        updateUI(userPostArrayList);
                        //Toast.makeText(getApplicationContext(),"Success in Fetching UserPost Data " +user,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            eventRef.addValueEventListener(valueEventListener);
            return userPostArrayList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<EventData> arrayList) {
            super.onPostExecute(arrayList);
            if (arrayList == null){
                return;
            }
            //updateUI(arrayList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //root.removeEventListener(valueEventListener);

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
