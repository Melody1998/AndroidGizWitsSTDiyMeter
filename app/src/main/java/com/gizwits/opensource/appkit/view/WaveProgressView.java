package com.gizwits.opensource.appkit.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 项目名： AS_Chat
 * 包名：com.gizwits.opensource.appkit.view
 * 文件名字： TODO
 * 创建时间：2019.11.19  11:48
 * 创建者博客： http://blog.csdn.net/xh870189248
 * 创建者GitHub： https://github.com/xuhongv
 * 创建者：徐宏
 * 描述： TODO
 */
public class WaveProgressView extends View {
    private int width;
    private int height;
    private Bitmap backgroundBitmap;
    private Path mPath;
    private Paint mPathPaint;
    private float mWaveHight;
    private float mWaveHalfWidth;
    private String mWaveColor;
    private int mWaveSpeed;
    private Paint mTextPaint;
    private String currentText;
    private String mTextColor;
    private int mTextSize;
    private int maxProgress;
    private int currentProgress;
    private float CurY;
    private float distance;
    private int RefreshGap;
    private static final int INVALIDATE = 1911;
    private Handler handler;

    public WaveProgressView(Context context) {
        this(context, (AttributeSet)null, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWaveHight = 20.0F;
        this.mWaveHalfWidth = 100.0F;
        this.mWaveColor = "#5be4ef";
        this.mWaveSpeed = 30;
        this.currentText = "";
        this.mTextColor = "#FFFFFF";
        this.mTextSize = 41;
        this.maxProgress = 100;
        this.currentProgress = 0;
        this.distance = 0.0F;
        this.RefreshGap = 10;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                    case 1911:
                        WaveProgressView.this.invalidate();
                        this.sendEmptyMessageDelayed(1911, (long)WaveProgressView.this.RefreshGap);
                    default:
                }
            }
        };
        this.Init();
    }

    public void setCurrent(int currentProgress, String currentText) {
        this.currentProgress = currentProgress;
        this.currentText = currentText;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setText(String mTextColor, int mTextSize) {
        this.mTextColor = mTextColor;
        this.mTextSize = mTextSize;
    }

    public void setWave(float mWaveHight, float mWaveWidth) {
        this.mWaveHight = mWaveHight;
        this.mWaveHalfWidth = mWaveWidth / 2.0F;
    }

    public void setWaveColor(String mWaveColor) {
        this.mWaveColor = mWaveColor;
    }

    public void setmWaveSpeed(int mWaveSpeed) {
        this.mWaveSpeed = mWaveSpeed;
    }

    private void Init() {
        if (null == this.getBackground()) {
            throw new IllegalArgumentException(String.format("background is null."));
        } else {
            this.backgroundBitmap = this.getBitmapFromDrawable(this.getBackground());
            this.mPath = new Path();
            this.mPathPaint = new Paint();
            this.mPathPaint.setAntiAlias(true);
            this.mPathPaint.setStyle(Style.FILL);
            this.mTextPaint = new Paint();
            this.mTextPaint.setAntiAlias(true);
            this.mTextPaint.setTextAlign(Align.CENTER);
            this.handler.sendEmptyMessageDelayed(1911, 100L);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.CurY = (float)(this.height = MeasureSpec.getSize(heightMeasureSpec));
    }

    protected void onDraw(Canvas canvas) {
        if (this.backgroundBitmap != null) {
            canvas.drawBitmap(this.createImage(), 0.0F, 0.0F, (Paint)null);
        }

    }

    private Bitmap createImage() {
        this.mPathPaint.setColor(Color.parseColor(this.mWaveColor));
        this.mTextPaint.setColor(Color.parseColor(this.mTextColor));
        this.mTextPaint.setTextSize((float)this.mTextSize);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBmp);
        float CurMidY = (float)(this.height * (this.maxProgress - this.currentProgress) / this.maxProgress);
        if (this.CurY > CurMidY) {
            this.CurY -= (this.CurY - CurMidY) / 10.0F;
        }

        this.mPath.reset();
        this.mPath.moveTo(0.0F - this.distance, this.CurY);
        int waveNum = this.width / ((int)this.mWaveHalfWidth * 4) + 1;
        int multiplier = 0;

        int min;
        for(min = 0; min < waveNum * 3; ++min) {
            this.mPath.quadTo(this.mWaveHalfWidth * (float)(multiplier + 1) - this.distance, this.CurY - this.mWaveHight, this.mWaveHalfWidth * (float)(multiplier + 2) - this.distance, this.CurY);
            this.mPath.quadTo(this.mWaveHalfWidth * (float)(multiplier + 3) - this.distance, this.CurY + this.mWaveHight, this.mWaveHalfWidth * (float)(multiplier + 4) - this.distance, this.CurY);
            multiplier += 4;
        }

        this.distance += this.mWaveHalfWidth / (float)this.mWaveSpeed;
        this.distance %= this.mWaveHalfWidth * 4.0F;
        this.mPath.lineTo((float)this.width, (float)this.height);
        this.mPath.lineTo(0.0F, (float)this.height);
        this.mPath.close();
        canvas.drawPath(this.mPath, this.mPathPaint);
        min = Math.min(this.width, this.height);
        this.backgroundBitmap = Bitmap.createScaledBitmap(this.backgroundBitmap, min, min, false);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_ATOP));
        canvas.drawBitmap(this.backgroundBitmap, 0.0F, 0.0F, paint);
        canvas.drawText(this.currentText, (float)(this.width / 2), (float)(this.height / 2), this.mTextPaint);
        return finalBmp;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } catch (OutOfMemoryError var4) {
                return null;
            }
        }
    }
}

