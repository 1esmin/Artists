package com.morlins.artists;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    JSONArray artists = new JSONArray();
    ListView list;
    LinkedList names = new LinkedList();
    Dictionary descriptions = new Hashtable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setText("update");
        btnOk.setOnClickListener(oclBtnOk);
        list = (ListView) findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra("description", descriptions.get(names.get(position)).toString());
                startActivity(intent);
            }
        });
    }

    public void Print (View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item, names);
        list.setAdapter(adapter);
    }

    View.OnClickListener oclBtnOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < artists.length(); i++) {
                try {
                    names.addLast(artists.getJSONObject(i).getString("name"));
                    descriptions.put(names.getLast(), artists.getJSONObject(i).getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";


        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://cache-default02f.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            try {
                JSONArray jarr = new JSONArray(strJson);
                for (int i = 0; i < jarr.length(); i++) {
                    artists.put(jarr.get(i));
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

}
