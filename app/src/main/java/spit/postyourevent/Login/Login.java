package spit.postyourevent.Login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import spit.postyourevent.R;

/**
 * Created by DELL on 09/10/2016.
 */

public class Login extends AppCompatActivity {

    private static final String TAG="Login Activity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText emailEditText,passwordEditText;
    private Button signupButton,loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText)findViewById(R.id.email_EditText);
        passwordEditText = (EditText)findViewById(R.id.password_EditText);
        signupButton = (Button)findViewById(R.id.sign_up_button);
        loginButton = (Button)findViewById(R.id.login_in_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(emailEditText.getText().toString(),passwordEditText.getText().toString());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailEditText.getText().toString(),passwordEditText.getText().toString());
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}











