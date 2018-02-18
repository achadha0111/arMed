package com.example.android.armed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MedInfoDisplayActivity extends AppCompatActivity {
    private String drugDosage;
    private String drugChildDosage;
    private String drugCommonSymptoms;
    //private String drugSideEffects;
    private String drugContraindications;
    TextView medicineNameView;
    TextView drugDosageView;
    TextView drugCommonSymptomsView;
    TextView drugSideEffectsView;
    TextView drugChildDosages;
    ProgressBar progressBar;
    CardView cardView;
    RequestQueue queue;
    String queryUrl = "https://c30de1e9.ngrok.io/medication/";
    String drugRequested;
    Button newScan;
    String correctDrugName = "";
    Button pillTaken;
    private RelativeLayout mRelativeLayout;

    // Hook onto the UI elements else it will throw a null pointer error further down
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info_display);
        medicineNameView = (TextView) findViewById(R.id.string_medicine_name);
        drugDosageView = (TextView) findViewById(R.id.drugDosage);
        drugCommonSymptomsView = (TextView) findViewById(R.id.drugCommonSymptoms);
        drugSideEffectsView = (TextView) findViewById(R.id.drugSideEffects);
        progressBar = (ProgressBar) findViewById(R.id.dataFetch);
        cardView = (CardView) findViewById(R.id.medInfo);
        newScan = (Button) findViewById(R.id.scanButton);
        pillTaken = (Button) findViewById(R.id.pillButton);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.content);
        drugChildDosages = (TextView) findViewById(R.id.drugChildDosages);
    }

    // Receive the name of the medicine from the Google Vision API
    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        Log.i("Started MedInfoDisplay", "true");

        // Drug Name
        drugRequested = (extras != null) ? extras.getString("drugName") : "ibuprofen";

        String[] commonMeds = {"Seconal", "Xanax", "Ambien", "Ibuprofen", "Paracetamol", "Codeine", "Percodan",
                "Vicodin", "Amphetamines", "Methylphenidate", "Dextromethorphan", "Pseudophedrine"};


        Boolean fuzzyCorrected = false;

        for (String drugName : commonMeds) {
            if (FuzzySearch.ratio(drugName.toLowerCase(), drugRequested.toLowerCase()) > 80) {
                correctDrugName = drugName;
                fuzzyCorrected = true;
            }
        }

        // Construct URL
        if (fuzzyCorrected) {

            queryUrl = queryUrl + correctDrugName;
            queue = Volley.newRequestQueue(this);

            // Run an async request for JSON data
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                    queryUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        drugDosage = response.getString("Adult:");
                        drugChildDosage = response.getString("Children:");
                        drugContraindications = response.getString("Contraindications:");
                        drugCommonSymptoms = response.getString("symptoms");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                medicineNameView.setText(correctDrugName);
                                drugDosageView.setText(drugDosage);
                                drugChildDosages.setText(drugChildDosage);
                                drugCommonSymptomsView.setText(drugCommonSymptoms);
                                drugSideEffectsView.setText(drugContraindications);

                                progressBar.setVisibility(View.INVISIBLE);
                                cardView.setVisibility(View.VISIBLE);
                                newScan.setVisibility(View.VISIBLE);
                                pillTaken.setVisibility(View.VISIBLE);
                            }
                        });
                        //drugCommonSymptoms = extras.getString("drugCommonSymptoms");
                        //drugSideEffects = extras.getString("drugSideEffects");
                    } catch (JSONException jsonException) {
                        Log.d("Exception ",  jsonException.getMessage());
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("Error", error.getMessage());

                }
            });


            // Prevent retrying of requests
            jsonRequest.setRetryPolicy(
                    new DefaultRetryPolicy(20000, 0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);

        } else {
            Snackbar.make(mRelativeLayout, "Couldn't find anything", Snackbar.LENGTH_LONG);
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.VISIBLE);
            newScan.setVisibility(View.VISIBLE);
        }


        final Intent mIntent = new Intent(MedInfoDisplayActivity.this, MainActivity.class);

        newScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MedInfoDisplayActivity.this.startActivity(mIntent);
            }
        });

        pillTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mRelativeLayout, "Your consumption has been recorded", Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        correctDrugName = "";
    }
}
