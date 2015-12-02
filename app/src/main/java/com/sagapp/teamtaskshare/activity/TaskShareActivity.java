package com.sagapp.teamtaskshare.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class TaskShareActivity extends Activity {

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
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        taskShareListAdapter = new taskShareListAdapter(this, factory);

        // Attach the query adapter to the view
        ListView taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
        taskShareListView.setAdapter(taskShareListAdapter);

        taskShareListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TaskShare taskShare = taskShareListAdapter.getItem(position);
                openEditView(taskShare);
            }
        });
    }
}