package com.example.keso.quizone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager  fragmentManager;
    MainFragment mainFragment;
    CategoryFragment categoryFragment;
    DifficultyFragment difficultyFragment;
    QuizFragment quizFragment;
    QuizResultFragment quizResultFragment;
    ArrayList<Question> questions = new ArrayList<>();
    User user;
    int category = 0;
    int difficulty = 0;
    private View mProgressView;
    private View mFragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        user = (User) i.getSerializableExtra("User");
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        categoryFragment = new CategoryFragment();
        difficultyFragment = new DifficultyFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
        mFragmentView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.quiz_progress);
    }


    public void play(View v){
        setTitle("Kategori");
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, categoryFragment)
                .addToBackStack("play")
                .commit();
    }


    public void choseCategory(View v){
        setTitle("Svårighetsgrad");
        Button b = (Button) v;
        String text = (String) b.getText();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, difficultyFragment)
                .addToBackStack("difficulty")
                .commit();
        if(text.equals("Film")){
            category = 1;
        }else if(text.equals("Musik")){
            category = 2;
        }else if(text.equals("Sport")){
            category = 3;
        }
    }

    public void choseDifficulty(View v){
        Button b = (Button) v;
        String text = (String) b.getText();
        if(text.equals("Lätt")){
            difficulty = 1;
        }else if(text.equals("Medel")){
            difficulty = 2;
        }else if(text.equals("Svår")){
            difficulty = 3;
        }
        showProgress(true);
        FetchQuestions task = new FetchQuestions(category, difficulty);
        task.execute();

    }

    public void startQuiz(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Questions", questions);
        bundle.putInt("Difficulty", difficulty);
        bundle.putInt("Category", category);
        bundle.putInt("UserID", user.getId());
        quizFragment = new QuizFragment();
        quizFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack("quiz")
                .commit();
    }

    public void testResult(View v){
        Result result = new Result();
        result.setCategory(3);
        result.setDifficulty(3);
        result.addResult(23);
        result.addResult(33);
        result.addResult(43);
        result.addResult(53);
        result.addResult(63);
        result.addResult(73);
        result.addResult(83);
        result.addResult(93);
        result.addResult(23);
        result.addResult(3);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Result", result);
        quizResultFragment = new QuizResultFragment();
        quizResultFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, quizResultFragment)
                .addToBackStack("quizResult")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(categoryFragment.isVisible()){
            setTitle("Kategori");
            super.onBackPressed();
        }
        if(mainFragment.isVisible()){
            setTitle("Quizone");
            super.onBackPressed();
        }
        if(quizFragment.isVisible()){
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFragmentView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFragmentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFragmentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFragmentView.setVisibility(show ? View.GONE : View.VISIBLE);
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

            showProgress(false);
            int iResponse = Integer.parseInt(response);
            if(iResponse>0){
                Toast.makeText(getApplicationContext(), "sparad!", Toast.LENGTH_LONG).show();
                result.setId(iResponse);
            }

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }



    public class FetchQuestions extends AsyncTask<String, Void, JSONObject> {

        private final int mCategory;
        private final int mDifficulty;

        FetchQuestions (int category, int difficulty) {
            mCategory = category;
            mDifficulty = difficulty;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject user = null;
            try {
                URL url = new URL(String.format("http://185.53.129.12/getquestions.php?category="+mCategory+"&difficulty="+mDifficulty));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String text = jsonObject.getString("text");
                    int id = jsonObject.getInt("id");
                    String answer = jsonObject.getString("answer");
                    String badAnswer1 = jsonObject.getString("badanswer1");
                    String badAnswer2 = jsonObject.getString("badanswer2");
                    String badAnswer3 = jsonObject.getString("badanswer3");
                    questions.add(new Question(text, id, answer, badAnswer1, badAnswer2, badAnswer3));
                }

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(JSONObject userJSON) {
            showProgress(false);
            startQuiz();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
