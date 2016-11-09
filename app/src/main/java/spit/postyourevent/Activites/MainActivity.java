package spit.postyourevent.Activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import spit.postyourevent.Constants;
import spit.postyourevent.CustomAdapter;
import spit.postyourevent.Database.EventData;
import spit.postyourevent.Login.Login;
import spit.postyourevent.Login.NewLogin;
import spit.postyourevent.R;
import spit.postyourevent.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private BroadcastReceiver broadcastReceiver;
    private FloatingActionButton fab;
    private View header;
    private Button header_Button;
    private TextView noItemTextView;
    private ArrayList<EventData> eventDataArrayList;

    private DatabaseReference root,eventRef;

    private  Snackbar no_connection_snackbar;

    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter Myadapter;
    private RecyclerView.LayoutManager MylayoutManager;

    private ValueEventListener valueEventListener;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar =(Toolbar)findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.mainCoordinatorLayout);
        fab = (FloatingActionButton)findViewById(R.id.main_fab);
        noItemTextView = (TextView)findViewById(R.id.noItemTextView);
        myrecyclerView = (RecyclerView)findViewById(R.id.main_recyclerView);
        no_connection_snackbar = Snackbar.make(coordinatorLayout,"No Connection",Snackbar.LENGTH_SHORT);
        noItemTextView = (TextView)findViewById(R.id.noItemTextView);



        root = FirebaseDatabase.getInstance().getReference();

        MylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager(MylayoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected(getBaseContext())){
                    Intent intent = new Intent(MainActivity.this, AddEvent.class);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(coordinatorLayout,"No Connection",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        header = navigationView.getHeaderView(0);
        header_Button = (Button)header.findViewById(R.id.header_signin);
        header_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewLogin.class);
                startActivity(intent);
            }
        });


        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_open,R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.myPostMenuItem:
                                if (isConnected(getBaseContext())){
                                    Intent intent = new Intent(MainActivity.this,MyPostsActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    no_connection_snackbar.show();
                                }

                                break;
                            case R.id.setUserInfo:
                                if(isConnected(getBaseContext())){
                                    Intent intent =  new Intent(MainActivity.this, SetUserInfo.class);
                                    startActivity(intent);
                                }
                                else
                                    no_connection_snackbar.show();

                                break;

                            case R.id.aboutMenuItem:
                                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(R.string.about_string)
                                        .setTitle("About us")
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //do nothing
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();

                                alertDialog.show();*/

                                Intent intent = new Intent(MainActivity.this,AboutPage.class);
                                startActivity(intent);
                                break;

                            case R.id.feedback:
                                Intent i = new Intent(MainActivity.this,FeedbackActivity.class);
                                startActivity(i);

                        }
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );

        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        if(swipeRefreshLayout!= null){
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //refreshData

                    if(isConnected(getApplicationContext())){
                        refreshData();
                    }
                    else{
                        no_connection_snackbar.show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }

        /*Initial*/
        if(isConnected(getBaseContext()))
            refreshData();
        else{
            no_connection_snackbar.show();
        }


    }

    private void updateUI(final ArrayList<EventData> eventDataArrayList) {

        Log.i("updateUI","Updating UI");
        swipeRefreshLayout.setRefreshing(false);
        Collections.reverse(eventDataArrayList);
        Myadapter = new CustomAdapter(getApplicationContext(),eventDataArrayList);
        myrecyclerView.setAdapter(Myadapter);
        myrecyclerView.scrollToPosition(0);

        if (eventDataArrayList.size() == 0)
            noItemTextView.setVisibility(View.VISIBLE);
        else
            noItemTextView.setVisibility(View.GONE);

        myrecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnClickItemInterface() {
                    @Override
                    public void onItemClick(View view, int position) {

                        try{
                            EventData ed = eventDataArrayList.get(position);
                            Intent i = new Intent();
                            i.setClass(MainActivity.this, EventDescription.class);
                            i.putExtra(Constants.EVENT_NAME, ed.getName());
                            i.putExtra(Constants.EVENT_VENUE,ed.getVenue());
                            i.putExtra(Constants.EVENT_TIME,ed.geteventTime());
                            i.putExtra(Constants.EVENT_DESCRIPTION,ed.getDescription());
                            i.putExtra(Constants.EVENT_USER,ed.getUserName());
                            //i.putExtra("mail_id",ed.getUserData().getEmail_id());
                            startActivity(i);
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(),"Data not loaded properly",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
        );

    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(isInitialStickyBroadcast()){}
                else{
                    if(isConnected(getBaseContext())){
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,R.string.connection_restored,Snackbar.LENGTH_LONG);
                        snackbar.setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshData();
                            }
                        });
                        snackbar.show();
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,R.string.connection_lost,Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        };
        registerReceiver(broadcastReceiver,intentFilter);
        super.onResume();




    }

    public void refreshData(){
        Log.i("refreshData","Refreshing Data.......................");
        swipeRefreshLayout.setRefreshing(true);
        FetchAllDataTask task = new FetchAllDataTask();
        task.execute();
    }


    private class FetchAllDataTask extends AsyncTask<String,Void,ArrayList<EventData>>{

        public FetchAllDataTask() {

        }

        @Override

        protected ArrayList<EventData> doInBackground(String... params) {
            Log.i("DoINBackground","Data begining");


            eventDataArrayList = new ArrayList<>();

            eventRef = root.child(Constants.EVENT);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventDataArrayList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String name = (String) snapshot.child(Constants.EVENT_NAME).getValue();
                        String descrip = (String) snapshot.child(Constants.EVENT_DESCRIPTION).getValue();
                        String time = (String) snapshot.child(Constants.EVENT_TIME).getValue();
                        String venue = (String) snapshot.child(Constants.EVENT_VENUE).getValue();
                        String user = (String) snapshot.child(Constants.EVENT_USER).getValue();

                        EventData eventData = new EventData(name,descrip,time,venue,user);
                        eventDataArrayList.add(eventData);
                    }
                    //Toast.makeText(getApplicationContext(),"Data Fetched",Toast.LENGTH_SHORT).show();
                    Log.i("DoINBackground","Data Fetched");
                    updateUI(eventDataArrayList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"onCancelled "+databaseError,Toast.LENGTH_SHORT).show();
                }
            };

            eventRef.addValueEventListener(valueEventListener);

            return eventDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<EventData> eventDatas) {
            super.onPostExecute(eventDatas);
        }
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.setUser_info_menuItem:
                if(isConnected(getBaseContext())){
                    Intent intent =  new Intent(MainActivity.this, SetUserInfo.class);
                    startActivity(intent);
                }
                else
                    no_connection_snackbar.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }*/

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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu,menu);
        return true;
    }*/

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
        //root.removeEventListener(valueEventListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

}

