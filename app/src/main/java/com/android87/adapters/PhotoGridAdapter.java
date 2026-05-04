package com.android87.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.model.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(int position);
        void onPhotoLongClick(int position);
    }

    private final Context context;
    private final List<Photo> photos;
    private final OnPhotoClickListener listener;

    public PhotoGridAdapter(Context context, List<Photo> photos, OnPhotoClickListener listener) {
        this.context = context;
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Bitmap bmp = loadThumbnail(photo.getUri(), 200);
        if (bmp != null) {
            holder.imageView.setImageBitmap(bmp);
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(holder.getAdapterPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onPhotoLongClick(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() { return photos.size(); }

    private Bitmap loadThumbnail(String uriString, int targetSize) {
        try {
            Uri uri = Uri.parse(uriString);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, opts);
            }
            opts.inSampleSize = calculateInSampleSize(opts, targetSize, targetSize);
            opts.inJustDecodeBounds = false;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                return BitmapFactory.decodeStream(in, null, opts);
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqW, int reqH) {
        int height = options.outHeight, width = options.outWidth, inSampleSize = 1;
        if (height > reqH || width > reqW) {
            int halfH = height / 2, halfW = width / 2;
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo_thumb);
        }
    }
}
