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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static String TAG = "----------RoadTrip Planner-------------";
    private static final int REQUEST_CODE = 1;

    private View view;
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


    private File localFile = null;

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
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        profile = new Profile(storageRef,userFireBase);
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
        return view;
    }

    private void getImageFromGallery()
    {
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
                profile.uploadProfilePicture(this,bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getProfileData()
    {
        Log.d(TAG,"-----fetching user informations of :  "+this.profile.uid+"   ------");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(this.profile.uid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                         Log.d(TAG,"-----data found !------");
                        if(dataSnapshot.child("email").getValue() != null) profile.email = dataSnapshot.child("email").getValue().toString();
                        if(dataSnapshot.child("lastname").getValue() != null)profile.lastName =dataSnapshot.child("lastname").getValue().toString();
                        if(dataSnapshot.child("firstname").getValue() != null) profile.firstName =dataSnapshot.child("firstname").getValue().toString();
                        if(dataSnapshot.child("birthdate").getValue() != null)profile.birthDate =dataSnapshot.child("birthdate").getValue().toString();

                        if(profile.profilePicture != null)
                        {
                            image.setImageBitmap(profile.profilePicture);
                        }
                        if(profile.email != null)
                        {
                            textEmail.setText(profile.email);
                        }
                        if(profile.birthDate != null)
                        {
                            textBirthDate.setText((profile.birthDate));
                        }
                        if(profile.firstName != null && profile.lastName != null)
                        {
                            textFirstName.setText((profile.firstName));
                            textLastname.setText((profile.lastName));
                        }
                        else
                        {
                            String fullName = profile.user.getDisplayName();
                            if(fullName != null)
                            {
                                fullName = fullName.replace("\n"," ");
                                String[] parts = fullName.split("");
                                profile.firstName = parts[0];
                                profile.lastName = parts[1];
                                textFirstName.setText(parts[0]);
                                textLastname.setText(parts[1]);
                            }

                        }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"-----"+databaseError.getMessage()+"----");
            }
        });
    }
    private  void getProfilePicture()
    {
        StorageReference pathReference = profile.storage.child("images/ProfilePicture_"+profile.uid+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
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

    private void showProfileEditLayout(boolean b)
    {
        if(b)
        {
            if(!TextUtils.isEmpty(profile.birthDate) && !profile.birthDate.equals("birthdate not set"))editProfileBirthdate.setText(profile.birthDate);
            if(!TextUtils.isEmpty(profile.firstName))editProfileFirstname.setText(profile.firstName);
            if(!TextUtils.isEmpty(profile.lastName))editProfileLastname.setText(profile.lastName);
            layoutProfileInfo.setVisibility(View.GONE);
            layoutProfileEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(profile.uid);
            Map<String, Object> userData = new HashMap<String, Object>();
            if(editProfileBirthdate.getText() != null ) {
                userData .put("birthdate", editProfileBirthdate.getText().toString());
                profile.birthDate = editProfileBirthdate.getText().toString();
                textBirthDate.setText(profile.birthDate);
            }
            if(editProfileFirstname.getText() != null ){
                userData .put("firstname", editProfileFirstname.getText().toString());
                profile.firstName = editProfileFirstname.getText().toString();
                textFirstName.setText(profile.firstName);
            }
            if(editProfileLastname.getText() != null ){
                userData .put("lastname", editProfileLastname.getText().toString());
                profile.lastName = editProfileLastname.getText().toString();
                textLastname.setText(profile.lastName);
            }
            reference.updateChildren(userData);
            layoutProfileInfo.setVisibility(View.VISIBLE);
            layoutProfileEdit.setVisibility(View.GONE);
        }
    }
    public void updateNewImage(Bitmap b)
    {
        image.setImageBitmap(b);
    }

    private void checkNewUser()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(profile.uid)) {

                    //reference.setValue(profile.uid);
                    final DatabaseReference referenceUID = reference.child(profile.uid);
                    Log.d(TAG,"-----creating new UID-----");
                    Map<String, Object> userData = new HashMap<String, Object>();

                    String fullName = profile.user.getDisplayName();
                    if(fullName != null)
                    {
                        Log.d(TAG,"-----"+fullName);
                        String[] parts = fullName.split(" ");
                        profile.firstName = parts[0];
                        profile.lastName = parts[1];
                    }
                    userData.put("textEmail", profile.email);
                    userData.put("textFirstName", profile.firstName);
                    userData.put("textLastname", profile.lastName);
                    userData.put("birthdate", "birthdate not set");
                    referenceUID.updateChildren(userData);
                }
                else
                {
                    Log.d(TAG,"-----UID exist-----");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"-----"+databaseError.getMessage()+"----");
            }
        });
    }
    private void logout()
    {
        ((MainActivity)getActivity()).exit();
    }
}
