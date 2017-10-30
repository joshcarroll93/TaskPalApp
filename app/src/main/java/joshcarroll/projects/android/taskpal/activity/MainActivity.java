package joshcarroll.projects.android.taskpal.activity;

import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.List;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.adapter.SectionsPagerAdapter;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.fragment.AddTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.DeleteTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.TabbedPlaceholderFragment;
import joshcarroll.projects.android.taskpal.fragment.ViewSingleTaskFragment;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;
import joshcarroll.projects.android.taskpal.service.LocationService;


public class MainActivity extends AppCompatActivity implements NewTaskListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<NewTask> tasks;
    private String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(getApplication(), LocationService.class));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.edit_icon_white);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTaskFragment dialogFragment = AddTaskFragment.newInstance();
                dialogFragment.setListener(MainActivity.this);
                dialogFragment.show(getFragmentManager(), "ADD_TASK_FRAGMENT");
            }
        });

        if (getIntent().getExtras() != null) {

            NewTask task = getIntent().getParcelableExtra("Task");

            if(task != null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(ViewSingleTaskFragment.newInstance(task), null);
                ft.commit();
            }

        }else{
            Log.d(TAG, "Parcelable Task is null");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar card_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void addTask(NewTask newTask) {
        //tasks.add(newTask);
//        //adding to all tasks
        TabbedPlaceholderFragment.tasks.add(newTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemInserted(newTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();

        if(newTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(newTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void removeTask(NewTask removedTask) {
        TabbedPlaceholderFragment.tasks.remove(removedTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemRemoved(removedTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();
        if(removedTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(removedTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
    }

    public void showDeleteTaskFragment(NewTask task){

        DeleteTaskFragment deleteFragment = DeleteTaskFragment.newInstance(task);
        deleteFragment.setListener(MainActivity.this);
        deleteFragment.show(getFragmentManager(), "CONFIRM_DELETE_FRAGMENT");
    }
    public void copyTaskToClipboard(NewTask task){
        String getTask = task.getTitle() + "\n" + task.getDescription();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Task to copy", getTask);
        clipboard.setPrimaryClip(clip);

    }
}
