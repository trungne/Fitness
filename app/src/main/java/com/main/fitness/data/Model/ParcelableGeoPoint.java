package com.main.fitness.data.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class ParcelableGeoPoint implements Parcelable {

    private GeoPoint geoPoint;

    public ParcelableGeoPoint(GeoPoint point) {
        geoPoint = point;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt((int) geoPoint.getLatitude());
        parcel.writeInt((int) geoPoint.getLongitude());
    }

    public static final Parcelable.Creator<ParcelableGeoPoint> CREATOR
            = new Parcelable.Creator<ParcelableGeoPoint>() {
        public ParcelableGeoPoint createFromParcel(Parcel in) {
            return new ParcelableGeoPoint(in);
        }

        public ParcelableGeoPoint[] newArray(int size) {
            return new ParcelableGeoPoint[size];
        }
    };

    private ParcelableGeoPoint(Parcel in) {
        int lat = in.readInt();
        int lon = in.readInt();
        geoPoint = new GeoPoint(lat, lon);
    }
}
