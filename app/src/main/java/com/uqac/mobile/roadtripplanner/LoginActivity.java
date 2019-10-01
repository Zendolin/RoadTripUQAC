package com.uqac.mobile.roadtripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

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
        firebase.signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Login Complete!",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void signup(String login , String password)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "signup;"+login,Toast.LENGTH_LONG);
        toast.show();
        firebase.signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "SignUp Complete!",Toast.LENGTH_LONG);
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
