package com.sagapp.teamtaskshare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sagapp.teamtaskshare.R;
import com.sagapp.teamtaskshare.TaskShare;
import com.sagapp.teamtaskshare.TaskShareListApplication;
import com.sagapp.teamtaskshare.ui.ParseLoginBuilder;

import java.util.List;

public class TaskShareListActivity extends Activity {

	private static final int LOGIN_ACTIVITY_CODE = 100;
	private static final int EDIT_ACTIVITY_CODE = 200;

	// Adapter for the TaskShares Parse Query
	private ParseQueryAdapter<TaskShare> taskShareListAdapter;

	private LayoutInflater inflater;

	// For showing empty and non-empty taskShare views
	private ListView taskShareListView;
	private LinearLayout noTaskSharesView;

	private TextView loggedInInfoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskshare_list);

		// Set up the views
		taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
		noTaskSharesView = (LinearLayout) findViewById(R.id.no_taskShares_view);
		taskShareListView.setEmptyView(noTaskSharesView);
		loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);

		// Set up the Parse query to use in the adapter
		ParseQueryAdapter.QueryFactory<TaskShare> factory = new ParseQueryAdapter.QueryFactory<TaskShare>() {
			public ParseQuery<TaskShare> create() {
				ParseQuery<TaskShare> query = TaskShare.getQuery();
				query.orderByDescending("createdAt");
				query.fromLocalDatastore();
				return query;
			}
		};
		// Set up the adapter
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		taskShareListAdapter = new TaskShareListAdapter(this, factory);

		// Attach the query adapter to the view
		ListView taskShareListView = (ListView) findViewById(R.id.taskShare_list_view);
		taskShareListView.setAdapter(taskShareListAdapter);

		taskShareListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskShare taskShare = taskShareListAdapter.getItem(position);
				openEditView(taskShare);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check if we have a real user
		if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			// Sync data to Parse
			syncTaskSharesToParse();
			// Update the logged in label info
			updateLoggedInInfo();
		}
	}

	private void updateLoggedInInfo() {
		if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			ParseUser currentUser = ParseUser.getCurrentUser();
			loggedInInfoView.setText(getString(R.string.logged_in,
					currentUser.getString("name")));
		} else {
			loggedInInfoView.setText(getString(R.string.not_logged_in));
		}
	}

	private void openEditView(TaskShare taskShare) {
		Intent i = new Intent(this, NewTaskShareActivity.class);
		i.putExtra("ID", taskShare.getUuidString());
		startActivityForResult(i, EDIT_ACTIVITY_CODE);
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
				// If the user is new, sync data to Parse,
				// else get the current list from Parse
				if (ParseUser.getCurrentUser().isNew()) {
					syncTaskSharesToParse();
				} else {
					loadFromParse();
				}
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
		if (item.getItemId() == R.id.action_new) {
			// Make sure there's a valid user, anonymous
			// or regular
			if (ParseUser.getCurrentUser() != null) {
				startActivityForResult(new Intent(this, NewTaskShareActivity.class),
						EDIT_ACTIVITY_CODE);
			}
		}

		if (item.getItemId() == R.id.action_sync) {
			syncTaskSharesToParse();
		}

		if (item.getItemId() == R.id.action_logout) {
			// Log out the current user
			ParseUser.logOut();
			// Create a new anonymous user
			ParseAnonymousUtils.logIn(null);
			// Update the logged in label info
			updateLoggedInInfo();
			// Clear the view
			taskShareListAdapter.clear();
			// Unpin all the current objects
			ParseObject
					.unpinAllInBackground(TaskShareListApplication.TASKSHARE_GROUP_NAME);
		}

		if (item.getItemId() == R.id.action_login) {
			ParseLoginBuilder builder = new ParseLoginBuilder(this);
			startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		boolean realUser = !ParseAnonymousUtils.isLinked(ParseUser
				.getCurrentUser());
		menu.findItem(R.id.action_login).setVisible(!realUser);
		menu.findItem(R.id.action_logout).setVisible(realUser);
		return true;
	}

	private void syncTaskSharesToParse() {
		// We could use saveEventually here, but we want to have some UI
		// around whether or not the draft has been saved to Parse
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if ((ni != null) && (ni.isConnected())) {
			if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
				// If we have a network connection and a current logged in user,
				// sync the
				// taskShares

				// In this app, local changes should overwrite content on the
				// server.

				ParseQuery<TaskShare> query = TaskShare.getQuery();
				query.fromPin(TaskShareListApplication.TASKSHARE_GROUP_NAME);
				query.whereEqualTo("isDraft", true);
				query.findInBackground(new FindCallback<TaskShare>() {
					public void done(List<TaskShare> taskShares, ParseException e) {
						if (e == null) {
							for (final TaskShare taskShare : taskShares) {
								// Set is draft flag to false before
								// syncing to Parse
								taskShare.setDraft(false);
								taskShare.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										if (e == null) {
											// Let adapter know to update view
											if (!isFinishing()) {
												taskShareListAdapter
														.notifyDataSetChanged();
											}
										} else {
											// Reset the is draft flag locally
											// to true
											taskShare.setDraft(true);
										}
									}

								});

							}
						} else {
							Log.i("TaskShareListActivity",
									"syncTAskSharesToParse: Error finding pinned taskShares: "
											+ e.getMessage());
						}
					}
				});
			} else {
				// If we have a network connection but no logged in user, direct
				// the person to log in or sign up.
				ParseLoginBuilder builder = new ParseLoginBuilder(this);
				startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
			}
		} else {
			// If there is no connection, let the user know the sync didn't
			// happen
			Toast.makeText(
					getApplicationContext(),
					"Your device appears to be offline. Some taskShares may not have been synced to Parse.",
					Toast.LENGTH_LONG).show();
		}

	}

	private void loadFromParse() {
		ParseQuery<TaskShare> query = TaskShare.getQuery();
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.findInBackground(new FindCallback<TaskShare>() {
			public void done(List<TaskShare> taskShares, ParseException e) {
				if (e == null) {
					ParseObject.pinAllInBackground((List<TaskShare>) taskShares,
							new SaveCallback() {
								public void done(ParseException e) {
									if (e == null) {
										if (!isFinishing()) {
											taskShareListAdapter.loadObjects();
										}
									} else {
										Log.i("TaskShareListActivity",
												"Error pinning taskShares: "
														+ e.getMessage());
									}
								}
							});
				} else {
					Log.i("TaskShareListActivity",
							"loadFromParse: Error finding pinned taskShares: "
									+ e.getMessage());
				}
			}
		});
	}

	private class TaskShareListAdapter extends ParseQueryAdapter<TaskShare> {

		public TaskShareListAdapter(Context context,
				ParseQueryAdapter.QueryFactory<TaskShare> queryFactory) {
			super(context, queryFactory);
		}

		@Override
		public View getItemView(TaskShare taskShare, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view == null) {
				view = inflater.inflate(R.layout.list_item_taskshare, parent, false);
				holder = new ViewHolder();
				holder.taskShareTitle = (TextView) view
						.findViewById(R.id.taskShare_title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			TextView taskShareTitle = holder.taskShareTitle;
			taskShareTitle.setText(taskShare.getTitle());
			if (taskShare.isDraft()) {
				taskShareTitle.setTypeface(null, Typeface.ITALIC);
			} else {
				taskShareTitle.setTypeface(null, Typeface.NORMAL);
			}
			return view;
		}
	}

	private static class ViewHolder {
		TextView taskShareTitle;
	}
}
