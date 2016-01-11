package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.sagapp.teamtaskshare.R;

/**
 * Created by Solar Employee on 1/10/2016.
 */
public class TaskShareUserProfileActivity extends Activity {

    private Spinner  profileLocationSpinner;
    private RadioGroup profileRadioGroup;
    private RadioButton profileDayButton;
    private RadioButton profileNightButton;
    private Boolean shift;
    private Spinner profileEquipmentSpinner;
    private EditText profileUnitEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saveIntent = new Intent();

            }
        });

        int selectedShift = profileRadioGroup.getCheckedRadioButtonId();

        if(selectedShift == profileDayButton.getId()){
            shift = true;
        }else if(selectedShift == profileNightButton.getId()) {
            shift = false;
        }else if(selectedShift != profileDayButton.getId() && selectedShift != profileNightButton.getId()){
            Toast.makeText(TaskShareUserProfileActivity.this, "Please select either Day shift or Night shift before saving ", Toast.LENGTH_LONG).show();
            return;
        }

        Spinner locationSpinner = (Spinner)findViewById(R.id.profile_location_spinner);
        String[] locations = getResources().getStringArray(R.array.locations);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations );
        locationSpinner.setAdapter(locationAdapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("location", (String)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TaskShareUserProfileActivity.this, "Please select a location before saving ", Toast.LENGTH_LONG).show();
                return;
            }
        });

        Spinner equipmentSpinner = (Spinner)findViewById(R.id.profile_equipment_spinner);
        String[] equipment = getResources().getStringArray(R.array.equiment_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, equipment);
        equipmentSpinner.setAdapter(adapter);

        equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("equipment", (String)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TaskShareUserProfileActivity.this, "Please select a piece of equipment before saving ", Toast.LENGTH_LONG).show();
                return;
            }
        });

    }


}
