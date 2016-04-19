package com.morlins.artists;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ResourceBundle;

public class ArtistActivity extends AppCompatActivity {
    private ImageView image_view;
    private TextView genres_text;
    private TextView albums_and_tracks_text;
    private TextView description_text;
    private TextView site_link_text;
    private TextView yandex_music_link_text;

    private String name, description, genres, site_link, yandex_music_link;
    private Integer albums, tracks, id;
    private String[] cover;

    //константы
    private static final int COVER_POSITION_SMALL_IMAGE = 0;
    private static final int COVER_POSITION_BIG_IMAGE   = 1;
    private static final String DELIM_GENRES = ", ";
    private static final String DELIM_ALBUMS_AND_TRACKS = " - ";
    private static final String ARTIST = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);
        setContentView(R.layout.activity_artist);

        //инициализация загрузчика картинок
        ImageLoader imageLoader = ImageLoader.getInstance();
        //настройки (включено кэширование в ОЗУ, ПЗУ)
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        site_link_text =        (TextView)  findViewById(R.id.artist_site_link);
        genres_text =           (TextView)  findViewById(R.id.artist_genres_text);
        albums_and_tracks_text =(TextView)  findViewById(R.id.artist_albums_and_tracks_text);
        description_text =      (TextView)  findViewById(R.id.artist_description_text);
        image_view       =      (ImageView) findViewById(R.id.artist_big_image);
        yandex_music_link_text =(TextView)  findViewById(R.id.yandex_music_link);

        yandex_music_link_text.setMovementMethod(LinkMovementMethod.getInstance());

        Intent intent = getIntent();

        Artist artist = intent.getParcelableExtra(ARTIST);

        id      =       artist.getId();
        genres  =       artist.getStringGenres(DELIM_GENRES);
        name =       artist.getName();
        albums  = artist.getAlbums();
        tracks  =       artist.getTracks();
        description =   artist.getDescription();
        cover   =       artist.getCover();
        String link =   artist.getLink();

        yandex_music_link
                = "<a href=\"http://music.yandex.ru/artist/"
                + id.toString() + "\">Страничка в Яндекс.Музыка</a>";

        setTitle(name);
        imageLoader.displayImage(cover[COVER_POSITION_BIG_IMAGE], image_view, options);
        genres_text.setText(genres);
        albums_and_tracks_text.setText(artist.getStringAlbumsAndTracks(DELIM_ALBUMS_AND_TRACKS));
        description_text.setText(name + DELIM_ALBUMS_AND_TRACKS + description);
        site_link_text.setText(site_link);
        yandex_music_link_text.setText(Html.fromHtml(yandex_music_link));

        if (link != null) {
            site_link =     "<a href=\"" + link + "\">Сайт исполнителя</a>";
            site_link_text.setText(Html.fromHtml(site_link));
        } else {
            site_link_text.setVisibility(View.GONE);
        }

        //TODO: top-songs (?)
    }

    @Override
    protected void onPause() {
        // Whenever this activity is paused (i.e. looses focus because another activity is started etc)
        // Override how this activity is animated out of view
        // The new activity is kept still and this activity is pushed out to the left
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
        super.onPause();
    }
}
