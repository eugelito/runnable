package com.example.runnable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

//Implement the onClickListener method
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //ET - Define the view objects in a variable, and creation of progress dialog to display
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextUsername, editTextPassword;
    private Button buttonRegister, buttonCancel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ET - Retrieve the views from the xml file and set it to the variables declared
        editTextFirstName = (EditText) findViewById(R.id.etFirstName);

        //ET - Validation if the length of text is null it appears an error message
        editTextFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(editTextFirstName.getText().length()<1) {
                    editTextFirstName.setError("Please enter First Name");
                }}});

        editTextLastName = (EditText) findViewById(R.id.etLastName);
        editTextLastName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(editTextLastName.getText().length() == 0) {
                    editTextLastName.setError("Please enter Last Name");
                }
            }});
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(editTextEmail.getText().length() == 0) {
                    editTextEmail.setError("Please enter email");
                }}});
        editTextUsername = (EditText) findViewById(R.id.etUsername);
        editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(editTextUsername.getText().length() == 0) {
                    editTextUsername.setError("Please Enter Username");
                }}});
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(editTextPassword.getText().length() == 0) {
                    editTextPassword.setError("Please enter Password");
                }}});

        //ET - Creation of buttons that references from the xml and is set an instance of the
        //declared variables
        buttonRegister = (Button) findViewById(R.id.registerBtn);
        buttonCancel = (Button) findViewById(R.id.cancelBtn);

        progressDialog = new ProgressDialog(this);

        //ET - Set an OnClick listener to the buttons to be referenced in the the view method
        //Attaching listener to following buttons
        buttonRegister.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);


    }


    //ET - Call script registerUser.php from server.
    private void registerUser() {

        //ET - Define the strings to get the text of the editTexts
        final String sFirstname = editTextFirstName.getText().toString().trim();
        final String sLastname = editTextLastName.getText().toString().trim();
        final String sEmail = editTextEmail.getText().toString().trim();
        final String sUsername = editTextUsername.getText().toString().trim();
        final String sPassword = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        /*ET - Creation of a request using the volley library,
        Defining a string request method with a constructor taking the parameters of post
        to insert the values, the second parameter being the URL in which the requests are sent */
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                //ET - If successful execute onResponse
                        try {
                            /*ET - Define the JsonObject and Pass the JSONObject under method onResponse,
                            ET - Check Exception to surround with try/catch, */
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            finish();
                            Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(loginIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                //ET - If unsuccessful execute following Toast  message to the device
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        //ET - Override the method getParams, inserting parameters that need to be sent in HashMap
            //ET - Defining a map object and putting parameters required
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstName", sFirstname);
                params.put("lastName", sLastname);
                params.put("email", sEmail);
                params.put("username", sUsername);
                params.put("password", sPassword);
                return params;
            }
        };
        //ET - Add the above requests to a requestQueue object

        RequestHandleri.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {

        //ET - Call the registerUser method when buttonRegister is clicked
        if (view == buttonRegister)
            registerUser();
        if (view == buttonCancel)
            startActivity(new Intent (this, LoginActivity.class));
    }
}