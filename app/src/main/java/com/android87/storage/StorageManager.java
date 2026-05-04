package com.android87.storage;

import android.content.Context;

import com.android87.model.Album;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private static final String FILE_NAME = "albums.dat";

    public static void saveAlbums(Context ctx, List<Album> albums) {
        File file = new File(ctx.getFilesDir(), FILE_NAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Album> loadAlbums(Context ctx) {
        File file = new File(ctx.getFilesDir(), FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
