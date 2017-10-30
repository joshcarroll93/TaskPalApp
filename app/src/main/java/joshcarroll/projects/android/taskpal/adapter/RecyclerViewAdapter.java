package joshcarroll.projects.android.taskpal.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.activity.MainActivity;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.fragment.DeleteTaskFragment;

/**
 * Created by Josh on 22/10/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<NewTask> mTasks;

    public RecyclerViewAdapter(Context context, List<NewTask> tasks){

        mContext = context;
        mTasks = tasks;
    }

    @Override
    public int getItemCount() {

        return mTasks.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder taskViewHolder, int i) {

        taskViewHolder.mTitle.setText(mTasks.get(i).getTitle());
        taskViewHolder.mDescription.setText(mTasks.get(i).getDescription());

        final int position = i;

        taskViewHolder.mImageSettingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.card_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener(mTasks.get(position)));
                popup.show();
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mDescription;
        private ImageView mImagePenIcon;
        private  ImageView mImageSettingIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            mImagePenIcon = (ImageView)itemView.findViewById(R.id.image_view);
            mTitle = (TextView)itemView.findViewById(R.id.id_title);
            mDescription = (TextView)itemView.findViewById(R.id.id_description);
            mImageSettingIcon = (ImageView)itemView.findViewById(R.id.card_button_menu);
        }
    }
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private NewTask mTask;

        public MyMenuItemClickListener(NewTask task) {
            mTask = task;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            MainActivity mainActivity = (MainActivity)mContext;

            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    //mainActivity.showEditTaskFragment();
                    return true;
                case R.id.action_delete:
                    mainActivity.showDeleteTaskFragment(mTask);
                    return true;
                case R.id.action_copy:
                    mainActivity.copyTaskToClipboard(mTask);
                default:
            }
            return false;
        }
    }
}

