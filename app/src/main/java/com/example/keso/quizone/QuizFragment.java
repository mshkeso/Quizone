package com.example.keso.quizone;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class QuizFragment extends Fragment implements View.OnClickListener{

    ProgressBar mProgressBar;
    ArrayList<Question> questions = new ArrayList<>();
    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView question;
    TextView displayQNumber;
    int index = 0;
    int progress = 0;
    Question currentQuestion;
    Drawable bgDefault;
    CountDownTimer timer;
    Result result;
    final int length_in_milliseconds = 10000;
    final int period_in_milliseconds = 25;
    AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        result = new Result();
        questions = getArguments().getParcelableArrayList("Questions");
        result.setDifficulty(getArguments().getInt("Difficulty"));
        result.setCategory(getArguments().getInt("Category"));
        result.setUserid(getArguments().getInt("UserID"));
        mProgressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        mProgressBar.setProgress(progress);
        question = (TextView) v.findViewById(R.id.textView);
        displayQNumber = (TextView) v.findViewById(R.id.textView2);
        b1 = (Button) v.findViewById(R.id.button11);
        b2 = (Button) v.findViewById(R.id.button8);
        b3 = (Button) v.findViewById(R.id.button9);
        b4 = (Button) v.findViewById(R.id.button10);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        bgDefault = b1.getBackground();
        dialog = new AlertDialog.Builder(getContext())
                .setTitle("Starta quiz?")
                .setMessage("Är du redo att starta frågesporten?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startQuiz();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        stopButtons();
        return v;
    }

    private void startQuiz() {
        prepareQuestion();
    }

    private void nextQuestion(){
        index++;
        if(index!=10){
            prepareQuestion();
        }else{
            endQuiz();
        }
    }

    private void endQuiz() {
        QuizResultFragment quizResultFragment = new QuizResultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Result", result);
        quizResultFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, quizResultFragment)
                .addToBackStack("quizResult")
                .commit();
    }


    private void prepareQuestion() {
        animationStopper();
        currentQuestion = questions.get(index);
        String displayText = (index+1)+getString(R.string.quiz_count);
        displayQNumber.setText(displayText);
        ArrayList<String> choices = new ArrayList<>();
        choices.add(currentQuestion.getAnswer());
        choices.add(currentQuestion.getBadAnswer1());
        choices.add(currentQuestion.getBadAnswer2());
        choices.add(currentQuestion.getBadAnswer3());
        Collections.shuffle(choices);

        b1.setText(choices.get(0));
        b2.setText(choices.get(1));
        b3.setText(choices.get(2));
        b4.setText(choices.get(3));
        question.setText(currentQuestion.getText());
        startTimer();
    }

    @Override
    public void onPause() {
        dialog.cancel();
        super.onPause();
    }

    private void falseAnswer(Button b) {
        result.addResult(0);
        animationStopper();
        b.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));
        final long changeTime = 1000L;
        b.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationStopper();
                //b.setBackgroundResource(bgDefault);
                if(b1.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b1.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b2.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b2.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b3.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b3.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b4.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b4.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }
                animationStopper();
                b1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationStopper();
                        b1.setBackground(bgDefault);
                        b2.setBackground(bgDefault);
                        b3.setBackground(bgDefault);
                        b4.setBackground(bgDefault);
                        nextQuestion();
                        animationStopper();
                    }
                }, changeTime);
            }
        }, changeTime);

    }

    private void correctAnswer(Button b) {
        result.addResult(100-(progress/4));
        animationStopper();
        b.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
        final long changeTime = 1000L;
        b.postDelayed(new Runnable() {
            @Override
            public void run() {
                b1.setBackground(bgDefault);
                b2.setBackground(bgDefault);
                b3.setBackground(bgDefault);
                b4.setBackground(bgDefault);
                nextQuestion();
                animationStopper();
            }
        }, changeTime);
    }

    private void startTimer(){
        animationStopper();
        progress=0;
        timer = new CountDownTimer(length_in_milliseconds,period_in_milliseconds) {
            private boolean warned = false;
            @Override
            public void onTick(long millisUntilFinished) {
                progress++;
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                progress++;
                mProgressBar.setProgress(progress);
                stopButtons();
                noAnswer();
            }
        };
        timer.start();
        startButtons();
    }

    private void noAnswer() {
        final long changeTime = 1000L;
        result.addResult(0);
        b1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //b.setBackgroundResource(bgDefault);
                if(b1.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b1.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b2.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b2.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b3.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b3.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }else if(b4.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b4.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
                }
                animationStopper();
                b1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b1.setBackground(bgDefault);
                        b2.setBackground(bgDefault);
                        b3.setBackground(bgDefault);
                        b4.setBackground(bgDefault);
                        animationStopper();
                        nextQuestion();
                    }
                }, changeTime);
            }
        }, changeTime);
    }
    public void animationStopper(){
        b1.clearAnimation();
        b2.clearAnimation();
        b3.clearAnimation();
        b4.clearAnimation();
    }

    @Override
    public void onClick(View v) {
        animationStopper();
        timer.cancel();
        stopButtons();
        Button b = (Button) v;
        String a =b.getText().toString();
        stopButtons();
        if(a.equalsIgnoreCase(currentQuestion.getAnswer())){
            correctAnswer(b);
        }else{
            falseAnswer(b);
        }
    }

    private void stopButtons() {
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
    }

    private void startButtons() {
        b1.setEnabled(true);
        b2.setEnabled(true);
        b3.setEnabled(true);
        b4.setEnabled(true);
    }
}