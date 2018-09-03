package joshcarroll.projects.android.taskpal.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.activity.MainActivity;
import joshcarroll.projects.android.taskpal.adapter.RecyclerViewAdapter;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * Created by Josh on 26/10/2017.
 */

public class TabbedPlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static DBHandler dbHandler;
    public static List<NewTask> activeTasks = new ArrayList<>();
    public static List<NewTask> tasks = new ArrayList<>();
    public RecyclerViewAdapter mListAdapter;
    public RecyclerView mRecyclerView;

    public static RecyclerView.LayoutManager mLayoutManager;
    private String TAG = getClass().getSimpleName();
    TextView mTextViewPlaceHolder;

    public TabbedPlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TabbedPlaceholderFragment newInstance(int sectionNumber) {
        TabbedPlaceholderFragment fragment = new TabbedPlaceholderFragment();
        Bundle args = new Bundle();
        Log.d("OUTPUT_DE", "Place holder being made for : " + sectionNumber);
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        VersionDbHandler versionDbHandler = new VersionDbHandler(getContext());
        int version = versionDbHandler.getVersionNumber();
        dbHandler = new DBHandler(getContext(), ++version);

        View rootView = inflater.inflate(R.layout.fragment_tabbed_placeholder, container, false);

        mRecyclerView =  rootView.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mTextViewPlaceHolder = rootView.findViewById(R.id.placeholder_tv);
        mTextViewPlaceHolder.setVisibility(View.INVISIBLE);


        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
            allTasksIsChecked();
        }
        else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
            activeTasksIsChecked();
        }
        return rootView;
    }

    public void allTasksIsChecked(){
        Log.d(TAG, "all_tasks_clicked");

        mListAdapter = new RecyclerViewAdapter(getContext(), tasks);

        if(dbHandler.getAllTasks() != null && !dbHandler.getAllTasks().isEmpty()){

            showRecyclerView();

            tasks = dbHandler.getAllTasks();
            mRecyclerView.setAdapter(mListAdapter);

        }
        else if(tasks.size() == 0) {

            showTextViewPlaceHolder();

        }

    }

    public void activeTasksIsChecked() {
        Log.d(TAG, "active_tasks_clicked");
        mListAdapter = new RecyclerViewAdapter(getContext(), activeTasks);
        if (dbHandler.getAllTasks() != null && !dbHandler.getAllTasks().isEmpty()) {

            showRecyclerView();

            activeTasks = new ArrayList<>();

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getStatus() == 0) {
                    activeTasks.add(tasks.get(i));
                }
            }

            mRecyclerView.setAdapter(mListAdapter);

        } else if(activeTasks.size() == 0){

            showTextViewPlaceHolder();

        }
    }

    public void showRecyclerView(){

        mRecyclerView.setVisibility(View.VISIBLE);
        mTextViewPlaceHolder.setVisibility(View.INVISIBLE);
    }

    public void showTextViewPlaceHolder(){

        mTextViewPlaceHolder.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
}

