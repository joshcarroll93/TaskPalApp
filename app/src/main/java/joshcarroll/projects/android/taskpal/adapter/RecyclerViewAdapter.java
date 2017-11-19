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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.activity.MainActivity;
import joshcarroll.projects.android.taskpal.data.NewTask;


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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder taskViewHolder, int i) {

        final int position = i;

        taskViewHolder.mTitle.setText(mTasks.get(i).getTitle());
        taskViewHolder.mDescription.setText(mTasks.get(i).getDescription());
        taskViewHolder.mAddress.setText(mTasks.get(i).getAddress());
        taskViewHolder.mMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, view);
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
        private TextView mAddress;
        private ImageView mImagePenIcon;
        private ImageView mImageSettingIcon;
        private ImageView mUpDownIcon;
        private RelativeLayout mExpandedCardLayout;
        private RelativeLayout mMenuLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            mImagePenIcon = (ImageView)itemView.findViewById(R.id.image_view);
            mTitle = (TextView)itemView.findViewById(R.id.id_title);
            mDescription = (TextView)itemView.findViewById(R.id.id_description);
//            mImageSettingIcon = (ImageView)itemView.findViewById(R.id.card_button_menu);
            mAddress = (TextView)itemView.findViewById(R.id.id_address);
            mUpDownIcon = (ImageView)itemView.findViewById(R.id.id_more_less_button);
            mExpandedCardLayout =(RelativeLayout) itemView.findViewById(R.id.expanding_card_layout);
            mMenuLayout = (RelativeLayout)itemView.findViewById(R.id.id_menu_layout);
            mExpandedCardLayout.setVisibility(View.GONE);
            //onClick to expand card view and show address field
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mExpandedCardLayout.getVisibility() == View.GONE){

                        mExpandedCardLayout.setVisibility(View.VISIBLE);
                        mUpDownIcon.setImageResource(R.drawable.up_arrow);
                    }
                    else{
                        mExpandedCardLayout.setVisibility(View.GONE);
                        mUpDownIcon.setImageResource(R.drawable.down_arrow);
                    }
                }
            });
        }
    }
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private NewTask mTask;

        private MyMenuItemClickListener(NewTask task) {
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
                    return true;
                default:
            }
            return false;
        }
    }
}

