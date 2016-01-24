package com.sagapp.teamtaskshare.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.sagapp.teamtaskshare.R;

/**
 * Created by Solar Employee on 1/24/2016.
 */
public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        init((ListView) findViewById(R.id.taskShare_list_view));

}
