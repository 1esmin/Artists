package com.morlins.artists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Morlins on 12.04.2016.
 */
public class ArtistActivity extends Activity{
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        description = (TextView) findViewById(R.id.description);
        Intent intent = getIntent();

        description.setText(intent.getStringExtra("description"));
    }
}
