package spit.postyourevent.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import spit.postyourevent.R;

/**
 * Created by DELL on 15/10/2016.
 */

public class NewLogin  extends AppCompatActivity{

    private static final String TAG="Login Activity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    Button signup, signin;
    EditText  password, email;
    private static final int RC_SIGN_IN = 9001;

    SignInButton googleButton;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login_activity);

        mAuth = FirebaseAuth.getInstance();

        signup = (Button) findViewById(R.id.bsignup);
        signin = (Button) findViewById(R.id.bsignin);
        email=(EditText)findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        googleButton = (SignInButton)findViewById(R.id.googleButton);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, SignUp.class);
                //startActivity(intent);
                signup(email.getText().toString(),password.getText().toString());
            }


        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final boolean value1=check_validation();
                final boolean value2 = validate();
                if (value2) {
                    //Toast.makeText(MainActivity.this, "LOGGED IN", Toast.LENGTH_SHORT).show();
                    login(email.getText().toString(),password.getText().toString());
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        /*Google SignUp*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,
                        new GoogleApiClient.OnConnectionFailedListener(){
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Toast.makeText(getApplicationContext(),"onConnectionFailed "+ connectionResult,Toast.LENGTH_LONG).show();
                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Yet To Code",Toast.LENGTH_SHORT).show();
                //googleSignIn();
            }
        });

    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                Toast.makeText(getApplicationContext(),"result.isSuccess",Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Toast.makeText(getApplicationContext(),"Google SignIn Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(),"SignInWithCredential Complete "+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                        /*if(!task.isSuccessful()){

                        }*/
                    }
                });
    }


    private void signup(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Auth Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(),"signInWithEmail:onComplete:" + task.isSuccessful(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()){
                            Log.w(TAG,"signInWithEmail:failed",task.getException());
                            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public boolean validate() {
        boolean valid = true;
        boolean ret3 = true;
        boolean ret4 = true;
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        ret3 = Validation.isText(email);
        ret4 = Validation.isText(password);
        if (email1.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            password.setError(null);
        }

        if (password1.isEmpty() || password1.length() <4 || password1.length() >10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (!ret3) {
            email.requestFocus();
            ret3 = false;
        }
        if (!ret4) {
            password.requestFocus();
            ret4 = false;
        }
        return valid;
    }

}
