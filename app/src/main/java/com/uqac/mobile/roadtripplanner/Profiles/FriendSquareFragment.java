package com.uqac.mobile.roadtripplanner.Profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.R;
import com.uqac.mobile.roadtripplanner.Utils.ProfileLoader;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FriendSquareFragment extends Fragment implements ProfileLoader {
    private static String TAG = "----------RoadTrip Planner-------------";
    private View v;
    private ImageView imageF;
    private TextView firstNameF;
    private TextView lastNameF;
    private TextView birthdateF;
    private Profile profileF;
    private boolean extend;
    private LinearLayout tripContainer;
    private Button buttonExtend;
    private Button buttonDelete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.friend_square_fragment_layout, container, false);
        v = view;
        imageF = v.findViewById(R.id.friend_imageViewProfile);
        firstNameF = v.findViewById(R.id.friend_textFirstName);
        lastNameF = v.findViewById(R.id.friend_textLastName);
        birthdateF = v.findViewById(R.id.friend_textBirthdate);
        tripContainer = (LinearLayout)v.findViewById(R.id.friend_TripsContainer);
        buttonExtend = v.findViewById(R.id.friend_buttonExtend);
        buttonExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"----------Click Extend");
                Extend();
            }
        });
        buttonDelete = v.findViewById(R.id.friend_buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"----------Click Delete");
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Removing Friend")
                        .setMessage("Are you sure you want to remove this friend?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteFriend();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        return view;
    }

    public void initFragment(ProfileRef ref)
    {
        profileF = new Profile(ref.uid);
        getProfilePicture();
        profileF.LoadProfile(this);
    }

    private void getProfilePicture() {
        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child("images/ProfilePicture_" + profileF.uid + ".jpg");
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap b  = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageF.setImageBitmap(b);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LoadProfileData(Profile p)
    {
        profileF = p;
        if (profileF.birthDate != null) {
            birthdateF.setText((profileF.birthDate));
        }
        if (profileF.firstName != null && profileF.lastName != null) {
            firstNameF.setText((profileF.firstName));
            lastNameF.setText((profileF.lastName));
        }
    }

    private void Extend()
    {
        extend = !extend;
        if(extend)
        {
            tripContainer.setVisibility(View.VISIBLE);
            int countTrips = 0;
            for(int i = 0 ; i< profileF.trips.size(); i ++)
            {
                if(!profileF.trips.get(i).isPrivate) countTrips+=1;
            }
            if(countTrips >0)
            {
                if(!isAdded()) return;
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if(!fragment.isAdded()) return;
                        getChildFragmentManager().beginTransaction().remove(fragment).commit();

                }
                for(int i = 0 ; i< profileF.trips.size();i++)
                {
                    if(!profileF.trips.get(i).isPrivate)
                    {
                        MyTripSquareFragment frag = new MyTripSquareFragment();
                        FragmentManager manager = getChildFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.add(R.id.friend_TripsContainer,frag,"MyTripSquare_FRAGMENT");
                        transaction.commit();
                        manager.executePendingTransactions();
                        frag.initMap(i,profileF);
                    }
                }
            }
            else
            {
                TextView txt = new TextView(getActivity());
                txt.setText("This user has not public trips yet !");
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txt.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                txt.setHeight(60);
                tripContainer.addView(txt);
            }
            Drawable d = getResources().getDrawable(android.R.drawable.arrow_up_float);
            buttonExtend.setBackground(d);
        }
        else
        {
            tripContainer.setVisibility(View.GONE);
            tripContainer.removeAllViews();
            Drawable d = getResources().getDrawable(android.R.drawable.arrow_down_float);
            buttonExtend.setBackground(d);
        }
    }
    private void DeleteFriend()
    {
        ((MainActivity)getActivity()).profile.RemoveFriend(profileF.uid);
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Friend removed.", Toast.LENGTH_SHORT);
        toast.show();
        FriendsListFragment FF = new FriendsListFragment();
        ((MainActivity)getActivity()).changeFragment(FF,"FriendsFragments");
    }
}
