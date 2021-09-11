package com.example.runnable;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.R.attr.fragment;


/**
 * A simple {@link Fragment} subclass.
 */


public class AccountFragment extends Fragment {

    private TextView textViewUsername, textViewEmail, textViewFirstName, textViewLastName;

    // ET - inflate the layout file used,
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);


  /*      //ET - If user is not logged in redirect to LoginActivity class
        if (!SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
*/
        /*ET - Find the views within the layout file and set the Text of the views
        using SharedPrefManager class to retrieve details */
        textViewUsername = (TextView) view.findViewById(R.id.editTextUsername);
        textViewUsername.setText(SharedPrefManager.getInstance(getActivity()).getUsername());
        textViewEmail = (TextView) view.findViewById(R.id.editTextEmail);
        textViewEmail.setText(SharedPrefManager.getInstance(getActivity()).getEmail());
        textViewFirstName = (TextView) view.findViewById(R.id.editTextFirstName);
        textViewFirstName.setText(SharedPrefManager.getInstance(getActivity()).getFirstName());
        textViewLastName = (TextView) view.findViewById(R.id.editTextLastName);
        textViewLastName.setText(SharedPrefManager.getInstance(getActivity()).getLastName());


        return view;
    }


    //ET - When the view is created set the title to Account
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Account");
    }


}
