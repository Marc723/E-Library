package com.marco.e_library.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marco.e_library.Buku;
import com.marco.e_library.GridBukuAdapter;
import com.marco.e_library.R;
import com.marco.e_library.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView rvHeroes;
    private GridBukuAdapter gridHeroAdapter;
    private TextView hiUserTextView;
    private TextView textViewUsername; // Added to display the username
    private ArrayList<Buku> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvHeroes = binding.rvHeroes;
        rvHeroes.setHasFixedSize(true);

        textViewUsername = root.findViewById(R.id.textView6); // Added to reference the TextView for the username
        gridHeroAdapter = new GridBukuAdapter(list);
        rvHeroes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHeroes.setAdapter(gridHeroAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If the user is authenticated, get the display name (replace with your desired user property)
            String username = currentUser.getDisplayName();

            if (username != null && !username.isEmpty()) {
                // Set the "Hi, user" text with the actual username
                hiUserTextView.setText("Hi, " + username);
                textViewUsername.setText(username); // Set the username in the TextView
            }

            // Fetch additional user data (username) from Firestore
            fetchUserData();
        }

        // Click listeners for notification and card clicks
        ImageView notificationIcon = root.findViewById(R.id.notification);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationClick();
            }
        });

        CardView favoriteCard = root.findViewById(R.id.favorite_card);
        favoriteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoritesCardClick();
            }
        });

        CardView genreCard = root.findViewById(R.id.genre_card);
        genreCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenreCardClick();
            }
        });

        CardView locationCard = root.findViewById(R.id.location_card);
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationCardClick();
            }
        });

        new FetchBooksTask().execute("https://www.dbooks.org/api/recent");

        return root;
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
                        String hiUsername = "Hi, " + username;
                        textViewUsername.setText(hiUsername); // Update the username in the TextView
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

    private void onNotificationClick() {
        Toast.makeText(requireContext(), "Notification Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for notification click here
    }

    private void onFavoritesCardClick() {
        Toast.makeText(requireContext(), "Favorites Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for favorites card click here
    }

    private void onGenreCardClick() {
        Toast.makeText(requireContext(), "Genre Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for genre card click here
    }

    private void onLocationCardClick() {
        Toast.makeText(requireContext(), "Location Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for location card click here
    }

    private class FetchBooksTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (urls.length == 0) {
                return null;
            }

            String apiUrl = urls[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream stream = connection.getInputStream();
                    Scanner scanner = new Scanner(stream);
                    scanner.useDelimiter("\\A");

                    if (scanner.hasNext()) {
                        return scanner.next();
                    } else {
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseJsonResponse(result);
                gridHeroAdapter.notifyDataSetChanged(); // Notify adapter of data change
            } else {
                Toast.makeText(requireContext(), "Buku sudah tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseJsonResponse(String jsonResponse) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);

                if (jsonObject.has("books")) {
                    JSONArray booksArray = jsonObject.getJSONArray("books");

                    for (int i = 0; i < booksArray.length(); i++) {
                        JSONObject bookObject = booksArray.getJSONObject(i);

                        String id = bookObject.getString("id"); // Extract the "id" field
                        String name = bookObject.getString("title");
                        String authors = bookObject.getString("authors");
                        String photoUrl = bookObject.getString("image");

                        Buku book = new Buku();
                        book.setId(id);
                        book.setName(name);
                        book.setAuthors(authors);
                        book.setPhotoUrl(photoUrl);

                        list.add(book);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
