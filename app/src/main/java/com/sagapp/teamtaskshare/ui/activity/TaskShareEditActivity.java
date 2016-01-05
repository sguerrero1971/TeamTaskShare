package com.sagapp.teamtaskshare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sagapp.teamtaskshare.R;

import java.io.File;

/**
 * Created by SolarUser on 12/14/2015.
 */
public class TaskShareEditActivity extends Activity {

    Button cameraButton;
    Button submitButton;
    public static EditText mEdit;
    private static String mItem;
    RadioGroup usableNotUsable;
    RadioButton usable;
    RadioButton notUsable;
    private Uri imageUri;
    private static final int CAMERA_REQUEST = 1971;
    private static boolean status;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_taskshare);
        mItem = String.valueOf((TextView) findViewById(R.id.taskShare_item));
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
        mEdit = (EditText) findViewById(R.id.editText);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        usableNotUsable = (RadioGroup) findViewById(R.id.radioGroup);
        usable = (RadioButton) findViewById(R.id.usable);
        notUsable = (RadioButton) findViewById(R.id.notUsable);

        Intent intent = getIntent();
        mItem = intent.getStringExtra(mItem);


        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.provider.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(),  "TaskShare.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivity(intent, CAMERA_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = usableNotUsable.getCheckedRadioButtonId();

                if (selectedId == usable.getId()) {
                    status = true;
                } else if (selectedId == notUsable.getId()) {
                    status = false;
                }

                Intent intent = new Intent();
                intent.putExtra("mItem", mItem);
                intent.putExtra("mEdit", mEdit.getText().toString());
                intent.putExtra("status", status);
                intent.putExtra("imageUri", imageUri);
                setResult(RESULT_OK, intent);
                TaskShareEditActivity.this.finish();
            }



        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
}
