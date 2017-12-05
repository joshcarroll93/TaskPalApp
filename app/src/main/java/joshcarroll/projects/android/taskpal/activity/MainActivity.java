package joshcarroll.projects.android.taskpal.activity;

import android.Manifest;
import android.support.v7.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.adapter.SectionsPagerAdapter;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.fragment.AddTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.DeleteTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.EditTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.SettingsFragment;
import joshcarroll.projects.android.taskpal.fragment.TabbedPlaceholderFragment;
import joshcarroll.projects.android.taskpal.fragment.ViewSingleTaskFragment;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;


public class MainActivity extends AppCompatActivity implements NewTaskListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String TAG = "MAIN_ACTIVITY";
    public ActionBar actionBar;
    public Menu menu;
    FloatingActionButton fab;
    public static final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 2;
    private MenuItem menuItem;

    @Override
    public void onBackPressed() {
        actionBar.setTitle(R.string.app_name);

        fab.setVisibility(View.VISIBLE);

        if (menuItem != null) {
            menuItem.setVisible(true);
        }

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();

        DBHandler dbHandler = new DBHandler(getApplication());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            dbHandler.setNotification(0);
//            NewTask task = new NewTask(0, "Title", "Description", 0.0, 0.0, "Address", 1);
//            dbHandler.addTask(task);

            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.edit_icon_white);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTaskFragment dialogFragment = new AddTaskFragment();
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
            Log.i(TAG, "Parcelable Task is null");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        this.menu = menu;

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

            SettingsFragment settingsFragment = SettingsFragment.newInstance();

            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.setting_fragment,settingsFragment,"SETTINGS_FRAGMENT");
            transaction.addToBackStack("SettingFragment");

            transaction.commit();

            actionBar.setTitle("Settings");

            fab.setVisibility(View.INVISIBLE);

            menuItem = item;
            menuItem.setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void addTask(NewTask newTask) {

        if(TabbedPlaceholderFragment.tasks != null)
            TabbedPlaceholderFragment.tasks.add(newTask);

        if(TabbedPlaceholderFragment.activeTasks!= null)
            TabbedPlaceholderFragment.activeTasks.add(newTask);

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
        TabbedPlaceholderFragment.activeTasks.remove(removedTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemRemoved(removedTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();

        if(removedTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(removedTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void editTask(NewTask task){
        TabbedPlaceholderFragment.tasks.remove(task);
        TabbedPlaceholderFragment.activeTasks.remove(task);

        mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemRemoved(task.getId());
        mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();

        TabbedPlaceholderFragment.tasks.add(task);
        TabbedPlaceholderFragment.activeTasks.add(task);

        if(task.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(task.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
    }
    public void showEditTaskFragment(NewTask task){

        EditTaskFragment editTaskFragment = EditTaskFragment.newInstance();
        editTaskFragment.setListener(MainActivity.this);

        Bundle bundle = new Bundle();
        bundle.putParcelable("TASK_TO_EDIT", task);
        editTaskFragment.setArguments(bundle);
        editTaskFragment.show(getFragmentManager(), "EDIT_TASK_FRAGMENT");
    }
    public void showDeleteTaskFragment(NewTask task){

        DeleteTaskFragment deleteFragment = DeleteTaskFragment.newInstance(task);
        deleteFragment.setListener(MainActivity.this);
        deleteFragment.show(getFragmentManager(), "DELETE_TASK_FRAGMENT");
    }
    public void copyTaskToClipboard(NewTask task){
        String getTask = task.getTitle() + "\n" + task.getDescription();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Task to copy", getTask);
        clipboard.setPrimaryClip(clip);
    }


}
