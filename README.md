# Android87 – Photo Album App

An Android app for organizing photos into albums, tagging them, and searching by tag. Built in Java with Android Studio as a team project for CS 213 (Software Methodology) at Rutgers, by Nischay Agarwal and Matthew Wang.

## Features

- **Albums** – create, rename, and delete albums; home screen lists every album with its photo count
- **Photos** – add photos from the device gallery, view them in a grid, remove them, or move them to another album
- **Photo viewer** – full-size view with previous/next navigation through the album (slideshow)
- **Tags** – add or delete `person` and `location` tags on any photo, with duplicate-tag checks
- **Search** – find photos across all albums by one tag or two tags combined with **AND** / **OR**, with autocomplete on tag values
- **Persistence** – albums, photos, and tags are saved between app launches

## Tech Stack

- **Language:** Java
- **UI:** Android XML layouts, RecyclerView, CardView, BottomAppBar
- **Storage:** Java serialization (`ObjectOutputStream`) to app-private storage
- **Target:** Android API 36

## Design

```
com.android87
├── activities/   HomeActivity, AlbumActivity, PhotoActivity, SearchActivity
├── adapters/     AlbumAdapter, PhotoGridAdapter, SearchResultAdapter
├── model/        Album, Photo, Tag (Serializable)
└── storage/      StorageManager (load/save all albums)
```

- Model classes are kept separate from activities, so UI code never handles file I/O directly.
- Photos are stored as content URIs with persistable read permission, so the app doesn't copy image files.
- Thumbnails and full-size images are downsampled with `BitmapFactory.inSampleSize` to keep memory use low on large photos.

## Running It

1. Clone the repo and open the folder in Android Studio.
2. Let Gradle sync.
3. Run on an emulator or device with Android API 36 (tested on a Pixel 6 emulator).

## Notes

How generative AI tools were used during development is documented in [GENAI_USAGE.md](GENAI_USAGE.md), as the assignment required.
