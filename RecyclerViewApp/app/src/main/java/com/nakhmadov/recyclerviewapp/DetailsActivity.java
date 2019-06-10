package com.nakhmadov.recyclerviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    TextView nameTV;
    TextView descriptionTV;
    TextView ratingTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        nameTV = (TextView) findViewById(R.id.d_name_id);
        descriptionTV = (TextView) findViewById(R.id.d_description_id);
        ratingTV = (TextView) findViewById(R.id.d_rating_id);

        Intent intent = getIntent();

        nameTV.setText(intent.getStringExtra("name"));
        descriptionTV.setText(intent.getStringExtra("description"));
        ratingTV.setText(intent.getStringExtra("rating"));

    }
}
