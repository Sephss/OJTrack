package com.example.ojtrack.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ojtrack.R;

public class DeleteNoteDialog {

    public static void show(Context context, String noteTitle, Runnable onConfirm) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_note);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.6f);
        }

        View card = dialog.findViewById(R.id.dialogCard);
        ImageView icon = dialog.findViewById(R.id.ivDialogIcon);
        TextView message = dialog.findViewById(R.id.tvDialogMessage);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);

        message.setText("\u201C" + noteTitle + "\u201D will be permanently deleted. "
                + "This can\u2019t be undone.");

        addPressEffect(btnCancel);
        addPressEffect(btnConfirm);

        btnCancel.setOnClickListener(v -> dismissAnimated(dialog, card, null));
        btnConfirm.setOnClickListener(v -> dismissAnimated(dialog, card, onConfirm));

        // Start states
        card.setScaleX(0.75f);
        card.setScaleY(0.75f);
        card.setAlpha(0f);
        icon.setScaleX(0f);
        icon.setScaleY(0f);

        dialog.show();

        // Card pops in
        card.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(280)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        // Trash icon bounces in, then wiggles like a shaking bin
        icon.animate()
                .scaleX(1f).scaleY(1f)
                .setStartDelay(150)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator(3f))
                .withEndAction(() -> {
                    ObjectAnimator wiggle = ObjectAnimator.ofFloat(
                            icon, View.ROTATION, 0f, -14f, 14f, -10f, 10f, -5f, 5f, 0f);
                    wiggle.setDuration(500);
                    wiggle.start();
                })
                .start();
    }

    private static void dismissAnimated(Dialog dialog, View card, Runnable after) {
        card.animate()
                .scaleX(0.85f).scaleY(0.85f).alpha(0f)
                .setDuration(140)
                .withEndAction(() -> {
                    dialog.dismiss();
                    if (after != null) after.run();
                })
                .start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private static void addPressEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    break;
            }
            return false;
        });
    }
}