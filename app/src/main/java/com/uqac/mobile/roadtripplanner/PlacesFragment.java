package com.uqac.mobile.roadtripplanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class PlacesFragment extends Fragment {

    PlacesClient placesClient;
    ImageView imageView;
    SupportMapFragment placesFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.places_fragment_layout, container, false);
       //setContentView(R.layout.places_fragment_layout);

        //ImageView imageView = (ImageView) findViewById(R.id.places_image);

        String apiKey = getString(R.string.google_api_key);

        if(!Places.isInitialized()){
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this.getActivity().getApplicationContext());


        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.PHOTO_METADATAS));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();

                Log.i("PlacesAPI", "onPlaceSelected: Latitude : " + latLng.latitude + ", Longitude : " + latLng.longitude);
                Log.i("PlacesAPI", "onPlaceSelected: Place ID : " + place.getId());
                for(PhotoMetadata photoMetadata : place.getPhotoMetadatas()){
                    Log.i("PlacesAPI", "onPlaceSelected: PhotoMetadata : " + photoMetadata);
                }


                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                String attributions = photoMetadata.getAttributions();

                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        // temporaire
                        ImageView imageView = (ImageView) view.findViewById(R.id.places_image);
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

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        return view;
    }
}
