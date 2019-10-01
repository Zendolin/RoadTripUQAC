package com.uqac.mobile.roadtripplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebase;
    ProgressBar progressBar;
    Toolbar toolbar;
    static String TAG = "RoadTrip Planner";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        toolbar = findViewById((R.id.toolbar));
        progressBar = findViewById((R.id.progressBar));
        progressBar.setVisibility(ProgressBar.GONE);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);


        FirebaseApp.initializeApp(this);

        firebase = FirebaseAuth.getInstance();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragmentHolder,new LoginFragment(),"LOGIN_FRAGMENT");
        transaction.commit();
    }

    public void changeFragment()
    {
        FragmentManager manager = getSupportFragmentManager();
        LoginFragment lf = (LoginFragment)manager.findFragmentByTag("LOGIN_FRAGMENT");
        SignupFragment suf = (SignupFragment)manager.findFragmentByTag("SIGNUP_FRAGMENT");
        if (lf != null && lf.isVisible()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentHolder,new SignupFragment(),"SIGNUP_FRAGMENT");
            transaction.commit();
        }
        else if (suf != null && suf.isVisible()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentHolder,new LoginFragment(),"LOGIN_FRAGMENT");
            transaction.commit();
        }
    }

    public void login(String login , String password)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "login;"+login,Toast.LENGTH_LONG);
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

    public void signup(String login , String password)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "signup;"+login,Toast.LENGTH_LONG);
        toast.show();
        progressBar.setVisibility(View.VISIBLE);
        firebase.signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "SignUp Complete!",Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast toast = Toast.makeText(getApplicationContext(), "Error Signup",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void SkipLogin(View v)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
