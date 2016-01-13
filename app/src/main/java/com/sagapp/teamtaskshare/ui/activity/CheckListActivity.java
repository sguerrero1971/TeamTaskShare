package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by SolarUser on 12/14/2015.
 */
public class CheckListActivity extends AppCompatActivity {

    private static final int EDIT_ACTIVITY_CODE = 200;
    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static int EMPTY_LIST = 0;
    private int currentPosition;
    private TaskShare taskShare;
    private String currentItem;
    private String mEdit;
    private String imageFileName;
    private String imageUri;
    private String status = "working";
    private TaskBaseAdapter adapter;
    Bitmap bmp;
    ParseFile pFile = null ;
    TextView empty;
    private ParseUser currentUser = ParseUser.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        init((ListView) findViewById(R.id.taskShare_list_view));
        empty = (Button) findViewById(R.id.emptyText);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMPTY_LIST = 1;
                submitTaskShare("completed");
                finish();
            }
        });

        if(currentUser == null){
            Intent loginintent = new Intent(this, TaskShareActivity.class);
            startActivityForResult(loginintent, LOGIN_ACTIVITY_CODE);
        }
    }

    private void init(ListView listView){
        adapter = new TaskBaseAdapter();
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
                    currentItem = adapter.getItem(position);
                    currentPosition = position;
                    openEditView();
                }
            }
        });
    }

    private void openEditView() {
        Intent editIntent = new Intent(this,TaskShareEditActivity.class);
        editIntent.putExtra("currentItem", currentItem);
        editIntent.putExtra("position", currentPosition);
        startActivityForResult(editIntent, EDIT_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ACTIVITY_CODE && resultCode == RESULT_OK && data != null) {
                // Coming back from the edit view, process edited item and update view
            currentItem = data.getStringExtra("currentItem");
            mEdit = data.getStringExtra("mEdit");
            status = data.getStringExtra("status");
            imageFileName = data.getStringExtra("imageFileName");
            imageUri = data.getStringExtra("imageUri");
            if (imageUri != null){
                try{
                    Uri imagePath = Uri.parse(imageUri);
                    bmp = MediaStore.Images.Media.getBitmap( this.getContentResolver(), imagePath);
            } catch (FileNotFoundException e){
            // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                pFile = new ParseFile(imageFileName, stream.toByteArray());
                try {
                    pFile.save();
                    Toast.makeText(CheckListActivity.this, "image Saved", Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    Toast.makeText(CheckListActivity.this, "Error Saving image", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            currentPosition = data.getIntExtra("position", currentPosition);
            //Toast.makeText(CheckListActivity.this, currentItem + mEdit + status + imageUri + currentPosition, Toast.LENGTH_LONG).show();
            adapter.remove(currentPosition);
            }else if (requestCode == LOGIN_ACTIVITY_CODE && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
        }

    private void submitTaskShare(String mItem){
        //Toast.makeText(CheckListActivity.this, "This is Empty list" + EMPTY_LIST, Toast.LENGTH_LONG).show();
        if(EMPTY_LIST == 0) {
            taskShare = new TaskShare();
            taskShare.setUploaded(false);
            if (mEdit != null){
                taskShare.setFaultText(mEdit);
            }
            if (imageFileName != null) {
                taskShare.setImageFile(imageFileName, pFile);
                imageFileName = null;
            }
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
                                        if (!isFinishing()) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Your Safety Checklist has been saved to the Server, Thank You",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        //  taskShareListAdapter
                                        //        .notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(getApplicationContext(),
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
 //           if(mTaskSet.size() <= 0){
 //               EMPTY_LIST = 1;
 //           }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskshare_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_item1:
                Intent intent = new Intent(this, TaskShareUserProfileActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}

