package com.sagapp.teamtaskshare.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.sagapp.teamtaskshare.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SolarUser on 12/14/2015.
 */
public class TaskShareEditActivity extends AppCompatActivity {

    public static EditText mDescrip;
    private TextView mItemTextView;
    private static String currentItem;
    private static int currentPosition;
    private static String mEdit;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private String mobileNumber;
    private Object mobileNumberObj;
    private String unitNumber;

    Button cameraButton;
    Button submitButton;
    RadioGroup usableNotUsable;
    RadioButton usable;
    RadioButton notUsable;


    private static String status;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "TaskShareEditActivity";
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_taskshare);
        mItemTextView = (TextView) findViewById(R.id.taskShare_item);
        this.mImageView = (ImageView) this.findViewById(R.id.imageView1);
        mDescrip = (EditText) findViewById(R.id.editText);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        usableNotUsable = (RadioGroup) findViewById(R.id.radioGroup);
        usable = (RadioButton) findViewById(R.id.usable);
        notUsable = (RadioButton) findViewById(R.id.notUsable);
        mItemTextView.setText(currentItem);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        currentItem = intent.getStringExtra("currentItem");
        currentPosition = intent.getIntExtra("position", currentPosition);

        // Toast.makeText(TaskShareEditActivity.this, currentItem + currentPosition, Toast.LENGTH_LONG).show();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit = mDescrip.getText().toString();

                int selectedId = usableNotUsable.getCheckedRadioButtonId();

                if (selectedId == usable.getId()) {
                    status = "usable";
                } else if (selectedId == notUsable.getId()) {
                    status = "not usable";
                } else if (selectedId != notUsable.getId() && selectedId != usable.getId()) {
                    Toast.makeText(TaskShareEditActivity.this, "Please select either USABLE or NOT USABLE before submitting ", Toast.LENGTH_LONG).show();
                    return;
                }


                if (imageFileName != null){
                    unitNumber = currentUser.getString("unitNumber");
                    mobileNumberObj = currentUser.get("mmsNumber");
                    mobileNumber = mobileNumberObj.toString();
                    String bodyText = "We're having issues with: " + currentItem + " on unit  number " + unitNumber;
                    Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                        mmsIntent.putExtra("address", mobileNumber);
                        mmsIntent.putExtra("sms_body", bodyText);
                        mmsIntent.setType("image/jpg");
                        mmsIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mCurrentPhotoPath));
                    startActivity(mmsIntent.createChooser(mmsIntent,"Send"));
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("currentItem", currentItem);
                returnIntent.putExtra("mEdit", mEdit);
                returnIntent.putExtra("status", status);
                returnIntent.putExtra("imageFileName",imageFileName);
                returnIntent.putExtra("imageUri", mCurrentPhotoPath);
                returnIntent.putExtra("position", currentPosition);
                setResult(RESULT_OK, returnIntent);
                TaskShareEditActivity.this.finish();
            }


        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        this.imageFileName = imageFileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                mImageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
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
