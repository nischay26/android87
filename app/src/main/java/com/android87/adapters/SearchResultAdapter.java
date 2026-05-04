package com.android87.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.model.Photo;
import com.android87.model.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }

    private final Context context;
    private final List<Photo> results;
    private final OnPhotoClickListener listener;

    public SearchResultAdapter(Context context, List<Photo> results, OnPhotoClickListener listener) {
        this.context = context;
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = results.get(position);
        Bitmap bmp = loadThumbnail(photo.getUri(), 150);
        if (bmp != null) holder.iv.setImageBitmap(bmp);
        else holder.iv.setImageResource(android.R.drawable.ic_menu_gallery);

        StringBuilder sb = new StringBuilder();
        for (Tag t : photo.getTags()) sb.append(t).append("\n");
        holder.tvTags.setText(sb.toString().trim());

        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));
    }

    @Override
    public int getItemCount() { return results.size(); }

    private Bitmap loadThumbnail(String uriString, int targetSize) {
        try {
            Uri uri = Uri.parse(uriString);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, opts);
            }
            opts.inSampleSize = PhotoGridAdapter.calculateInSampleSize(opts, targetSize, targetSize);
            opts.inJustDecodeBounds = false;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                return BitmapFactory.decodeStream(in, null, opts);
            }
        } catch (IOException e) {
            return null;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvTags;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_result_thumb);
            tvTags = itemView.findViewById(R.id.tv_result_tags);
        }
    }
}
