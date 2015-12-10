package com.sagapp.teamtaskshare.api;

import android.content.Context;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

import java.util.List;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class CreateTaskShare {

    //setup scope of DataStore
    private TaskShare taskShare;
    //Set ParseQueryAdapter to list tasklist items
    public TaskShare taskShares;
    //Retrieve the list of Task from the tasklist resource file
    private String[] mItems;




    public void CreateTaskShare(final Context context) {

        mItems = context.getResources().getStringArray(R.array.tasklist);

        //Determine if there is a local DataStore if not then create it and populate it with tasks
        ParseQuery<TaskShare> query = ParseQuery.getQuery("TaskShare");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<TaskShare>() {
            @Override
            public void done(List<TaskShare> tasks, ParseException e) {
                if (e == null) {

                    return;

                } else {
                    for (int i = 0; i < mItems.length; i++) {

                        taskShare = new TaskShare();
                        taskShare.setUuidString();
                        taskShare.setTask(mItems[i]);
                        taskShare.setStatus(true);
                        taskShare.setAuthor(ParseUser.getCurrentUser());
                        taskShare.pinInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME,
                                new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Toast.makeText(context,
                                                    "Error saving: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                           // return;
                                        } else {
                                            Toast.makeText(context,
                                                    "Error saving: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                    }
                }
            }
        });
    }
}

