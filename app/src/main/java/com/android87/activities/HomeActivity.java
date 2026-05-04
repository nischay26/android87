package com.android87.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.adapters.AlbumAdapter;
import com.android87.model.Album;
import com.android87.storage.StorageManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    static List<Album> albums;
    private AlbumAdapter adapter;

    private final ActivityResultLauncher<Intent> albumLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                adapter.notifyDataSetChanged();
                StorageManager.saveAlbums(this, albums);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albums = StorageManager.loadAlbums(this);

        RecyclerView rv = findViewById(R.id.rv_albums);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumAdapter(albums, new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(int position) {
                Intent intent = new Intent(HomeActivity.this, AlbumActivity.class);
                intent.putExtra("albumIndex", position);
                albumLauncher.launch(intent);
            }

            @Override
            public void onAlbumLongClick(int position) {
                showAlbumOptions(position);
            }
        });
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_album);
        fab.setOnClickListener(v -> showCreateAlbumDialog());

        findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });

        findViewById(R.id.btn_info_bottom).setOnClickListener(v -> {
            Toast.makeText(this, R.string.app_credits, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (albums != null) StorageManager.saveAlbums(this, albums);
    }

    private void showCreateAlbumDialog() {
        EditText input = new EditText(this);
        input.setHint(R.string.album_name_hint);
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_album_title)
                .setView(input)
                .setPositiveButton(R.string.create_btn, (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    albums.add(new Album(name));
                    adapter.notifyItemInserted(albums.size() - 1);
                    StorageManager.saveAlbums(this, albums);
                })
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }

    private void showAlbumOptions(int position) {
        new AlertDialog.Builder(this)
                .setTitle(albums.get(position).getName())
                .setItems(new String[]{getString(R.string.rename_btn), getString(R.string.delete_btn)}, (d, which) -> {
                    if (which == 0) showRenameDialog(position);
                    else showDeleteDialog(position);
                })
                .show();
    }

    private void showRenameDialog(int position) {
        EditText input = new EditText(this);
        input.setText(albums.get(position).getName());
        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_album_title)
                .setView(input)
                .setPositiveButton(R.string.rename_btn, (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    albums.get(position).setName(name);
                    adapter.notifyItemChanged(position);
                    StorageManager.saveAlbums(this, albums);
                })
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_album_title)
                .setMessage(getString(R.string.delete_album_message, albums.get(position).getName()))
                .setPositiveButton(R.string.delete_btn, (d, w) -> {
                    albums.remove(position);
                    adapter.notifyItemRemoved(position);
                    StorageManager.saveAlbums(this, albums);
                })
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }
}
