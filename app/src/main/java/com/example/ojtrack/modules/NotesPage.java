package com.example.ojtrack.modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ojtrack.R;

public class NotesPage extends AppCompatActivity {
    private ImageView homeBtn, ojtBtn;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_page);

        homeBtn = findViewById(R.id.homeBtn);
        ojtBtn = findViewById(R.id.ojtBtn);
        btnAdd = findViewById(R.id.btnAdd);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeBtn.setOnClickListener(d -> {
            Intent intent = new Intent(NotesPage.this, HomePage.class);
            startActivity(intent);
            finish();
        });

        ojtBtn.setOnClickListener(g -> {
            Intent intent = new Intent(NotesPage.this, OJTHoursPage.class);
            startActivity(intent);
            finish();
        });

        btnAdd.setOnClickListener(d -> {
            Intent intent = new Intent(NotesPage.this, CreateNotesPage.class);
            startActivity(intent);

        });
    }
}