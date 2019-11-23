package com.uqac.mobile.roadtripplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uqac.mobile.roadtripplanner.Utils.ProfileLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ProfileFragment extends Fragment implements ProfileLoader {

    private static String TAG = "----------RoadTrip Planner-------------";
    private static final int REQUEST_CODE = 1;
    private File localFile = null;

    private View view;
    private MainActivity mainActivity;
    private Profile profile;
    private ImageView image;
    private TextView textEmail;
    private TextView textFirstName;
    private TextView textLastname;
    private TextView textBirthDate;
    private Bitmap bitmap;

    private ConstraintLayout layoutProfileInfo;
    private ConstraintLayout layoutProfileEdit;
    private ImageView imageProfileCheck;
    private ImageView imageProfileEdit;
    private EditText editProfileFirstname;
    private EditText editProfileLastname;
    private EditText editProfileBirthdate;

    private ArrayList<MyTrip> listTrips = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        TextView txtLogout = view.findViewById(R.id.textLogout);

        image = view.findViewById(R.id.profile_imageViewProfile);
        textEmail = view.findViewById(R.id.profile_textEmail);
        textBirthDate = view.findViewById(R.id.profile_textDate);
        textFirstName = view.findViewById(R.id.profile_textFirstName);
        textLastname = view.findViewById(R.id.profile_textLastName);

        layoutProfileInfo = view.findViewById(R.id.LayoutProfile);
        layoutProfileEdit = view.findViewById(R.id.LayoutEditProfile);
        imageProfileEdit = view.findViewById(R.id.imageViewEditProfile);
        imageProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileEditLayout(true);
            }
        });
        imageProfileCheck = view.findViewById(R.id.imageViewProfileCheck);
        imageProfileCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileEditLayout(false);
            }
        });
        editProfileBirthdate = view.findViewById(R.id.profile_editBrithdate);
        editProfileLastname = view.findViewById(R.id.profile_editLastname);
        editProfileFirstname = view.findViewById(R.id.profile_editFirstname);


        FirebaseUser userFireBase = FirebaseAuth.getInstance().getCurrentUser();

        profile = new Profile(userFireBase);
        checkNewUser();
        getProfileData();
        getProfilePicture();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mainActivity = (MainActivity)getActivity();
        return view;
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                InputStream stream = getActivity().getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                profile.uploadProfilePicture(this, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getProfileData() {
        profile.LoadProfile(this);
    }

    private void getProfilePicture() {
        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child("images/ProfilePicture_" + profile.uid + ".jpg");
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    profile.profilePicture = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    image.setImageBitmap(profile.profilePicture);
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

    private void showProfileEditLayout(boolean b) {
        if (b) {
            if (!TextUtils.isEmpty(profile.birthDate) && !profile.birthDate.equals("birthdate not set"))
                editProfileBirthdate.setText(profile.birthDate);
            if (!TextUtils.isEmpty(profile.firstName))
                editProfileFirstname.setText(profile.firstName);
            if (!TextUtils.isEmpty(profile.lastName)) editProfileLastname.setText(profile.lastName);
            layoutProfileInfo.setVisibility(View.GONE);
            layoutProfileEdit.setVisibility(View.VISIBLE);
        } else {
            if (editProfileBirthdate.getText() != null) {
                profile.birthDate = editProfileBirthdate.getText().toString();
                textBirthDate.setText(profile.birthDate);
            }
            if (editProfileFirstname.getText() != null) {
                profile.firstName = editProfileFirstname.getText().toString();
                textFirstName.setText(profile.firstName);
            }
            if (editProfileLastname.getText() != null) {
                profile.lastName = editProfileLastname.getText().toString();
                textLastname.setText(profile.lastName);
            }
            profile.SaveProfile();
            layoutProfileInfo.setVisibility(View.VISIBLE);
            layoutProfileEdit.setVisibility(View.GONE);
        }
    }

    public void updateNewImage(Bitmap b) {
        image.setImageBitmap(b);
    }

    private void checkNewUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(profile.uid)) {
                    final DatabaseReference referenceUID = reference.child(profile.uid);
                    Log.d(TAG, "-----creating new UID-----");
                    Map<String, Object> userData = new HashMap<String, Object>();

                    String fullName = profile.user.getDisplayName();
                    if (fullName != null) {
                        Log.d(TAG, "-----" + fullName);
                        String[] parts = fullName.split(" ");
                        profile.firstName = parts[0];
                        profile.lastName = parts[1];
                    }
                    userData.put("email", profile.email);
                    userData.put("firstname", profile.firstName);
                    userData.put("lastname", profile.lastName);
                    userData.put("birthdate", "");
                    referenceUID.updateChildren(userData);
                    ((MainActivity) getActivity()).profile = profile;
                } else {
                    Log.d(TAG, "-----UID exist-----");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "-----" + databaseError.getMessage() + "----");
            }
        });
    }

    private void listMyTrips() {

        if(!isAdded()) return;
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if(!fragment.isAdded()) return;
                if (fragment != null) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        for(MyTrip t : profile.trips)
        {
            MyTripSquareFragment frag = new MyTripSquareFragment();
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.myTripSquareFragment_holder,frag,"MyTripSquare_FRAGMENT");
            transaction.commit();
            manager.executePendingTransactions();

            frag.initMap(t);
        }
    }

    private void logout() {
        ((MainActivity) getActivity()).exit();
    }

    @Override
    public void LoadProfileData(Profile p) {
        if (profile.profilePicture != null) {
            image.setImageBitmap(profile.profilePicture);
        }
        if (profile.email != null) {
            textEmail.setText(profile.email);
        }
        if (profile.birthDate != null) {
            textBirthDate.setText((profile.birthDate));
        }
        if (profile.firstName != null && profile.lastName != null) {
            textFirstName.setText((profile.firstName));
            textLastname.setText((profile.lastName));
        } else {
            String fullName = profile.user.getDisplayName();
            if (fullName != null) {
                fullName = fullName.replace("\n", " ");
                String[] parts = fullName.split("");
                profile.firstName = parts[0];
                profile.lastName = parts[1];
                textFirstName.setText(parts[0]);
                textLastname.setText(parts[1]);
            }
        }
        if(profile != null)
        {
            mainActivity.profile = profile;
            listMyTrips();
        }
    }
}
