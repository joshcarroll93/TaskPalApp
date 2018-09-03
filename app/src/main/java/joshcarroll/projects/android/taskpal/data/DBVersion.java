package joshcarroll.projects.android.taskpal.data;

/**
 * Created by Josh on 30/08/2018.
 */

public class DBVersion {

    int mId;
    int mVersionnumber;

    public DBVersion(int id, int versionNumber){
        this.mId = id;
        this.mVersionnumber = versionNumber;
    }

    public int getVersionNumber(){
        return mVersionnumber;
    }
}
