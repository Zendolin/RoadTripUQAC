package com.uqac.mobile.roadtripplanner.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uqac.mobile.roadtripplanner.R;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment{

    EditText loginField;
    EditText passwordField;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);

        android.widget.Button buttonSignUp = view.findViewById(R.id.buttonLogin);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp(v);
            }
        });

        TextView txt = (TextView)view.findViewById(R.id.textNewAccount);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNewAccount(v);
            }
        });

        loginField = (EditText)view.findViewById(R.id.editTextEmailLogin);
        passwordField = (EditText)view.findViewById(R.id.editTextPasswordLogin);
        return view;
    }

    public void onClickNewAccount(View v)
    {
        ((LoginActivity)getActivity()).changeFragment();
    }

    public void onClickSignUp(View v)
    {
        if(loginField.getText() != null && passwordField.getText() != null)
        {
            ((LoginActivity)getActivity()).signup(loginField.getText().toString(),passwordField.getText().toString());

        }
        else
        {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Error , please fill the inputs !",Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
