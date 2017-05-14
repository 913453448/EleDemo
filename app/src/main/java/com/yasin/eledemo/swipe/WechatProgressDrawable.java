package com.yasin.eledemo.swipe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.yasin.eledemo.R;

import java.util.ArrayList;

/**
 * Created by leo on 17/5/14.
 */

public class WechatProgressDrawable extends Drawable implements Animatable{
    private Resources mResources;
    private View mParent;
    /** The list of animators operating on this drawable. */
    private final ArrayList<Animation> mAnimators = new ArrayList<Animation>();


    // Maps to ProgressBar.Large style
    static final int LARGE = 0;
    // Maps to ProgressBar default style
    static final int DEFAULT = 1;
    static final float LARGE_SIZE=56;
    static final float DEFAULT_SIZE=40;
    static final long ANIMATION_DURATION=3000;
    private float mWidth,mHeight;
    private Animation mAnimation;
    private float mRotation;
    private Bitmap mBitmap;
    public WechatProgressDrawable(Context context, View parent) {
        mParent = parent;
        mResources = context.getResources();
        updateSizes(DEFAULT);
        setupAnimators();
        mBitmap= BitmapFactory.decodeResource(mResources, R.mipmap.icon_wechat1);
        mBitmap=Bitmap.createScaledBitmap(mBitmap,(int)mWidth,(int)mHeight,true);
    }
    public void updateSizes( int size) {
        if (size == LARGE) {
            setSizeParameters(LARGE_SIZE, LARGE_SIZE);
        } else {
            setSizeParameters(DEFAULT_SIZE, DEFAULT_SIZE);
        }
    }

    private void setSizeParameters(float width, float height) {
        final DisplayMetrics metrics = mResources.getDisplayMetrics();
        final float screenDensity = metrics.density;

        mWidth = width * screenDensity;
        mHeight = height * screenDensity;
    }
    private void setupAnimators() {
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float rotation=interpolatedTime*1.0f*360;
                setProgressRotation(rotation);
            }
        };
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimation = animation;
    }
    @Override
    public void draw(Canvas c) {
        final Rect bounds = getBounds();
        final int saveCount = c.save();
        c.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        c.drawBitmap(mBitmap,0,0,null);
        c.restoreToCount(saveCount);
    }
    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation is from [0..1]
     */
    public void setProgressRotation(float rotation) {
        this.mRotation=rotation;
        invalidateSelf();
    }
    @Override
    public int getIntrinsicHeight() {
        return (int) mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mWidth;
    }
    @Override
    public void setAlpha(int alpha) {
    }
    public int getAlpha() {
        return 255;
    }
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        mAnimation.reset();
        mAnimation.setDuration(ANIMATION_DURATION);
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        setProgressRotation(0);
    }

    @Override
    public boolean isRunning() {
        final ArrayList<Animation> animators = mAnimators;
        final int N = animators.size();
        for (int i = 0; i < N; i++) {
            final Animation animator = animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }
}
