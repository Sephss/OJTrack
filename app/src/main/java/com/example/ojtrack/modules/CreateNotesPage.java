package com.example.ojtrack.modules;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ojtrack.DatabaseHelpers.NotesDbHelper;
import com.example.ojtrack.R;
import com.example.ojtrack.models.NewNoteModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class CreateNotesPage extends AppCompatActivity {
    private EditText noteTitle, noteDescription;
    private Button saveNoteBtn;
    private SwitchMaterial mySwitch;
    private String isPinned = "no";
    private String category = "Technical";
    private NotesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_notes_page);

        noteTitle = findViewById(R.id.noteTitle);
        noteDescription = findViewById(R.id.noteDescription);
        saveNoteBtn = findViewById(R.id.button);
        mySwitch = findViewById(R.id.mySwitch);

        dbHelper = new NotesDbHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCategorySelection();

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPinned = isChecked ? "yes" : "no";
        });

        saveNoteBtn.setOnClickListener(d -> {
            createNote();
        });
    }

    private void setupCategorySelection() {

        LinearLayout categoryContainer = findViewById(R.id.categoryContainer);

        for (int i = 0; i < categoryContainer.getChildCount(); i++) {

            View child = categoryContainer.getChildAt(i);

            if (child instanceof TextView) {

                TextView categoryView = (TextView) child;

                categoryView.setOnClickListener(v -> {

                    // Update category value
                    category = categoryView.getText().toString();

                    // Reset all categories
                    for (int j = 0; j < categoryContainer.getChildCount(); j++) {

                        View item = categoryContainer.getChildAt(j);

                        if (item instanceof TextView) {

                            TextView textView = (TextView) item;

                            textView.setBackgroundResource(
                                    R.drawable.category_unselected_bg
                            );

                            textView.setTextColor(
                                    ContextCompat.getColor(this, R.color.darktext)
                            );
                        }
                    }

                    // Apply selected appearance
                    categoryView.setBackgroundResource(
                            R.drawable.category_selected_bg
                    );

                    categoryView.setTextColor(
                            ContextCompat.getColor(this, R.color.white)
                    );

                    // Selection animation
                    categoryView.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(80)
                            .withEndAction(() -> categoryView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                            .start();

                });
            }
        }
    }
    private void createNote() {
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();
        String selectedCategory = category;
        String isNotePinned = isPinned;

        if(title.isEmpty()) {
            noteTitle.setError("Title is required");
            return;
        }

        NewNoteModel note = new NewNoteModel();
        note.setTitle(title);
        note.setDescription(description);
        note.setCategory(selectedCategory);
        note.setIsPinned(isNotePinned);

        long result = dbHelper.insertNote(note);
        if(result != -1) {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }

    }

}