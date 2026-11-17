package com.example.ojtrack.modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ojtrack.R;

public class OJTHoursPage extends AppCompatActivity {
    private ImageView homePage, notePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ojthours_page);

        homePage = findViewById(R.id.homePage);
        notePage = findViewById(R.id.notesBtn);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homePage.setOnClickListener(g -> {
            Intent intent = new Intent(OJTHoursPage.this, HomePage.class);
            startActivity(intent);
            finish();
        });

        notePage.setOnClickListener(d -> {
            Intent intent = new Intent(OJTHoursPage.this, NotesPage.class);
            startActivity(intent);
            finish();
        });
    }
}