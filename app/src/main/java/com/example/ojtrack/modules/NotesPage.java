package com.example.ojtrack.modules;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojtrack.DatabaseHelpers.NotesDbHelper;
import com.example.ojtrack.R;
import com.example.ojtrack.adapters.NotesAdapter;
import com.example.ojtrack.models.NewNoteModel;
import com.example.ojtrack.utils.DeleteNoteDialog;

import java.util.ArrayList;
import java.util.List;

public class NotesPage extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {
    private ImageView homeBtn, ojtBtn;
    private Button btnAdd;
    private EditText etSearch;
    private RecyclerView rvPinned, rvNotes;
    private View pinnedSection;
    private ViewGroup contentLayout;
    private TextView tvEmpty;

    private NotesDbHelper dbHelper;
    private NotesAdapter pinnedAdapter, notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_page);

        homeBtn = findViewById(R.id.homeBtn);
        ojtBtn = findViewById(R.id.ojtBtn);
        btnAdd = findViewById(R.id.btnAdd);
        etSearch = findViewById(R.id.etSearch);
        rvPinned = findViewById(R.id.rvPinned);
        rvNotes = findViewById(R.id.rvNotes);
        pinnedSection = findViewById(R.id.pinnedSection);
        contentLayout = findViewById(R.id.contentLayout);
        tvEmpty = findViewById(R.id.tvEmpty);

        dbHelper = new NotesDbHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerViews();
        setupSearch();

        homeBtn.setOnClickListener(d -> {
            startActivity(new Intent(NotesPage.this, HomePage.class));
            finish();
        });

        ojtBtn.setOnClickListener(g -> {
            startActivity(new Intent(NotesPage.this, OJTHoursPage.class));
            finish();
        });

        btnAdd.setOnClickListener(d ->
                startActivity(new Intent(NotesPage.this, CreateNotesPage.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cards fall in with an animation whenever the page is shown
        loadNotes(etSearch.getText().toString().trim(), true);
    }

    private void setupRecyclerViews() {
        pinnedAdapter = new NotesAdapter(this);
        notesAdapter = new NotesAdapter(this);

        rvPinned.setLayoutManager(new GridLayoutManager(this, 2));
        rvPinned.setAdapter(pinnedAdapter);
        rvPinned.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down));

        rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        rvNotes.setAdapter(notesAdapter);
        rvNotes.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down));
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No fall-in animation while typing, it would flicker
                loadNotes(s.toString().trim(), false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadNotes(String keyword, boolean animate) {
        List<NewNoteModel> all = keyword.isEmpty()
                ? dbHelper.getAllNotes()
                : dbHelper.searchNotes(keyword);

        List<NewNoteModel> pinned = new ArrayList<>();
        List<NewNoteModel> others = new ArrayList<>();

        for (NewNoteModel note : all) {
            if ("yes".equals(note.getIsPinned())) {
                pinned.add(note);
            } else {
                others.add(note);
            }
        }

        pinnedAdapter.setNotes(pinned);
        notesAdapter.setNotes(others);
        updateVisibility();

        if (animate) {
            rvPinned.scheduleLayoutAnimation();
            rvNotes.scheduleLayoutAnimation();
        }
    }

    private void updateVisibility() {
        boolean hasPinned = pinnedAdapter.getItemCount() > 0;
        boolean isEmpty = !hasPinned && notesAdapter.getItemCount() == 0;

        pinnedSection.setVisibility(hasPinned ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    // ---- Card interactions ----

    @Override
    public void onNoteClick(NewNoteModel note) {
        Intent intent = new Intent(this, EditNotePage.class);
        intent.putExtra("note_id", note.getNoteId());
        startActivity(intent);
    }

    @Override
    public void onNoteLongClick(NewNoteModel note) {
        DeleteNoteDialog.show(this, note.getTitle(), () -> {
            dbHelper.deleteNote(note.getNoteId());

            // Smoothly collapse the section if it becomes empty
            TransitionManager.beginDelayedTransition(contentLayout);
            pinnedAdapter.removeNote(note.getNoteId());
            notesAdapter.removeNote(note.getNoteId());
            updateVisibility();
        });
    }
}