package com.marco.e_library;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Buku book = getIntent().getParcelableExtra("SELECTED_BOOK");

        if (book != null) {
            // Use Picasso to load the image from the URL
            ImageView imageView = findViewById(R.id.img_item_photo);
            Picasso.get().load(book.getPhotoUrl()).into(imageView);

            TextView nameTextView = findViewById(R.id.tv_item_name);
            TextView descriptionTextView = findViewById(R.id.tv_item_description);

            // Set the views with the data from the Buku object
            nameTextView.setText(book.getName());
            descriptionTextView.setText(book.getDescription());
        }
    }
}
