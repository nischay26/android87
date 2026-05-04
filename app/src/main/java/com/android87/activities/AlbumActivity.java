package com.android87.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.adapters.PhotoGridAdapter;
import com.android87.model.Album;
import com.android87.model.Photo;
import com.android87.storage.StorageManager;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private int albumIndex;
    private Album album;
    private PhotoGridAdapter adapter;

    private final ActivityResultLauncher<Intent> photoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                adapter.notifyDataSetChanged();
                StorageManager.saveAlbums(this, HomeActivity.albums);
            });

    private final ActivityResultLauncher<String[]> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                album.addPhoto(new Photo(uri.toString()));
                adapter.notifyItemInserted(album.getPhotoCount() - 1);
                StorageManager.saveAlbums(this, HomeActivity.albums);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumIndex = getIntent().getIntExtra("albumIndex", 0);
        album = HomeActivity.albums.get(albumIndex);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(album.getName());

        RecyclerView rv = findViewById(R.id.rv_photos);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoGridAdapter(this, album.getPhotos(), new PhotoGridAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(int position) {
                Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
                intent.putExtra("albumIndex", albumIndex);
                intent.putExtra("photoIndex", position);
                photoLauncher.launch(intent);
            }

            @Override
            public void onPhotoLongClick(int position) {
                showPhotoOptions(position);
            }
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btn_add_photo).setOnClickListener(v ->
                pickImageLauncher.launch(new String[]{"image/*"}));
    }

    @Override
    protected void onPause() {
        super.onPause();
        StorageManager.saveAlbums(this, HomeActivity.albums);
    }

    private void showPhotoOptions(int position) {
        List<Album> albums = HomeActivity.albums;
        String[] options = {getString(R.string.remove_option), getString(R.string.move_to_option)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.photo_options_title)
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        album.removePhoto(album.getPhotos().get(position));
                        adapter.notifyItemRemoved(position);
                        StorageManager.saveAlbums(this, albums);
                    } else {
                        showMoveDialog(position);
                    }
                })
                .show();
    }

    private void showMoveDialog(int position) {
        List<Album> albums = HomeActivity.albums;
        String[] names = albums.stream()
                .filter(a -> a != album)
                .map(Album::getName)
                .toArray(String[]::new);
        if (names.length == 0) {
            Toast.makeText(this, R.string.no_other_albums, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.move_to_album_title)
                .setItems(names, (d, which) -> {
                    Photo photo = album.getPhotos().get(position);
                    Album target = albums.stream()
                            .filter(a -> a.getName().equals(names[which]))
                            .findFirst().orElse(null);
                    if (target != null) {
                        target.addPhoto(photo);
                        album.removePhoto(photo);
                        adapter.notifyItemRemoved(position);
                        StorageManager.saveAlbums(this, albums);
                    }
                })
                .show();
    }
}
