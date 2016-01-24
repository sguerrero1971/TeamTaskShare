package com.sagapp.teamtaskshare.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;
import com.sagapp.teamtaskshare.TaskShareProfile;

import java.util.List;

/**
 * Created by Solar Employee on 1/24/2016.
 */
public class TaskSync {

    private String taskShareSelection;
    private TaskShare taskShare;
    private TaskShareProfile taskShareProfile;

    public TaskSync(final String tasksync, final Context context){

        taskShareSelection = tasksync;
        if(tasksync == "taskShare"){

            //Toast.makeText(CheckListActivity.this, "Made it this far" + EMPTY_LIST, Toast.LENGTH_LONG).show();
            ParseQuery<TaskShare> query = TaskShare.getQuery();
            query.fromPin(TaskShareListApplication.TASKSHARE_GROUP_NAME);
            query.whereEqualTo("uploaded", false);
            query.findInBackground(new FindCallback<TaskShare>() {
                public void done(List<TaskShare> taskShares, ParseException e) {
                    if (e == null) {
                        for (final TaskShare taskShare : taskShares) {
                            // Set the uploaded flag to true before
                            // syncing to Parse
                            taskShare.setUploaded(true);
                            taskShare.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseObject.unpinAllInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME);
                                        // Let adapter know to update view
                                            Toast.makeText(context,
                                                    "Your Safety Checklist has been saved to the Server, Thank You",
                                                    Toast.LENGTH_LONG).show();
                                            return;

                                        //  taskShareListAdapter
                                        //        .notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(context,
                                                "Error syncing: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        // Reset the is status flag locally
                                        // to false
                                        taskShare.setUploaded(false);
                                    }
                                }

                            });

                        }
                    } else {
                        Log.i(tasksync,
                                "syncTaskShareToParse: Error finding pinned taskShares: "
                                        + e.getMessage());
                    }
                }
            });
        }else if(tasksync == "taskShareProfile"){

            //Toast.makeText(CheckListActivity.this, "Made it this far" + EMPTY_LIST, Toast.LENGTH_LONG).show();
            ParseQuery<TaskShareProfile> query = TaskShareProfile.getQuery();
            query.fromPin(TaskShareListApplication.TASKSHARE_PROFILE);
            query.whereEqualTo("uploaded", false);
            query.findInBackground(new FindCallback<TaskShareProfile>() {
                public void done(List<TaskShareProfile> taskShareProfileItems, ParseException e) {
                    if (e == null) {
                        for (final TaskShareProfile taskShareProfile : taskShareProfileItems) {
                            // Set the uploaded flag to true before
                            // syncing to Parse
                            taskShareProfile.setUploaded(true);
                            taskShareProfile.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseObject.unpinAllInBackground(TaskShareListApplication.TASKSHARE_PROFILE);
                                        // Let adapter know to update view
                                        Toast.makeText(context,
                                                    "Your Safety Checklist has been saved to the Server, Thank You",
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        //  taskShareListAdapter
                                        //        .notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(context,
                                                "Error syncing: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        // Reset the is status flag locally
                                        // to false
                                        taskShareProfile.setUploaded(false);
                                    }
                                }

                            });

                        }
                    } else {
                        Log.i(tasksync,
                                "syncTaskShareToParse: Error finding pinned taskShares: "
                                        + e.getMessage());
                    }
                }
            });
        }
    }


}
