package com.marco.e_library;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.marco.e_library.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    // AsyncTask to perform network operations in the background
    private static class FetchBooksTask extends AsyncTask<Void, Void, ArrayList<Buku>> {

        @Override
        protected ArrayList<Buku> doInBackground(Void... voids) {
            return fetchBooksData();
        }

        @Override
        protected void onPostExecute(ArrayList<Buku> books) {
            // Update your RecyclerView or UI with the fetched data
            // You can create and set the adapter with the fetched data
            // For simplicity, let's assume you have a RecyclerView with your ListBukuAdapter
            ListBukuAdapter adapter = new ListBukuAdapter(books);
            // Set the adapter to your RecyclerView
            // recyclerView.setAdapter(adapter);
        }

        private ArrayList<Buku> fetchBooksData() {
            ArrayList<Buku> booksList = new ArrayList<>();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the API
                URL url = new URL("https://www.dbooks.org/api/recent");

                // Open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null; // Nothing to do
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null; // Stream was empty
                }

                // Parse the JSON response
                parseJsonResponse(buffer.toString(), booksList);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                // Close the connections
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException ignored) {
                    }
                }
            }

            return booksList;
        }

        private void parseJsonResponse(String jsonResponse, ArrayList<Buku> booksList) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("books")) {
                JSONArray booksArray = jsonObject.getJSONArray("books");

                for (int i = 0; i < booksArray.length(); i++) {
                    JSONObject bookObject = booksArray.getJSONObject(i);

                    // Extract data from the JSON object
                    String name = bookObject.getString("title");
                    String description = bookObject.getString("authors");
                    String photoUrl = bookObject.getString("image");

                    // Create a Buku object and add it to the list
                    Buku book = new Buku();
                    book.setName(name);
                    book.setDescription(description);
                    book.setPhotoUrl(photoUrl); // Set the photo URL
                    booksList.add(book);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (item.getItemId() == R.id.navigation_home) {
            navController.navigate(R.id.navigation_home);
        } else if (item.getItemId() == R.id.navigation_dashboard) {
            navController.navigate(R.id.navigation_dashboard);
        } else if (item.getItemId() == R.id.navigation_profile) {
            navController.navigate(R.id.navigation_profile);
        }
        return super.onOptionsItemSelected(item);
    }
}