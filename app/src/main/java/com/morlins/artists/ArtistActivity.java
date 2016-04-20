/*
 *
 *  * Copyright (C) 2016 Kuliev Eduard, http://github.com/1esmin/artists
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.morlins.artists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

//активити для странички исполнителя с подробной информацией
public class ArtistActivity extends AppCompatActivity {
    //view для:
    private ImageView image_view;           //обложки исполнителя
    private TextView genres_view;           //жанров
    private TextView albums_and_tracks_view;//"творческого результата" исполнителя (кол-во альбомов и треков)
    private TextView description_view;      //описания
    private TextView site_link_view;        //ссылки на сайт (если ее нет, то view не отображается)
    private TextView yandex_music_link_view;//ссылки на страничку в яндекс.музыка

    //значения для
    private String name_text, description_text, //имя исполнителя, описания
                   link_text, genres_text,      //ссылки на сайт, жанров,
                   site_link_text,              //ссылки на сайт,
                   yandex_music_link_text;      //ссылки на страничку в яндекс.музыка
    private Integer id;                         //индификатора
    private String cover;                       //обложка (большая)

    private static final int COVER_POSITION_BIG_IMAGE   = 1;    //индекс большой обложки в cover
    private String DELIM_COMMA;  //разделитель ', '
    private String DELIM_DASH;   //разделитель для " - "
    private String ARTIST;       //строка-ключ для интента

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold); //анимация появления
        setContentView(R.layout.activity_artist);

        DELIM_COMMA    = getString(R.string.delim_comma) + " ";
        DELIM_DASH     = " " + getString(R.string.delim_dash) + " ";
        ARTIST         = getString(R.string.artist_intent_text);

        ImageLoader imageLoader = ImageLoader.getInstance();    //инициализация загрузчика картинок
        //настройки (включено кэширование в ОЗУ, ПЗУ)
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        //находим нужные view
        site_link_view =        (TextView)  findViewById(R.id.artist_site_link);
        genres_view =           (TextView)  findViewById(R.id.artist_genres_text);
        albums_and_tracks_view =(TextView)  findViewById(R.id.artist_albums_and_tracks_text);
        description_view =      (TextView)  findViewById(R.id.artist_description_text);
        image_view       =      (ImageView) findViewById(R.id.artist_big_image);
        yandex_music_link_view =(TextView)  findViewById(R.id.yandex_music_link);

        //необходимо для естественной работы ссылок (без этого просто текст)
        yandex_music_link_view.setMovementMethod(LinkMovementMethod.getInstance());
        site_link_view.setMovementMethod(LinkMovementMethod.getInstance());

        Intent intent = getIntent();

        Artist artist = intent.getParcelableExtra(ARTIST);

        //вытаскиваем необходимые значения из artist
        id =                artist.getId();
        genres_text =       artist.getStringGenres(DELIM_COMMA);
        name_text   =       artist.getName();
        description_text =  artist.getDescription();
        cover       =       artist.getBigCover();
        link_text   =       artist.getLink();

        //собираем строку для ссылки на яндекс.музыку
        yandex_music_link_text = "<a href=\"" + getString(R.string.yandex_url)
                + id.toString() + "\">" + getString(R.string.site_yandex_music) + "</a>";

        //заполняем:
        setTitle(name_text);
        imageLoader.displayImage(cover, image_view, options);
        genres_view.setText(genres_text);
        albums_and_tracks_view.setText(
                artist.getStringAlbumsAndTracks(DELIM_DASH)
        ); //// TODO: 20.04.2016 rename delim
        description_view.setText(name_text + DELIM_DASH + description_text);
        site_link_view.setText(site_link_text);
        yandex_music_link_view.setText(Html.fromHtml(yandex_music_link_text));

        //если ссылка есть, то она будет выведена, иначе view не будет отображен
        if (link_text != null) {
            site_link_text =     "<a href=\"" + link_text
                    + "\">" + getString(R.string.artist_site) + "</a>";
            site_link_view.setText(Html.fromHtml(site_link_text));
        } else {
            site_link_view.setVisibility(View.GONE);
        }
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
