package com.uqac.mobile.roadtripplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static String TAG = "----------RoadTrip Planner-------------";
    private static final int REQUEST_CODE = 1;

    private View view;
    private Profile profile;
    private ImageView image;
    private TextView email;
    private TextView firstname;
    private TextView lastname;
    private TextView birthDate;
    private Bitmap bitmap;

    private File localFile = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        TextView txtLogout = view.findViewById(R.id.textLogout);

        image = view.findViewById(R.id.profile_imageViewProfile);
        email = view.findViewById(R.id.profile_textEmail);
        birthDate = view.findViewById(R.id.profile_textDate);
        firstname = view.findViewById(R.id.profile_textFirstName);
        lastname = view.findViewById(R.id.profile_textLastName);

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
                //for(DataSnapshot datas: dataSnapshot.getChildren()){
                    if(dataSnapshot.child("email").getValue() != null)
                    {
                        Log.d(TAG,"-----user found : "+ dataSnapshot.child("email").getValue().toString()+" ------");
                        profile.email = dataSnapshot.child("email").getValue().toString();
                        profile.lastName =dataSnapshot.child("lastname").getValue().toString();
                        profile.firstName =dataSnapshot.child("firstname").getValue().toString();
                        profile.birthDate =dataSnapshot.child("birthdate").getValue().toString();

                        if(profile.profilePicture != null)
                        {
                            image.setImageBitmap(profile.profilePicture);
                        }
                        if(profile.email != null)
                        {
                            email.setText(profile.email);
                        }
                        if(profile.birthDate != null)
                        {
                            birthDate.setText((profile.birthDate));
                        }
                        if(profile.firstName != null && profile.lastName != null)
                        {
                            firstname.setText((profile.firstName));
                            lastname.setText((profile.lastName));
                        }
                        else
                        {
                            String fullName = profile.user.getDisplayName();
                            if(fullName != null)
                            {
                                fullName = fullName.replace("\n"," ");
                                String[] parts = fullName.split("");
                                firstname.setText(parts[0]);
                                profile.firstName = parts[0];
                                profile.lastName = parts[1];
                                lastname.setText(parts[1]);
                            }

                        }
                  //  }
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

    private void setProfileData()
    {

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
                    reference.setValue(profile.uid);
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
                    userData.put("email", profile.email);
                    userData.put("firstname", profile.firstName);
                    userData.put("lastname", profile.lastName);
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
