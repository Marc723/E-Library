package com.marco.e_library.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marco.e_library.EditProfileActivity;
import com.marco.e_library.LoginActivity;
import com.marco.e_library.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set onClickListeners for the different sections
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Optionally, you can navigate to the EditProfileActivity
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logout the user
                FirebaseAuth.getInstance().signOut();

                // Optionally, you can navigate to the LoginActivity or any other desired screen after logout
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish(); // Close the current activity to prevent going back to the logged-in state
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAccountConfirmationDialog();
            }
        });

        // Set user information in the UI
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            binding.textView5.setText(email);

            // Fetch additional user data (username) from Firestore
            fetchUserData();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Method to handle section clicks and display a Toast
    private void onSectionClick(String sectionName) {
        // Display a Toast for the clicked section
        Toast.makeText(requireContext(), sectionName + " Clicked", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Yes," log out and then proceed with account deletion
                        logoutAndDeleteAccount();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "No," do nothing or provide additional logic if needed
                    }
                })
                .show();
    }

    private void logoutAndDeleteAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get user ID
            String uid = currentUser.getUid();

            // Reference to the "users" collection in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            // Delete user data in Firestore
            userRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // User data deleted successfully

                    // Now, delete the user account
                    currentUser.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            // Account deleted successfully
                            Toast.makeText(requireContext(), "Account and data deleted", Toast.LENGTH_SHORT).show();

                            // Optionally, you can navigate to the LoginActivity or any other desired screen after account deletion
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish(); // Close the current activity to prevent going back to the logged-in state
                        } else {
                            // Error deleting account
                            Toast.makeText(requireContext(), "Error deleting account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error deleting user data
                    Toast.makeText(requireContext(), "Error deleting user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Assuming you have a "users" collection in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User data found in Firestore
                        String username = document.getString("username");
                        binding.textView14.setText(username);
                    } else {
                        // User data not found in Firestore
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error fetching user data
                    Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
