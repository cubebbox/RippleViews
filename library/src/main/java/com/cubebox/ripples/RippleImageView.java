/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Robin Chutaux
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cubebox.ripples;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.cubebox.ripples.listener.OnClickConfirmListener;
import com.cubebox.ripples.listener.OnRippleCompleteListener;


/**
 * RippleView custom layout
 * <p/>
 * Custom Layout that allows to use Ripple UI pattern above API 21
 *
 * @author luozisong
 * @version 2016.0303
 */
public class RippleImageView extends ImageView {

    private int WIDTH;
    private int HEIGHT;
    private int rippleDuration = 500;
    private int pressBgDuration = 200;
    private int rippleAlpha = 90;
    private int pressBgAlpha = 45;
    private float radiusMax = 0;
    private float radiusMin = 0;
    private float currRadius = 0;
    private int currAlpha = 0;
    private int pressBgType = 0;//defalut rect
    private float x = -1;
    private float y = -1;
    private int zoomDuration = 200;
    private float zoomScale = 1.03f;
    private ScaleAnimation scaleAnimation;
    private boolean hasToZoom = false;
    private boolean isCentered = true;
    private boolean holdBgInPressing = false;
    private Paint circlePaint, pressPaint;
    private Bitmap originBitmap;
    private int rippleColor = getResources().getColor(R.color.rippelColor);//default white
    private int pressBgColor = getResources().getColor(R.color.pressColor);//default white
    private boolean pressBgEnable = true;
    private int ripplePadding = 0;
    private GestureDetector gestureDetector;

    private ValueAnimator bgAnim = null;

    private OnRippleCompleteListener onCompletionListener;
    private OnClickConfirmListener onClickConfirmListener = null;

    public RippleImageView(Context context) {
        super(context);
    }

