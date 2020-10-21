package com.technicalround.iskconsadhanareport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

public class ActivityProfilePic extends AppCompatActivity implements View.OnClickListener,
 InterfaceTaskCompleted {

    public static AppPreferences userPreferences;
    private Button buttonUpload;
    private Button buttonChoose;

    private EditText editText;
    private ImageView imageView;

    public static final String KEY_IMAGE = "image";
    public static final String KEY_TEXT = "name";

    private int PICK_IMAGE_REQUEST = 1;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);
        userPreferences = new AppPreferences(this);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonChoose = (Button) findViewById(R.id.buttonChooseImage);

        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonUpload.setEnabled(false);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                buttonUpload.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //Reduce the size of bitmap to 100px * 100px
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        thumb.compress(Bitmap.CompressFormat.JPEG, 30, baos);//0:Low quality, 10:middle, 100:high
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public void uploadImage(){
        final String text = editText.getText().toString().trim();
        final String image = getStringImage(bitmap);
        if (AppPreferences.user.id < 1) {
            Toast.makeText(getApplicationContext(), "GUEST user can't upload profile pic", Toast.LENGTH_SHORT).show();
            return;
        }

/*
        class UploadImage extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ActivityProfilePic.this,"Please wait...","uploading",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ActivityProfilePic.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ExecuteHttpPostKeys rh = new ExecuteHttpPostKeys();
                HashMap<String,String> param = new HashMap<String,String>();
                param.put(KEY_TEXT,text);
                param.put("id",userPreferences.user.id+"");
                param.put(KEY_IMAGE,image);
                String imageUrl = getApplicationContext().getString(R.string.WEB_ROOT)+"image.php?act=upload";
                String result = rh.sendPostRequest(imageUrl, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();

        */
        String imageUrl = getApplicationContext().getString(R.string.WEB_ROOT)+"devotee.php?act=pic";
        try{
            //Encode image data as it itself may have '&' in between causing a confusion
            String data = "id="+userPreferences.user.id+"&image="+
                    URLEncoder.encode(image, "UTF-8");
            new ExecuteHttpPost(this, this, null).execute(imageUrl, data);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        if(v == buttonChoose){
            showFileChooser();
        }
        if(v == buttonUpload){
            uploadImage();
        }
    }
    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            //Check whether successful sign-in
            //Log.d("Ashu:", "In callback listener in ActivityProfilePic" + response);
            if(response.compareToIgnoreCase("Success")==0) {
                AppPreferences.updatePicVer();
            }
            Intent intent = new Intent(getApplicationContext(), ActivityMainDevotee.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.d("ISS", "LoginHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        //Since previous activity was killed so restart the main activity.
        Intent intent = new Intent(getApplicationContext(), ActivityMainDevotee.class);
        startActivity(intent);
        finish();
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {
    }

    @Override
    public void onTimePickSelection(View view, int combinedTime, boolean delayIgnoreDialog) {
    }
}