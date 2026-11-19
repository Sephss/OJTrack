package com.example.ojtrack.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojtrack.R;
import com.example.ojtrack.models.NewNoteModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(NewNoteModel note);
        void onNoteLongClick(NewNoteModel note);
    }

    private static final int[] CARD_BACKGROUNDS = {
            R.drawable.journal_bg,
            R.drawable.note_bg,
            R.drawable.timein_bg,
            R.drawable.pink_bg
    };

    private static final SimpleDateFormat DB_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault());

    private final List<NewNoteModel> notes = new ArrayList<>();
    private final OnNoteClickListener listener;

    public NotesAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<NewNoteModel> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

    /** Removes one note with the default item animation. */
    public void removeNote(int noteId) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getNoteId() == noteId) {
                notes.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NewNoteModel note = notes.get(position);
        View card = holder.card;

        // Reset any leftover animation state from a recycled view
        card.animate().cancel();
        card.setScaleX(1f);
        card.setScaleY(1f);
        card.setAlpha(1f);
        card.setRotation(0f);

        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        holder.date.setText(formatDate(note.getUpdatedAt()));

        int bg = CARD_BACKGROUNDS[Math.abs(note.getNoteId()) % CARD_BACKGROUNDS.length];
        card.setBackgroundResource(bg);

        // Press / hold effect: the card slowly "charges" down while held,
        // then springs back when released.
        card.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.94f).scaleY(0.94f).alpha(0.85f)
                            .setDuration(ViewConfiguration.getLongPressTimeout())
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f).scaleY(1f).alpha(1f)
                            .setDuration(250)
                            .setInterpolator(new OvershootInterpolator(2f))
                            .start();
                    break;
            }
            return false; // let click / long click still work
        });

        card.setOnClickListener(v -> {
            if (listener != null) listener.onNoteClick(note);
        });

        card.setOnLongClickListener(v -> {
            // Wiggle, then open the delete dialog
            ObjectAnimator wiggle = ObjectAnimator.ofFloat(
                    v, View.ROTATION, 0f, -4f, 4f, -3f, 3f, 0f);
            wiggle.setDuration(280);
            wiggle.start();

            v.postDelayed(() -> {
                if (listener != null) listener.onNoteLongClick(note);
            }, 150);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private String formatDate(String dbDate) {
        if (dbDate == null) return "";
        try {
            Date date = DB_FORMAT.parse(dbDate);
            return date != null ? DISPLAY_FORMAT.format(date) : dbDate;
        } catch (ParseException e) {
            return dbDate;
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card;
        TextView title, description, date;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.noteCard);
            title = itemView.findViewById(R.id.tvNoteTitle);
            description = itemView.findViewById(R.id.tvNoteDescription);
            date = itemView.findViewById(R.id.tvNoteDate);
        }
    }
}