package com.uqac.mobile.roadtripplanner.Profiles;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uqac.mobile.roadtripplanner.MyTrip;
import com.uqac.mobile.roadtripplanner.Stage;
import com.uqac.mobile.roadtripplanner.Utils.ProfileLoader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import androidx.annotation.NonNull;

public class Profile {

    private static String TAG = "----------RoadTrip Planner-------------";
    public FirebaseUser user;
    public String uid;

    public String email;
    public String lastName;
    public String firstName;
    public String birthDate;
    public Bitmap profilePicture;
    public ArrayList<ProfileRef> friends = new ArrayList<>();
    public ArrayList<MyTrip> trips = new ArrayList<>();

    public Profile(FirebaseUser user)
    {
        if(user != null)
        {
            this.user = user;
            this.email = user.getEmail();
            this.uid = user.getUid();
        }
    }
    public Profile(String uid)
    {
        this.uid = uid;
    }
    public Profile(String email, String firstName, String lastName,String birthDate)
    {
        this.email =email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public void uploadProfilePicture(final ProfileFragment pf , final Bitmap b)
    {
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images/ProfilePicture_"+this.uid+".jpg");
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

    public void RemoveFriend(String uid)
    {
        ListIterator<ProfileRef> iter = friends.listIterator();
        while(iter.hasNext()){
            if(iter.next().uid.equals(uid)){
                iter.remove();
            }
        }
        SaveProfile();
    }
    public void SaveProfile()
    {
        try
        {
            Log.d(TAG,"-----Saving data------");
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(this.uid);
            if(this.uid == null) Log.e(TAG,"------UID NULL---");
            Map<String, Object> userData = new HashMap<String, Object>();
            userData.put("email", this.email);
            userData.put("birthdate", this.birthDate);
            userData.put("firstname", this.firstName);
            userData.put("lastname", this.lastName);
            userData.put("friends",this.friends);
            userData.put("trips",this.trips);
            reference.updateChildren(userData);
            Log.d(TAG,"Save profile Likes :" + this.trips.get(0).likeCount);
        }
       catch (Exception e)
       {
           Log.e(TAG,"-----Error in profile : "+e.toString());
       }
    }

    public void LoadProfile(final ProfileLoader loader)
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(this.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("email").getValue() != null)
                    Profile.this.email = dataSnapshot.child("email").getValue().toString();
                if (dataSnapshot.child("lastname").getValue() != null)
                    Profile.this.lastName = dataSnapshot.child("lastname").getValue().toString();
                if (dataSnapshot.child("firstname").getValue() != null)
                    Profile.this.firstName = dataSnapshot.child("firstname").getValue().toString();
                if (dataSnapshot.child("birthdate").getValue() != null)
                    Profile.this.birthDate = dataSnapshot.child("birthdate").getValue().toString();
                //get trips
                if (dataSnapshot.child("trips").getValue() != null)
                {
                    Profile.this.trips = new ArrayList<>();
                    ArrayList<HashMap> o = (ArrayList<HashMap>)dataSnapshot.child("trips").getValue();
                    for(HashMap hm : o)
                    {
                        String endDate = hm.get("endDate").toString();
                        String likeCount = hm.get("likeCount").toString();
                        String tripName = hm.get("tripName").toString();
                        String uid = hm.get("ownerUID").toString();
                        String startDate = hm.get("startDate").toString();
                        ArrayList<String> likesUID = (ArrayList) hm.get("likesUID");
                        Boolean isPrivate = (boolean)hm.get("isPrivate");
                        MyTrip mt = new MyTrip(uid,tripName,startDate,endDate,likeCount,likesUID,isPrivate);

                        ArrayList<HashMap> stages= (ArrayList<HashMap> )hm.get("listStages");
                        for(HashMap hmStage : stages)
                        {
                            String endDateStage = hmStage.get("endDate").toString();
                            Double latitude = (Double)hmStage.get("latitude");
                            String startDateStage = hmStage.get("startDate").toString();
                            Double longitude = (Double)hmStage.get("longitude");
                            Stage s = new Stage(latitude,longitude,startDateStage,endDateStage);
                            mt.listStages.add(s);
                        }
                        Profile.this.trips.add(mt);
                    }
                }
                //get friends
                if (dataSnapshot.child("friends").getValue() != null)
                {
                    Profile.this.friends = new ArrayList<>();
                    ArrayList<HashMap> array = (ArrayList<HashMap>)dataSnapshot.child("friends").getValue();
                    for(HashMap hm : array)
                    {
                        String uid = hm.get("uid").toString();
                        String firstName = hm.get("firstName").toString();
                        String lastName = hm.get("lastName").toString();
                        ProfileRef f = new ProfileRef(uid,lastName,firstName);
                        Profile.this.friends.add(f);
                    }
                }
                loader.LoadProfileData(Profile.this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "-----" + databaseError.getMessage() + "----");
            }
        });
    }
}
