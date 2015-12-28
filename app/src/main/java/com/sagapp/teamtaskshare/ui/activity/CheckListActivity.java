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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by SolarUser on 12/14/2015.
 */
public class CheckListActivity extends Activity {

    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    private TaskShare taskShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        init((ListView) findViewById(R.id.taskShare_list_view));
    }

    private void init(ListView listView){
        final TaskBaseAdapter adapter = new TaskBaseAdapter();
        listView.setAdapter(adapter);
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
                    //openEditView();
                }
            }
        });
    }

    private void openEditView() {
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

    private void submitTaskShare(){

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

     private class TaskBaseAdapter extends BaseAdapter {

         ArrayList<HashMap<String, String>> mTaskSet = new ArrayList<>();
         String[] mItems = getResources().getStringArray(R.array.tasklist);

         TaskBaseAdapter(){
             HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < mItems.length; i++) {
                map.put(mItems[i], mItems[i]);
            }
            mTaskSet.add(map);
        }


        @Override
        public int getCount() {
            return mTaskSet.size();
        }

        @Override
        public String getItem(int position) {

                return mTaskSet.get();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            mTaskSet.remove(position);
            notifyDataSetChanged();
            submitTaskShare();
            Toast.makeText(CheckListActivity.this, "Position " + position, Toast.LENGTH_LONG).show();

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

