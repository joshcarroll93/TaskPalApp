package joshcarroll.projects.android.taskpal.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;


import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.service.LocationService;

public class SettingsFragment extends DialogFragment {

    private View view;
    private TextView mTextView;
    private SwitchCompat mSwitch;

    public static SettingsFragment newInstance(){

        SettingsFragment settingsFragment = new SettingsFragment();

        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialog_theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_settings, null);

        onCreateFields(view);

        return view;
    }

    private void onCreateFields(View view){

        final DBHandler dbHandler = new DBHandler(getActivity());

       // mTextView = (TextView)view.findViewById(R.id.id_text_view_notifications);
        mSwitch = (SwitchCompat)view.findViewById(R.id.id_notifications_switch);

        if(dbHandler.getNotificationSetting()== 1){
            mSwitch.setChecked(true);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    //starting location service
                    getActivity().startService(new Intent(getActivity(), LocationService.class));
                    dbHandler.setNotification(1);
                    Toast.makeText(getActivity(), "Notifications On", Toast.LENGTH_LONG).show();
                }else{
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                    dbHandler.setNotification(0);
                    Toast.makeText(getActivity(), "Notifications Off", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
