package com.example.oblong;




import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

/**
 * Utility class for generating profile pictures with a random background color
 * and the first letter of a given name.
 */
public class ProfilePicGenerator {
    private Database db;

    /**
     * Generates a Bitmap representing a profile picture.
     * The picture is a square with a random background color and the first letter
     * of the provided name displayed in white at the center.
     *
     * @param name The name to extract the first letter from. If the name is null
     *             or empty, a "?" will be displayed instead.
     * @return A {@link Bitmap} object representing the generated profile picture.
     */
    public static Bitmap generateProfilePic(String name) {

        // Extract the first letter of the name
        String firstLetter = name != null && !name.isEmpty()
                ? name.substring(0, 1).toUpperCase()
                : "?";

        // Dimensions of the Bitmap
        int size = 150; // Width and height of the square Bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Generate a random background color
        int backgroundColor = getRandomColor();

        // Draw the background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);
        canvas.drawRect(new RectF(0, 0, size, size), backgroundPaint);

        // Draw the letter
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Text color
        textPaint.setTextSize(size / 2.5f); // Font size
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Draw the text in the center
        float centerX = size / 2f;
        float centerY = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(firstLetter, centerX, centerY, textPaint);

        return bitmap;
    }
    /**
     * Generates a random color in RGB format.
     *
     * @return An integer representing the generated color.
     */
    private static int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }


}


