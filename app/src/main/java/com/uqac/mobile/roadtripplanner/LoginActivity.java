package com.uqac.mobile.roadtripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebase;
    GoogleSignInClient clientGoogle;
    GoogleSignInAccount accountGoogle;

    ProgressBar progressBar;
    Toolbar toolbar;

    static String TAG = "----------RoadTrip Planner-------------";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //toolbar init
        toolbar = findViewById((R.id.toolbar));
        progressBar = findViewById((R.id.progressBar));
        progressBar.setVisibility(ProgressBar.GONE);


        //firebase init
        FirebaseApp.initializeApp(this);
        firebase = FirebaseAuth.getInstance();

        //google init
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        clientGoogle = GoogleSignIn.getClient(this, gso);
        accountGoogle = GoogleSignIn.getLastSignedInAccount(this);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickGoogleSignIn(v);
            }
        });

        //fragments init
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentHolder,new LoginFragment(),"LOGIN_FRAGMENT");
        transaction.commit();
    }



    public void changeFragment()
    {
        FragmentManager manager = getSupportFragmentManager();
        LoginFragment lf = (LoginFragment)manager.findFragmentByTag("LOGIN_FRAGMENT");
        NewAccountFragment suf = (NewAccountFragment)manager.findFragmentByTag("SIGNUP_FRAGMENT");
        if (lf != null && lf.isVisible()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentHolder,new NewAccountFragment(),"SIGNUP_FRAGMENT");
            transaction.commit();
        }
        else if (suf != null && suf.isVisible()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentHolder,new LoginFragment(),"LOGIN_FRAGMENT");
            transaction.commit();
        }
    }

    public void OnClickGoogleSignIn(View v)
    {
        Intent signInIntent = clientGoogle.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR Google Auth",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signup(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR Google:" + e.toString(),Toast.LENGTH_LONG);
            toast.show();
            Log.d("TAG","---------------"+e.toString());
        }
    }

    public void signup(String login , String password)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "login as "+login,Toast.LENGTH_LONG);
        toast.show();
        progressBar.setVisibility(View.VISIBLE);
        firebase.signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Login Complete!",Toast.LENGTH_LONG);
                    toast.show();
                    ChangeActivity();
                }
                else
                {
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast toast = Toast.makeText(getApplicationContext(), "Error Login",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void signup(GoogleSignInAccount account)
    {
        if(account == null) {

            Log.d(TAG,"account NULL");
            Toast toast = Toast.makeText(getApplicationContext(), "Account NULL",Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(),null);
            progressBar.setVisibility(View.VISIBLE);
            firebase.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        ChangeActivity();
                    }
                    else
                    {
                        Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                        Toast toast = Toast.makeText(getApplicationContext(), "Error Signup Google ",Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }

    }

    public void newAccount(String login , String password)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "signup as : "+login,Toast.LENGTH_LONG);
        toast.show();
        progressBar.setVisibility(View.VISIBLE);
        firebase.createUserWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    ChangeActivity();
                }
                else
                {
                    Log.e(TAG, "signInWithCustomToken:failure---------", task.getException());
                    Toast toast = Toast.makeText(getApplicationContext(), "Error Signup",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void ChangeActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
