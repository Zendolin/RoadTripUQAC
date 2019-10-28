package com.uqac.mobile.roadtripplanner.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

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

public class ImageAdapter extends BaseAdapter {

    private List<PhotoMetadata> photos = new ArrayList<>();
    private Context context;

    public ImageAdapter(Context context, List<PhotoMetadata> photos) {
        this.photos = photos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = LayoutInflater.from(context).
                inflate(R.layout.destination_image, parent, false);

        PlacesClient placesClient = Places.createClient(context);

        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photos.get(position))
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
                .build();

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                // temporaire
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                View destination_image = inflater.inflate(R.layout.image, null);
                ImageView imageView = (ImageView) destination_image.findViewById(R.id.place_image);

                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
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

        return null;
    }
}
