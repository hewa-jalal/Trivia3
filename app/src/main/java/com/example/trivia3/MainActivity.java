package com.example.trivia3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia3.data.AnswerListAsyncResponse;
import com.example.trivia3.data.QuestionBank;
import com.example.trivia3.model.Question;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_question;
    private TextView tv_counter;
    private TextView tv_highScore;
    private Button btn_true;
    private Button btn_false;
    private ImageButton ibtn_next;
    private ImageButton ibtn_prev;
    private TextView tv_score;
    private int currentQuestionindex = 0;
    private int score = 0;
    private int highScore = score;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_question = findViewById(R.id.tv_question);
        tv_counter = findViewById(R.id.tv_counter);
        tv_score = findViewById(R.id.tv_score);
        btn_false = findViewById(R.id.btn_false);
        btn_true = findViewById(R.id.btn_true);
        ibtn_next = findViewById(R.id.ibtn_next);
        ibtn_prev = findViewById(R.id.ibtn_prev);
        tv_highScore = findViewById(R.id.tv_highscore);

        btn_false.setOnClickListener(this);
        btn_true.setOnClickListener(this);
        ibtn_next.setOnClickListener(this);
        ibtn_prev.setOnClickListener(this);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                tv_question.setText(questionArrayList.get(currentQuestionindex).getAnswer());
                tv_counter.setText(currentQuestionindex + " / " + questionArrayList.size());
                Log.d("Main", "processFinished: " + questionArrayList);
            }
        });

        SharedPreferences getSharedData = getSharedPreferences("sp", MODE_PRIVATE);

        highScore = getSharedData.getInt("highScore", 0);
        tv_highScore.setText("Your high score is " + highScore);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_prev:
                if (currentQuestionindex != 0) {
                    currentQuestionindex = (currentQuestionindex - 1) % questionList.size();
                    updateQuestion();
                }
                if (currentQuestionindex == 0) {
                    tv_question.setText("No Previous Question");
                }
                break;
            case R.id.ibtn_next:
                currentQuestionindex = (currentQuestionindex + 1) % questionList.size();
                updateQuestion();
                break;
            case R.id.btn_true:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.btn_false:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void checkAnswer(boolean userChoose) {
        boolean answer = questionList.get(currentQuestionindex).isAnswerTrue(); // get answer from JSON array
        int toastMsgId;
        if (userChoose == answer) {
            fadeView();
            toastMsgId = R.string.correct_answer;
            score++;
            // don't save when score is less than high score
            if (score > highScore) {
                SharedPreferences sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("highScore", score);
                editor.apply();
                tv_highScore.setText("Your high score is " + score);
            }
            tv_score.setText("Your Score is:" + score);

        } else {
            shakeAnimation();
            toastMsgId = R.string.wrong_answer;
            if (score > 0) {
                score--;
            }
            tv_score.setText("Your Score is:" + score);


        }
        Toast.makeText(MainActivity.this, toastMsgId, Toast.LENGTH_SHORT).show();
        updateQuestion();
        currentQuestionindex = (currentQuestionindex + 1) % questionList.size();
    }

    public void updateQuestion() {
        String question = questionList.get(currentQuestionindex).getAnswer();
        tv_question.setText(question);
        tv_counter.setText(MessageFormat.format("{0} / {1}", currentQuestionindex, questionList.size()));
    }


    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
