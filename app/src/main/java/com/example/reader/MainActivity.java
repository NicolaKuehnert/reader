package com.example.reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enterMessage(View view){

        EditText text = findViewById(R.id.editText);
        String message = text.getText().toString();

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("Titel")
                .setCancelable(true)
                .show();


    }
}
