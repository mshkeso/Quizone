package com.example.keso.quizone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager  fragmentManager;
    MainFragment mainFragment;
    CategoryFragment categoryFragment;
    DifficultyFragment difficultyFragment;
    QuizFragment quizFragment;
    QuizResultFragment quizResultFragment;
    ToplistFragment toplistFragment;
    FAQFragment faqFragment;
    ArrayList<Question> questions = new ArrayList<>();
    User user;
    int category = 0;
    int difficulty = 0;
    private View mProgressView;
    private View mFragmentView;
    private View coinView;
    private TextView coinText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        user = (User) i.getSerializableExtra("User");
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        categoryFragment = new CategoryFragment();
        difficultyFragment = new DifficultyFragment();
        quizResultFragment = new QuizResultFragment();
        toplistFragment = new ToplistFragment();
        faqFragment = new FAQFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
        mFragmentView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.quiz_progress);
        coinView = findViewById(R.id.coinGroup);
        coinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_addcoin);
                dialog.setTitle("Köp mer Quizcoin!");
                View small = dialog.findViewById(R.id.coinSmall);
                small.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCoin(5);
                        dialog.cancel();
                    }
                });
                View medium = dialog.findViewById(R.id.coinMedium);
                medium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCoin(25);
                        dialog.cancel();
                    }
                });
                View large = dialog.findViewById(R.id.coinLarge);
                large.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCoin(100);
                        dialog.cancel();
                    }
                });
                View xl = dialog.findViewById(R.id.coinXL);
                xl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCoin(200);
                        dialog.cancel();
                    }
                });
                View cancel = dialog.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
        coinText = (TextView) findViewById(R.id.coinText);
        checkFirstLoginOfDay();
        updateCoinView();
        quizFragment = new QuizFragment();
    }

    private void checkFirstLoginOfDay() {
        Date userDate = user.getDate();
        Date dDate = new Date();
        Date sDate = new Date();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        String date = year+"-"+month+"-"+day;
        try {
            dDate = dateFormat.parse(date);
            sDate = dateFormat.parse("0000-00-00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("start date",dateFormat.format(sDate));
        Log.e("today date",dateFormat.format(dDate));
        Log.e("user date",dateFormat.format(userDate));
        if(userDate.compareTo(sDate)==0){
            Log.e("test","First login");
            updateTime(dDate);
            addCoin(20);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Välkommen");
            alertDialog.setMessage("Din första inloggning!\nDu har fått 20 Quizcoins.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }else if(userDate.before(dDate)){
            updateTime(dDate);
            addCoin(3);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Välkommen");
            alertDialog.setMessage("Första inloggningen för dagen!\nDu har fått 3 Quizcoins.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    public void updateTime(Date date){
        UpdateDate task = new UpdateDate(user.getId(),date);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_faq:
                showFAQ(null);
                return true;

            case R.id.action_logout:
                this.finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void addCoin(int i){
        user.setQuizcoin(user.getQuizcoin()+i);
        UpdateQuizcoin task = new UpdateQuizcoin(user.getId(), user.getQuizcoin());
        task.execute();
    }

    public void updateCoinView(){
        coinText.setText(user.getQuizcoin()+"");
    }

    public void showCoinView(Boolean view){
        if(view){
            coinView.setVisibility(View.VISIBLE);
        }else{
            coinView.setVisibility(View.GONE);
        }
    }

    public void play(View v){
        setTitle("Kategori");
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, categoryFragment)
                .addToBackStack("play")
                .commit();
    }

    public void showFAQ(View v){
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, faqFragment)
                .addToBackStack("FAQ")
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
        if(text.equals(getString(R.string.easy))){
            difficulty = 1;
        }else if(text.equals(getString(R.string.medium))){
            difficulty = 2;
        }else if(text.equals(getString(R.string.hard))){
            difficulty = 3;
        }
        questions = new ArrayList<>();
        if(user.getQuizcoin()>=5){
            showProgress(true);
            FetchQuestions task = new FetchQuestions(category, difficulty);
            task.execute();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Varning");
            alertDialog.setMessage("Ej tillräckligt med Quizcoins!\nDet kostar 5 att spela.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    public void showToplist(View v){
        Bundle bundle = new Bundle();
        bundle.putSerializable("User", user);
        toplistFragment = new ToplistFragment();
        toplistFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toplistFragment)
                .addToBackStack("toplist")
                .commit();
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

    public void showQuizResult(Result result, int amountRight){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Result", result);
        bundle.putInt("amountRight", amountRight);
        quizResultFragment = new QuizResultFragment();
        quizResultFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, quizResultFragment)
                .addToBackStack("quizResult")
                .commit();
    }

    public void quizResultDone(View v){
        fragmentManager.popBackStack("Category", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        for(int i = 0; i< fragmentManager.getBackStackEntryCount();i++){
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        if(quizFragment.isVisible()){
        }else if(quizResultFragment.isVisible()){
        }else{
            super.onBackPressed();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
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

    public class UpdateDate extends AsyncTask<String, Void, String> {

        private final int userID;
        private final Date date;

        UpdateDate (int userID, Date date) {
            this.userID = userID;
            this.date = date;
        }

        @Override
        protected String doInBackground(String... strings) {
            String inputString = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String sDate = dateFormat.format(date);
                URL url = new URL(String.format("http://185.53.129.12/updatedate.php?userid="+userID+"&date="+sDate));
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
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


    public class UpdateQuizcoin extends AsyncTask<String, Void, String> {

        private final int userID;
        private final int newValue;

        UpdateQuizcoin (int userID, int newValue) {
            this.userID = userID;
            this.newValue = newValue;
        }

        @Override
        protected String doInBackground(String... strings) {
            String inputString = null;
            try {
                URL url = new URL(String.format("http://185.53.129.12/updateqc.php?userid="+userID+"&quizcoin="+newValue));
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
            if(response.equals("1")){
                updateCoinView();
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
