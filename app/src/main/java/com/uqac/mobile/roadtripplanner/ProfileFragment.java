package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);
        TextView txtLogout = view.findViewById(R.id.textLogout);

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
        return view;
    }


    private void Logout()
    {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "LOG OUT",Toast.LENGTH_LONG);
        toast.show();
    }
}
