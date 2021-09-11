package com.example.runnable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * Created by User on 18/04/2017.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    //JC - Declare local variables
    // JC - Declare the Locoation of php file required to upload images, declare values needed for upload feature
    private static final String UPLOAD_URL = "http://wcrozier02.students.cs.qub.ac.uk/public_html/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String KEY_TEXT = "name";
    private int PICK_IMAGE_REQUEST = 1;
    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttonView;
    private EditText editName;
    private EditText editDate;
    private EditText editLocation;
    private ImageView imageView;
    private Bitmap bitmap;
    private Uri filePath;

    // JC - inflate the layout file used,
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_gallery, container, false);

        //JC - Assign variables to buttons, textviews, image views in XML layout file
        buttonChoose = (Button) rootView.findViewById(R.id.buttonChoose);
        buttonUpload = (Button) rootView.findViewById(R.id.buttonUpload);
        buttonView = (Button) rootView.findViewById(R.id.buttonViewImage);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        editName = (EditText) rootView.findViewById(R.id.editName);
        editDate = (EditText) rootView.findViewById(R.id.timeInfo);
        editLocation = (EditText) rootView.findViewById(R.id.locationInfo);

        //JC - Assign onCLickListeners
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonView.setOnClickListener(this);

        return rootView;
    }

    //JC - When the view is created set the title to Upload
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Upload");
    }

    //JC - method to call intent for image selection
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //JC - Check for permissions needed for Android 6.0 or higher
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //JC - method to encode image
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //JC - method is responsible for the uploading of images
    private void uploadImage(){

        final String text = editName.getText().toString().trim();
        class UploadImage extends AsyncTask<Bitmap,Void,String>{

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getActivity().getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put(KEY_TEXT, text);
                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }
    //JC - click listener for buttons
    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if(v == buttonUpload){
            uploadImage();
        }
        if(v == buttonView){
            viewImage();
        }
    }

    private void viewImage() {
        startActivity(new Intent(getActivity(), ImageListView.class));
    }
}

