package com.example.runnable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //ET - Define the view objects in a variable
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private ProgressDialog progressDialog;

    //Barry - Facebook login variables
    CallbackManager callbackManager;
    LoginButton fbLoginBtn;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //ET - If a user is logged in direct to MainActivity, allows the uer to stay logged in
        // on app close
        /* if((SharedPrefManager.getInstance(this).isLoggedIn())){
         finish();
         startActivity(new Intent(this, MainActivity.class));
           return;
        }

    //Commented due to facebook login needs to work
    */

        //ET - Request a reference to the view components from the activity by calling “findViewById”.
        //Assign the retrieved view components to instance variables 'editTextUsername', 'editTextPassword',
        editTextUsername = (EditText) findViewById(R.id.etUsername);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        buttonLogin = (Button) findViewById(R.id.loginBtn);
        buttonRegister = (Button) findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        //ET - Set on click listeners on these buttons to be reference in the onClick method
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        //Barry - Facebook Login has to be final
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Intent MainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(MainActivityIntent);
            }


            @Override
            public void onCancel() {

                textView.setText("Login Cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Intent MainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(MainActivityIntent);
            }
        });
    }



    private void register(){
        startActivity(new Intent (this, RegisterActivity.class));
    }

    //ET - Define username and  password using the editText referenced in the xml
    private void userLogin(){
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.show();
    /*ET - Creation of a request using the volley library,
        Defining a string request method with a constructor taking the parameters of post
        to insert the values, the second parameter being the URL in which the requests are sent */
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //ET - If successful execute onResponse
                        try {
                            /*ET - Define the JsonObject and Pass the JSONObject under method onResponse,
                            ET - Check Exception to surround with try/catch, */

                            //ET - If error is not true, user has successfully authenticated otherwise
                            //ET - Invalid information used to login
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("userID"),
                                                obj.getString("firstName"),
                                                obj.getString("lastName"),
                                                obj.getString("email"),
                                                obj.getString("username")

                                        );
                                //ET - If username and password is empty toast to indicate
                                if (( editTextUsername.getText().toString().trim().equals("")))
                                {
                                    Toast.makeText(getApplicationContext(), "Username and Password is empty", Toast.LENGTH_LONG).show();
                                }
                                //ET - Successful login, direct to MainActivity
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("You have successfully logged in"),
                                        Toast.LENGTH_LONG
                                ).show();
                                finish();
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                //ET - If unsuccessful execute following Toast  message to the device
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            //ET - Override the method getParams, inserting parameters that need to be sent in HashMap
            //ET - Defining a map object and putting parameters required username and password
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

        };
        //ET - Add the above requests to a requestQueue object
        RequestHandleri.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        //ET - Call the userLogin method when buttonLogin is clicked
        if(view == buttonLogin){
            userLogin();
        }
        //ET - Call the register method when buttonRegister is clicked
        if(view == buttonRegister){
            register();
        }
    }

    //Barry - FB
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
