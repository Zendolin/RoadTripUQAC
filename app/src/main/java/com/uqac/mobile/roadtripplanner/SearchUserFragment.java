package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;

public class SearchUserFragment extends DialogFragment {

    Button btn;
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    ArrayList<ProfileRef> users = new ArrayList<>() ;
    ArrayList<String> userNames = new ArrayList<>() ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.searchuser_dialogfragment, null);

        getDialog().setTitle("Search User");

        lv= rootView.findViewById(R.id.searchUser_listView);
        sv=  rootView.findViewById(R.id.searchUser_searchView);
        btn= rootView.findViewById(R.id.searchUser_dismiss);

        adapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,userNames);
        lv.setAdapter(adapter);

        //SEARCH
        sv.setQueryHint("Search..");
        sv.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String txt) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(txt);
                return false;
            }
        });

        //BUTTON
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        return rootView;
    }
    public void SetUsers(ArrayList<ProfileRef> list)
    {
        this.users = list;
        userNames = new ArrayList<>();
        for(ProfileRef p : list)
        {
            String fullName =  p.firstName + " " + p.lastName;
            userNames.add(fullName);
        }
    }
}