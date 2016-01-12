package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    private boolean profileCompleted = false;
    private String email;
    private String fullName;
    private Button loginOrLogoutButton;
    public ParseUser currentUser = ParseUser.getCurrentUser();


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
                if (currentUser == null) {
                    // User clicked to log in.
                    ParseLoginBuilder builder = new ParseLoginBuilder(TaskShareActivity.this);
                    startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
                } else {
                    // User clicked to log out.
                    ParseUser.logOut();
                    currentUser = null;
                    showProfileLoggedOut();
                }
            }
        });

        profileCompleted = currentUser.getBoolean("profileCompleted");

        if (currentUser != null && profileCompleted != false){
            Intent myIntent = new Intent(this, CheckListActivity.class);
            startActivity(myIntent);
        } else if(profileCompleted != true) {
            fillInProfile();
        }else {
            Intent myIntent = new Intent(this, CheckListActivity.class);
            startActivity(myIntent);
        }
    }

  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {

                    Intent myIntent = new Intent(this, CheckListActivity.class);
                    startActivity(myIntent);
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

        switch(item.getItemId()) {
            case R.id.menu_item1:
                Intent intent = new Intent(this, TaskShareUserProfileActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_item2:
                // another startActivity, this is for item with id "menu_item2"
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void fillInProfile() {
        email = currentUser.getEmail();
        fullName = currentUser.getString("name");
        Intent profileIntent = new Intent(this, TaskShareUserProfileActivity.class);
        profileIntent.putExtra("fullName", fullName);
        profileIntent.putExtra("email", email);
        startActivity(profileIntent);
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