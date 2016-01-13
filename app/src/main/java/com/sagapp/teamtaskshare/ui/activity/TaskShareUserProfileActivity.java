package com.sagapp.teamtaskshare.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;

/**
 * Created by Solar Employee on 1/10/2016.
 */
public class TaskShareUserProfileActivity extends AppCompatActivity {

    private Spinner  profileLocationSpinner;
    private RadioGroup profileRadioGroup;
    private RadioButton profileDayButton;
    private RadioButton profileNightButton;
    private String mmsName;
    private String mobileNumber = null;
    private String shift = null;
    private String location = null;
    private String equipment = null;
    private String profileUnitNumber;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private Spinner profileEquipmentSpinner;
    private EditText profileUnitNumberText;
    private Button contactButton;
    private Button saveButton;
    private TextView fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        saveButton = (Button) findViewById(R.id.profile_save);
        contactButton = (Button) findViewById(R.id.profile_contact);
        profileRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_profile);
        profileDayButton = (RadioButton) findViewById(R.id.profile_day_shift);
        profileNightButton = (RadioButton) findViewById(R.id.profile_night_shift);
        fullName = (TextView) findViewById(R.id.profile_fullName);
        fullName.setText(currentUser.getString("name"));
        profileUnitNumberText = (EditText) findViewById(R.id.profile_unit_number);

        if (currentUser.getBoolean("profileCompleted") == true){
            shift = currentUser.getString("shift");
            location = currentUser.getString("location");
            equipment = currentUser.getString("equipment");
            profileUnitNumber = currentUser.getString("unitNumber");
        }

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileUnitNumber = profileUnitNumberText.getText().toString();

                int selectedShift = profileRadioGroup.getCheckedRadioButtonId();

                if(selectedShift == profileDayButton.getId()){
                    shift = "day";
                }else if(selectedShift == profileNightButton.getId()) {
                    shift = "night";
                }else if(selectedShift != profileDayButton.getId() && selectedShift != profileNightButton.getId()){
                    Toast.makeText(TaskShareUserProfileActivity.this, "Please select either Day shift or Night shift before saving ", Toast.LENGTH_LONG).show();
                    return;
                }
                //Toast.makeText(TaskShareUserProfileActivity.this,shift + location + equipment + profileUnitNumber + mobileNumber, Toast.LENGTH_LONG).show();
                if(shift != null && location != null && equipment != null && profileUnitNumber != null && mobileNumber != null) {

                    currentUser.put("shift", shift);
                    currentUser.put("location", location);
                    currentUser.put("equipment", equipment);
                    currentUser.put("unitNumber", profileUnitNumber);
                    currentUser.put("mmsName", mmsName);
                    currentUser.put("mmsNumber", mobileNumber);
                    currentUser.put("profileCompleted", true);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(TaskShareUserProfileActivity.this, CheckListActivity.class);
                                startActivity(intent);
                            } else {
                                //log error
                            }
                        }
                    });
                }

            }
        });

        profileLocationSpinner = (Spinner)findViewById(R.id.profile_location_spinner);
        String[] locations = getResources().getStringArray(R.array.locations);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        profileLocationSpinner.setAdapter(locationAdapter);

        profileLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TaskShareUserProfileActivity.this, "Please select a location before saving ", Toast.LENGTH_LONG).show();
                return;
            }
        });

        profileEquipmentSpinner = (Spinner)findViewById(R.id.profile_equipment_spinner);
        String[] equipment_types = getResources().getStringArray(R.array.equipment_types);
        ArrayAdapter<String> equipment_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, equipment_types);
        profileEquipmentSpinner.setAdapter(equipment_adapter);

        profileEquipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipment = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TaskShareUserProfileActivity.this, "Please select a piece of equipment before saving ", Toast.LENGTH_LONG).show();
                return;
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.Contacts.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        mmsName = c.getString(0);
                        mobileNumber = c.getString(1);
                        int type = c.getInt(2);

                        showSelectedNumber(mmsName, type, mobileNumber);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public void showSelectedNumber(String mmsName, int type, String mobileNumber) {
        //Toast.makeText(this, "You've Selected to send MMS to " + mmsName + type + ": " + mobileNumber, Toast.LENGTH_LONG).show();
    }

}
