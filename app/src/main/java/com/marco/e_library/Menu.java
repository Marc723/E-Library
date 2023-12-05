package com.marco.e_library;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.marco.e_library.databinding.MenuBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Menu extends AppCompatActivity {

    private MenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Buku book = getIntent().getParcelableExtra("SELECTED_BOOK");

        if (book != null) {
            Picasso.get().load(book.getPhotoUrl()).into(binding.imgItemPhoto);
            binding.tvItemName.setText(book.getName());
            binding.tvItemDescription.setText(book.getDescription());
            binding.tvItemAuthors.setText(book.getAuthors());

            // Fetch detailed information using AsyncTask
            new FetchBookDetailsTask().execute("https://www.dbooks.org/api/book/" + book.getId());
        }
    }

    private class FetchBookDetailsTask extends AsyncTask<String, Void, String> {
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
            } else {
                Toast.makeText(Menu.this, "Failed to fetch detailed data from API", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseJsonResponse(String jsonResponse) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);

                // Extract detailed information
                String title = jsonObject.getString("title");
                String id = jsonObject.getString("id");
                String authors = jsonObject.getString("authors");
                String description = jsonObject.getString("description");
                String imageUrl = jsonObject.getString("image"); // Assuming there is an "image" field in your JSON

                // Update UI with the extracted information
                binding.tvItemName.setText("Judul: " + title);
                binding.tvItemAuthors.setText("Penulis: " + authors);
                binding.tvItemDescription.setText("Deskripsi: " + description);
                Picasso.get().load(imageUrl).into(binding.imgItemPhoto);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
