package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.sagapp.teamtaskshare.R;

/**
 * Created by Solar Employee on 12/2/2015.
 */
public class TaskShareActivity extends Activity {


    private static final int LOGIN_ACTIVITY_CODE = 100;
    private TextView titleTextView;
    private TextView emailTextView;
    private TextView nameTextView;
    private Button loginOrLogoutButton;
    public ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskshare);

        //Setup views for Profile
        titleTextView = (TextView) findViewById(R.id.profile_title);
        emailTextView = (TextView) findViewById(R.id.profile_email);
        nameTextView = (TextView) findViewById(R.id.profile_name);
        titleTextView.setText(R.string.profile_title_logged_out);

        //Button to allow user to log in
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
                    ParseLoginBuilder builder = new ParseLoginBuilder(TaskShareActivity.this);
                    startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);

                }
                openCheckListActivity();
            }
        });
    }



    private void openCheckListActivity(){
        Intent myIntent = new Intent(this, CheckListActivity.class);
        startActivityForResult(myIntent, LOGIN_ACTIVITY_CODE);
    }


 /*   @Override
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
        currentUser = ParseUser.getCurrentUser();

        if (item.getItemId() == R.id.action_sync) {
            TaskSync.syncTaskListToParse(this);
        }

        if (item.getItemId() == R.id.action_logout) {
            // Log out the current user
            showProfileLoggedOut();
            // Clear the view
            taskShareListAdapter.clear();
        }

        if (item.getItemId() == R.id.action_login) {
            showProfileLoggedIn();
        }

        return super.onOptionsItemSelected(item);
    }

*/


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
        openCheckListActivity();
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