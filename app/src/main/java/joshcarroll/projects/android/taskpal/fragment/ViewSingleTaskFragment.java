package joshcarroll.projects.android.taskpal.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.data.NewTask;

public class ViewSingleTaskFragment extends DialogFragment {

    private NewTask newTask;

    public static ViewSingleTaskFragment newInstance(NewTask task) {
        ViewSingleTaskFragment dialogFragment = new ViewSingleTaskFragment();

        dialogFragment.newTask = task;

        return dialogFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_view_single_task, null);

        onCreateFields(view);

        return view;
    }

    public  void onCreateFields(View view){

        TextView titleField = (TextView)view.findViewById(R.id.title_id);
        TextView descriptionField = (TextView)view.findViewById(R.id.description_id);
        TextView addressField = (TextView)view.findViewById(R.id.address_id);

        titleField.setText(newTask.getTitle());

        if(newTask.getDescription().length() > 0){

            descriptionField.setText(newTask.getDescription());
        }
        else{
            descriptionField.setText("Description Not Found.");
        }

        if(newTask.getAddress().length() > 0 ) {
            addressField.setText(newTask.getAddress());
        }
        else{
            addressField.setText("Address Not Found.");
        }
    }
}