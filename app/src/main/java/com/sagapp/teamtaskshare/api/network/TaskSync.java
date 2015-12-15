package com.sagapp.teamtaskshare.api.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

import java.util.List;

/**
 * Created by SolarUser on 12/8/2015.
 */
public class TaskSync {

    public static void syncTaskListToParse(Context context) {

        // We could use saveEventually here, but we want to have some UI
        // around whether or not the draft has been saved to Parse
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected == true) {
            if (ParseUser.getCurrentUser() != null) {
                // If we have a network connection and a current logged in user,
                // sync the
                // TaskList

                // In this app, local changes overwrite content on the
                // server.

                ParseQuery<TaskShare> query = TaskShare.getQuery();
                query.fromPin(TaskShareListApplication.TASKSHARE_GROUP_NAME);
                query.whereEqualTo("uploaded", false);
                query.findInBackground(new FindCallback<TaskShare>() {
                    public void done(List<TaskShare> taskShares, ParseException e) {
                        if (e == null) {
                            for (final TaskShare taskShare : taskShares) {
                                // Set is draft flag to false before
                                // syncing to Parse
                                taskShare.setStatus(true);
                                taskShare.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            // Let adapter know to update view
                                            //if (!isFinishing()) {
                                            //  taskShareListAdapter
                                            //        .notifyDataSetChanged();

                                        }else {
                                            // Reset the is draft flag locally
                                            // to true
                                            taskShare.setStatus(false);
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("TaskShareActivity",
                                    "syncTaskShareToParse: Error finding pinned taskShares: "
                                            + e.getMessage());
                        }
                    }
                });
        } else {
            // If there is no connection, let the user know the sync didn't
            // happen
            Toast.makeText(
                    context,
                    "Your device appears to be offline. Some todos may not have been synced to Parse.",
                    Toast.LENGTH_LONG).show();
        }

    }

   /* private void loadFromParse() {
        ParseQuery<TaskShare> query = TaskShare.getQuery();
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<TaskShare>() {
            public void done(List<TaskShare> taskShares, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<TaskShare>) taskShares,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (!isFinishing()) {
                                            taskShareListAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.i("TaskShareListActivity",
                                                "Error pinning tasklists: "
                                                        + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.i("TodoListActivity",
                            "loadFromParse: Error finding pinned todos: "
                                    + e.getMessage());
                }
            }
        });
    */}
}
