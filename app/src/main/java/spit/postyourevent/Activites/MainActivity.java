package spit.postyourevent.Activites;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import spit.postyourevent.Constants;
import spit.postyourevent.CustomAdapter;
import spit.postyourevent.Database.EventData;
import spit.postyourevent.Login.Login;
import spit.postyourevent.Login.NewLogin;
import spit.postyourevent.Login.SigninActivity;
import spit.postyourevent.R;
import spit.postyourevent.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private BroadcastReceiver broadcastReceiver;
    private FloatingActionButton fab;
    private View header,No_net_layout;
    //private Button header_Button;
    private TextView noItemTextView,header_text1,header_text2;
    private ArrayList<EventData> eventDataArrayList;
    private DatabaseReference root,eventRef;
    private Snackbar no_connection_snackbar;

    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter Myadapter;
    private RecyclerView.LayoutManager MylayoutManager;

    private ValueEventListener valueEventListener;

    private CircleImageView profile_pic;

    private final int RC_SIGN_IN =123;
    FirebaseAuth auth;
    final String LOG ="MAINACTIVITY";

    private static final String TAG = "signin1";

    SharedPreferences prefs;


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
        No_net_layout =(View)findViewById(R.id.no_net_layout) ;

        root = FirebaseDatabase.getInstance().getReference();


        prefs = getSharedPreferences("prefs",MODE_PRIVATE);


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
        profile_pic = (CircleImageView)header.findViewById(R.id.profile_pic);
        header_text1 =(TextView)header.findViewById(R.id.headerText1);
        header_text2 =(TextView)header.findViewById(R.id.headerText2);

        //String pic_uri = prefs.getString(Constants.PROFILE_PIC_URI,"");
       /* try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(pic_uri));
            profile_pic.setImageBitmap(bitmap);
        }
        catch (Exception e){}*/
        header_text1.setText(prefs.getString(Constants.USER_NAME,""));
        try {
            String URL = prefs.getString(Constants.PROFILE_PIC_URI, "");
            Log.e("PROFILE_IMAGE",URL);
            Bitmap bitmap = getBitmapFromURL(URL);
            profile_pic.setImageBitmap(bitmap);
        }
        catch (Exception e){}




        header_text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSignOutDialog();
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
                                Intent intent = new Intent(MainActivity.this,AboutPage.class);
                                startActivity(intent);
                                break;
                            case R.id.feedback:
                                Intent intent1 = new Intent(MainActivity.this,FeedbackActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.developerMenuItem:
                                startActivity(new Intent(MainActivity.this,DeveloperActivity.class));
                                break;
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

        //checkUserSignedIn();

        /*Initial*/
        if(isConnected(getBaseContext()))
            refreshData();
        else{
            no_connection_snackbar.show();
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    private void createSignOutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(prefs.getString(Constants.USER_NAME,""))
                .setNeutralButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //FirebaseAuth.getInstance().signOut();
                        //Plus.AccountApi.clearDefaultAccount(SigninActivity.mGoogleApiClient);
                        //SigninActivity.mGoogleApiClient.disconnect();
                        //SigninActivity.mGoogleApiClient.connect();
                        //startActivity(new Intent(MainActivity.this,SigninActivity.class));
                        Toast.makeText(getApplicationContext(),"Yet to be implemented",Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void checkUserSignedIn(){
        //to check if the user is signed in
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

        }
        else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.AppTheme)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),RC_SIGN_IN);
        }
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                /*GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount acct = result.getSignInAccount();
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                header_text1.setText(personName);*/
                /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null){
                    String name = user.getDisplayName();
                    Uri pic_uri = user.getPhotoUrl();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pic_uri);
                        profile_pic.setImageBitmap(bitmap);
                        header_text1.setText(name);

                    }
                    catch (IOException e){}
                }
                startActivity(new Intent(MainActivity.this,SetUserInfo.class));

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //  showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //      showSnackbar(R.string.unknown_error);
                    return;
                }
            }
            //showSnackbar(R.string.unknown_sign_in_response);
        }
    }*/


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
                        String user_uid =(String) snapshot.child(Constants.USER_UID).getValue();

                        EventData eventData = new EventData(name,descrip,time,venue,user,user_uid);
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


    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

