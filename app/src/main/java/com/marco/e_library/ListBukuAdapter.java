package com.marco.e_library;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListBukuAdapter extends RecyclerView.Adapter<ListBukuAdapter.ListViewHolder> {
    private ArrayList<Buku> listHero;
    private OnItemClickCallback onItemClickCallback;
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback){
        this.onItemClickCallback = onItemClickCallback;
    }
    public ListBukuAdapter(ArrayList<Buku> list) { this.listHero = list; }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_row_hero, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        Buku hero = listHero.get(position);
        holder.imgPhoto.setImageResource(hero.getPhoto());
        holder.tvName.setText(hero.getName());
        holder.tvDescription.setText(hero.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Buku selectedHero = listHero.get(holder.getAdapterPosition());
            Intent intent = new Intent(holder.itemView.getContext(), Menu.class);
            intent.putExtra("HERO_PHOTO", hero.getPhoto());
            intent.putExtra("HERO_NAME", hero.getName());
            intent.putExtra("HERO_DESCRIPTION", hero.getDescription());
            Toast.makeText(holder.itemView.getContext(), "Kamu memilih " + listHero.get(holder.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            holder.itemView.getContext().startActivity(intent);
        });
    }

    public interface OnItemClickCallback{
        void onItemClicked(Buku data);
    }

    @Override
    public int getItemCount() {
        return listHero.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvName, tvDescription;

        ListViewHolder(View itemView){
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_item_photo);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
        }
    }
}