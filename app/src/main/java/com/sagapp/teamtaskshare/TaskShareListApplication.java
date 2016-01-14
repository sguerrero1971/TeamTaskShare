package com.sagapp.teamtaskshare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class TaskShareListApplication extends Application {
	
	public static final String TASKSHARE_GROUP_NAME = "ALL_TASKSHARES";
    public static final String TASKSHARE_PROFILE = "TASKSHARE_PROFILE";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// add TaskShare subclass
		ParseObject.registerSubclass(TaskShare.class);
        ParseObject.registerSubclass(TaskShareProfile.class);
		
		// enable the Local Datastore
		Parse.enableLocalDatastore(getApplicationContext());
		String app_id = getString(R.string.parse_app_id);
		String client_key = getString(R.string.parse_client_key);
		Parse.initialize(this,app_id, client_key);
		//ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);	
	}
}
