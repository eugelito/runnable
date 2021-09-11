package com.example.runnable;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;


public class ParseJSON extends ActionBarActivity implements View.OnClickListener{
    //JC - declaring local variables
    private String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String ID = "metricsID";
    private static final String DISTANCE= "distance";
    private static final String STEPS = "steps";
    private static final String PACE= "pace";
    private static final String TIME = "time";
    private JSONArray users = null;
    private int TRACK = 0;
    private EditText editTextId;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private EditText pacestat;
    private EditText timestat;

    Button btnPrev;
    Button btnNext;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_json);

        Intent intent = getIntent();
        myJSONString = intent.getStringExtra(ActivityFragment.MY_JSON);


        editTextId = (EditText) findViewById(R.id.editTextID);
        editTextUserName = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        pacestat = (EditText) findViewById(R.id.paceTextID);
        timestat = (EditText) findViewById(R.id.timeTextID);


        btnPrev = (Button) findViewById(R.id.buttonPrev);
        btnNext = (Button) findViewById(R.id.buttonNext);
        btnBack = (Button) findViewById(R.id.buttonBack);

        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        extractJSON();

        showData();




    }



    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            users = jsonObject.getJSONArray(JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //JC - Method to move to next activity
    private void moveNext(){
        if(TRACK<users.length()){
            TRACK++;
        }
        showData();
    }
    //JC - Method to move to previous activity
    private void movePrev(){
        if(TRACK>0){
            TRACK--;
        }
        showData();
    }
    //JC - Load activites into json object
    private void showData(){
        try {
            JSONObject jsonObject = users.getJSONObject(TRACK);

            editTextId.setText(jsonObject.getString(ID));
            editTextUserName.setText(jsonObject.getString(DISTANCE));
            editTextPassword.setText(jsonObject.getString(STEPS));
            pacestat.setText(jsonObject.getString(PACE));
            timestat.setText(jsonObject.getString(TIME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    @Override
    public void onClick(View v) {
        if(v == btnNext){
            moveNext();
        }
        if(v == btnPrev){
            movePrev();
        }
        if(v == btnBack){
            new AccountFragment();
            finish();
        }


    }
}