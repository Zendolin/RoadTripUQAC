package com.uqac.mobile.roadtripplanner.Profiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FriendsListFragment extends Fragment {

    private static String TAG = "----------RoadTrip Planner-------------";
    private Button btn;
    private Profile profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.friend_fragment_layout, container, false);
        btn = view.findViewById(R.id.friends_btnSearch);
        final FragmentManager fm=getFragmentManager();
        final SearchUserFragment searchFrag =  new SearchUserFragment();
        searchFrag.fragList = this;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchFrag.show(fm, "Users");
            }
        });
        profile = ((MainActivity)getActivity()).profile;

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data)
            {
                ArrayList<ProfileRef> listProfiles = new ArrayList<>();
                HashMap hm = (HashMap)data.getValue();
                for (Object key : hm.keySet())
                {
                    if(!key.toString().equals(profile.uid))
                    {
                        boolean notFriend = true;
                        for(ProfileRef f : profile.friends)
                        {
                            if(f.uid.equals(key.toString()))
                            {
                                notFriend = false;
                                break;
                            }
                        }
                        if(notFriend)
                        {
                            String uid = key.toString();
                            HashMap profileHM = (HashMap)hm.get(key);
                            Log.d(TAG,"");
                            if(profileHM.get("firstname") != null && profileHM.get("lastname") != null)
                            {
                                String firstName = profileHM.get("firstname").toString();
                                String lastName = profileHM.get("lastname").toString();
                                ProfileRef pr = new ProfileRef(uid,lastName,firstName);
                                listProfiles.add(pr);
                            }

                        }
                    }
                }
                searchFrag.SetUsers(listProfiles);
                Log.d(TAG, "---------");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "-----" + databaseError.getMessage() + "----");
            }
        });

        listFragments();
        return view;
    }
    public void listFragments()
    {
        for(ProfileRef pr : profile.friends)
        {
            FriendSquareFragment frag = new FriendSquareFragment();
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.friends_container,frag,"FriendSquareFragment_FRAGMENT");
            transaction.commit();
            manager.executePendingTransactions();
            frag.initFragment(pr);
        }
    }

}
