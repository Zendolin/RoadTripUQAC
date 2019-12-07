package com.uqac.mobile.roadtripplanner;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uqac.mobile.roadtripplanner.Adapters.PlacesAdapter;
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
import com.uqac.mobile.roadtripplanner.Profiles.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.uqac.mobile.roadtripplanner.Utils.Constants.DEFAULT_ZOOM;
import static com.uqac.mobile.roadtripplanner.Utils.Constants.ERROR_DIALOG_REQUEST;
import static com.uqac.mobile.roadtripplanner.Utils.Constants.MY_LOCATION;
import static com.uqac.mobile.roadtripplanner.Utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.uqac.mobile.roadtripplanner.Utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapFragment";
    private ArrayList<Place> places = new ArrayList<>();
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private GoogleApiClient googleApiClient;
    PlacesClient placesClient;
    SupportMapFragment mapFragment;

    AutoCompleteTextView searchText;
    ImageView gps;
    Profile profile;
    //ImageView imageView;
    //ListView listOfDestinations;

    PlacesAdapter listOfDestinationsAdapter;
    ImageView imageDelete;
    ImageView imageSave;
    ImageView imageStart;

    LatLng point;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      /*  SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps = view.findViewById(R.id.ic_gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGps(v);
            }
        });

        //getProfileData();
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
               /* //GO VERS DatePickerFragment
                // Create new fragment and transaction

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.ContentLayout, new DatePickerFragment(), "DatepickerFragment");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();*/
                DatePickerFragment newFragment = new DatePickerFragment();
                ((MainActivity) getActivity()).changeFragment(newFragment, "dataFragment");

            }
        });
        //Intent intent = new Intent(this, PlacesActivity.class);
        //Intent intent = new Intent(this, RoadsActivity.class);
        //startActivity(intent);
        String apiKey = getString(R.string.google_maps_key);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this.getActivity().getApplicationContext());
        searchText = view.findViewById(R.id.input_search);
        gps = view.findViewById(R.id.ic_gps);

        getLocationPermission();
        profile = ((MainActivity)getActivity()).profile;
        /*final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.PHOTO_METADATAS));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();

                Log.i("PlacesAPI", "onPlaceSelected: Latitude : " + latLng.latitude + ", Longitude : " + latLng.longitude);
                Log.i("PlacesAPI", "onPlaceSelected: Place ID : " + place.getId());

                updateListOfDestinations(place);
                moveToDestination(place);


                // get photos
                if( place.getPhotoMetadatas() !=  null){
                    for(PhotoMetadata photoMetadata : place.getPhotoMetadatas()){
                        Log.i("PlacesAPI", "onPlaceSelected: PhotoMetadata : " + photoMetadata);
                    }
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
                        ImageView imageView = (ImageView) findViewById(R.id.places_image);
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

        listOfDestinations = (ListView) findViewById(R.id.desinations);
        listOfDestinationsAdapter = new PlacesAdapter(this, places);
        listOfDestinations.setAdapter(listOfDestinationsAdapter);
        */
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms();
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, MY_LOCATION);
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
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            // Disable the default button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng p) {
                    //allPoints.add(point);
                    point = p;
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(point));
                    imageDelete.setVisibility(View.VISIBLE);
                    imageSave.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void deletePoint() {
        mMap.clear();
        point = null;
        imageDelete.setVisibility(View.INVISIBLE);
        imageSave.setVisibility(View.INVISIBLE);
    }

    private void saveLocation() {
        if (point != null) {
            AlertDialog.Builder alertDiag = new AlertDialog.Builder(getActivity());
            final EditText edittext = new EditText(getActivity());
            alertDiag.setMessage("Choose a name for your trip");
            alertDiag.setTitle("Save a Trip");
            alertDiag.setView(edittext);
            alertDiag.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        MyTrip trip = new MyTrip(profile.uid,edittext.getText().toString(),"","","0",new ArrayList());
                        //TODO Dates
                        Stage st = new Stage(point.latitude,point.longitude,"","");
                        if(trip.listStages == null)      Log.e(TAG, "---listStages NULL");
                        trip.listStages.add(st);
                        if( profile.trips == null)      Log.e(TAG, "---profile List NULL");
                        profile.trips.add(trip);
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
        });
    }
/*
    private void getProfileData() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "-----data found !------");
                if (dataSnapshot.child("email").getValue() != null)
                    profile.email = dataSnapshot.child("email").getValue().toString();
                if (dataSnapshot.child("lastname").getValue() != null)
                    profile.lastName = dataSnapshot.child("lastname").getValue().toString();
                if (dataSnapshot.child("firstname").getValue() != null)
                    profile.firstName = dataSnapshot.child("firstname").getValue().toString();
                if (dataSnapshot.child("birthdate").getValue() != null)
                    profile.birthDate = dataSnapshot.child("birthdate").getValue().toString();
                if (dataSnapshot.child("countSavedTrips").getValue() != null)
                    profile.countSavedTrips = dataSnapshot.child("countSavedTrips").getValue().toString();
                Log.d(TAG, "---Profile get  , count : " + profile.countSavedTrips+"---");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "-----" + databaseError.getMessage() + "----");
            }
        });
    }*/
}
