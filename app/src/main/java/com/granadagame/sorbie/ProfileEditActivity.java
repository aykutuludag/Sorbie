package com.granadagame.sorbie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    String UPDATE_USER_INFO = "http://granadagame.com/Sorbie/update_user_info.php";

    Toolbar toolbar;
    Window window;
    EditText editName, editMail, editLocation, editBirthday, editTextJob;
    RadioGroup editGender;
    RadioButton bMale, bFemale, bOther;
    SharedPreferences prefs;
    String name, email, photo, gender, birthday, location, username, job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.sorbie);

        //Window
        window = this.getWindow();
        coloredBars(Color.parseColor("#626262"), Color.parseColor("#ffffff"));

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "-");
        email = prefs.getString("Email", "-");
        photo = prefs.getString("ProfilePhoto", "http://granadagame.com/Sorbie/profile.png");
        gender = prefs.getString("Gender", "-");
        birthday = prefs.getString("Birthday", "-");
        location = prefs.getString("Location", "-");
        username = prefs.getString("UserName", "-");
        job = prefs.getString("Job", "-");

        editName = findViewById(R.id.editEventName);
        editMail = findViewById(R.id.editTextDesc);
        editLocation = findViewById(R.id.editTextLocation);
        editBirthday = findViewById(R.id.editTextBirthday);
        editGender = findViewById(R.id.radioGroupGender);
        bMale = findViewById(R.id.genderMale);
        bFemale = findViewById(R.id.genderFemale);
        bOther = findViewById(R.id.genderOther);
        editTextJob = findViewById(R.id.editTextJob);

        // Setting name
        editName.setText(name);

        // Setting email
        editMail.setText(email);

        // Setting photo
        ImageView profilePic = findViewById(R.id.event_photo);
        Picasso.with(this).load(photo).error(R.drawable.profile).placeholder(R.drawable.profile)
                .into(profilePic);

        //  Setting location and retrieving changes
        editLocation.setText(location);
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(ProfileEditActivity.this);
                    startActivityForResult(intent, 1320);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        //  Setting birthday and retrieving changes
        editBirthday.setText(birthday);
        editBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int calendarYear = 1999;
                int calendarMonth = 11;
                int calendarDay = 31;

                if (!birthday.contains("-")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY", Locale.GERMAN);
                        Date birthDateasDate = sdf.parse(birthday);
                        calendarDay = Calendar.DAY_OF_MONTH;
                        calendarMonth = Calendar.MONTH;
                        calendarYear = Calendar.YEAR;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                DatePickerDialog datePicker = new DatePickerDialog(ProfileEditActivity.this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birthday = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        editBirthday.setText(birthday);
                        prefs.edit().putString("Birthday", birthday).apply();
                    }
                }, calendarYear, calendarMonth, calendarDay);

                datePicker.setTitle("Bir tarih seçin");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePicker);
                datePicker.show();

                SimpleDateFormat dateForm = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date convertedDate = dateForm.parse(birthday);
                    System.out.println(convertedDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        //  Set gender and retrieve changes
        if (gender.equals("Male")) {
            bMale.setChecked(true);
        } else if (gender.equals("Female")) {
            bFemale.setChecked(true);
        } else {
            bOther.setChecked(true);
        }
        editGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (checkedId == R.id.genderMale) {
                    gender = "Erkek";
                    prefs.edit().putString("Gender", gender).apply();
                } else if (checkedId == R.id.genderFemale) {
                    gender = "Kadın";
                    prefs.edit().putString("Gender", gender).apply();
                } else {
                    gender = "Diğer";
                    prefs.edit().putString("Gender", gender).apply();
                }
            }
        });

        editTextJob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                job = editable.toString();
                updateUserInfo();
            }
        });
    }

    private void updateUserInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_USER_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Showing toast
                        Toast.makeText(ProfileEditActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("username", username);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileEditActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void coloredBars(int color1, int color2) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color1);
            toolbar.setBackgroundColor(color2);
        } else {
            toolbar.setBackgroundColor(color2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1320) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                location = place.getName().toString();
                editLocation.setText(location);
                prefs.edit().putString("Location", location).apply();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("Error", status.getStatusMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

