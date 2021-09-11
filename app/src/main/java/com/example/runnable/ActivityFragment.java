package com.example.runnable;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends Fragment implements View.OnClickListener {


    private TextView textViewJSON;
    private Button buttonGet;
    private Button buttonParse;

    // JC - Declare the Locoation of php file required to retrieve previous run
    public static final String MY_JSON = "MY_JSON";
    private static final String JSON_URL = "http://wcrozier02.students.cs.qub.ac.uk/public_html/jsonfeed.php";

    // JC - inflate the layout file used,
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity, container, false);

  // JC - Find the views within the layout file and set the Text of the views
        textViewJSON = (TextView) rootView.findViewById(R.id.textViewJSON);
        textViewJSON.setMovementMethod(new ScrollingMovementMethod());
        buttonGet = (Button) rootView.findViewById(R.id.buttonGet);
        buttonParse = (Button) rootView.findViewById(R.id.buttonParse);
        buttonGet.setOnClickListener(this);
        buttonParse.setOnClickListener(this);
        getJSON(JSON_URL);

        return rootView;
    }
    //JC - When the view is created set the title to Activity
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Activity");
    }

    //JC - On Click listener to retrieve JSON feed of previou activites and show activities
    public void onClick(View v) {
        if (v == buttonGet) {
            getJSON(JSON_URL);// JC - Get location of JSON URL to parse
        }

        if (v == buttonParse) {
            ;
            showParseActivity();// JC - Call method
        }
    }

    //JC - creating intent to use ParseJSON class, display json feed in app but visibility is hidden
    private void showParseActivity() {
        Intent intent = new Intent(getActivity(), ParseJSON.class);
        intent.putExtra(MY_JSON, textViewJSON.getText().toString());
        getActivity().startActivity(intent);
    }


    private void getJSON(String url) {
        class GetJSON extends AsyncTask<String, Void, String> {
            //ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loading = ProgressDialog.show(ActivityFragment.this, "Please Wait...",null,true,true); Removed unneeded step
            }

            //JC - method is passing parameters through reader, passes the values into a string and appends all records
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                textViewJSON.setText(s);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }
}

