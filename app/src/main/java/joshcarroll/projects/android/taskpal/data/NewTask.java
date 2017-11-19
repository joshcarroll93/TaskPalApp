package joshcarroll.projects.android.taskpal.data;

import android.os.Parcel;
import android.os.Parcelable;



public class NewTask implements Parcelable{
    private int id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private int status;

    public NewTask(int id, String title, String description, double latitude, double longitude, String address, int status){
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.status = status;
    }

    public NewTask(Parcel parcel){
        id = parcel.readInt();
        title = parcel.readString();
        description = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        address = parcel.readString();
        status = parcel.readInt();
    }

    public static final Parcelable.Creator<NewTask> CREATOR = new Parcelable.Creator<NewTask>(){

        @Override
        public NewTask createFromParcel(Parcel source) {
            return new NewTask(source);
        }

        @Override
        public NewTask[] newArray(int size) {

            return new NewTask[size];
        }
    };
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude(){return latitude;}

    public void setLatitude(double latitude){this.latitude = latitude;}

    public double getLongitude(){return longitude;}

    public void setLongitude(double longitude){this.longitude = longitude;}

    public String getAddress(){return address;}

    public void setAddress(String address){this.address = address;}

    public int getStatus(){return status;}

    public void setStatus(int status){this.status = status;}

    @Override
    public String toString() {
        return getId() + " " + getTitle() + "\n" + getDescription() + "\n" + getLatitude() + "\n" + getLongitude();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(status);
    }
}
