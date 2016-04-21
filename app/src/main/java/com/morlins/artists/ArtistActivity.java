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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    ImageLoader imageLoader;
    DisplayImageOptions options;
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

        imageLoader = ImageLoader.getInstance();    //инициализация загрузчика картинок
        //настройки (включено кэширование в ОЗУ, ПЗУ)
        options = new DisplayImageOptions.Builder()
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

        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(image_view);
            }
        });
    }

    @Override
    protected void onPause() {
        // Whenever this activity is paused (i.e. looses focus because another activity is started etc)
        // Override how this activity is animated out of view
        // The new activity is kept still and this activity is pushed out to the left
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
        super.onPause();
    }

    //http://developer.android.com/intl/ru/training/animation/zoom.html
    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageBitmap(imageLoader.loadImageSync(cover));

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
