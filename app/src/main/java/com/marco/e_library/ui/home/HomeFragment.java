package com.marco.e_library.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
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
    private ListBukuAdapter listHeroAdapter;
    private ArrayList<Buku> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvHeroes = binding.rvHeroes;

        rvHeroes.setHasFixedSize(true);

        listHeroAdapter = new ListBukuAdapter(list);
        rvHeroes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHeroes.setAdapter(listHeroAdapter);
        listHeroAdapter.setOnItemClickCallback(this::showSelectedHero);

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
                listHeroAdapter.notifyDataSetChanged(); // Notify adapter of data change
            } else {
                Toast.makeText(requireContext(), "Failed to fetch data from API", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseJsonResponse(String jsonResponse) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);

                if (jsonObject.has("books")) {
                    JSONArray booksArray = jsonObject.getJSONArray("books");

                    for (int i = 0; i < booksArray.length(); i++) {
                        JSONObject bookObject = booksArray.getJSONObject(i);

                        String name = bookObject.getString("title");
                        String description = bookObject.getString("authors");
                        String photoUrl = bookObject.getString("image");

                        Buku book = new Buku();
                        book.setName(name);
                        book.setDescription(description);
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
