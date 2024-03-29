package com.uqac.mobile.roadtripplanner.Profiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.Profiles.ProfileRef;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchUserFragment extends DialogFragment {

    Button btn;
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    ArrayList<ProfileRef> users = new ArrayList<>() ;
    ArrayList<String> userNames = new ArrayList<>() ;
    FriendsListFragment fragList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.searchuser_dialogfragment, null);

        getDialog().setTitle("Search User");

        lv= rootView.findViewById(R.id.searchUser_listView);
        sv=  rootView.findViewById(R.id.searchUser_searchView);
        btn= rootView.findViewById(R.id.searchUser_dismiss);

        adapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,userNames);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                ((MainActivity)getActivity()).profile.friends.add(users.get(position));
                Toast.makeText(getApplicationContext(), ((TextView) view).getText() + " added to you friends !",Toast.LENGTH_SHORT).show();
                userNames.remove(userNames.get(position));
                users.remove(users.get(position));
                adapter.notifyDataSetChanged();
                fragList.listFragments();
            }
        });
        //SEARCH
        sv.setQueryHint("Search..");
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String txt) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String txt) {
                adapter.getFilter().filter(txt);
                return false;
            }
        });
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
                ((MainActivity)getActivity()).profile.SaveProfile();
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