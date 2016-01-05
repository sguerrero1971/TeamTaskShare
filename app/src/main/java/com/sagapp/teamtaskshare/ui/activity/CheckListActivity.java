package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by SolarUser on 12/14/2015.
 */
public class CheckListActivity extends Activity {

    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;
    private static int EMPTY_LIST = 0;
    private TaskShare taskShare;
    private String mItem;
    private String mEdit;
    private String imageUri;
    private boolean status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        init((ListView) findViewById(R.id.taskShare_list_view));
    }

    private void init(ListView listView){
        final TaskBaseAdapter adapter = new TaskBaseAdapter();
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyText));
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Toast.makeText(CheckListActivity.this, "Position " + position, Toast.LENGTH_LONG).show();
                    openEditView();
                }
            }
        });
    }

    private void openEditView() {
        Intent editIntent = new Intent(this,TaskShareEditActivity.class);
        editIntent.putExtra("mItem", mItem );
        startActivityForResult(editIntent, EDIT_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_CODE) {
                // Coming back from the edit view, update the view
                Intent intent = getIntent();
                mEdit = intent.getStringExtra(String.valueOf(TaskShareEditActivity.mEdit));
                status = Boolean.parseBoolean(intent.getStringExtra("status"));
                imageUri = intent.getStringExtra("imageUri");
               // taskShareListAdapter.loadObjects();
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, create local datastore


               // for (String mItem : mItems) {



               // }
            }

        }
    }

    private void submitTaskShare(String mItem){
        if(EMPTY_LIST == 0) {
            taskShare = new TaskShare();
            taskShare.setUploaded(false);
            taskShare.setFaultText(mEdit);
            taskShare.setUuidString();
            taskShare.setTask(mItem);
            taskShare.setStatus(status);
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
                                // finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        }else {
            ParseQuery<TaskShare> query = TaskShare.getQuery();
            query.fromPin(TaskShareListApplication.TASKSHARE_GROUP_NAME);
            query.whereEqualTo("uploaded", false);
            query.findInBackground(new FindCallback<TaskShare>() {
                public void done(List<TaskShare> taskShares, ParseException e) {
                    if (e == null) {
                        for (final TaskShare taskShare : taskShares) {
                            // Set the status flag to true before
                            // syncing to Parse
                            taskShare.setStatus(true);
                            taskShare.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseObject.unpinAllInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME);
                                        // Let adapter know to update view
                                        //if (!isFinishing()) {
                                        //  taskShareListAdapter
                                        //        .notifyDataSetChanged();

                                    }else {
                                        // Reset the is status flag locally
                                        // to false
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

        }
    }

     private class TaskBaseAdapter extends BaseAdapter {

         String[] mItems = getResources().getStringArray(R.array.tasklist);
         List<String> mTaskSet = new ArrayList<>(Arrays.asList(mItems));


         TaskBaseAdapter(){
         }

        @Override
        public int getCount() {
            if(mTaskSet.size()==0){
                EMPTY_LIST = 1;
            }
            return mTaskSet.size();
        }

        @Override
        public String getItem(int position) {
                return mTaskSet.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            String mItem = mTaskSet.get(position);
            submitTaskShare(mItem);
            mTaskSet.remove(position);
            notifyDataSetChanged();

        }

        private class ViewHolder {
            TextView taskShareTitle;
            ViewHolder(View view) {
                taskShareTitle = ((TextView) view.findViewById(R.id.txt_data));
                view.setTag(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_list_view, parent, false))
                    : (ViewHolder) convertView.getTag();

            viewHolder.taskShareTitle.setText(mTaskSet.get(position));
            return convertView;
        }
    }
}

