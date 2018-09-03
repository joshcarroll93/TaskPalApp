package joshcarroll.projects.android.taskpal.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;

public class DeleteTaskFragment extends DialogFragment {

    private NewTask mTask;
    private DBHandler db;
    private NewTaskListener mListener;

    public static DeleteTaskFragment newInstance(NewTask task) {

        DeleteTaskFragment dialogFragment = new DeleteTaskFragment();

        dialogFragment.mTask = task;

        return dialogFragment;
    }

    public void setListener(NewTaskListener listener){
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_delete_task, container);


        Button deleteButton =(Button)view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VersionDbHandler versionDbHandler = new VersionDbHandler(getContext());
                int version = versionDbHandler.getVersionNumber();
                db = new DBHandler(getActivity(), version);

                db.deleteTask(mTask);
                mListener.removeTask(mTask);

                Toast.makeText(getActivity(), "Task successfully deleted", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
        return view;
    }
}
