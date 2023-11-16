package com.marco.e_library.ui.home;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marco.e_library.Buku;
import com.marco.e_library.ListBukuAdapter;
import com.marco.e_library.R;
import com.marco.e_library.databinding.FragmentHomeBinding;

import java.util.ArrayList;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView rvHeroes;
    private ArrayList<Buku> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvHeroes = binding.rvHeroes;

        rvHeroes.setHasFixedSize(true);

        list.addAll(getListHeroes());
        showRecyclerList();
        return root;
    }

    public ArrayList<Buku> getListHeroes(){
        String[] dataName = getResources().getStringArray(R.array.data_name);
        String[] dataDescription = getResources().getStringArray
                (R.array.data_description);
        TypedArray dataPhoto = getResources().obtainTypedArray(R.array.data_photo);
        ArrayList<Buku> listHero = new ArrayList<>();
        for (int i = 0; i < dataName.length; i++){
            Buku hero = new Buku();
            hero.setName(dataName[i]);
            hero.setDescription(dataDescription[i]);
            hero.setPhoto(dataPhoto.getResourceId(i, -1));
            listHero.add(hero);
        }
        return listHero;
    }


    private void showRecyclerList() {
        rvHeroes.setLayoutManager(new LinearLayoutManager(requireContext()));
        ListBukuAdapter listHeroAdapter = new ListBukuAdapter(list);
        rvHeroes.setAdapter(listHeroAdapter);
        listHeroAdapter.setOnItemClickCallback(this::showSelectedHero);
    }

    private void showSelectedHero(Buku hero) {
        Toast.makeText(requireContext(), "Kamu Memilih " + hero.getName(), Toast.LENGTH_SHORT).show();
    }

}