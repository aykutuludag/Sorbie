package com.granadagame.sorbie;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeed extends Fragment {


    public FragmentFeed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feeds,container,false);

        // Analytics
        Tracker t = ((AnalyticsApplication) getActivity().getApplicationContext()).getDefaultTracker();
        t.setScreenName("Feed");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

}
