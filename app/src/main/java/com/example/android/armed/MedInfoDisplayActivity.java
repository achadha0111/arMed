package com.example.android.armed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MedInfoDisplayActivity extends AppCompatActivity {
    private String drugName;
    private String drugDosage;
    private String drugCommonSymptoms;
    private String drugSideEffects;
    TextView medicineNameView;
    TextView drugDosageView;
    TextView drugCommonSymptomsView;
    TextView drugSideEffectsView;
    ProgressBar progressBar;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info_display);
        medicineNameView = findViewById(R.id.string_medicine_name);
        drugDosageView = findViewById(R.id.drugDosage);
        drugCommonSymptomsView = findViewById(R.id.drugCommonSymptoms);
        drugSideEffectsView = findViewById(R.id.drugSideEffects);
        progressBar = findViewById(R.id.dataFetch);
        cardView = findViewById(R.id.medInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();


        if (extras != null) {

           drugName = extras.getString("drugName");
           drugDosage = extras.getString("drugDosage");
           drugCommonSymptoms = extras.getString("drugCommonSymptoms");
           drugSideEffects = extras.getString("drugSideEffects");

           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                    medicineNameView.setText(drugName);
                    drugDosageView.setText(drugDosage);
                    drugCommonSymptomsView.setText(drugCommonSymptoms);
                    drugSideEffectsView.setText(drugSideEffects);

                   progressBar.setVisibility(View.INVISIBLE);
                   cardView.setVisibility(View.VISIBLE);
               }
           });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    medicineNameView.setText("Avil");
                    drugDosageView.setText("2 Pills/Day");
                    drugCommonSymptomsView.setText("Allergies");
                    drugSideEffectsView.setText("Drowsiness");

                    progressBar.setVisibility(View.INVISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
