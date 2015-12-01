package com.sagapp.teamtaskshare.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;

public class NewTaskShareActivity extends Activity {

	private Button saveButton;
	private Button deleteButton;
	private EditText taskShareText;
	private TaskShare taskShare;
	private String taskShareId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_taskshare);

		// Fetch the taskShareId from the Extra data
		if (getIntent().hasExtra("ID")) {
			taskShareId = getIntent().getExtras().getString("ID");
		}

		taskShareText = (EditText) findViewById(R.id.taskShare_text);
		saveButton = (Button) findViewById(R.id.saveButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);

		if (taskShareId == null) {
			taskShare = new TaskShare();
			taskShare.setUuidString();
		} else {
			ParseQuery<TaskShare> query = TaskShare.getQuery();
			query.fromLocalDatastore();
			query.whereEqualTo("uuid", taskShareId);
			query.getFirstInBackground(new GetCallback<TaskShare>() {

				@Override
				public void done(TaskShare object, ParseException e) {
					if (!isFinishing()) {
						taskShare = object;
						taskShareText.setText(taskShare.getTitle());
						deleteButton.setVisibility(View.VISIBLE);
					}
				}

			});

		}

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				taskShare.setTitle(taskShareText.getText().toString());
				taskShare.setDraft(true);
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

		});

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// The taskShare will be deleted eventually but will
				// immediately be excluded from query results.
				taskShare.deleteEventually();
				setResult(Activity.RESULT_OK);
				finish();
			}

		});

	}

}
