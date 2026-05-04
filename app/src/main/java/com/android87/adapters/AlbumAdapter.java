package com.android87.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClick(int position);
        void onAlbumLongClick(int position);
    }

    private final List<Album> albums;
    private final OnAlbumClickListener listener;

    public AlbumAdapter(List<Album> albums, OnAlbumClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.tvName.setText(album.getName());
        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(holder.getAdapterPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onAlbumLongClick(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() { return albums.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.id_album_name_text);
        }
    }
}
