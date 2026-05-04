package com.android87.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android87.R;
import com.android87.adapters.SearchResultAdapter;
import com.android87.model.Album;
import com.android87.model.Photo;
import com.android87.model.Tag;
import com.android87.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinnerType1, spinnerType2, spinnerAndOr;
    private AutoCompleteTextView actvValue1, actvValue2;
    private Switch switchTwoTags;
    private RecyclerView rvResults;
    private TextView tvResultCount;
    private List<Album> albums;
    private ArrayAdapter<String> adapter1, adapter2;
    private List<String> suggestions1 = new ArrayList<>(), suggestions2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        albums = StorageManager.loadAlbums(this);

        spinnerType1 = findViewById(R.id.spinner_type1);
        spinnerType2 = findViewById(R.id.spinner_type2);
        spinnerAndOr = findViewById(R.id.spinner_and_or);
        actvValue1 = findViewById(R.id.actv_value1);
        actvValue2 = findViewById(R.id.actv_value2);
        switchTwoTags = findViewById(R.id.switch_two_tags);
        rvResults = findViewById(R.id.rv_results);
        tvResultCount = findViewById(R.id.tv_result_count);
        Button btnSearch = findViewById(R.id.btn_search);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{getString(R.string.tag_person), getString(R.string.tag_location)});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType1.setAdapter(typeAdapter);
        spinnerType2.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{getString(R.string.tag_person), getString(R.string.tag_location)}));
        ((ArrayAdapter<?>) spinnerType2.getAdapter())
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> andOrAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"AND", "OR"});
        andOrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAndOr.setAdapter(andOrAdapter);

        rvResults.setLayoutManager(new LinearLayoutManager(this));

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions1);
        actvValue1.setAdapter(adapter1);
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions2);
        actvValue2.setAdapter(adapter2);

        switchTwoTags.setOnCheckedChangeListener((btn, checked) -> {
            int vis = checked ? android.view.View.VISIBLE : android.view.View.GONE;
            spinnerType2.setVisibility(vis);
            actvValue2.setVisibility(vis);
            spinnerAndOr.setVisibility(vis);
        });
        spinnerType2.setVisibility(android.view.View.GONE);
        actvValue2.setVisibility(android.view.View.GONE);
        spinnerAndOr.setVisibility(android.view.View.GONE);

        // Clear and update auto-complete when tag type changes
        android.widget.AdapterView.OnItemSelectedListener typeListener = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if (parent == spinnerType1) {
                    updateSuggestions(suggestions1, adapter1, selectedType);
                } else if (parent == spinnerType2) {
                    updateSuggestions(suggestions2, adapter2, selectedType);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        };
        spinnerType1.setOnItemSelectedListener(typeListener);
        spinnerType2.setOnItemSelectedListener(typeListener);

        // Explicitly set threshold to 1 and fix auto-complete behavior
        actvValue1.setThreshold(1);
        actvValue2.setThreshold(1);
        
        // Initial suggestion load
        updateSuggestions(suggestions1, adapter1, getString(R.string.tag_person));
        updateSuggestions(suggestions2, adapter2, getString(R.string.tag_person));

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void updateSuggestions(List<String> suggestions, ArrayAdapter<String> adapter, String tagType) {
        suggestions.clear();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getName().equalsIgnoreCase(tagType)) {
                        String value = tag.getValue();
                        boolean alreadyAdded = false;
                        for (String s : suggestions) {
                            if (s.equalsIgnoreCase(value)) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded) {
                            suggestions.add(value);
                        }
                    }
                }
            }
        }
        // Force a fresh adapter to ensure AutoCompleteTextView doesn't keep old filter state
        if (adapter == adapter1) {
            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions1);
            actvValue1.setAdapter(adapter1);
        } else if (adapter == adapter2) {
            adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions2);
            actvValue2.setAdapter(adapter2);
        }
    }

    private void performSearch() {
        String type1 = spinnerType1.getSelectedItem().toString();
        String val1 = actvValue1.getText().toString().trim();
        if (val1.isEmpty()) return;

        boolean twoTags = switchTwoTags.isChecked();
        String type2 = twoTags ? spinnerType2.getSelectedItem().toString() : null;
        String val2 = twoTags ? actvValue2.getText().toString().trim() : null;
        String andOr = twoTags ? spinnerAndOr.getSelectedItem().toString() : "SINGLE";

        Tag tag1 = new Tag(type1, val1);
        Tag tag2 = (type2 != null && val2 != null && !val2.isEmpty()) ? new Tag(type2, val2) : null;

        List<Photo> results = new ArrayList<>();
        for (Album album : albums) {
            for (Photo p : album.getPhotos()) {
                boolean hasTag1 = p.getTags().contains(tag1);
                boolean hasTag2 = (tag2 != null) && p.getTags().contains(tag2);
                boolean match;
                if (!twoTags || tag2 == null) {
                    match = hasTag1;
                } else if ("AND".equals(andOr)) {
                    match = hasTag1 && hasTag2;
                } else {
                    match = hasTag1 || hasTag2;
                }
                if (match && !results.contains(p)) results.add(p);
            }
        }

        tvResultCount.setText(getString(R.string.result_count_format, results.size()));
        rvResults.setAdapter(new SearchResultAdapter(this, results, photo -> {
            for (int aIdx = 0; aIdx < albums.size(); aIdx++) {
                List<Photo> albumPhotos = albums.get(aIdx).getPhotos();
                for (int pIdx = 0; pIdx < albumPhotos.size(); pIdx++) {
                    if (albumPhotos.get(pIdx).getUri().equals(photo.getUri())) {
                        android.content.Intent intent = new android.content.Intent(this, PhotoActivity.class);
                        intent.putExtra("albumIndex", aIdx);
                        intent.putExtra("photoIndex", pIdx);
                        startActivity(intent);
                        return;
                    }
                }
            }
        }));
    }
}
