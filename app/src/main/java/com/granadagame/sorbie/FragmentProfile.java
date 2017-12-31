package com.granadagame.sorbie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import static com.granadagame.sorbie.MainActivity.birthday;
import static com.granadagame.sorbie.MainActivity.email;
import static com.granadagame.sorbie.MainActivity.gender;
import static com.granadagame.sorbie.MainActivity.location;
import static com.granadagame.sorbie.MainActivity.name;

public class FragmentProfile extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Analytics
        Tracker t = ((AnalyticsApplication) getActivity().getApplicationContext()).getDefaultTracker();
        t.setScreenName("Profil");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        //Name
        TextView navUsername = v.findViewById(R.id.profile_name);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = v.findViewById(R.id.profile_mail);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = v.findViewById(R.id.profile_pic);
        Picasso.with(getActivity()).load(MainActivity.photo).error(R.drawable.profile).placeholder(R.drawable.profile)
                .into(profilePic);
        //Age
        TextView birthtext = v.findViewById(R.id.profile_birthday);
        birthtext.setText(birthday);

        //Location
        TextView loc = v.findViewById(R.id.profile_loc);
        loc.setText(location);

        //Gender
        TextView sex = v.findViewById(R.id.profile_gender);
        sex.setText(gender);

        return v;
    }

}
