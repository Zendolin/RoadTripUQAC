package com.uqac.mobile.roadtripplanner.Calendar.Calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.uqac.mobile.roadtripplanner.Adapters.MyCustomAdapter;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.Maps.MapFragment;
import com.uqac.mobile.roadtripplanner.MyTrip;
import com.uqac.mobile.roadtripplanner.Profiles.Profile;
import com.uqac.mobile.roadtripplanner.R;
import com.uqac.mobile.roadtripplanner.Stage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;

public class CalendarFragment extends Fragment implements View.OnClickListener, Serializable {
    Context context;
    Button show;
    Button add;
    Button addCalendar;
    Button finish;
    Cursor cursor;
    ListView listView;
    EditText input;
    EditText edittext;

    Bundle bundle;
    FragmentTransaction fragmentTransaction;

    Profile profile;


    long startMillis = 0;
    long endMillis = 0;
    Event event = new Event();
    Boolean firstTime = true ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_fragment, container, false);


        show = (Button) v.findViewById(R.id.show);
        add = (Button) v.findViewById(R.id.add);
        listView = (ListView) v.findViewById(R.id.listview);
        edittext = (EditText) v.findViewById(R.id.editText2);
        addCalendar = (Button) v.findViewById(R.id.addCalendar);
        finish = (Button) v.findViewById(R.id.finish);
        Bundle bundle = this.getArguments();
        context = v.getContext();
        profile = ((MainActivity)getActivity()).profile;

        if(bundle != null){
            firstTime = bundle.getBoolean("firstTime");
            if(firstTime){

            addCalendar(profile.trips.get(profile.trips.size()-1).tripName);
            edittext.setText(getCalendarDispName(profile.trips.get(profile.trips.size() - 1).tripName)+"");
            Calendar cal = Calendar.getInstance();
            Calendar cal1 = Calendar.getInstance();
            cal1.add(HOUR_OF_DAY,1);
            for(Stage t :profile.trips.get(profile.trips.size()-1).listStages ) {
                addEvent(getCalendarDispName(profile.trips.get(profile.trips.size() - 1).tripName), cal, cal1, t.name);
                cal.add(DAY_OF_MONTH,1);
                cal1.add(DAY_OF_MONTH,1);
            }
            firstTime = false;
            }else{
                edittext.setText(bundle.getLong("CalId")+"");
            }
        }




        readEvents(getCalendarDispName(profile.trips.get(profile.trips.size()-1).tripName));

        show.setOnClickListener(this);
        add.setOnClickListener(this);
        addCalendar.setOnClickListener(this);
        finish.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.show:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                if (cursor.moveToNext() == false) {
                }
                //getCalendars();
                readEvents(Integer.parseInt(edittext.getText().toString()));

                break;
            case R.id.add:
                ActionEvent addevent = new ActionEvent();
                bundle = new Bundle();
                bundle.putString("action", "add");
                bundle.putLong("calID", Integer.parseInt(edittext.getText().toString()));
                addevent.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ContentLayout, addevent);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //addEvent(Integer.parseInt(edittext.getText().toString()));
                break;
            case R.id.addCalendar:
                addCalendar("For Demo");
                break;
            case R.id.finish:
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ContentLayout, new MapFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

        }
    }


    public long getCalendarDispName(String DisplayName){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 7);
            return 0 ;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID
                // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;


        ContentResolver contentResolver = context.getContentResolver();
        String selection = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{"" + DisplayName};
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null);
        long calID = 0;
        while (cur.moveToNext()) {


            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);


        }
        return calID;
    }




    public void getCalendars() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 7);
            return;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.ACCOUNT_TYPE,         // 2
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_ACCOUNT_NAME = 1;
        final int PROJECTION_DISPLAY_ACCOUNT_TYPE = 2;
        final int PROJECTION_NAME = 3;
        final int PROJECTION_CALENDAR_COLOR = 4;
        final int PROJECTION_CALENDAR_ACCESS_LEVEL = 5;
        final int PROJECTION_OWNER_ACCOUNT = 6;
        final int PROJECTION_CALENDAR_DISPLAY_NAME = 7;


        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);

        ArrayList<String> calendarInfos = new ArrayList<>();
        while (cur.moveToNext()) {
            long calID = 0;
            String accountName = null;
            String accountType = null;
            String name= null;
            String calendarColor= null;
            String calendar_acces_lvl= null;
            String owner_account= null;
            String calendar_display_name = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_ACCOUNT_NAME);
            accountType = cur.getString(PROJECTION_DISPLAY_ACCOUNT_TYPE);
            name = cur.getString(PROJECTION_NAME);
            calendarColor = cur.getString(PROJECTION_CALENDAR_COLOR);
            calendar_acces_lvl = cur.getString(PROJECTION_CALENDAR_ACCESS_LEVEL);
            owner_account = cur.getString(PROJECTION_OWNER_ACCOUNT);
            calendar_display_name = cur.getString(PROJECTION_CALENDAR_DISPLAY_NAME);



            String calendarInfo = String.format("Calendar ID: %s\naccountName: %s\naccountType %s\nname: %s\ncalendarColor : %s\ncalendar_acces_lvl : %s\nowner_account :%s\ncalendar_display_name : %s ",
                    calID, accountName, accountType, name,calendarColor,calendar_acces_lvl,owner_account,calendar_display_name);
            calendarInfos.add(calendarInfo);
        }

        ArrayAdapter stringArrayAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1, android.R.id.text1, calendarInfos);
        listView.setAdapter(stringArrayAdapter);
    }


    public void addCalendar(String dispName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "newtrip@roadtripplanner.com");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, "newtrip.roadtripplanner.com");
        contentValues.put(CalendarContract.Calendars.NAME, dispName);
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, dispName);
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, "232323");
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "newtrip@roadtripplanner.com");
        contentValues.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "METHOD_ALERT, METHOD_EMAIL, METHOD_ALARM");
        contentValues.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "TYPE_OPTIONAL, TYPE_REQUIRED, TYPE_RESOURCE");
        contentValues.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "AVAILABILITY_BUSY, AVAILABILITY_FREE, AVAILABILITY_TENTATIVE");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        uri = uri.buildUpon().appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "newtrip@roadtripplanner.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "newtrip.roadtripplanner.com").build();
        Toast.makeText(context, "Calendar created ! ", Toast.LENGTH_SHORT).show();
        context.getContentResolver().insert(uri, contentValues);
        getCalendars();
    }


    public void readEvents(long calID) {

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };
        final int ID_EVENT = 0;
        final int EVENT_TITLE = 1;
        final int EVENT_LOCATION = 2;
        final int DTSART = 3;
        final int DEND = 4;
        // Submit the query
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Instances.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{"" + calID};
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }
        Cursor cur = context.getContentResolver().query(uri, mProjection, selection, selectionArgs, null);


        ArrayList<Event> events = new ArrayList<>();
        while (cur.moveToNext()) {

            // Get the field values
            long eventID = cur.getLong(ID_EVENT);
            long beginVal = cur.getLong(DTSART);
            long endVal = cur.getLong(DEND);
            String title = cur.getString(EVENT_TITLE);
            String location = cur.getString(EVENT_LOCATION);

            Event event = new Event(eventID, calID, title, location, beginVal, endVal);
            events.add(event);
        }

        // ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, events);
        MyCustomAdapter adap = new MyCustomAdapter(events, context, CalendarFragment.this);
        listView.setAdapter(adap);
    }


    public MyTrip getCurrentTrip(long CalID){
        MyTrip trip  = null;
        for(MyTrip t : profile.trips){
            if(t.tripName.equals(getCalendarID(CalID))){
                Log.d("NOM CALENDRIER " , getCalendarID(CalID)+"");
                return t;
            }

        }
        return trip;

    }

    public String getCalendarID(long ID){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 7);
            return "" ;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;


        ContentResolver contentResolver = context.getContentResolver();
        String selection = CalendarContract.Calendars._ID + " = ?";
        String[] selectionArgs = new String[]{"" + ID};
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null);
        String disp = "";
        while (cur.moveToNext()) {


            // Get the field values
            disp = cur.getString(PROJECTION_ID_INDEX);


        }
        return disp;
    }
    public void updateEventDialog(final Event event){
                ActionEvent updateevent = new ActionEvent();

        bundle = new Bundle();
        bundle.putString("action","update");
        bundle.putSerializable("event", (Serializable) event);
        bundle.putLong("calID", Integer.parseInt(edittext.getText().toString()));
        bundle.putString("titleEvent", event.getTitle());
        bundle.putLong("startMillis", event.getDateBegin());
        bundle.putLong("endMillis", event.getDateEnd());
         updateevent.setArguments(bundle);

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ContentLayout, updateevent);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void deleteEvent(Event eventdel){
        try {
            ContentResolver cr = context.getContentResolver();
            Uri deleteUri = null;
            MyTrip trip = getCurrentTrip(eventdel.getCalendar_ID());
            for(Stage t :trip.listStages){
                Log.d("t.name",t.name);
                if(t.name.equals(event.getTitle())){
                    trip.listStages.remove(t);
                }
            }
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventdel.getEvent_ID());
            profile.SaveProfile();
            cr.delete(deleteUri, null, null);
        }catch (Exception e)
        {

        }

    }

    public void addEvent(long calID, Calendar begin, Calendar end, String title) {

        startMillis = begin.getTimeInMillis();
        endMillis = end.getTimeInMillis();
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        event.setDateBegin(startMillis);
        event.setCalendar_ID(calID);
        event.setTitle(title);
        event.setDateEnd(endMillis);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "TEST");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }
        cr.insert(CalendarContract.Events.CONTENT_URI, values);



    }

}
