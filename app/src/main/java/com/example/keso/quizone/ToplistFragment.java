package com.example.keso.quizone;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KESO on 19/02/2017.
 */

public class ToplistFragment extends Fragment {

    User user;
    ArrayList<Result> topList = new ArrayList<>();
    ListView listView;
    ProgressBar progressBar;
    Spinner spinnerCat;
    Spinner spinnerDiff;
    //ToplistAdapter adapter;
    Boolean first = true;
    Boolean first2 = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_toplist, container, false);
        user = (User) getArguments().getSerializable("User");
        progressBar =(ProgressBar) v.findViewById(R.id.topProgressBar);
        listView =(ListView) v.findViewById(R.id.topListView);
        spinnerCat = (Spinner) v.findViewById(R.id.spinnerCat);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(first){
                   first = false;
                }else{
                    fetchList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDiff = (Spinner) v.findViewById(R.id.spinnerDiff);
        spinnerDiff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(first2){
                    first2 = false;
                }else{
                    fetchList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fetchList();
        return v;
    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).showCoinView(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        getActivity().setTitle("Topplista");
        ((MainActivity) getActivity()).showCoinView(false);
        super.onResume();
    }

    private void fetchList(){
        topList = new ArrayList<>();
        showProgress(true);
        GetTopTen task = new GetTopTen(spinnerCat.getSelectedItemPosition(),spinnerDiff.getSelectedItemPosition());
        task.execute();
    }


    private void populateListView(){
        ToplistAdapter adapter = new ToplistAdapter(getActivity(), topList);
        listView.setAdapter(adapter);
    }

    private void showProgress(Boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public class ToplistAdapter extends ArrayAdapter<Result> {
        // View lookup cache
        private class ViewHolder {
            TextView tvRank;
            TextView tvUsername;
            TextView tvPoints;
            TextView tvCategory;
            TextView tvDifficulty;
        }

        public ToplistAdapter(Context context, ArrayList<Result> result) {
            super(context, R.layout.quiz_result_row, result);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Result result = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ToplistAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ToplistAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.toplist_row, parent, false);
                viewHolder.tvRank = (TextView) convertView.findViewById(R.id.tvRank);
                viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
                viewHolder.tvPoints = (TextView) convertView.findViewById(R.id.tvPoints);
                viewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
                viewHolder.tvDifficulty = (TextView) convertView.findViewById(R.id.tvDifficulty);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ToplistAdapter.ViewHolder) convertView.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.tvRank.setText("Rank: "+result.getRank());
            viewHolder.tvUsername.setText("Användare: "+result.getUsername());
            viewHolder.tvPoints.setText("Poäng: "+result.getTotal());
            viewHolder.tvCategory.setText("Kategori: "+result.getCategoryToString());
            viewHolder.tvDifficulty.setText("Svårighetsgrad: "+result.getDifficultyToString());
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class GetTopTen extends AsyncTask<String, Void, JSONObject> {

        private final int mCategory;
        private final int mDifficulty;

        GetTopTen (int category, int difficulty) {
            mCategory = category;
            mDifficulty = difficulty;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject user1 = null;
            try {
                URL url;
                if(mCategory==0 && mDifficulty ==0){
                    url = new URL(String.format("http://185.53.129.12/topten.php"));
                }else if(mCategory==0 && mDifficulty!=0){
                    url = new URL(String.format("http://185.53.129.12/topten.php?difficulty="+mDifficulty));
                }else if(mCategory!=0 && mDifficulty==0){
                    url = new URL(String.format("http://185.53.129.12/topten.php?category="+mCategory));
                }else{
                    url = new URL(String.format("http://185.53.129.12/topten.php?category="+mCategory+"&difficulty="+mDifficulty));
                }
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                Boolean getUserTop = true;
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<Integer> result = new ArrayList<>();
                    int id = jsonObject.getInt("id");
                    int userid = jsonObject.getInt("userID");
                    int r1 = jsonObject.getInt("r1");
                    result.add(r1);
                    int r2 = jsonObject.getInt("r2");
                    result.add(r2);
                    int r3 = jsonObject.getInt("r3");
                    result.add(r3);
                    int r4 = jsonObject.getInt("r4");
                    result.add(r4);
                    int r5 = jsonObject.getInt("r5");
                    result.add(r5);
                    int r6 = jsonObject.getInt("r6");
                    result.add(r6);
                    int r7 = jsonObject.getInt("r7");
                    result.add(r7);
                    int r8 = jsonObject.getInt("r8");
                    result.add(r8);
                    int r9 = jsonObject.getInt("r9");
                    result.add(r9);
                    int r10 = jsonObject.getInt("r10");
                    result.add(r10);
                    int total = jsonObject.getInt("total");
                    int difficulty = jsonObject.getInt("difficulty");
                    int category = jsonObject.getInt("category");
                    String username = jsonObject.getString("name");
                    if(username.equalsIgnoreCase(user.getUsername())){
                        getUserTop = false;
                    }
                    int rank = jsonObject.getInt("rank");
                    topList.add(new Result(id, userid, result, difficulty, category, total, rank, username));
                }

                if(getUserTop){
                    if(mCategory==0 && mDifficulty ==0){
                        url = new URL(String.format("http://185.53.129.12/topuser.php?userid="+user.getId()));
                    }else if(mCategory==0 && mDifficulty!=0){
                        url = new URL(String.format("http://185.53.129.12/topuser.php?userid="+user.getId()+"&difficulty="+mDifficulty));
                    }else if(mCategory!=0 && mDifficulty==0){
                        url = new URL(String.format("http://185.53.129.12/topuser.php?userid="+user.getId()+"&category="+mCategory));
                    }else{
                        url = new URL(String.format("http://185.53.129.12/topuser.php?userid="+user.getId()+"&category="+mCategory+"&difficulty="+mDifficulty));
                    }
                    urlConnection = (HttpURLConnection) url.openConnection();
                    stream = new BufferedInputStream(urlConnection.getInputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    builder = new StringBuilder();

                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }

                    jsonArray = new JSONArray(builder.toString());
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ArrayList<Integer> result = new ArrayList<>();
                        int id = jsonObject.getInt("id");
                        int userid = jsonObject.getInt("userID");
                        int r1 = jsonObject.getInt("r1");
                        result.add(r1);
                        int r2 = jsonObject.getInt("r2");
                        result.add(r2);
                        int r3 = jsonObject.getInt("r3");
                        result.add(r3);
                        int r4 = jsonObject.getInt("r4");
                        result.add(r4);
                        int r5 = jsonObject.getInt("r5");
                        result.add(r5);
                        int r6 = jsonObject.getInt("r6");
                        result.add(r6);
                        int r7 = jsonObject.getInt("r7");
                        result.add(r7);
                        int r8 = jsonObject.getInt("r8");
                        result.add(r8);
                        int r9 = jsonObject.getInt("r9");
                        result.add(r9);
                        int r10 = jsonObject.getInt("r10");
                        result.add(r10);
                        int total = jsonObject.getInt("total");
                        int difficulty = jsonObject.getInt("difficulty");
                        int category = jsonObject.getInt("category");
                        String username = jsonObject.getString("name");
                        int rank = jsonObject.getInt("rank");
                        topList.add(new Result(id, userid, result, difficulty, category, total, rank, username));
                    }
                }

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return user1;
        }

        @Override
        protected void onPostExecute(JSONObject userJSON) {
            showProgress(false);
            populateListView();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


}
