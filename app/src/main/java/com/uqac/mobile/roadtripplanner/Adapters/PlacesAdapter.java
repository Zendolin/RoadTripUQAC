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
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.Maps.BlurDialogFragment;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;
import java.util.List;

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

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        }

        TextView tvPlace = convertView.findViewById(R.id.destination);
        TextView tvAddress = convertView.findViewById(R.id.info);

        final Place place = places.get(position);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                BlurDialogFragment blurDialogFragment = new BlurDialogFragment(place);
                FragmentManager manager = ((MainActivity) view.getContext()).getSupportFragmentManager();

                blurDialogFragment.show(manager, blurDialogFragment.getClass().getSimpleName());
                return false;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "onClick : " + place.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        tvPlace.setText(place.getName());

        tvAddress.setText(getAddress(place));
        //Todo récupérer l'information sur le type, style monument, ville, village, etc ... Pour changer l'icone
        //icon.setImageResource(R.drawable.ic_menu_gallery);
        //icon.getLayoutParams().width = 90;
        //icon.getLayoutParams().height = 90;

        return convertView;
    }

    private String getAddress(Place place){
        List<AddressComponent> addressComponents = place.getAddressComponents().asList();

        String street_number = "";
        String route = "";
        String locality = "";
        String postal_code = "";
        String country = "";

        for(AddressComponent addressComponent : addressComponents){
            List<String> types = addressComponent.getTypes();
            if(types.contains("street_number")) street_number = addressComponent.getName();
            if(types.contains("route")) route = addressComponent.getName();
            if(types.contains("locality")) locality = addressComponent.getName();
            if(types.contains("postal_code")) postal_code = addressComponent.getName();
            if(types.contains("country")) country = addressComponent.getName();
        }

        return !street_number.isEmpty()
                ? street_number + " " + route + ", " + postal_code + " " + locality + ", " + country
                : postal_code + " " + locality + ", " + country;
    }
}
