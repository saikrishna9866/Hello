package com.thincovate.taskmanager.smartqs.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thincovate.taskmanager.smartqs.R;

public class WelcomeActivity extends AppCompatActivity {

    private TextView tvHyperLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);


        tvHyperLink = (TextView) findViewById(R.id.tvHyperLink);
        Button btnLetsGo = (Button) findViewById(R.id.btnLetsGo);
        //tvHyperLink.setText("<a href=\"http://www.google.com\">Wow! Sounds interesting.. I want to know more about the Time Management Quadrant</a> ");
        tvHyperLink.setText(Html.fromHtml(
                "<a href=\"https://en.wikipedia.org/wiki/Time_management\">Wow! Sounds interesting.. I want to know more about the Time Management Quadrant</a> "));
        //Linkify.addLinks(tvHyperLink, Linkify.ALL);
        tvHyperLink.setMovementMethod(LinkMovementMethod.getInstance());

        /*if (tvHyperLink.getLinksClickable()) {

            tvHyperLink.setLinkTextColor(Color.BLUE);

        }*/

        btnLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }


}
