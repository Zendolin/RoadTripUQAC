package com.uqac.mobile.roadtripplanner;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;

public class Profile {

    private static String TAG = "----------RoadTrip Planner-------------";
    public StorageReference storage;
    public FirebaseUser user;
    public String uid;

    public String email;
    public String lastName;
    public String firstName;
    public String birthDate;
    public Bitmap profilePicture;

    Profile(StorageReference storage, FirebaseUser user)
    {
        if(user!= null)
        {
            this.storage = storage;
            this.user = user;
            this.email = user.getEmail();
            this.uid = user.getUid();
        }
    }

    public void uploadProfilePicture(final ProfileFragment pf , final Bitmap b)
    {
        StorageReference imagesRef = storage.child("images/ProfilePicture_"+this.uid+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                profilePicture = b;
                pf.updateNewImage(b);
            }
        });
    }
}
