package com.example.ojtrack.modules;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ojtrack.R;

public class HomePage extends AppCompatActivity {
    private ImageView notesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        LinearLayout weekBars = findViewById(R.id.weekBars);
        float[] hours = {8f, 7.5f, 8f, 0f, 8.25f, 4f, 6f};      // last 7 days
        String[] labels = {"M", "T", "W", "T", "F", "S", "S"};
        renderWeekBars(weekBars, hours, labels, 6);               // index of today

        notesBtn = findViewById(R.id.notesBtn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notesBtn.setOnClickListener(d -> {
            Intent intent = new Intent(HomePage.this, NotesPage.class);
            startActivity(intent);
            finish();
        });
    }
    private void renderWeekBars(LinearLayout container, float[] hours, String[] labels, int todayIndex) {
        container.removeAllViews();
        float d = getResources().getDisplayMetrics().density; // dp -> px

        for (int i = 0; i < hours.length; i++) {
            // One column per day
            LinearLayout col = new LinearLayout(this);   // use requireContext() in a Fragment
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams colLp =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            colLp.setMargins((int) (5 * d), 0, (int) (5 * d), 0);
            col.setLayoutParams(colLp);

            // The bar (hours x 8dp, minimum 4dp so empty days still show a stub)
            View bar = new View(this);
            int barHeight = (int) (Math.max(4f, hours[i] * 8f) * d);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, barHeight));

            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(8 * d);
            shape.setColor(i == todayIndex ? 0xFF49418F : 0xD96C63C7); // deep purple for today
            bar.setBackground(shape);

            // Day letter under the bar
            TextView label = new TextView(this);
            label.setText(labels[i]);
            label.setTextSize(11);
            label.setTextColor(0xFF85849A);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, (int) (4 * d), 0, 0);

            col.addView(bar);
            col.addView(label);
            container.addView(col);
        }
    }
}