package com.marco.e_library.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marco.e_library.Buku;
import com.marco.e_library.GridBukuAdapter;
import com.marco.e_library.ListBukuAdapter;
import com.marco.e_library.R;
import com.marco.e_library.databinding.FragmentHomeBinding;

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
    private ArrayList<Buku> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvHeroes = binding.rvHeroes;
        rvHeroes.setHasFixedSize(true);
        gridHeroAdapter = new GridBukuAdapter(list);
        rvHeroes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        rvHeroes.setAdapter(gridHeroAdapter);
        gridHeroAdapter.setOnItemClickCallback(this::showSelectedHero);
        new FetchBooksTask().execute("https://www.dbooks.org/api/recent");

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

        return root;
    }

    // Method for notification click
    public void onNotificationClick() {
        Toast.makeText(requireContext(), "Notification Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for notification click here
    }

    // Method for favorites card click
    public void onFavoritesCardClick() {
        Toast.makeText(requireContext(), "Favorites Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for favorites card click here
    }

    // Method for genre card click
    public void onGenreCardClick() {
        Toast.makeText(requireContext(), "Genre Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for genre card click here
    }

    // Method for location card click
    public void onLocationCardClick() {
        Toast.makeText(requireContext(), "Location Card Clicked", Toast.LENGTH_SHORT).show();
        // Add your additional logic for location card click here
    }

    private void showSelectedHero(Buku hero) {
        Toast.makeText(requireContext(), "Kamu Memilih " + hero.getName(), Toast.LENGTH_SHORT).show();
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
