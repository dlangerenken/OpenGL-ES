package de.dala.puzzle_animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Daniel on 10.11.13.
 */
public class PuzzleImageView extends ImageView implements View.OnTouchListener {

    private static final int HIDE = -1;
    private static final int SHOW = 0;

    private int rows;
    private int columns;
    private Drawable hidingDrawable;

    private int[][] cubes;
    private Paint paint;

    private int currentImageWidth;
    private float currentImagePositionX;
    private float currentImagePositionY;
    private int currentImageHeight;
    private float cubeWidth;
    private float cubeHeight;

    private Random r = new Random();

    private enum PuzzleMode {
        TOUCH, TOUCH_RANDOM, TIME
    }

    private PuzzleMode mode = PuzzleMode.TOUCH;
    private int millisecondsToRelease;

    public PuzzleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setHidingDrawable(Drawable hidingDrawable) {
        this.hidingDrawable = hidingDrawable;
    }

    public void hideAllCubes() {
        if (cubes != null) {
            for (int x = 0; x < cubes.length; x++) {
                for (int y = 0; y < cubes[x].length; y++) {
                    cubes[x][y] = HIDE;
                }
            }
        }
    }

    public void showAllCubes() {
        if (cubes != null) {
            for (int x = 0; x < cubes.length; x++) {
                for (int y = 0; y < cubes[x].length; y++) {
                    cubes[x][y] = SHOW;
                }
            }
        }
    }

    public PuzzleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PuzzleImageView);
        Drawable drawable = a != null ? a
                .getDrawable(R.styleable.PuzzleImageView_hide_background) : null;
        if (drawable != null) {
            hidingDrawable = drawable;
        }

        rows = a.getInt(R.styleable.PuzzleImageView_cube_rows, 0);
        columns = a.getInt(R.styleable.PuzzleImageView_cube_columns, 0);
        mode = PuzzleMode.values()[a.getInt(
                R.styleable.PuzzleImageView_puzzle_mode, 0)];
        millisecondsToRelease = a.getInt(
                R.styleable.PuzzleImageView_milliseconds_to_release, 3000);

        cubes = new int[rows][columns];
        hideAllCubes();
        a.recycle();
        setOnTouchListener(this);
    }

    public PuzzleImageView(Context context) {
        super(context);
    }

    public void showCube(int rowId, int columnId) {
        cubes[rowId][columnId] = SHOW;
        invalidate();
    }

    private void showCubeByClick(float eX, float eY) {
        for (int x = 0; x < cubes.length; x++) {
            for (int y = 0; y < cubes[0].length; y++) {
                if (eX - cubeWidth - currentImagePositionX < y * cubeWidth) {
                    if (eY - cubeHeight - currentImagePositionY < x
                            * cubeHeight) {
                        showCube(x, y);
                        return;
                    }
                }
            }
        }
    }

    private boolean allCubesVisible() {
        if (cubes != null) {
            for (int x = 0; x < cubes.length; x++) {
                for (int y = 0; y < cubes[x].length; y++) {
                    if (cubes[x][y] == HIDE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void showRandomCube() {
        int x ;
        int y;
        do {
            x = r.nextInt(cubes.length);
            y = r.nextInt(cubes[0].length);
        } while (cubes[x][y] == SHOW && !allCubesVisible());
        showCube(x, y);
    }

    public void startTimer() {
        startTimer(millisecondsToRelease);
    }

    public void startTimer(int time) {
        int sizeOfCubes = rows * columns;
        final int timePerCube = time / sizeOfCubes;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                showRandomCube();
                if (!allCubesVisible()) {
                    handler.postDelayed(this, timePerCube);
                } else {
                    //TODO OnFinishedListener?
                }
            }
        }, timePerCube);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        hidingDrawable.setBounds((int) currentImagePositionX,
                (int) currentImagePositionY,
                (int) (currentImageWidth + currentImagePositionX),
                (int) (currentImageHeight + currentImagePositionY));
        hidingDrawable.draw(canvas);

        Drawable tempDrawable = getDrawable();
        if (cubes != null) {
            if (tempDrawable != null) {
                if (tempDrawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) tempDrawable).getBitmap();
                    Bitmap[][] bitmaps = splitImageIntoPieces(rows, columns,
                            bitmap);
                    // These holds the ratios for the ImageView and the bitmap
                    for (int x = 0; x < cubes.length; x++) {
                        for (int y = 0; y < cubes[0].length; y++) {
                            if (cubes[x][y] == SHOW) {
                                canvas.drawBitmap(bitmaps[x][y], cubeWidth * y
                                        + currentImagePositionX, cubeHeight * x
                                        + currentImagePositionY, paint);
                            }
                        }
                    }
                }
            }
        }
    }

    // based on
    // http://stackoverflow.com/questions/16193282/how-to-get-the-position-of-a-picture-inside-an-imageview
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio
        // maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and
        // getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        currentImageWidth = Math.round(origW * scaleX);
        currentImageHeight = Math.round(origH * scaleY);
        currentImagePositionX = f[Matrix.MTRANS_X];
        currentImagePositionY = f[Matrix.MTRANS_Y];

        cubeWidth = currentImageWidth / columns;
        cubeHeight = currentImageHeight / rows;
    }

    // based on
    // http://androidattop.blogspot.com.au/2012/05/splitting-image-into-smaller-chunks-in.html
    private Bitmap[][] splitImageIntoPieces(int rows, int columns, Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                currentImageWidth, currentImageHeight, true);
        if (rows > 0 && columns > 0) {
            Bitmap[][] bitmaps = new Bitmap[rows][columns];

            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    bitmaps[row][column] = Bitmap.createBitmap(scaledBitmap,
                            column * (int) cubeWidth, row * (int) cubeHeight,
                            (int) cubeWidth, (int) cubeHeight);
                }
            }
            return bitmaps;
        }
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (mode) {
            case TOUCH:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showCubeByClick(event.getX(), event.getY());
                }
                break;
            case TOUCH_RANDOM:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showRandomCube();
                }
                break;
            default:
                break;
        }
        return true;
    }

}
