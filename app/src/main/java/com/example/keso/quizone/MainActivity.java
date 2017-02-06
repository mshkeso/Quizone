package com.example.keso.quizone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager  fragmentManager;
    MainFragment mainFragment;
    CategoryFragment categoryFragment;
    DifficultyFragment difficultyFragment;
    QuizFragment quizFragment;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);Intent i = getIntent();
        user = (User) i.getSerializableExtra("User");
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        categoryFragment = new CategoryFragment();
        quizFragment = new QuizFragment();
        difficultyFragment = new DifficultyFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
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
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void startQuiz(View v){
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack("quiz")
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(categoryFragment.isVisible()){
            setTitle("Kategori");
        }
        if(mainFragment.isVisible()){
            setTitle("Quizone");
        }
    }
}
