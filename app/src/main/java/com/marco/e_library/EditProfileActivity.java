package com.marco.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Add any additional setup code if needed
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the submit button click event

                // You can add code here to save the edited profile data if needed

                // Navigate back to the ProfileFragment
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                // Assuming MainActivity hosts the ProfileFragment
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Finish the EditProfileActivity to remove it from the back stack
            }
        });
    }
}