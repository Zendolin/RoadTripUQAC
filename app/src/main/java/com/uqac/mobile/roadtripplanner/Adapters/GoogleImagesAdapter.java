package com.uqac.mobile.roadtripplanner.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class GoogleImagesAdapter extends BaseAdapter {

    private static final int MAX_PHOTOS = Integer.MAX_VALUE;
    private static final String TAG = "GoogleImagesAdapter";

    private List<PhotoMetadata> photoMetadatas = new ArrayList<>();
    private Context context;

    public GoogleImagesAdapter(Context context) {
        Log.d(TAG, "GoogleImagesAdapter: " + context);
        this.context = context;
    }

    public GoogleImagesAdapter(Context context, List<PhotoMetadata> photoMetadatas) {
        Log.d(TAG, "GoogleImagesAdapter: " + context);
        this.context = context;
        this.photoMetadatas = photoMetadatas;
    }

    @Override
    public int getCount() {
        return photoMetadatas.size();
    }

    @Override
    public Object getItem(int position) {
        return photoMetadatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.d(TAG, "getView: " + convertView.getContext());
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        }


        final ImageView ivGoogleImage = convertView.findViewById(R.id.googleImage);

        if (photoMetadatas.size() == 0) {
            ivGoogleImage.setImageResource(R.mipmap.ic_no_photo_available);
            return convertView;
        }

        if (position <= MAX_PHOTOS) {

            PhotoMetadata photoMetadata = photoMetadatas.get(position);

            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();

            PlacesClient placesClient = Places.createClient(convertView.getContext().getApplicationContext());

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    // temporaire
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    ivGoogleImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("PlacesAPI", "Place not found: " + exception.getMessage());
                    }
                }
            });
        }

        return convertView;

    }
}