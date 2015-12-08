package com.sagapp.teamtaskshare.api;

import android.app.Activity;
import android.content.res.Resources;
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
public class CreateTaskShare {

    public static interface Callback<Result> {
        public void onSuccess(Result result);
        public void onError(String errorMessage);
    }

    Resources res = getResources();

    private TaskShare taskShare;
    private String taskShareId = null;
    //Retrieve the list of Task from the tasklist resource file
    private String[] mItems = res.getStringArray (R.array.tasklist);

    public CreateTaskShare() {


        // Fetch the taskShareId from the Extra data
        if (getIntent().hasExtra("ID")) {
            taskShareId = getIntent().getExtras().getString("ID");
        }

        if (taskShareId == null) {
            for(int i=0; i < mItems.length; i++) {

                taskShare = new TaskShare();
                taskShare.setUuidString();
                taskShare.setTask(mItems[i]);
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
                    }
                }

            });

        }
    }
}
