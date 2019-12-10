package com.uqac.mobile.roadtripplanner.Maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.uqac.mobile.roadtripplanner.Adapters.PlacesAdapter;
import com.uqac.mobile.roadtripplanner.Calendar.Calendar.CalendarFragment;
import com.uqac.mobile.roadtripplanner.Helpers.FetchURL;
import com.uqac.mobile.roadtripplanner.Helpers.TaskLoadedCallback;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.MyTrip;
import com.uqac.mobile.roadtripplanner.Profiles.Profile;
import com.uqac.mobile.roadtripplanner.R;
import com.uqac.mobile.roadtripplanner.Stage;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.uqac.mobile.roadtripplanner.Maps.DestinationMatrixTask.SEPARATOR;

public class MapFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback, DestinationMatrixCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapFragment";
    CalendarFragment calendarFragment;
    View view;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private GoogleApiClient googleApiClient;
    PlacesClient placesClient;
    private ArrayList<Polyline> polylines;
    private Polyline currentPolyline;
    SupportMapFragment mapFragment;
    AutoCompleteTextView searchText;
    ImageView gps;
    Profile profile;
    //ImageView imageView;
    //ListView listOfDestinations;


    ImageView imageDelete;
    ImageView imageSave;
    ImageView imageStart;
    ImageView btnAddplace;
    Bundle bundle;

    ArrayList<LatLng> points;
    public ListView listViewPlaces;
    public PlacesAdapter listOfDestinationsAdapter;
    public ArrayList<Place> places = new ArrayList<>();

    private Place currentPlace = null;

    //tests -----------------
    private static final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS_COMPONENTS);

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 15;
    private static final String MY_LOCATION = "MY_LOCATION";
    private static final String DIRECTION_MODE = "driving";
    // ------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps = view.findViewById(R.id.ic_gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGps(v);
            }
        });
        points = new ArrayList<>();

        placesClient = Places.createClient(getActivity());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(placeFields);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new CustomPlaceSelectionListener());

        //getProfileData();
        btnAddplace = view.findViewById(R.id.ic_add_place);
        Log.d(TAG,"#--" + btnAddplace);
        btnAddplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddPlace(v);
            }
        });
        imageDelete = view.findViewById(R.id.image_map_deletePoint);
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePoint();
            }
        });
        imageSave = view.findViewById(R.id.image_map_savePoint);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });
        imageStart = view.findViewById(R.id.image_start_trip);
        imageStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                ((MainActivity) getActivity()).changeFragment(newFragment, "dataFragment");

            }
        });


        String apiKey = getString(R.string.google_maps_key);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this.getActivity().getApplicationContext());
        gps = view.findViewById(R.id.ic_gps);

        getLocationPermission();
        profile = ((MainActivity)getActivity()).profile;

        listViewPlaces = getActivity().findViewById(R.id.places);
        listOfDestinationsAdapter = new PlacesAdapter(getActivity(), places);
        listViewPlaces.setAdapter(listOfDestinationsAdapter);

        return view;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void onClickGps(View view) {
        getDeviceLocation();
    }

    private void init() {
        Log.i(TAG, "init: initilazing");


        // Define a Place ID.
        String placeId = "ChIJrTLr-GyuEmsRBfy61i59si0";

        logPlacesData(placeId);

        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(this.getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException + " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found an address : " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM));

                            /*
                            MarkerOptions options = new MarkerOptions().position(latlng).title(MY_LOCATION);
                            mMap.addMarker(options);
                             */
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }

    public void onClickAddPlace(View view) {
       places.add(currentPlace);
       listOfDestinationsAdapter.notifyDataSetChanged();
        Log.d(TAG, "onClickAddPlace: places.size() = " + places.size());
        if ( places.size() > 1) {
            drawPath();

        }
        imageDelete.setVisibility(View.VISIBLE);
        imageSave.setVisibility(View.VISIBLE);
    }

    public void onClickNavToGallery(View view) {
        GalleryFragment galleryFragment = new GalleryFragment();
        replaceFragment(galleryFragment);
    }


    public void replaceFragment(Fragment someFragment) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void drawPath() {
        Log.d(TAG,"#-----" + places.size());
        Log.d(TAG, "drawPath: path drawing");
        for (int position = 1; position <   places.size(); position++) {
            LatLng origin =   places.get(position - 1).getLatLng();
            LatLng destination =  places.get(position).getLatLng();

            String url = getUrl(origin, destination, DIRECTION_MODE);

            new FetchURL(this).execute(url, DIRECTION_MODE);
        }
    }

    private void calculatePathLowestDistance() {
        //TODO get lower distances between places using distance matric
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC&destinations=San+Francisco&mode=" + DIRECTION_MODE + " &language=fr-FR&key=" + getString(R.string.roads_api_key);
        new DestinationMatrixTask(getActivity()).execute(url);
    }

    private void calculatePathLowestTime() {
        //TODO get lower time between places using distance matric
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.roads_api_key);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                //TODO: use application
            } else {
                getLocationPermission();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    //getChatrooms();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        btnAddplace.setVisibility(View.INVISIBLE);
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            // Disable the default button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();/*
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng p) {
                    //allPoints.add(point);
                    points.add(p);
                    mMap.addMarker(new MarkerOptions().position(p));
                    imageDelete.setVisibility(View.VISIBLE);
                    imageSave.setVisibility(View.VISIBLE);
                }
            });*/
        }
    }

    private void deletePoint() {
        mMap.clear();
        points = new ArrayList<LatLng>();
        imageDelete.setVisibility(View.INVISIBLE);
        imageSave.setVisibility(View.INVISIBLE);
    }

    private void saveLocation() {
        if (places.size() > 0) {
            AlertDialog.Builder alertDiag = new AlertDialog.Builder(getActivity());
            final EditText edittext = new EditText(getActivity());
            alertDiag.setMessage("Choose a name for your trip");
                        alertDiag.setTitle("Save a Trip");
                        alertDiag.setView(edittext);
                        alertDiag.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                        MyTrip trip = new MyTrip(profile.uid,edittext.getText().toString(),"","","0",new ArrayList(),false);
                        //TODO Dates

                        for(Place p : places)
                        {
                            LatLng l = p.getLatLng();
                            Stage st = new Stage(p.getName(),l.latitude,l.longitude,"","");
                            trip.listStages.add(st);
                        }
                        profile.trips.add(trip);
                        bundle = new Bundle();
                        bundle.putBoolean("firstTime", true);
                        CalendarFragment calFrag = new CalendarFragment();
                        calFrag.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.ContentLayout, calFrag);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        profile.SaveProfile();
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Location saved !", Toast.LENGTH_LONG);
                        toast.show();
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "An error occured...", Toast.LENGTH_LONG);
                        Log.e(TAG, "---------------Error saving Point-------");
                        toast.show();
                        Log.e(TAG,"------" + e.toString());
                    }
                }
            });
            alertDiag.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = alertDiag.create();
            dialog.show();
        } else Log.d(TAG, "---------------Saved point is null-------");
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to lat " + latLng.latitude + " | lng " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (title != MY_LOCATION) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    public void moveToDestination(Place place) {
        LatLng dest = place.getLatLng();
        mMap.addMarker(new MarkerOptions().position(dest).title(place.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
    }

    public void updateListOfDestinations(Place place) {
        Log.i("updateList", "list updated");
        places.add(place);
        listOfDestinationsAdapter.notifyDataSetChanged();

    }

    private void hideSoftKeyboard() {
        if (this.getActivity().getWindow() != null) {
            this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void logPlacesData(String placeId) {

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.d(TAG, "Place found: " + place.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + e.getMessage());
                }
            }
        });
        /*
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    // search

                    geoLocate();
                }
                return false;
            }
        });*/
    }

    @Override
    public void onTaskDone(Object... values) {
        mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onTaskDone(String result) {
        String[] res = result.split(SEPARATOR);
        double min = Double.parseDouble(res[0]) / 60;
        int dist = Integer.parseInt(res[1]) / 1000;
        String origin = res[2];
        String destination = res[3];
        Log.d(TAG, "setDouble: Duration    = " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins");
        Log.d(TAG, "setDouble: Distance    = " + dist + " kilometers");
        Log.d(TAG, "setDouble: Origin      = " + origin);
        Log.d(TAG, "setDouble: Destination = " + destination);
    }

    public class CustomPlaceSelectionListener implements PlaceSelectionListener
    {
        @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            currentPlace = place;
            btnAddplace.setVisibility(View.VISIBLE);
            moveToDestination(place);
        }

        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
        }
    }
}
