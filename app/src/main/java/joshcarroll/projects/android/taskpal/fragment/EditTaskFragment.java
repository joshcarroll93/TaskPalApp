package joshcarroll.projects.android.taskpal.fragment;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;
import joshcarroll.projects.android.taskpal.utils.Utils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class EditTaskFragment extends DialogFragment {

    private NewTask mTask;
    private String TAG = "EDIT_TASK_FRAGMENT";
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private NewTaskListener mListener;
    private Intent intent;
    private Place place;
    private EditText addressField;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public static EditTaskFragment newInstance(){

        return new EditTaskFragment();
    }
    public void setListener(NewTaskListener listener){
        this.mListener = listener;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        if (autocompleteFragment != null)
//            getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_edit_task, container, false);

        createFields(view);

        return view;
    }

    public void createFields(View view){

        mTask = getArguments().getParcelable("TASK_TO_EDIT");
        final int position = mTask.getId();

        final EditText titleField = (EditText)view.findViewById(R.id.task_edit_title_field);
        final EditText descField = (EditText)view.findViewById(R.id.task_edit_desc_field);
        addressField = (EditText)view.findViewById(R.id.edit_task_address_field);

        final Button clearTitleButton = (Button )view.findViewById(R.id.clear_button_edit_title);
        final Button clearDescButton = (Button) view.findViewById(R.id.clear_button_edit_desc);
        final Button clearAddressButton = (Button) view.findViewById(R.id.clear_button_edit_address);
        final Button submitButton = (Button) view.findViewById(R.id.submit_edited_task_button);

        titleField.setText(mTask.getTitle());
        descField.setText(mTask.getDescription());
        addressField.setText(mTask.getAddress());
//        addressField.setEnabled(false);

        mAddress = mTask.getAddress();
        mLatitude = mTask.getLatitude();
        mLongitude = mTask.getLongitude();

        addressField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If the user clicks the address field to change it, launch the intent for searching a place
               findAddress();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(titleField.getText() != null && mAddress != null) {

                    NewTask task = new NewTask(position
                            ,titleField.getText().toString()
                            ,descField.getText().toString()
                            ,mLatitude
                            ,mLongitude
                            ,mAddress
                            ,0
                    );

                    Log.d(TAG+ " Lat:", ""+mLatitude);
                    Log.d(TAG+ " Lng:", ""+mLongitude);

                    VersionDbHandler versionDbHandler = new VersionDbHandler(getContext());
                    int version = versionDbHandler.getVersionNumber();

                    DBHandler db = new DBHandler(getActivity(), ++version);

                    db.editTask(task, position);
                    mListener.removeTask(mTask);
                    mListener.addTask(task);

                    dismiss();
                }else{
                    Snackbar.make(view, "You must give your task a title and address!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        clearTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleField.requestFocus();
                titleField.setText("");
            }
        });

        clearDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descField.requestFocus();
                descField.setText("");
            }
        });

        clearAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressField.requestFocus();
                addressField.setText("");
                findAddress();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(getActivity(), data);
                mAddress = place.getAddress().toString();
                LatLng queriedLocation = place.getLatLng();
                mLatitude = queriedLocation.latitude;
                mLongitude = queriedLocation.longitude;
                addressField.setText(mAddress);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.e(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void findAddress(){
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IE")
                    .build();
            intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(getActivity());

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException gpsre) {
            gpsre.printStackTrace();

        } catch (GooglePlayServicesNotAvailableException gpsnae) {
            gpsnae.printStackTrace();
        }
    }
}
