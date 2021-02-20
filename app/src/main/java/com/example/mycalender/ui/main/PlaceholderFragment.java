package com.example.mycalender.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalender.CalcEphemeris;
import com.example.mycalender.R;
import com.example.mycalender.Swlib;

import java.text.BreakIterator;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_YEAR_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    Context mContext;
    int mpageindex;
    private EphimerisAdapter mAdapter;
    public static int myear;
    static Resources resource;
    static  String[] strSolarMonths;
    public static PlaceholderFragment newInstance(int index, int year) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageViewModel = //ViewModelProviders.of(this).get(PageViewModel.class);
                     new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        int year = 2020;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            mpageindex= index;
        }
        pageViewModel.setIndex(index);
        pageViewModel.setYear(myear);
        mContext=getContext();
        resource = getResources();
        strSolarMonths= resource.getStringArray(R.array.solar_months);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
                View root = inflater.inflate(/*R.layout.fragment_tab_demo*/
                R.layout.eph_page_view
                , container, false);

      final RecyclerView recycler = root.findViewById(R.id.recylerEph);
        /////////////////recycler
       // final ArrayList<PlanetPositions> planetPositionsList = new ArrayList<>();
        // RecyclerView recycler = findViewById(R.id.recylerVW);
        mAdapter = new EphimerisAdapter(/*mContext*/ getContext());

         recycler.setAdapter(mAdapter);
         recycler.setLayoutManager( new LinearLayoutManager(mContext));

        pageViewModel.getYear().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer year) {
                myear = year;
                UpdateEphemerislist(myear,mpageindex);
            }
        });
        pageViewModel.getTab().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer page) {

                mpageindex=page;
               // textView.setText(":nyearPG = "+ myear + ":idx=" +mpageindex);
                UpdateEphemerislist(myear,mpageindex);

                Log.d("eph", "pageFragmnet-createview");

            }
        });

        return root;
    }
    void UpdateEphemerislist(int year,int month){
        int[] daysformonth={31,28,31,30,31,30,31,31,30,31,30,31};
        int maxdays = daysformonth[month-1];
        if(year%4 ==0 && month==2) maxdays +=1;
        ArrayList<PlanetPositions> planetPositionsList = new ArrayList<>();
        for(int i=0 ; i<maxdays; ++i){
            double[] dbl = GetEphemris(year,month,i+1,5.5);
            PlanetPositions planet = new PlanetPositions(dbl,i+1);
            planetPositionsList.add(planet);
        }
        mAdapter.SetEphimirisList(planetPositionsList);
    }
    public void OnYearChange( int year){
          setYear(year);
    }
    double[] GetEphemris(int year, int month, int day, double tzOff) {
        return Swlib.CalcEphimeris(year, month, day, tzOff);
    }

    public void setYear(int year) {
        myear = year;
        Log.d("eph", "frg_setyear - " + year);
        pageViewModel.setYear(year);
    }

    class PlanetPositions{
        double sun,moon,mercury,venus,mars,jupiter,saturn,rahu;
        double sunspeed, moonspeed;

        public String strWeek,strSun, strMoon, strMercury, strVenus, strMars,strJupiter,strSaturn,strRahu;
        public String strSunSpeed, strMoonSpeed;
        String[] weekday = {"Mn","Tu","We","Th","Fr","Sa","Su"};
        PlanetPositions(double[] posArray,int pos){
           strWeek = String.valueOf(pos)+"-";
           strWeek+=weekday[(int)posArray[8]];
            sun=posArray[0];
            moon = posArray[1];
            mercury = posArray[2];
            venus = posArray[3];
            mars = posArray[4];
            jupiter = posArray[5];
            saturn = posArray[6];
            rahu = posArray[7];
            sunspeed = posArray[10];
            moonspeed = posArray[11];
            strSun= FormatPlanetPos( sun);
            strMoon= FormatPlanetPos(moon);
            strMercury= FormatPlanetPos(mercury);
            strVenus= FormatPlanetPos(venus);
            strMars= FormatPlanetPos(mars);
            strJupiter= FormatPlanetPos(jupiter);
            strSaturn= FormatPlanetPos(saturn);
            strRahu= FormatPlanetPos(rahu);
            strSunSpeed= FormatPlanetPos(sunspeed);
            strMoonSpeed= FormatPlanetPos(moonspeed);
        }
        String FormatPlanetPos(double lon){
            double totalsecs = lon*3600;
            int sgn = (int)lon/30;
            int deg = (int)lon%30;
            double dmin = (lon-sgn*30-deg)*60;
            int imin = (int)dmin;
            double sec = (dmin-imin)*60;
            return   strSolarMonths[sgn]+ String.format("\n%2d:%2d",deg,imin);

        }
    }
    private class EphimerisAdapter extends RecyclerView.Adapter{
        List<PlaceholderFragment.PlanetPositions> mPlanetList;
        LayoutInflater m_Inflater;
        EphimerisAdapter(Context context){
            m_Inflater = LayoutInflater.from(context);
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view_item = m_Inflater.inflate(R.layout.ephlist_item, parent,false);
            return new PlaceholderFragment.EphimerisAdapter.EphViewHolder(view_item);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            String strPlanet = mPlanetList.get(position).strSun;
            ((EphViewHolder)holder).mTxtVw0.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strWeek;
            ((EphViewHolder)holder).mTxtWeek.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strMoon;
            ((EphViewHolder)holder).mTxtVw1.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strMercury;
            ((EphViewHolder)holder).mTxtVw2.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strVenus;
            ((EphViewHolder)holder).mTxtVw3.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strMars;
            ((EphViewHolder)holder).mTxtVw4.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strJupiter;
            ((EphViewHolder)holder).mTxtVw5.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strSaturn;
            ((EphViewHolder)holder).mTxtVw6.setText(strPlanet);
            strPlanet = mPlanetList.get(position).strRahu;
            ((EphViewHolder)holder).mTxtVw7.setText(strPlanet);


        }

        @Override
        public int getItemCount() {
            if(mPlanetList!=null)
                return mPlanetList.size();
            else return 0;
        }
        void SetEphimirisList(List<PlaceholderFragment.PlanetPositions> planetPositions){
            mPlanetList = planetPositions;
            notifyDataSetChanged();
        }

        private class EphViewHolder extends RecyclerView.ViewHolder{
            public TextView mTxtVw0;
            public TextView mTxtVw1;
            public TextView mTxtVw2;
            public TextView mTxtVw3;
            public TextView mTxtVw4;
            public TextView mTxtVw5;
            public TextView mTxtVw6;
            public TextView mTxtVw7;
            public TextView mTxtWeek;

            public EphViewHolder(@NonNull View itemView) {
                super(itemView);
               // itemView.findViewById(R.id.geoname).setVisibility(View.GONE);
                mTxtWeek = itemView.findViewById(R.id.textwk);
                mTxtVw0 = itemView.findViewById(R.id.text0);
                mTxtVw1 = itemView.findViewById(R.id.text1);
                mTxtVw2 = itemView.findViewById(R.id.text2);
                mTxtVw3 = itemView.findViewById(R.id.text3);
                mTxtVw4 = itemView.findViewById(R.id.text4);
                mTxtVw5 = itemView.findViewById(R.id.text5);
                mTxtVw6 = itemView.findViewById(R.id.text6);
                mTxtVw7 = itemView.findViewById(R.id.text7);


            }
        }
    }
}