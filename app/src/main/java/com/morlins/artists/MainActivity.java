package com.morlins.artists;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Parcelable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private ListView list;
    private ArtistDatabase ArtistDB;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinkedList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    //строка-ключ для интента
    private static final String ARTIST = "artist";
    //время, которое крутится кружок во время обновления
    private static final int REFRESH_TIME = 1000;
    //layout для item'а ListView
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item;
    //цвета для кружочка обновления
    private static final int HOLO_BLUE_BRIGHT   = android.R.color.holo_blue_bright;
    private static final int HOLO_GREEN_LIGHT   = android.R.color.holo_green_light;
    private static final int HOLO_ORANGE_LIGHT  = android.R.color.holo_orange_light;
    private static final int HOLO_RED_LIGHT     = android.R.color.holo_red_light;
    private static final int SWIPE_CONTAINER    = R.id.swipe_container;
    //пока картинка не загрузилась, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_ONLOADING  = R.color.onloading;
    //если загрузить не получилось, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_ON_FAIL    = COLOR_IMAGE_ONLOADING;
    //если uri оказался пуст, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_IF_EMPTY_URI  = COLOR_IMAGE_ONLOADING;
    private Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.v("Create Activity");
        list = (ListView) findViewById(R.id.list);

        LogUtil.v("Configure ImageLoader");
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(COLOR_IMAGE_IF_EMPTY_URI)
                .showImageOnFail(COLOR_IMAGE_ON_FAIL)
                .showImageOnLoading(COLOR_IMAGE_ONLOADING)
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        LogUtil.v("ArtistDB.execute()");
        ArtistDatabase ArtistDB = new ArtistDatabase(this, list,
                imageLoader, options, state);
        ArtistDB.execute();

        artists = ArtistDB.getArtists();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(SWIPE_CONTAINER);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(
                HOLO_BLUE_BRIGHT,
                HOLO_GREEN_LIGHT,
                HOLO_ORANGE_LIGHT,
                HOLO_RED_LIGHT
        );

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LogUtil.v("Click Item");
                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra(ARTIST, artists.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(state != null) {
            LogUtil.v("trying to restore listview state..");
            list.onRestoreInstanceState(state);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        LogUtil.v("saving listview state @ onPause");
        state = list.onSaveInstanceState();
        super.onStop();
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        LogUtil.v("Refresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                ArtistDB = new ArtistDatabase(getBaseContext(), list, imageLoader, options);
                ArtistDB.execute();
            }
        }, REFRESH_TIME);
    }
}