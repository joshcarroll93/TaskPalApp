package joshcarroll.projects.android.taskpal.data;

import joshcarroll.projects.android.taskpal.fragment.SettingsFragment;

/**
 * Created by Josh on 10/11/2017.
 */

public class Setting {

    private int setingId;
    private int notification;

    public Setting(int id, int notification){

        this.setingId = id;
        this.notification = notification;
    }

    public void setSettingId(int id){
        this.setingId = id;
    }
    public int getSettingId(){
        return setingId;
    }
    public void setNotification(int notification){
        this.notification = notification;
    }
    public int getNotification(){
        return notification;
    }
}
