package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class CustomToast extends Toast {
    private static TextView toastText;

    @SuppressLint("ResourceAsColor")
    public CustomToast(Context context, boolean isValid) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout root = (ConstraintLayout) inflater.inflate(R.layout.toast_info, null);
        ImageView toastImage = root.findViewById(R.id.toast_icon);
        toastText = root.findViewById(R.id.toast_message);

        if (isValid) {
            toastImage.setImageResource(R.drawable.valid_icon);
            int errorRedColor = ContextCompat.getColor(context, R.color.valid_green);
            root.setBackgroundColor(toARGBFormat(errorRedColor));
        } else {
            toastImage.setImageResource(R.drawable.invalid_icon);
            int validGreenColor = ContextCompat.getColor(context, R.color.error_red);
            root.setBackgroundColor(toARGBFormat(validGreenColor));
        }

        this.setView(root);
        this.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        this.setDuration(Toast.LENGTH_SHORT);
    }

    public static CustomToast makeText(Context context, CharSequence text, boolean isValid) {
        CustomToast result = new CustomToast(context, isValid);
        toastText.setText(text);

        return result;
    }

    public static Toast makeText(Context context, int resId, boolean isValid) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), isValid);
    }

    private int toARGBFormat(int colorCode) {
        return Color.alpha(colorCode) << 24
                | Color.red(colorCode) << 16
                | Color.green(colorCode) << 8
                | Color.blue(colorCode);
    }
}