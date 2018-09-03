package joshcarroll.projects.android.taskpal.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import joshcarroll.projects.android.taskpal.BuildConfig;
import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.adapter.SectionsPagerAdapter;
import joshcarroll.projects.android.taskpal.data.DBVersion;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;
import joshcarroll.projects.android.taskpal.fragment.AddTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.DeleteTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.EditTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.TabbedPlaceholderFragment;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;
import joshcarroll.projects.android.taskpal.service.DatabaseUpdateService;


public class MainActivity extends AppCompatActivity implements NewTaskListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String TAG = "MAIN_ACTIVITY";
    private FloatingActionButton fab;
    String s = BuildConfig.GOOGLE_PLACES_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), DatabaseUpdateService.class));



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here


            VersionDbHandler versionDbHandler = new VersionDbHandler(getApplicationContext());
            versionDbHandler.setVersionNumber(5);
            int version = versionDbHandler.getVersionNumber();
            DBHandler dbHandler = new DBHandler(this,+version);
            dbHandler.setNotification(0);
            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab =  findViewById(R.id.fab);
        fab.setImageResource(R.drawable.edit_icon_white);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTaskFragment dialogFragment = new AddTaskFragment();
                dialogFragment.setListener(MainActivity.this);
                dialogFragment.show(getFragmentManager(), "ADD_TASK_FRAGMENT");
            }
        });

        if (getIntent().getExtras() != null){

            NewTask task = getIntent().getParcelableExtra("Task");

            if(task != null){
                TabbedPlaceholderFragment.mLayoutManager.scrollToPosition(task.getId());
                Toast.makeText(getApplicationContext(), task.getTitle(), Toast.LENGTH_LONG).show();
            }

        }else{
            Log.i(TAG, "Parcelable Task is null");

        }

        if(isMyServiceRunning(DatabaseUpdateService.class)){
            Toast.makeText(getApplicationContext(), "Database Service Running", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Service running");
        }else{
            Toast.makeText(getApplicationContext(), "Database Service NOT Running", Toast.LENGTH_LONG).show();
        }
    }
    private boolean isMyServiceRunning(Class< DatabaseUpdateService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);

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
        mSectionsPagerAdapter.allTasksFragment.allTasksIsChecked();
        mSectionsPagerAdapter.allTasksFragment.showRecyclerView();

        if(newTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(newTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
            mSectionsPagerAdapter.activeTasksFragment.activeTasksIsChecked();
            mSectionsPagerAdapter.activeTasksFragment.showRecyclerView();
        }
    }

    @Override
    public void removeTask(NewTask removedTask) {
        VersionDbHandler versionDbHandler = new VersionDbHandler(getApplicationContext());
        DBHandler db = new DBHandler(getApplicationContext(), versionDbHandler.getVersionNumber());

        TabbedPlaceholderFragment.tasks.remove(removedTask);
        TabbedPlaceholderFragment.activeTasks.remove(removedTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemRemoved(removedTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();
        if(db.getEntriesCount() < 0){
            mSectionsPagerAdapter.allTasksFragment.showTextViewPlaceHolder();
        }

        if(removedTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(removedTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
            mSectionsPagerAdapter.activeTasksFragment.showTextViewPlaceHolder();

            if(db.getEntriesCount() < 0){
                mSectionsPagerAdapter.activeTasksFragment.showTextViewPlaceHolder();
            }
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
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
    }
}
