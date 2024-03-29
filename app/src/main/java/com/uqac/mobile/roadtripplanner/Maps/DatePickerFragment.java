package com.uqac.mobile.roadtripplanner.Maps;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePickerFragment extends Fragment {
    private CalendarPickerView calendar;
    Button button_done ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.date_picker_fragment_layout, container, false);

        Date today = new Date();
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR,1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        button_done = (Button) view.findViewById(R.id.done_button);

        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDate(new Date());

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> SelectedDates = calendar.getSelectedDates();
                for (int i = 0; i < SelectedDates.size(); i++) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "date " + SelectedDates.get(i)  ,Toast.LENGTH_LONG);
                    toast.show();
                }

            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacesFragment fragmentPlace = new PlacesFragment();
                ((MainActivity)getActivity()).changeFragment(fragmentPlace, "fragmentPlace");
            }
        });


        return view;
    }




}
