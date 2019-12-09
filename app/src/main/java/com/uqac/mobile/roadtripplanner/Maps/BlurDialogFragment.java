package com.uqac.mobile.roadtripplanner.Maps;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.uqac.mobile.roadtripplanner.Adapters.GoogleImagesAdapter;
import com.uqac.mobile.roadtripplanner.R;

import java.util.List;

import androidx.annotation.Nullable;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class BlurDialogFragment extends SupportBlurDialogFragment {

    private static final String TAG = "BlurDialogFragment";

    private Place place;
    private ListView lvGoogleImage;

    public BlurDialogFragment() {
        // Required empty public constructor
    }

    public BlurDialogFragment(Place place) {
        this.place = place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_blur_dialog, container, false);

        ListView lvGoogleImage = view.findViewById(R.id.googleImageList);

        List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();

        if(photoMetadatas !=  null){
            Log.d(TAG, "onCreateView: " + getActivity());
            GoogleImagesAdapter googleImagesAdapter = new GoogleImagesAdapter(getActivity(), photoMetadatas);
            lvGoogleImage.setAdapter(googleImagesAdapter);
        } else {
            GoogleImagesAdapter googleImagesAdapter = new GoogleImagesAdapter(getActivity());
            lvGoogleImage.setAdapter(googleImagesAdapter);
            Log.d(TAG, "onLongClick: no photos");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }
}