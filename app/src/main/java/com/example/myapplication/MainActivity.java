package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends menuActivity {

    Button button;
    EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Load();

        button = findViewById(R.id.button);
        nameInput = findViewById(R.id.nameInput);

        Intent nextPage = new Intent(this, DatePicker.class);
        button.setOnClickListener(v -> {
            if (nameInput.getText().length() > 0) {
                nextPage.putExtra("name", nameInput.getText().toString());
                startActivityForResult(nextPage, 1);
            }
            else {
                Toast.makeText(this, "WOW that's a lot!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        nameInput.setText(name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("name", nameInput.getText().toString());
        edit.commit();
    }
}