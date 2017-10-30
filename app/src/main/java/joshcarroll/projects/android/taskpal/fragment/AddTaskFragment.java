package joshcarroll.projects.android.taskpal.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;


public class AddTaskFragment extends DialogFragment {

    PlaceAutocompleteFragment autocompleteFragment;

    double lat;
    double lng;
    View view;
    private AddTaskFragment mAddTaskFragment;
    private NewTaskListener mListener;

    public static AddTaskFragment newInstance() {
        //AddTaskFragment dialogFragment = new AddTaskFragment();
        return new AddTaskFragment();
    }
    public void setListener(NewTaskListener listener){

        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        if(view != null){
            ViewGroup parent =(ViewGroup)view.getParent();
            if(parent!=null)
                parent.removeView(view);
        }

        view =  inflater.inflate(R.layout.fragment_add_task, null);

        onCreateFields(view);

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (autocompleteFragment != null)
            getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }
    public void onCreateFields(View view){

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("IE").build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Place: ", place.getName().toString());
                LatLng queriedLocation = place.getLatLng();
                lat = queriedLocation.latitude;
                lng = queriedLocation.longitude;
            }

            @Override
            public void onError(Status status) {
                Log.d("Status: ", status.toString());
            }
        });

        final EditText titleField = (EditText)view.findViewById(R.id.task_title_field);
        final EditText descField = (EditText)view.findViewById(R.id.task_desc_field);
        final Button clearTitle = (Button)view.findViewById(R.id.clearable_button_add_title);
        final Button clearDesc = (Button)view.findViewById(R.id.clearable_button_add_desc);
        Button submitButton = (Button)view.findViewById(R.id.submit_task_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Here goes the place for adding the stuff to your
                final String title = titleField.getText().toString();
                final String description = descField.getText().toString();

                if(title.length() > 0) {

                    NewTask task = new NewTask(0, title, description, lat, lng, 0);

                    Log.d("AddTaskFragment Lat: ", ""+lat);
                    Log.d("AddTaskFragment Lng: ", ""+lng);
                    DBHandler db = new DBHandler(getActivity());
                    db.addTask(task);
//                    taskListAdapter.add(task);
                    mListener.addTask(task);

                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "you must give your task a title",   Toast.LENGTH_LONG).show();
                }
            }
        });
        clearTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                titleField.setText("");
            }
        });

        clearDesc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                descField.setText("");
            }
        });
    }
}
