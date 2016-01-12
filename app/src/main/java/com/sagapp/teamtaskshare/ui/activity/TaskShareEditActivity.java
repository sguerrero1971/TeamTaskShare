package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sagapp.teamtaskshare.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SolarUser on 12/14/2015.
 */
public class TaskShareEditActivity extends Activity {

    public static EditText mDescrip;
    private TextView mItemTextView;
    private static String currentItem;
    private static int currentPosition;
    private static String mEdit;

    Button cameraButton;
    Button submitButton;
    RadioGroup usableNotUsable;
    RadioButton usable;
    RadioButton notUsable;


    private static boolean status;

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
                    status = true;
                } else if (selectedId == notUsable.getId()) {
                    status = false;
                } else if (selectedId != notUsable.getId() && selectedId != usable.getId()) {
                    Toast.makeText(TaskShareEditActivity.this, "Please select either USABLE or NOT USABLE before submitting ", Toast.LENGTH_LONG).show();
                    return;
                }

                if (imageFileName != null){
                String bodyText = "We're having issues with: " + currentItem;
                Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                    mmsIntent.putExtra("sms_body", bodyText);
                    mmsIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
                    mmsIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mCurrentPhotoPath)));
                    mmsIntent.setType("image/*");
                    startActivity(Intent.createChooser(mmsIntent,"Send image To:"));
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
}
