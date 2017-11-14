package joshcarroll.projects.android.taskpal.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;


/**
 * Created by Josh on 26/10/2017.
 */

public class TabbedPlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static DBHandler dbHandler;
    public static ArrayList<NewTask> activeTasks;
    public static List<NewTask> tasks;
    public static NewTaskListener mListener;
    public RecyclerViewAdapter mListAdapter;
    public RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

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

//    public void setListener(NewTaskListener listener){
//
//        this.mListener = listener;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_placeholder, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);




        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
            allTasksIsChecked(rootView);
        }
        else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
            activeTasksIsChecked(rootView);
        }
        return rootView;
    }

    public void allTasksIsChecked(View view){
        Log.d("OUTPUT_DE", "all_tasks_clicked");

//        TextView textView = (TextView)view.findViewById(R.id.id_placeholder_text_field);
        dbHandler = new DBHandler(getContext());

        if(dbHandler.getAllTasks() != null){
            //textView.setVisibility(View.INVISIBLE);
            tasks = dbHandler.getAllTasks();

            mListAdapter = new RecyclerViewAdapter(getContext(), tasks);
            mRecyclerView.setAdapter(mListAdapter);
        }
//        else{
//            textView.setVisibility(View.VISIBLE);
//        }
    }

    public void activeTasksIsChecked(View view){
        Log.d("OUTPUT_DE", "active_tasks_clicked");

//        TextView textView = (TextView)view.findViewById(R.id.id_placeholder_text_field);

        if (dbHandler.getAllTasks() != null) {

//            textView.setVisibility(View.INVISIBLE);
            activeTasks = new ArrayList<>();

            for(int i = 0; i < tasks.size(); i++){
                if(tasks.get(i).getStatus() == 0){
                    activeTasks.add(tasks.get(i));
                }
            }
            mListAdapter = new RecyclerViewAdapter(getContext(), activeTasks);
            mRecyclerView.setAdapter(mListAdapter);
        }
//        else{
//            textView.setVisibility(View.VISIBLE);
//
//        }

//        boolean hasTasks = mListener.hasTasks(dbHandler.getAllTasks());
//        if(!hasTasks){
//            textView.setVisibility(View.VISIBLE);
//        }
//        else{
//            textView.setVisibility(View.INVISIBLE);
//            activeTasks = new ArrayList<>();
//
//            for(int i = 0; i < tasks.size(); i++){
//                if(tasks.get(i).getStatus() == 0){
//                    activeTasks.add(tasks.get(i));
//                }
//            }
//            mListAdapter = new RecyclerViewAdapter(getContext(), activeTasks);
//            mRecyclerView.setAdapter(mListAdapter);
//        }
    }

}

