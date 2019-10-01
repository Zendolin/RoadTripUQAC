package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SignupFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.signup_fragment_layout, container, false);

        android.widget.Button buttonSignup = (android.widget.Button)view.findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickSignUp(v);
            }
        });

        TextView txt = (TextView)view.findViewById(R.id.textGoBack);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickGoBack(v);
            }
        });

        return view;
    }


    public void OnClickGoBack(View v)
    {
        ((LoginActivity)getActivity()).changeFragment();
    }

    public void OnClickSignUp(View v)
    {
        ((LoginActivity)getActivity()).signup("newLogin","mdp");
    }

}