    public RippleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Method that initializes all fields and sets listeners
     *
     * @param context Context used to create this view
     * @param attrs   Attribute used to initialize fields
     */
    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_rv_rippleColor, rippleColor);//default white
        pressBgColor = typedArray.getColor(R.styleable.RippleView_rv_pressColor, pressBgColor);//default white
        pressBgEnable = typedArray.getBoolean(R.styleable.RippleView_rv_pressBgEnable, pressBgEnable);
        pressBgType = typedArray.getInt(R.styleable.RippleView_rv_pressBgType, pressBgType);
        hasToZoom = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, hasToZoom);
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, isCentered);
        holdBgInPressing = typedArray.getBoolean(R.styleable.RippleView_rv_holdBgInPressing, holdBgInPressing);
        rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, rippleDuration);
        rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha);
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, ripplePadding);
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, zoomScale);
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, zoomDuration);
        pressBgAlpha = rippleAlpha / 3;
        typedArray.recycle();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(rippleColor);
        circlePaint.setAlpha(rippleAlpha);

        pressPaint = new Paint();
        pressPaint.setAntiAlias(true);
        pressPaint.setStyle(Paint.Style.FILL);
        pressPaint.setColor(pressBgColor);
        pressPaint.setAlpha(pressBgAlpha);


        this.setWillNotDraw(false);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                animateRipple(event);
                sendClickEvent(true);
            }


            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                animateRipple(event);
                sendClickEvent(false);
                return super.onSingleTapUp(event);
            }


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onClickConfirmListener != null)
                    onClickConfirmListener.onConfirmClick(RippleImageView.this);
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (holdBgInPressing)
                    stopBgAnim();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        });

        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isInEditMode()) return;
        canvas.save();
        // There is problem on Android M where canvas.restore() seems to be called automatically
        // For now, don't call canvas.restore() manually on Android M (API 23)
        if (Build.VERSION.SDK_INT != 23) {
            canvas.restore();
        }


        switch (pressBgType) {
            case 0://rect
                /**
                 * draw ripple
                 * */
                circlePaint.setAlpha((int) (((radiusMax - currRadius) / radiusMax) * 100));
                canvas.drawCircle(x, y, currRadius, circlePaint);

                /**
                 * draw circle
                 * */
                RectF vRect = new RectF(0, 0, WIDTH, HEIGHT);
                pressPaint.setAlpha(currAlpha);
                canvas.drawRect(vRect, pressPaint);

                break;
            case 1://circle fit min
                /**
                 * draw ripple
                 * */
                circlePaint.setAlpha((int) ((((radiusMin / 2) - currRadius) / (radiusMin / 2)) * 100));
                canvas.drawCircle(x, y, currRadius, circlePaint);

                /**
                 * draw circle
                 * */
                pressPaint.setAlpha(currAlpha);
                canvas.drawCircle(WIDTH / 2, HEIGHT / 2, radiusMin / 2, pressPaint);
                break;
            case 2://circle fit max
                /**
                 * draw ripple
                 * */
                circlePaint.setAlpha((int) ((((radiusMax / 2) - currRadius) / (radiusMax / 2)) * 100));
                canvas.drawCircle(x, y, currRadius, circlePaint);

                /**
                 * draw circle
                 * */
                pressPaint.setAlpha(currAlpha);
                canvas.drawCircle(WIDTH / 2, HEIGHT / 2, radiusMax / 2, pressPaint);
                break;
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, w / 2, h / 2);
        scaleAnimation.setDuration(zoomDuration);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
    }

    private boolean ACTION_CANCEL = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startBgAnim();
                ACTION_CANCEL = false;
                break;
            case MotionEvent.ACTION_UP:
                if (!holdBgInPressing) stopBgAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                ACTION_CANCEL = true;
                stopBgAnim();
                break;

        }
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    /**
     * Launch Ripple animation for the current view with a MotionEvent
     *
     * @param event MotionEvent registered by the Ripple gesture listener
     */
    public void animateRipple(MotionEvent event) {
        createAnimation(event.getX(), event.getY());
    }


    /**
     * Launch Ripple animation for the current view centered at x and y position
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    public void animateRipple(final float x, final float y) {
        createAnimation(x, y);
    }

    /**
     * Create Ripple animation centered at x, y
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    private void createAnimation(final float x, final float y) {
        if (this.isEnabled()) {
            if (hasToZoom)
                this.startAnimation(scaleAnimation);
            /**
             * get max radius
             * */
            radiusMax = Math.max(WIDTH, HEIGHT);
            radiusMin = Math.min(WIDTH, HEIGHT);

            radiusMax -= ripplePadding;
            radiusMin -= ripplePadding;

            if (isCentered) {
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            } else {
                this.x = x;
                this.y = y;
            }

            if (originBitmap == null)
                originBitmap = getDrawingCache(true);

            startRippleAnim();
        }
    }


    /**
     * 绘制矩形的渐变背景色
     */
    private void startBgAnim() {
        if (this.isEnabled() && pressBgEnable) {
            bgAnim = ValueAnimator.ofFloat(0, pressBgAlpha);
            bgAnim.setDuration(pressBgDuration);
            bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (ACTION_CANCEL) return;
                    currAlpha = (int) Double.parseDouble(animation.getAnimatedValue().toString());
                    invalidate();
                }
            });
            bgAnim.start();
        }
    }

    /**
     * 停止背景色叠加
     */
    private void stopBgAnim() {
        if (!pressBgEnable) return;
        if (bgAnim != null && bgAnim.isRunning()) bgAnim.cancel();

        bgAnim = ValueAnimator.ofFloat(currAlpha, 0);
        bgAnim.setDuration(pressBgDuration);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currAlpha = (int) Double.parseDouble(animation.getAnimatedValue().toString());
                invalidate();
            }
        });
        bgAnim.start();
    }

    /**
     * start ripple anim
     */
    public void startRippleAnim() {
        float finalRadius = 0;
        /**
         * 根据类型判断背景类型
         * */
        switch (pressBgType) {
            case 0:
                finalRadius = radiusMax;
                break;
            case 1:// fit min
                finalRadius = radiusMin / 2;
                break;
            case 2:// fit max
                finalRadius = radiusMax / 2;
                break;
        }
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, finalRadius);
        valueAnimator.setDuration(rippleDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currRadius = (float) Double.parseDouble(animation.getAnimatedValue().toString());
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onCompletionListener != null)
                    onCompletionListener.onComplete(RippleImageView.this);
                stopBgAnim();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (onCompletionListener != null)
                    onCompletionListener.onComplete(RippleImageView.this);
                stopBgAnim();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }


    /**
     * Send a click event if parent view is a Listview instance
     *
     * @param isLongClick Is the event a long click ?
     */
    private void sendClickEvent(final Boolean isLongClick) {
        if (getParent() instanceof AdapterView) {
            final AdapterView adapterView = (AdapterView) getParent();
            final int position = adapterView.getPositionForView(this);
            final long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick) {
                if (adapterView.getOnItemLongClickListener() != null)
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
            } else {
                if (adapterView.getOnItemClickListener() != null)
                    adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
            }
        }
    }

    private Bitmap getCircleBitmap(final int radius) {
        final Bitmap output = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect((int) (x - radius), (int) (y - radius), (int) (x + radius), (int) (y + radius));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(x, y, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Set Ripple color, default is #FFFFFF
     *
     * @param rippleColor New color resource
     */
    @ColorRes
    public void setRippleColor(int rippleColor) {
        this.rippleColor = getResources().getColor(rippleColor);
    }

    public int getRippleColor() {
        return rippleColor;
    }


    public Boolean isCentered() {
        return isCentered;
    }

    /**
     * Set if ripple animation has to be centered in its parent view or not, default is False
     *
     * @param isCentered
     */
    public void setCentered(final Boolean isCentered) {
        this.isCentered = isCentered;
    }

    public int getRipplePadding() {
        return ripplePadding;
    }

    /**
     * Set Ripple padding if you want to avoid some graphic glitch
     *
     * @param ripplePadding New Ripple padding in pixel, default is 0px
     */
    public void setRipplePadding(int ripplePadding) {
        this.ripplePadding = ripplePadding;
    }

    public Boolean isZooming() {
        return hasToZoom;
    }

    /**
     * At the end of Ripple effect, the child views has to zoom
     *
     * @param hasToZoom Do the child views have to zoom ? default is False
     */
    public void setZooming(Boolean hasToZoom) {
        this.hasToZoom = hasToZoom;
    }

    public float getZoomScale() {
        return zoomScale;
    }

    /**
     * Scale of the end animation
     *
     * @param zoomScale Value of scale animation, default is 1.03f
     */
    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public int getZoomDuration() {
        return zoomDuration;
    }

    /**
     * Duration of the ending animation in ms
     *
     * @param zoomDuration Duration, default is 200ms
     */
    public void setZoomDuration(int zoomDuration) {
        this.zoomDuration = zoomDuration;
    }

    public int getRippleDuration() {
        return rippleDuration;
    }

    /**
     * Duration of the Ripple animation in ms
     *
     * @param rippleDuration Duration, default is 400ms
     */
    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }


    public int getRippleAlpha() {
        return rippleAlpha;
    }

    /**
     * Set alpha for ripple effect color
     *
     * @param rippleAlpha Alpha value between 0 and 255, default is 90
     */
    public void setRippleAlpha(int rippleAlpha) {
        this.rippleAlpha = rippleAlpha;
    }

    public void setOnRippleCompleteListener(OnRippleCompleteListener listener) {
        this.onCompletionListener = listener;
    }

    public void setOnClickConfirmListener(OnClickConfirmListener onClickConfirmListener) {
        this.onClickConfirmListener = onClickConfirmListener;
    }


    public PressBgType getPressBgType() {
        return PressBgType.values()[pressBgType];
    }

    public void setPressBgType(final PressBgType pressBgType) {
        this.pressBgType = pressBgType.ordinal();
    }

    public enum PressBgType {
        REXT(0),
        CIRCLE_FIT_MIN(1),
        CIRCLE_FIT_MAX(2);

        int type;

        PressBgType(int type) {
            this.type = type;
        }
    }
}
