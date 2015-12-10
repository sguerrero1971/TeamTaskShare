package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.api.CreateTaskShare;
import com.sagapp.teamtaskshare.api.network.TaskSync;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class TaskShareActivity extends Activity {

//    private static final int LOGIN_REQUEST = 0;
    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    public static ParseQueryAdapter taskShareListAdapter;

    public static LayoutInflater inflater;

    public static ListView taskShareView;

 //   private TextView loggedInInfoView;
    private TextView titleTextView;
    private TextView emailTextView;
    private TextView nameTextView;
    private Button loginOrLogoutButton;
    private ParseUser currentUser;
    private Button getListButton;
    private CreateTaskShare createTaskShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskshare);
        // Set up the views
        taskShareView = (ListView) findViewById(R.id.taskShare_list_view);
       // loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);

        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        taskShareListAdapter = new ParseQueryAdapter<>(this, (ParseQueryAdapter.QueryFactory<TaskShare>) createTaskShare);




        getListButton = (Button) findViewById(R.id.get_list_button);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTaskShare = new CreateTaskShare();

                // Attach the query adapter to the view
                ListView taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
                taskShareListView.setAdapter(taskShareListAdapter);
                taskShareListAdapter.loadObjects();


            }
        });

        // Set up the Parse query to use in the adapter
     //   ParseQueryAdapter.QueryFactory<TaskShare> factory = new ParseQueryAdapter.QueryFactory<TaskShare>() {
     //       public ParseQuery<TaskShare> create() {
      //          ParseQuery<TaskShare> query = TaskShare.getQuery();
      //          query.fromLocalDatastore();
      //          return query;
       //     }
     //   };


        //taskShareListAdapter = new ParseQueryAdapter<>(this,factory);
        //taskShareListAdapter.setTextKey("title");
        //taskShareListAdapter.setTextKey("currentUser");



        //Setup views for Profile
        titleTextView = (TextView) findViewById(R.id.profile_title);
        emailTextView = (TextView) findViewById(R.id.profile_email);
        nameTextView = (TextView) findViewById(R.id.profile_name);
        titleTextView.setText(R.string.profile_title_logged_in);

        loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
        loginOrLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    // User clicked to log out.
                    ParseUser.logOut();
                    currentUser = null;
                    showProfileLoggedOut();
                } else {
                    // User clicked to log in.
                    // This example customizes ParseLoginActivity in code.
                    ParseLoginBuilder builder = new ParseLoginBuilder(TaskShareActivity.this);
                    startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);

                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            showProfileLoggedIn();
        } else {
            showProfileLoggedOut();
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
        if (item.getItemId() == R.id.action_sync) {
            TaskSync.syncTaskListToParse(this);
        }

  //      if (item.getItemId() == R.id.action_logout) {
   //         // Log out the current user
  //          ParseUser.logOut();
  //          // Update the logged in label info
  //          ParseUser currentUser = ParseUser.getCurrentUser();
  //              loggedInInfoView.setText(getString(R.string.logged_in,
  //                      currentUser.getString("name")));
//            // Clear the view
//            taskShareListAdapter.clear();
//            // Unpin all the current objects
 //           ParseObject
 //                   .unpinAllInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME);
 //       }

 //       if (item.getItemId() == R.id.action_login) {
//            ParseLoginBuilder builder = new ParseLoginBuilder(this);
//            startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
 //       }

        return super.onOptionsItemSelected(item);
    }


 //   private class taskshareListAdapter extends ParseQueryAdapter<TaskShare> {

   //     public taskshareListAdapter(Context context,
     //                          ParseQueryAdapter.QueryFactory<TaskShare> queryFactory) {
       //     super(context, queryFactory);
        //}

  //      @Override
    //    public View getItemView(TaskShare taskShare, View view, ViewGroup parent) {
      //      ViewHolder holder;
        //    if (view == null) {
          //      view = inflater.inflate(R.layout.list_item_taskshare, parent, false);
            //    holder = new ViewHolder();
              //  holder.taskShareTitle = (TextView) view
  //                      .findViewById(R.id.taskShare_title);
  //              view.setTag(holder);
    //        } else {
      //          holder = (ViewHolder) view.getTag();
        //    }
          //  TextView taskShareTitle = holder.taskShareTitle;
  //          taskShareTitle.setText(taskShare.getTask());
    //        if (taskShare.status()) {
      //          taskShareTitle.setTypeface(null, Typeface.ITALIC);
        //    } else {
          //      taskShareTitle.setTypeface(null, Typeface.NORMAL);
  //          }
    //        return view;
      //  }
  //  }

 //   private static class ViewHolder {
     //   TextView taskShareTitle;
 //   }

    /**
     * Shows the profile of the given user.
     */
    private void showProfileLoggedIn() {
        titleTextView.setText(R.string.profile_title_logged_in);
        emailTextView.setText(currentUser.getEmail());
        String fullName = currentUser.getString("name");
        if (fullName != null) {
            nameTextView.setText(fullName);
        }
        loginOrLogoutButton.setText(R.string.profile_logout_button_label);
    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {
        titleTextView.setText(R.string.profile_title_logged_out);
        emailTextView.setText("");
        nameTextView.setText("");
        loginOrLogoutButton.setText(R.string.profile_login_button_label);
    }
}