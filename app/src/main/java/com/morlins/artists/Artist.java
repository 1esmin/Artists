

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

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class Artist implements Parcelable {
    private Integer id, tracks, albums;     //индификатор, кол-во треков, кол-во альбомов
    private String name, link, description; //имя, ссылка на сайт, описание
    private ArrayList<String> genres;       //жанры
    private ArrayList<String> cover;        //обложки

    //ключи для получения данных из json
    private static final String ID      = "id";
    private static final String TRACKS  = "tracks";
    private static final String ALBUMS  = "albums";
    private static final String NAME    = "name";
    private static final String LINK    = "link";
    private static final String DESCRIPTION = "description";
    private static final String GENRES  = "genres";
    private static final String COVER   = "cover";
    private static final String SMALL   = "small";
    private static final String BIG     = "big";

    private static final int SMALL_IMAGE_INDEX  = 0;
    private static final int BIG_IMAGE_INDEX    = 1;

    ///вспомогательные строки для метода getStringAlbumsAndTracks
    private static final String ALBUM_WORD = " альбом";
    private static final String TRACK_WORD = " пес";

    //окончания для "альбом" и "песня"
    private final String[] ALBUM_WORD_ENDS = {"", "а", "ов"};
    private final String[] TRACK_WORD_ENDS = {"ня", "ни", "ен"};

    //основной конструктор
    public Artist(Integer id, Integer tracks, Integer albums, String name,
                  String link, String description, ArrayList<String> genres,
                  ArrayList<String> cover) {
        this.id     = id;
        this.tracks = tracks;
        this.albums = albums;
        this.name   = name;
        this.link   = link;
        this.description = description;
        this.genres = genres;
        this.cover  = cover;
    }

    //вспомогательный конструктор
    public Artist(
            Integer id, Integer tracks, Integer albums, String name,
            String link, String description, JSONArray genres, JSONObject cover
    ) throws JSONException {
        this(
                id, tracks, albums,
                name, link, description,
                getArrayList(genres),
                getArrayList(cover,
                        new String[]{SMALL, BIG})
        );
    }

    //вспомогательный конструктор
    public Artist(Parcel in) {
        this(
                in.readInt(),   in.readInt(),
                in.readInt(),   in.readString(),
                in.readString(),in.readString(),
                (ArrayList<String>) in.readSerializable(),
                (ArrayList<String>) in.readSerializable()
        );
    }

    public Artist(JSONObject obj) throws JSONException {
        this(
                (Integer) obj.get(ID), (Integer) obj.get(TRACKS),
                (Integer) obj.get(ALBUMS), (String) obj.get(NAME),
                (String) ifHasGetElseNull(obj, LINK), (String) obj.get(DESCRIPTION),
                (JSONArray) obj.get(GENRES), (JSONObject) obj.get(COVER)
        );
    }

    //достает из obj значение с key, если нет, то возвращает null
    public static Object ifHasGetElseNull(JSONObject obj, String key) throws JSONException {
        return obj.has(key) ? obj.get(key) : null;
    }

    //конвертирует JSONArray в ArrayList<String>
    private static ArrayList<String> getArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> array = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++)
            array.add(i, jsonArray.getString(i));

        return array;
    }

    //конвертирует JSONObject в ArrayList<String>
    private static ArrayList<String> getArrayList(JSONObject jsonObject,
                                                  String[] keys) throws JSONException {
        ArrayList<String> array = new ArrayList<>(jsonObject.length());

        int i = 0;
        for (String key : keys)
            array.add(i++, jsonObject.getString(key));

        return array;
    }

    //переопределения для реализации интерфейса Parcelable
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(tracks);
        dest.writeInt(albums);

        dest.writeString(name);
        dest.writeString(link);
        dest.writeString(description);

        dest.writeSerializable(genres);
        dest.writeSerializable(cover);
    }

    public Integer getId() {
        return id;
    }

    public Integer getTracks() {
        return tracks;
    }

    public Integer getAlbums() {
        return albums;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getCover() {
        return cover;
    }

    public String getBigCover() {
        return getCover().get(BIG_IMAGE_INDEX);
    }

    public String getSmallCover() {
        return getCover().get(SMALL_IMAGE_INDEX);
    }
    /*
        правильное окончание в словосочетании с числом
        !!!массив окончаний должен содержать 3 окончания: для чисел заканчивающихся на 1;
            для чисел на 2, 3, 4; для чисел на 0, 5, 6, 7, 8, 9 и 11-19;
     */
    public String getRussianEnd(Integer number, String word, String[] ends) {
        String end = ends[0];

        switch (number % 10) {
            case 1:
                break;

            case 2:
            case 3:
            case 4:
                end = ends[1];
                break;

            case 0:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                end = ends[2];
                break;
        }
        //если число от 10 до 19
        String resultEnd = (number / 10 % 10 != 1 ? end : ends[2]);

        return word + resultEnd;
    }

    //отдает строку количества альбомов и песен
    public String getStringAlbumsAndTracks (String delim) {
        Integer albums = getAlbums();
        Integer tracks = getTracks();
        String[] albumsEnds = ALBUM_WORD_ENDS;
        String[] tracksEnds = TRACK_WORD_ENDS;

        return  albums + getRussianEnd(albums, ALBUM_WORD, albumsEnds) +
                delim  +
                tracks + getRussianEnd(tracks, TRACK_WORD, tracksEnds);
    }

    //отдает строку жанров ('pop, dance')
    public String getStringGenres(String delim) {
        StringBuilder buffer = new StringBuilder();

        for (String genre : genres) {
            //если элемент последний, то разделитель не добавляется
            String realStr = genre +
                    (!genre.equals(genres.get(genres.size() - 1)) ? delim : "");
            buffer.append(realStr);
        }

        return buffer.toString();
    }
}
