package com.android87.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android87.R;
import com.android87.adapters.PhotoGridAdapter;
import com.android87.model.Album;
import com.android87.model.Photo;
import com.android87.model.Tag;
import com.android87.storage.StorageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private int albumIndex;
    private int photoIndex;
    private Album album;
    private ImageView ivPhoto;
    private LinearLayout llTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        albumIndex = getIntent().getIntExtra("albumIndex", 0);
        photoIndex = getIntent().getIntExtra("photoIndex", 0);
        album = HomeActivity.albums.get(albumIndex);

        ivPhoto = findViewById(R.id.iv_full_photo);
        llTags = findViewById(R.id.ll_tags);

        findViewById(R.id.btn_prev).setOnClickListener(v -> navigate(-1));
        findViewById(R.id.btn_next).setOnClickListener(v -> navigate(1));
        findViewById(R.id.btn_add_tag).setOnClickListener(v -> showAddTagDialog());
        findViewById(R.id.btn_delete_tag).setOnClickListener(v -> showDeleteTagDialog());

        displayPhoto();
    }

    private void navigate(int delta) {
        int size = album.getPhotoCount();
        photoIndex = (photoIndex + delta + size) % size;
        displayPhoto();
    }

    private void displayPhoto() {
        Photo photo = album.getPhotos().get(photoIndex);
        loadFullImage(photo.getUri());
        renderTags(photo);
    }

    private void loadFullImage(String uriString) {
        try {
            Uri uri = Uri.parse(uriString);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            try (InputStream in = getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, opts);
            }
            opts.inSampleSize = PhotoGridAdapter.calculateInSampleSize(opts, 1080, 1080);
            opts.inJustDecodeBounds = false;
            Bitmap bmp;
            try (InputStream in = getContentResolver().openInputStream(uri)) {
                bmp = BitmapFactory.decodeStream(in, null, opts);
            }
            ivPhoto.setImageBitmap(bmp);
        } catch (IOException e) {
            ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void renderTags(Photo photo) {
        llTags.removeAllViews();
        for (Tag tag : photo.getTags()) {
            TextView tv = new TextView(this);
            tv.setText(tag.toString());
            tv.setTextSize(14f);
            tv.setPadding(0, 4, 0, 4);
            llTags.addView(tv);
        }
        if (photo.getTags().isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(R.string.no_tags_text);
            tv.setTextColor(0xFF888888);
            llTags.addView(tv);
        }
    }

    private void showAddTagDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
        Spinner spinner = view.findViewById(R.id.spinner_tag_type);
        EditText etValue = view.findViewById(R.id.et_tag_value);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{getString(R.string.tag_person), getString(R.string.tag_location)});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equalsIgnoreCase(getString(R.string.tag_person))) {
                    etValue.setHint(R.string.tag_value_hint_person);
                } else {
                    etValue.setHint(R.string.tag_value_hint_location);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_tag_title)
                .setView(view)
                .setPositiveButton(R.string.add_btn, (d, w) -> {
                    String type = spinner.getSelectedItem().toString();
                    String value = etValue.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(this, R.string.error_empty_tag_value, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Photo photo = album.getPhotos().get(photoIndex);
                    if (!photo.addTag(type, value)) {
                        Toast.makeText(this, R.string.error_tag_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    renderTags(photo);
                    StorageManager.saveAlbums(this, HomeActivity.albums);
                })
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }

    private void showDeleteTagDialog() {
        Photo photo = album.getPhotos().get(photoIndex);
        List<Tag> tags = photo.getTags();
        if (tags.isEmpty()) {
            Toast.makeText(this, R.string.no_tags_to_delete, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] labels = tags.stream().map(Tag::toString).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_tag_title)
                .setItems(labels, (d, which) -> {
                    photo.removeTag(tags.get(which));
                    renderTags(photo);
                    StorageManager.saveAlbums(this, HomeActivity.albums);
                })
                .show();
    }
}
