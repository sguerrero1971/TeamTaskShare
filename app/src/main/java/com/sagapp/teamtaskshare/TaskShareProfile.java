package com.sagapp.teamtaskshare;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

/**
 * Created by Solar Employee on 1/13/2016.
 */

@ParseClassName("TaskShareProfile")
public class TaskShareProfile extends ParseObject{

    public String getShift() {
        return getString("shift");
    }

    public void setShift(String shift) {
        put("shift", shift);
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String location) {
        put("location", location);
    }

    public String getEquipment() {
        return getString("equipment");
    }

    public void setEquipment(String equipment) {
        put("equipment", equipment);
    }

    public String getunitNumber() {
        return getString("unitNumber");
    }

    public void setunitNumber(String unitNumber) {
        put("unitNumber", unitNumber);
    }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }

    public String getUuidString() {
        return getString("uuid");
    }

    public void setMmsName (String mmsName) {
        put("mmsName", mmsName);
    }

    public String getMmsName() {
        return getString("mmsName");
    }

    public void setMmsNumber (String mmsNumber) {
        put("mmsNumber", mmsNumber);
    }

    public String getMmsNumber() {
        return getString("mmsNumber");
    }

    public void setProfileCompleted (Boolean profileCompleted) {
        put("profileCompleted", profileCompleted);
    }

    public boolean getProfileCompleted() {
        return getBoolean("profileCompleted");
    }

    public boolean uploaded() {
        return getBoolean("uploaded");
    }

    public void setUploaded(boolean uploaded) {
        put("uploaded", uploaded);
    }

    public static ParseQuery<TaskShareProfile> getQuery() {
        return ParseQuery.getQuery(TaskShareProfile.class);
    }


}
