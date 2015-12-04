package com.sagapp.teamtaskshare.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class CreateTaskShareActivity extends Activity  {

    private Button selectButton;
    private Button resumeButton;
    private TaskShare taskShare;
    private EditText taskShareText;
    private String taskShareId = null;
    private String[] mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtaskshare);

        // Create an ArrayAdapter that will contain all list items
        ArrayAdapter<String> adapter;

        mItems = getResources().getStringArray(R.array.tasklist);


        // Fetch the taskShareId from the Extra data
        if (getIntent().hasExtra("ID")) {
            taskShareId = getIntent().getExtras().getString("ID");
        }

        taskShareText = (EditText) findViewById(R.id.taskShare_text);
        selectButton = (Button) findViewById(R.id.saveButton);
        resumeButton = (Button) findViewById(R.id.deleteButton);

        if (taskShareId == null) {
            taskShare = new TaskShare();
            taskShare.setUuidString();



            for (i=0; i < mItems.length; i++) {

                taskShare.setTask(taskShareText.getText().toString());
                taskShare.setStatus(true);
                taskShare.setAuthor(ParseUser.getCurrentUser());
                taskShare.pinInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME,
                        new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (isFinishing()) {
                                    return;
                                }
                                if (e == null) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                        });

            }

        } else {
            ParseQuery<TaskShare> query = TaskShare.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", taskShareId);
            query.getFirstInBackground(new GetCallback<TaskShare>() {

                @Override
                public void done(TaskShare object, ParseException e) {
                    if (!isFinishing()) {
                        taskShare = object;
                        taskShareText.setText(taskShare.getTask());
                        resumeButton.setVisibility(View.VISIBLE);
                    }
                }

            });

        }
    }

    selectButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick (View v){

            taskShare.setTask(taskShareText.getText().toString());
            taskShare.setStatus(true);
            taskShare.setAuthor(ParseUser.getCurrentUser());
            taskShare.pinInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME,
                    new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (isFinishing()) {
                                return;
                            }
                            if (e == null) {
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        }
    }

        resumeButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // The taskShareList will be reloaded if it hasn't been sent to Parse.
                        taskShare.deleteEventually();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                });

    }
}
