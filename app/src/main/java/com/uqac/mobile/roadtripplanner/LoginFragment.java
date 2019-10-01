package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);

        android.widget.Button buttonLogin = (android.widget.Button)view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin(v);
            }
        });

        TextView txt = (TextView)view.findViewById(R.id.textNewAccount);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNewAccount(v);
            }
        });

        return view;
    }

    public void onClickNewAccount(View v)
    {
        ((LoginActivity)getActivity()).changeFragment();
    }

    public void onClickLogin(View v)
    {
        ((LoginActivity)getActivity()).login("login","mdp");
    }
}
