package com.b07.salesandroid.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.TransitionButtonController;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    setTitle("CSCB07F19 Android Sales Application");

    Button login = findViewById(R.id.loginPageButton);
    login.setOnClickListener(new TransitionButtonController(this));

    Button initialize = findViewById(R.id.initializeButton);
    initialize.setOnClickListener(new TransitionButtonController(this));
  }

}
