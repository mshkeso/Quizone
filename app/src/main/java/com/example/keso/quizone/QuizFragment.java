package com.example.keso.quizone;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by KESO on 02/02/2017.
 */

public class QuizFragment extends Fragment implements View.OnClickListener{

    ProgressBar mProgressBar;
    ObjectAnimator animation;
    ArrayList<Question> questions = new ArrayList<>();
    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView question;
    TextView displayQNumber;
    int index =0;
    int i=0;
    Question currentQuestion;
    Drawable bgDefault;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        createTestQuestions();

        mProgressBar=(ProgressBar)v.findViewById(R.id.progressBar);
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
        new AlertDialog.Builder(getContext())
                .setTitle("Starta quiz?")
                .setMessage("Är du redo att starta frågesporten?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startQuiz();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        end();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return v;
    }

    private void end(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void startQuiz() {
        prepareQuestion();
        startTimer();
    }

    private void nextQuestion(){
        index++;
        if(index!=10){
            prepareQuestion();
            startTimer();
        }else{
            endQuiz();
        }
    }

    private void endQuiz() {

    }

    private void prepareQuestion() {
        currentQuestion = questions.get(index);
        displayQNumber.setText((index+1)+"/10");
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
    }

    private void falseAnswer(Button b) {
        b.setBackgroundColor(getResources().getColor(R.color.red));
        final long changeTime = 1000L;
        b.postDelayed(new Runnable() {
            @Override
            public void run() {
                //b.setBackgroundResource(bgDefault);
                if(b1.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b1.setBackgroundColor(getResources().getColor(R.color.green));
                }else if(b2.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b2.setBackgroundColor(getResources().getColor(R.color.green));
                }else if(b3.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b3.setBackgroundColor(getResources().getColor(R.color.green));
                }else if(b4.getText().toString().equalsIgnoreCase(currentQuestion.getAnswer())){
                    b4.setBackgroundColor(getResources().getColor(R.color.green));
                }
                b1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b1.setBackground(bgDefault);
                        b2.setBackground(bgDefault);
                        b3.setBackground(bgDefault);
                        b4.setBackground(bgDefault);
                        nextQuestion();
                    }
                }, changeTime);
            }
        }, changeTime);

    }

    private void correctAnswer(Button b) {
        b.setBackgroundColor(getResources().getColor(R.color.green));
        final long changeTime = 1000L;
        b.postDelayed(new Runnable() {
            @Override
            public void run() {
                b1.setBackground(bgDefault);
                b2.setBackground(bgDefault);
                b3.setBackground(bgDefault);
                b4.setBackground(bgDefault);
                nextQuestion();
            }
        }, changeTime);
    }

    private void startTimer(){
        animation = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 500);
        animation.setDuration(20000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                //do something when the countdown is complete
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        animation.start();
    }

    private void createTestQuestions() {
        questions.add(new Question("1+2", 1, "3", "0","2","4"));
        questions.add(new Question("4+2", 2, "6", "5","7","8"));
        questions.add(new Question("1+3", 3, "4", "2","3","5"));
        questions.add(new Question("2-1", 4, "1", "0","2","4"));
        questions.add(new Question("20/5", 5, "4", "5","2","3"));
        questions.add(new Question("10+10", 6, "20", "30","50","40"));
        questions.add(new Question("1+7", 7, "8", "9","7","4"));
        questions.add(new Question("0+2", 8, "2", "0","3","4"));
        questions.add(new Question("1*2", 9, "2", "0","3","4"));
        questions.add(new Question("4*0", 10, "0", "3","2","4"));
    }


    @Override
    public void onClick(View v) {
        animation.cancel();
        Button b = (Button) v;
        String a =b.getText().toString();
        if(a.equalsIgnoreCase(currentQuestion.getAnswer())){
            correctAnswer(b);
        }else{
            falseAnswer(b);
        }
    }
}