package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;




import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import com.parse.ui.ParseLoginBuilder;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;
import com.sagapp.teamtaskshare.api.CreateTaskShare;
import com.sagapp.teamtaskshare.api.network.TaskSync;

import java.util.List;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class TaskShareActivity extends Activity {

    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    private ParseQueryAdapter<TaskShare> taskShareListAdapter;

    private LayoutInflater inflater;

    private ListView taskShareView;

    private TextView loggedInInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskshare);

        // Set up the views
        taskShareView = (ListView) findViewById(R.id.taskShare_list_view);
        loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<TaskShare> factory = new ParseQueryAdapter.QueryFactory<TaskShare>() {
            public ParseQuery<TaskShare> create() {
                ParseQuery<TaskShare> query = TaskShare.getQuery();
                query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        taskShareListAdapter = new taskshareListAdapter(this, factory);

        // Attach the query adapter to the view
        ListView taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
        taskShareListView.setAdapter(taskShareListAdapter);
    }

    private void updateLoggedInInfo() {
            ParseUser currentUser = ParseUser.getCurrentUser();
        if  (currentUser != null){
            loggedInInfoView.setText(getString(R.string.logged_in,
                    currentUser.getString("name")));
        } else {
            ParseLoginBuilder builder = new ParseLoginBuilder(TaskShareActivity.this);
            startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_CODE) {
                // Coming back from the edit view, update the view
                taskShareListAdapter.loadObjects();
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, require login or create account,
                // else get the current list from Parse

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskshare_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new) {
            // Make sure there's a valid user, anonymous
            // or regular
            if (ParseUser.getCurrentUser() != null) {
                startActivityForResult(new Intent(this, CreateTaskShare.class),
                        EDIT_ACTIVITY_CODE);
            }
        }

        if (item.getItemId() == R.id.action_sync) {
            TaskSync.syncTaskListToParse();
        }

        if (item.getItemId() == R.id.action_logout) {
            // Log out the current user
            ParseUser.logOut();
            // Update the logged in label info
            ParseUser currentUser = ParseUser.getCurrentUser();
                loggedInInfoView.setText(getString(R.string.logged_in,
                        currentUser.getString("name")));
            // Clear the view
            taskShareListAdapter.clear();
            // Unpin all the current objects
            ParseObject
                    .unpinAllInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME);
        }

        if (item.getItemId() == R.id.action_login) {
            ParseLoginBuilder builder = new ParseLoginBuilder(this);
            startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean realUser = (ParseUser.getCurrentUser() != null);
            menu.findItem(R.id.action_logout).setVisible(!realUser);
            menu.findItem(R.id.action_login).setVisible(realUser);
        return true;
    }

    private class taskshareListAdapter extends ParseQueryAdapter<TaskShare> {

        public taskshareListAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<TaskShare> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(TaskShare taskShare, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_taskshare, parent, false);
                holder = new ViewHolder();
                holder.taskShareTitle = (TextView) view
                        .findViewById(R.id.taskShare_title);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView taskShareTitle = holder.taskShareTitle;
            taskShareTitle.setText(taskShare.getTask());
            if (taskShare.status()) {
                taskShareTitle.setTypeface(null, Typeface.ITALIC);
            } else {
                taskShareTitle.setTypeface(null, Typeface.NORMAL);
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView taskShareTitle;
    }
}