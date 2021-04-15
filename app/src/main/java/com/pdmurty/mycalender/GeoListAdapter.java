package com.pdmurty.mycalender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoListAdapter extends RecyclerView.Adapter {
    List<GeoDao.GeoLoc> mGeonamesList;
    LayoutInflater m_Inflater;
    GeoDao.GeoLoc selectedGeo;
    Activity parent;
    private String mCountryName;
    GeoListAdapter(Context context){

        m_Inflater = LayoutInflater.from(context);
        parent = (Activity) context;

    }
    public void SetCountryName(String countryname){
        mCountryName = countryname;
    }
    @NonNull
    @Override
    public GeonameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view_item = m_Inflater.inflate(R.layout.geolist_item, parent,false);

       return new GeonameHolder(view_item,this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((GeonameHolder)holder).mTxtVw.setText( mGeonamesList.get(position).name);


          
    }

    public void SetGeoList(List<GeoDao.GeoLoc> geolist){
        mGeonamesList = geolist;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(mGeonamesList!=null) {
            int count = mGeonamesList.size();
        return count ;
        }
        else return 0;

    }

    class GeonameHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        TextView mTxtVw;

        View mview;
        //GridView mgview;
        GeoListAdapter mAdapter;
        public GeonameHolder(@NonNull View itemView, GeoListAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            mTxtVw = itemView.findViewById(R.id.geoname);
            //itemView.findViewById(R.id.rcView).setVisibility(View.GONE);
            mview = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            selectedGeo = mGeonamesList.get(pos);
            Intent intent = new Intent();
            intent.putExtra("GEONAME",selectedGeo.name +","+mCountryName);
            intent.putExtra("LONG", selectedGeo.lon);
            intent.putExtra("LAT",selectedGeo.lat);
            intent.putExtra("TZONE", selectedGeo.offset);
            SharedPreferences.Editor edit=
                    PreferenceManager.getDefaultSharedPreferences(parent).edit();

            edit.putString("KEY_LOCNAME", selectedGeo.name +","+mCountryName);
            edit.putFloat("KEY_LON", (float)selectedGeo.lon);
            edit.putFloat("KEY_LAT", (float)selectedGeo.lat);
            edit.putInt("KEY_TZONE",(int)(selectedGeo.offset*3600000) );
            edit.putInt("LOC_PREF",2);
            edit.commit();

            parent.setResult(Activity.RESULT_OK);
            parent.finish();

        }

    }
}
