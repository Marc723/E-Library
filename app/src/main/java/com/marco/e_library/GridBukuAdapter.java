package com.marco.e_library;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridBukuAdapter extends RecyclerView.Adapter<GridBukuAdapter.ListViewHolder> {
    private ArrayList<Buku> listBuku;
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public GridBukuAdapter(ArrayList<Buku> list) {
        this.listBuku = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_buku, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        Buku buku = listBuku.get(position);

        // Load image using Picasso (replace 'your_image_view_id' with your actual ImageView ID)
        Picasso.get().load(buku.getPhotoUrl()).into(holder.imgPhoto);

        holder.tvName.setText(buku.getName());
        holder.tvDescription.setText(buku.getDescription());

        // In your GridBukuAdapter
        holder.itemView.setOnClickListener(v -> {
            Buku selectedBook = listBuku.get(holder.getAdapterPosition());

            Intent intent = new Intent(holder.itemView.getContext(), Menu.class);

            // Pass the selected Buku object to Menu activity
            intent.putExtra("SELECTED_BOOK", selectedBook);
            Toast.makeText(holder.itemView.getContext(), "Kamu memilih " + selectedBook.getName(), Toast.LENGTH_SHORT).show();
            holder.itemView.getContext().startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return listBuku.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvName, tvDescription;

        ListViewHolder(View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_item_photo);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(Buku data);
    }
}