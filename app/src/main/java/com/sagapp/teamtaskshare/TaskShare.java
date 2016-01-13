package com.sagapp.teamtaskshare;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

@ParseClassName("TaskShare")
public class TaskShare extends ParseObject {
	
	public String getTask(){
		return getString("task");
	}
	
	public void setTask(String title) {
        put("task", title);
	}
	
	public ParseUser getAuthor() {
        return getParseUser("author");
	}
	
	public void setAuthor(ParseUser currentUser) {
        put("author", currentUser);
	}
	
	public String Status() {
        return getString("status");
	}
	
	public void setStatus(String status) {
        put("status", status);
	}

	public void setUuidString() {
	    UUID uuid = UUID.randomUUID();
	    put("uuid", uuid.toString());
	}
	
	public String getUuidString() {
        return getString("uuid");
    }

	public boolean uploaded() {
        return getBoolean("uploaded");
    }

	public void setUploaded(boolean uploaded) {
        put("uploaded", uploaded);
    }

	public void setFaultText(String faultText) {put("faultText", faultText);}


	public void setImageFile(String imageFileName, ParseFile dataFile) {
		put(imageFileName, dataFile);
	}
	
	public static ParseQuery<TaskShare> getQuery() {
        return ParseQuery.getQuery(TaskShare.class);
	}
}
