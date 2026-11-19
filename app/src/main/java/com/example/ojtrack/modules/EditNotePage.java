package com.example.ojtrack.modules;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ojtrack.DatabaseHelpers.NotesDbHelper;
import com.example.ojtrack.R;
import com.example.ojtrack.models.NewNoteModel;
import com.example.ojtrack.utils.DeleteNoteDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class EditNotePage extends AppCompatActivity {

    private static final String[] CATEGORIES = {
            "Technical", "Administrative", "Learning", "Meetings", "Daily Tasks",
            "Documentation", "Troubleshooting", "Accomplishments", "Reminders"
    };

    private EditText noteTitle, noteDescription;
    private Button updateBtn;
    private SwitchMaterial mySwitch;
    private View btnBack, btnDelete;
    private LinearLayout categoryContainer;

    private NotesDbHelper dbHelper;
    private NewNoteModel note;
    private int noteId;

    private String isPinned = "no";
    private String category = "Technical";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteTitle = findViewById(R.id.noteTitle);
        noteDescription = findViewById(R.id.noteDescription);
        updateBtn = findViewById(R.id.button);
        mySwitch = findViewById(R.id.mySwitch);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        categoryContainer = findViewById(R.id.categoryContainer);

        dbHelper = new NotesDbHelper(this);

        noteId = getIntent().getIntExtra("note_id", -1);
        note = dbHelper.getNoteById(noteId);

        if (note == null) {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateFields();
        setupCategories();

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                isPinned = isChecked ? "yes" : "no");

        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> confirmDelete());
        updateBtn.setOnClickListener(v -> updateNote());
    }

    private void populateFields() {
        noteTitle.setText(note.getTitle());
        noteDescription.setText(note.getDescription());
        noteTitle.setSelection(noteTitle.getText().length());

        isPinned = "yes".equals(note.getIsPinned()) ? "yes" : "no";
        mySwitch.setChecked("yes".equals(isPinned));

        if (note.getCategory() != null) {
            category = note.getCategory();
        }
    }

    // ---- Category chips (built in code) ----

    private void setupCategories() {
        for (String name : CATEGORIES) {
            TextView chip = new TextView(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            chip.setLayoutParams(lp);

            chip.setText(name);
            chip.setTextSize(14);
            chip.setTypeface(ResourcesCompat.getFont(this, R.font.bricolage));
            chip.setOnClickListener(v -> {
                selectCategory(name);
                popAnimation(chip);
            });

            categoryContainer.addView(chip);
        }
        selectCategory(category); // pre-select the note's saved category
    }

    private void selectCategory(String name) {
        category = name;

        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            TextView chip = (TextView) categoryContainer.getChildAt(i);
            boolean selected = chip.getText().toString().equals(name);

            chip.setBackgroundResource(selected
                    ? R.drawable.category_selected_bg
                    : R.drawable.category_unselected_bg);
            chip.setTextColor(ContextCompat.getColor(this,
                    selected ? R.color.white : R.color.darktext));
            chip.setPadding(dp(16), dp(9), dp(16), dp(9));
        }
    }

    private void popAnimation(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    // ---- Update ----

    private void updateNote() {
        String title = noteTitle.getText().toString().trim();
        String description = noteDescription.getText().toString().trim();

        if (title.isEmpty()) {
            noteTitle.setError("Title is required");
            return;
        }

        note.setTitle(title);
        note.setDescription(description);
        note.setCategory(category);
        note.setIsPinned(isPinned);

        int rows = dbHelper.updateNote(note);
        if (rows > 0) {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show();
        }
    }

    // ---- Delete ----

    private void confirmDelete() {
        // Little shake on the trash button, then the dialog
        ObjectAnimator.ofFloat(btnDelete, View.ROTATION, 0f, -12f, 12f, -8f, 8f, 0f)
                .setDuration(300)
                .start();

        btnDelete.postDelayed(() ->
                DeleteNoteDialog.show(this, note.getTitle(), () -> {
                    dbHelper.deleteNote(noteId);
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }), 150);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}