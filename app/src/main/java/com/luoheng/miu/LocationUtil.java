package com.luoheng.miu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.List;

public class LocationUtil {
    private static Activity mContext;
    private static LocationUtil mInstance;
    private static LocationManager mLocationManager;
    private static Location mLocation;
    private static String mProvider;
    static boolean hasProvider;
    private static LocationListener listener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation=location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if(provider.equals(LocationManager.NETWORK_PROVIDER)){
                hasProvider=true;
                if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                        ==PackageManager.PERMISSION_GRANTED) {
                    mLocation=mLocationManager.getLastKnownLocation(provider);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(provider.equals(LocationManager.NETWORK_PROVIDER)){
                hasProvider=false;
            }
        }
    };
    private LocationUtil(Activity context){
        mContext=context;
    }
    public static LocationUtil getInstance(Activity context){
        if (mInstance == null){
            synchronized (LocationUtil.class){
                if (mInstance == null){
                    mInstance = new LocationUtil(context);
                    mContext = context;

                }
            }
            initLocation();
        }
        return mInstance;

    }

    public static Location getLocation() {
        return mLocation;
    }

    public static void initLocation(){
        mLocationManager =(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList= mLocationManager.getProviders(true);
        if(providerList.contains(LocationManager.NETWORK_PROVIDER))
            mProvider =LocationManager.NETWORK_PROVIDER;
        else {
            hasProvider=false;
            return;
        }
        if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            mLocation = mLocationManager.getLastKnownLocation(mProvider);
            mLocationManager.requestLocationUpdates(mProvider, 1000 * 10, 100,listener);
        }
    }
    public static void remove(){
        if(mLocationManager!=null)
            mLocationManager.removeUpdates(listener);
    }

}
