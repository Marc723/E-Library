package com.marco.e_library.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marco.e_library.Buku;
import com.marco.e_library.GridBukuAdapter;
import com.marco.e_library.ListBukuAdapter;
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
        return root;
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
