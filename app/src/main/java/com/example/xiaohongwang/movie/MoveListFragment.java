package com.example.xiaohongwang.movie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoveListFragment extends Fragment {
    movieAdapter mMovieAdapter;

    public MoveListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie(){
        FetchMoviePoster movieTask = new FetchMoviePoster();
        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_move_list, container, false);
        mMovieAdapter = new movieAdapter(getActivity());

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview_movie);
        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecatAddapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        }
        return rootview;
    }

    public class FetchMoviePoster extends AsyncTask<String, Void, MovieInfo[]> {

        private final String LOG_TAG = FetchMoviePoster.class.getSimpleName();

        @Override
        protected MovieInfo[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = prefs.getString(getString(R.string.pref_sort_type), getString(R.string.default_sort_type));

            String sort_by = "popular?";
            if(sort.equals(getString(R.string.pref_sort_by_popularity))) {
                sort_by = "popular?";
            } else if(sort.equals(getString(R.string.pref_sort_by_rate))) {
                sort_by = "top_rated?";
            }

            try{
                final String BASE_URL = "https://api.themoviedb.org/3/movie/" + sort_by;
                final String API_KEY = "d9a666180f1de1ee1100b99122367c0f";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", API_KEY).build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }

                if(buffer.length() == 0){
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, "Error parsing JSON string", e);
            }
            return null;
        }

        private MovieInfo[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            //Pass the json file returned from db to find the info
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            int nMovie = movieArray.length();
            MovieInfo[] movieInfos = new MovieInfo[nMovie];
            for(int i = 0; i < nMovie; i++) {
                JSONObject oneMovie = movieArray.getJSONObject(i);
                String posterPath = oneMovie.getString("poster_path");
                String name = oneMovie.getString("original_title");

                movieInfos[i] = new MovieInfo(name, posterPath);
            }
            return movieInfos;
        }

        @Override
        public void onPostExecute(MovieInfo[] result) {
            if (result != null){
                mMovieAdapter.clear();
                mMovieAdapter.addAll(result);
            }
        }
    }
}
