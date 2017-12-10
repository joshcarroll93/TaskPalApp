package joshcarroll.projects.android.taskpal.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
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

    private PlaceAutocompleteFragment autocompleteFragment;
    private String TAG = "ADD_TASK_FRAGMENT";
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private View view;
    private NewTaskListener mListener;

    public AddTaskFragment(){

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

        view =  inflater.inflate(R.layout.fragment_add_task, container);

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
                Log.d(TAG , place.getName().toString());
                mAddress = place.getAddress().toString();
                LatLng queriedLocation = place.getLatLng();
                mLatitude = queriedLocation.latitude;
                mLongitude = queriedLocation.longitude;

            }

            @Override
            public void onError(Status status) {

                Log.d("Status: ", status.toString());
            }

        });

        final EditText titleField = (EditText)view.findViewById(R.id.task_title_field);
        titleField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final EditText descField = (EditText)view.findViewById(R.id.task_desc_field);
        descField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final Button clearTitle = (Button)view.findViewById(R.id.clear_button_add_title);
        final Button clearDesc = (Button)view.findViewById(R.id.clear_button_add_desc);
        Button submitButton = (Button)view.findViewById(R.id.submit_task_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String title = titleField.getText().toString();
                final String description = descField.getText().toString();


                if(title.length() > 0 && mAddress != null) {

                    NewTask task = new NewTask(0, title, description, mLatitude, mLongitude, mAddress, 0);

                    Log.d("AddTaskFragment Lat: ", ""+mLatitude);
                    Log.d("AddTaskFragment Lng: ", ""+mLongitude);
                    DBHandler db = new DBHandler(getActivity());

                    db.addTask(task);
                    mListener.addTask(task);

                    dismiss();
                }else{
                    Snackbar.make(view, "You must give your task a title and address!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        clearTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                titleField.requestFocus();
                titleField.setText("");
            }
        });

        clearDesc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                descField.requestFocus();
                descField.setText("");
            }
        });
    }
}
