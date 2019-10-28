package com.uqac.mobile.roadtripplanner.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;

public class PlacesAdapter extends BaseAdapter {

    private ArrayList<Place> places = new ArrayList<>();
    private Context context;

    public PlacesAdapter(Context context, ArrayList<Place> places) {
        super();
        this.places = places;
        this.context = context;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = LayoutInflater.from(context).
                inflate(R.layout.destination, parent, false);

        TextView place = (TextView) rowView.findViewById(R.id.destination);
        ImageView icon = (ImageView) rowView.findViewById(R.id.place_icon);
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //LinearLayout destination_layout = inflater.inflate(R.layout.destination_image, null).findViewById(R.id.destination_layout);
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                View destination_image = inflater.inflate(R.layout.destination_image, null);
                ImageAdapter ia = new ImageAdapter(context, places.get(0).getPhotoMetadatas());
                ListView images = (ListView) destination_image.findViewById(R.id.images);
                //images.setAdapter(ia);
                settingsDialog.setContentView(destination_image);
                settingsDialog.show();
            }
        });

        place.setText(places.get(position).getName());
        //Todo récupérer l'information sur le type, style monument, ville, village, etc ... Pour changer l'icone
        icon.setImageResource(R.mipmap.city);
        icon.getLayoutParams().width = 90;
        icon.getLayoutParams().height = 90;

        return rowView;
    }
}
