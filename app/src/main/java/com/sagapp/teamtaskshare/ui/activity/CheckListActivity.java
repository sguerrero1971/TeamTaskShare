package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;

import java.util.Arrays;
import java.util.List;


/**
 * Created by SolarUser on 12/14/2015.
 */
public class CheckListActivity extends Activity {

    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    //private ArrayAdapter<String> taskShareListAdapter;
   // private LayoutInflater inflater;
    //private ListView taskShareListView;
    public static String[] mItems;
    //private TaskShare taskShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        init((ListView) findViewById(R.id.taskShare_list_view));
       // taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
/*
        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<TaskShare> factory = new ParseQueryAdapter.QueryFactory<TaskShare>() {
            public ParseQuery<TaskShare> create() {
                ParseQuery<TaskShare> query = TaskShare.getQuery();
                query.whereEqualTo("uploaded", false);
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
*/
    }

    private void init(ListView taskShareListView){
        final TaskBaseAdapter taskShareListAdapter = new TaskBaseAdapter();
        taskShareListView.setAdapter(taskShareListAdapter);
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(taskShareListView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                taskShareListAdapter.remove(position);

                            }
                        });
        taskShareListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        taskShareListView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        taskShareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Toast.makeText(CheckListActivity.this, "Position " + position, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openEditView(TaskShare taskShare) {
        Intent editIntent = new Intent(this,TaskShareEditActivity.class);
        editIntent.putExtra("ID", taskShare.getUuidString());
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
               // taskShareListAdapter.loadObjects();
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, create local datastore


               // for (String mItem : mItems) {



               // }
            }

        }
    }

/*    public SetTaskShare() {

        taskShare = new TaskShare();
        taskShare.setUploaded(false);
        taskShare.setUuidString();
        taskShare.setTask(mItem);
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
*/
    static class TaskBaseAdapter extends BaseAdapter {

        mItems = getResources().getStringArray(R.array.tasklist);

        private final List<String[]> mTaskSet = ArraysList<mItems[]>();

        @Override
        public int getCount() {
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

