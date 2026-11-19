package com.example.ojtrack.DatabaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ojtrack.models.NewNoteModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ojtrack.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    public static final String TABLE_NOTES = "notes";
    public static final String COL_ID = "note_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CATEGORY = "category";
    public static final String COL_IS_PINNED = "is_pinned";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITLE + " TEXT NOT NULL, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_CATEGORY + " TEXT, " +
                    COL_IS_PINNED + " TEXT DEFAULT 'no', " +
                    COL_UPDATED_AT + " TEXT, " +
                    COL_TIMESTAMP + " TEXT" +
                    ")";

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy: drop and recreate.
        // For production, use ALTER TABLE migrations to avoid losing user data.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // ---------------------------------------------------------------------
    // Helper: current date/time string
    // ---------------------------------------------------------------------
    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // ---------------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------------
    /**
     * Inserts a new note.
     * @return the new row id, or -1 if the insert failed
     */
    public long insertNote(NewNoteModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentTime = now();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, note.getTitle());
        values.put(COL_DESCRIPTION, note.getDescription());
        values.put(COL_CATEGORY, note.getCategory());
        values.put(COL_IS_PINNED, note.getIsPinned());
        values.put(COL_UPDATED_AT, currentTime);
        values.put(COL_TIMESTAMP, currentTime);

        return db.insert(TABLE_NOTES, null, values);
    }

    // ---------------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------------
    /**
     * Updates an existing note by its id. The original timestamp is kept,
     * only updated_at changes.
     * @return number of rows affected (1 if success, 0 if not found)
     */
    public int updateNote(NewNoteModel note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, note.getTitle());
        values.put(COL_DESCRIPTION, note.getDescription());
        values.put(COL_CATEGORY, note.getCategory());
        values.put(COL_IS_PINNED, note.getIsPinned());
        values.put(COL_UPDATED_AT, now());

        return db.update(TABLE_NOTES, values, COL_ID + " = ?",
                new String[]{String.valueOf(note.getNoteId())});
    }

    /**
     * Quick toggle for pin/unpin without rewriting the whole note.
     */
    public int updatePinned(int noteId, String isPinned) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_IS_PINNED, isPinned);
        values.put(COL_UPDATED_AT, now());

        return db.update(TABLE_NOTES, values, COL_ID + " = ?",
                new String[]{String.valueOf(noteId)});
    }

    // ---------------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------------
    /**
     * Deletes a note by id.
     * @return number of rows deleted
     */
    public int deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTES, COL_ID + " = ?",
                new String[]{String.valueOf(noteId)});
    }

    // ---------------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------------
    /**
     * Gets a single note by id, or null if it doesn't exist.
     */
    public NewNoteModel getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        NewNoteModel note = null;

        Cursor cursor = db.query(TABLE_NOTES, null, COL_ID + " = ?",
                new String[]{String.valueOf(noteId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                note = cursorToNote(cursor);
            }
            cursor.close();
        }
        return note;
    }

    /**
     * Gets all notes. Pinned notes come first, then newest updated.
     */
    public List<NewNoteModel> getAllNotes() {
        return queryNotes(null, null);
    }

    /**
     * Gets notes filtered by category (e.g. "Technical").
     */
    public List<NewNoteModel> getNotesByCategory(String category) {
        return queryNotes(COL_CATEGORY + " = ?", new String[]{category});
    }

    /**
     * Searches notes by title or description.
     */
    public List<NewNoteModel> searchNotes(String keyword) {
        String like = "%" + keyword + "%";
        return queryNotes(
                COL_TITLE + " LIKE ? OR " + COL_DESCRIPTION + " LIKE ?",
                new String[]{like, like});
    }

    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------
    private List<NewNoteModel> queryNotes(String selection, String[] selectionArgs) {
        List<NewNoteModel> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Pinned first ('yes' sorts after 'no', so DESC), then most recently updated
        String orderBy = COL_IS_PINNED + " DESC, " + COL_UPDATED_AT + " DESC";

        Cursor cursor = db.query(TABLE_NOTES, null, selection, selectionArgs,
                null, null, orderBy);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    notes.add(cursorToNote(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return notes;
    }

    private NewNoteModel cursorToNote(Cursor cursor) {
        return new NewNoteModel(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_IS_PINNED)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))
        );
    }
}