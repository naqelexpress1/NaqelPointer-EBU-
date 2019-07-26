package com.naqelexpress.naqelpointer.Activity.routeMap;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hasna on 7/22/18.
 */
//transient
public class Places implements Parcelable {
    public int position;
    public LatLng latlng;

    public Places(int position, LatLng latlng,String test) {
        this.position = position;
        this.latlng = latlng;
    }

    protected Places(Parcel in) {
        position = in.readInt();
        latlng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public Places() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(position);
        parcel.writeParcelable(latlng, i);
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}