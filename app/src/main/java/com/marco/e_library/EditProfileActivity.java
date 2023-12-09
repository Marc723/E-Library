package com.marco.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button submitButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        submitButton = findViewById(R.id.submitButton);

        db = FirebaseFirestore.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String newUsername = editTextUsername.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();

            // Update the username in Firestore using UID as the identifier
            updateUsername(currentUser.getUid(), newUsername);

            // Update the password using Firebase Authentication
            if (!newPassword.isEmpty()) {
                updatePassword(newPassword);
            }

            // Navigate back to MainActivity
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void updateUsername(String uid, String newUsername) {
        DocumentReference userRef = db.collection("users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);

        userRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                            if (task.getException() != null) {
                                Log.e("username", "Error updating username: " + task.getException().getMessage());
                            }
                        }
                    }
                });
    }




    private void updatePassword(String newPassword) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
