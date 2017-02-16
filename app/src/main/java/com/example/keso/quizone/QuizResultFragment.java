package com.example.keso.quizone;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KESO on 16/02/2017.
 */

public class QuizResultFragment extends Fragment {

    TextView category;
    TextView difficulty;
    TextView total;
    TextView score;
    ListView resultList;
    Result result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        category = (TextView) v.findViewById(R.id.tvCategory);
        difficulty = (TextView) v.findViewById(R.id.tvDifficulty);
        total = (TextView) v.findViewById(R.id.tvTotal);
        score = (TextView) v.findViewById(R.id.tvScore);
        resultList = (ListView) v.findViewById(R.id.listView);
        result = (Result) getArguments().getSerializable("Result");
        result.calculateTotal();
        setTopInfo();
        populateList();
        ((MainActivity)getActivity()).showProgress(true);
        SaveResult task = new SaveResult(result);
        task.execute();
        return v;
    }

    private void populateList() {
        ResultAdapter adapter = new ResultAdapter(getActivity(), result.getResult());
        resultList.setAdapter(adapter);
    }

    private void setTopInfo() {
        String sCategory = "Kategori: ";
        String sDifficulty = "Svårighetsgrad: ";
        String sTotal = "Total poäng: "+result.getTotal();
        int count = 0;
        if(result.getCategory()==1){
            sCategory += "Film";
        }else if(result.getCategory()==2){
            sCategory += "Musik";
        }else if(result.getCategory()==3){
            sCategory += "Sport";
        }
        if(result.getDifficulty()==1){
            sDifficulty += "Lätt";
        }else if(result.getDifficulty()==2){
            sDifficulty += "Medel";
        }else if(result.getDifficulty()==3){
            sDifficulty += "Svår";
        }
        int tot = 0;
        for(int i = 0; i<result.getResult().size();i++){
            tot++;
            if(result.getSpecificResult(i)!=0){
                count++;
            }
        }
        String sScore = "Antal rätt: "+count+" av "+tot;
        category.setText(sCategory);
        difficulty.setText(sDifficulty);
        total.setText(sTotal);
        score.setText(sScore);
    }

    public class ResultAdapter extends ArrayAdapter<Integer>{
        // View lookup cache
        private class ViewHolder {
            TextView tvResult;
            TextView tvQuestion;
            ProgressBar progressBar;
        }

        public ResultAdapter(Context context, ArrayList<Integer> result) {
            super(context, R.layout.quiz_result_row, result);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            int result = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.quiz_result_row, parent, false);
                viewHolder.tvResult = (TextView) convertView.findViewById(R.id.tvResult);
                viewHolder.tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.tvResult.setText(result+"/100");
            viewHolder.tvQuestion.setText("Fråga: "+(position+1));
            viewHolder.progressBar.setProgress(result*4);
            // Return the completed view to render on screen
            return convertView;
        }
    }


    public class SaveResult extends AsyncTask<String, Void, String> {

        private final Result result;

        SaveResult(Result result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            String inputString = null;
            try {
                URL url = new URL(String.format("http://185.53.129.12/saveresult.php?userid="+result.getUserid()+
                        "&r1="+result.getSpecificResult(0)+
                        "&r2="+result.getSpecificResult(1)+
                        "&r3="+result.getSpecificResult(2)+
                        "&r4="+result.getSpecificResult(3)+
                        "&r5="+result.getSpecificResult(4)+
                        "&r6="+result.getSpecificResult(5)+
                        "&r7="+result.getSpecificResult(6)+
                        "&r8="+result.getSpecificResult(7)+
                        "&r9="+result.getSpecificResult(8)+
                        "&r10="+result.getSpecificResult(9)+
                        "&total="+result.getTotal() +
                        "&difficulty="+result.getDifficulty() +
                        "&category=4"+result.getCategory()));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

                inputString = bufferedReader.readLine();


                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputString;
        }

        @Override
        protected void onPostExecute(String response) {

            ((MainActivity)getActivity()).showProgress(false);
            int iResponse = Integer.parseInt(response);
            if(iResponse>0){
                result.setId(iResponse);
            }

        }

        @Override
        protected void onCancelled() {
            ((MainActivity)getActivity()).showProgress(false);
        }
    }


}
