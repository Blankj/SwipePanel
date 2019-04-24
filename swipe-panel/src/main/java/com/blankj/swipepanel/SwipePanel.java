package com.blankj.swipepanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2019/03/22
 *     desc  :
 * </pre>
 */
public class SwipePanel extends FrameLayout {

    private static final String TAG = "SwipePanel";

    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;

    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    private static final float TRIGGER_PROGRESS = 0.95f;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;

    private float halfSize;
    private float unit;

    private int mTouchSlop;

    private Path[] mPath = new Path[4];
    private int[] mPaintColor = new int[4];
    private int[] mEdgeSizes = new int[4];
    private Drawable[] mDrawables = new Drawable[4];
    private Bitmap[] mBitmaps = new Bitmap[4];
    private boolean[] mIsStart = new boolean[4];
    private float[] mDown = new float[4];
    private float[] progresses = new float[4];
    private float[] preProgresses = new float[4];
    private boolean[] mCloses = new boolean[4];
    private float[] mStartSpeed = new float[4];
    private boolean[] mIsCenter = new boolean[4];
    private boolean[] mEnabled = {true, true, true, true};

    private float mDownX;
    private float mDownY;
    private float mCurrentX;
    private float mCurrentY;
    private RectF mRectF = new RectF();

    private boolean mIsEdgeStart;
    private int mStartDirection = -1;

    private int mLimit;

    private OnFullSwipeListener mListener;

    public SwipePanel(@NonNull Context context) {
        this(context, null);
    }

    public SwipePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int edgeSlop = ViewConfiguration.get(context).getScaledEdgeSlop();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        halfSize = dp2px(72);
        unit = halfSize / 16;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipePanel);

            setLeftSwipeColor(ta.getColor(R.styleable.SwipePanel_leftSwipeColor, Color.BLACK));
            setTopSwipeColor(ta.getColor(R.styleable.SwipePanel_topSwipeColor, Color.BLACK));
            setRightSwipeColor(ta.getColor(R.styleable.SwipePanel_rightSwipeColor, Color.BLACK));
            setBottomSwipeColor(ta.getColor(R.styleable.SwipePanel_bottomSwipeColor, Color.BLACK));

            setLeftEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_leftEdgeSize, edgeSlop));
            setTopEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_topEdgeSize, edgeSlop));
            setRightEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_rightEdgeSize, edgeSlop));
            setBottomEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_bottomEdgeSize, edgeSlop));

            setLeftDrawable(ta.getDrawable(R.styleable.SwipePanel_leftDrawable));
            setTopDrawable(ta.getDrawable(R.styleable.SwipePanel_topDrawable));
            setRightDrawable(ta.getDrawable(R.styleable.SwipePanel_rightDrawable));
            setBottomDrawable(ta.getDrawable(R.styleable.SwipePanel_bottomDrawable));

            setLeftCenter(ta.getBoolean(R.styleable.SwipePanel_isLeftCenter, false));
            setTopCenter(ta.getBoolean(R.styleable.SwipePanel_isTopCenter, false));
            setRightCenter(ta.getBoolean(R.styleable.SwipePanel_isRightCenter, false));
            setBottomCenter(ta.getBoolean(R.styleable.SwipePanel_isBottomCenter, false));

            setLeftEnabled(ta.getBoolean(R.styleable.SwipePanel_isLeftEnabled, true));
            setTopEnabled(ta.getBoolean(R.styleable.SwipePanel_isTopEnabled, true));
            setRightEnabled(ta.getBoolean(R.styleable.SwipePanel_isRightEnabled, true));
            setBottomEnabled(ta.getBoolean(R.styleable.SwipePanel_isBottomEnabled, true));

            ta.recycle();
        }
    }

    public void setLeftSwipeColor(int color) {
        setSwipeColor(color, LEFT);
    }

    public void setTopSwipeColor(int color) {
        setSwipeColor(color, TOP);
    }

    public void setRightSwipeColor(int color) {
        setSwipeColor(color, RIGHT);
    }

    public void setBottomSwipeColor(int color) {
        setSwipeColor(color, BOTTOM);
    }

    private void setSwipeColor(int color, int direction) {
        mPaintColor[direction] = color;
    }

    public void setLeftEdgeSize(int size) {
        mEdgeSizes[LEFT] = size;
    }

    public void setTopEdgeSize(int size) {
        mEdgeSizes[TOP] = size;
    }

    public void setRightEdgeSize(int size) {
        mEdgeSizes[RIGHT] = size;
    }

    public void setBottomEdgeSize(int size) {
        mEdgeSizes[BOTTOM] = size;
    }

    public void setLeftDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, LEFT);
    }

    public void setTopDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, TOP);
    }

    public void setRightDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, RIGHT);
    }

    public void setBottomDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, BOTTOM);
    }

    private void setDrawable(int drawableId, int direction) {
        mDrawables[direction] = getDrawable(getContext(), drawableId);
    }

    public void setLeftDrawable(Drawable drawable) {
        setDrawable(drawable, LEFT);
    }

    public void setTopDrawable(Drawable drawable) {
        setDrawable(drawable, TOP);
    }

    public void setRightDrawable(Drawable drawable) {
        setDrawable(drawable, RIGHT);
    }

    public void setBottomDrawable(Drawable drawable) {
        setDrawable(drawable, BOTTOM);
    }

    private void setDrawable(Drawable drawable, int direction) {
        if (drawable == null) return;
        mDrawables[direction] = drawable;
    }

    public void setLeftCenter(boolean isCenter) {
        setCenter(isCenter, LEFT);
    }

    public void setTopCenter(boolean isCenter) {
        setCenter(isCenter, TOP);
    }

    public void setRightCenter(boolean isCenter) {
        setCenter(isCenter, RIGHT);
    }

    public void setBottomCenter(boolean isCenter) {
        setCenter(isCenter, BOTTOM);
    }

    private void setCenter(boolean isCenter, int direction) {
        mIsCenter[direction] = isCenter;
    }

    public void setLeftEnabled(boolean enabled) {
        setEnabled(enabled, LEFT);
    }

    public void setTopEnabled(boolean enabled) {
        setEnabled(enabled, TOP);
    }

    public void setRightEnabled(boolean enabled) {
        setEnabled(enabled, RIGHT);
    }

    public void setBottomEnabled(boolean enabled) {
        setEnabled(enabled, BOTTOM);
    }

    private void setEnabled(boolean enabled, int direction) {
        mEnabled[direction] = enabled;
    }

    public void wrapView(@NonNull View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            int i = group.indexOfChild(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            group.removeViewAt(i);
            group.addView(this, i, layoutParams);
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public void setOnFullSwipeListener(OnFullSwipeListener listener) {
        mListener = listener;
    }

    public boolean isOpen(int direction) {
        return progresses[direction] >= TRIGGER_PROGRESS;
    }

    public void close() {
        mCloses[LEFT] = true;
        mCloses[TOP] = true;
        mCloses[RIGHT] = true;
        mCloses[BOTTOM] = true;
        postInvalidate();
    }

    public void close(@Direction int direction) {
        mCloses[direction] = true;
        mStartSpeed[direction] = 0.01f;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mLimit = Math.min(mWidth, mHeight) / 3;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawPath(canvas);
        animClose();
    }

    private void drawPath(Canvas canvas) {
        drawPath(canvas, LEFT);
        drawPath(canvas, TOP);
        drawPath(canvas, RIGHT);
        drawPath(canvas, BOTTOM);
    }

    private void drawPath(Canvas canvas, int direction) {
        if (mPath[direction] == null) return;
        updatePaint(direction);
        canvas.drawPath(getPath(direction), mPaint);
        drawIcon(canvas, direction);
    }

    private Path getPath(int direction) {
        if (preProgresses[direction] != progresses[direction]) {
            mPath[direction].reset();
            float edge, pivot = mDown[direction];
            int mark;
            if (direction == LEFT) {
                edge = 0;
                mark = 1;
            } else if (direction == TOP) {
                edge = 0;
                mark = 1;
            } else if (direction == RIGHT) {
                edge = mWidth;
                mark = -1;
            } else {
                edge = mHeight;
                mark = -1;
            }
            if (direction == LEFT || direction == RIGHT) {
                curPathX = edge;
                curPathY = pivot - halfSize;
            } else {
                curPathX = pivot - halfSize;
                curPathY = edge;
            }
            mPath[direction].moveTo(curPathX, curPathY);

            quad(edge, pivot - halfSize, direction);
            quad(edge + progresses[direction] * unit * mark, pivot - halfSize + 5 * unit, direction);// 1, 5
            quad(edge + progresses[direction] * 10 * unit * mark, pivot, direction);// 10, 16
            quad(edge + progresses[direction] * unit * mark, pivot + halfSize - 5 * unit, direction);
            quad(edge, pivot + halfSize, direction);
            quad(edge, pivot + halfSize, direction);
        }
        return mPath[direction];
    }

    private void drawIcon(Canvas canvas, int direction) {
        if (mDrawables[direction] == null) return;
        if (mBitmaps[direction] == null || mBitmaps[direction].isRecycled()) {
            mBitmaps[direction] = drawable2Bitmap(mDrawables[direction]);
        }
        if (mBitmaps[direction] == null || mBitmaps[direction].isRecycled()) {
            Log.e(TAG, "couldn't get bitmap.");
            return;
        }
        float bitmapWidth = mBitmaps[direction].getWidth();
        float bitmapHeight = mBitmaps[direction].getHeight();
        float fitSize = (int) (progresses[direction] * 5 * unit);

        float width, height, deltaWidth = 0, deltaHeight = 0;

        if (bitmapWidth >= bitmapHeight) {
            width = fitSize;
            height = width * bitmapHeight / bitmapWidth;
            deltaHeight = fitSize - height;
        } else {
            height = fitSize;
            width = height * bitmapWidth / bitmapHeight;
            deltaWidth = fitSize - width;
        }

        if (direction == LEFT) {
            mRectF.left = 0 + progresses[direction] * unit * 1 + deltaWidth / 2 * 1;
            mRectF.top = mDown[LEFT] - height / 2;
            mRectF.right = mRectF.left + width;
            mRectF.bottom = mRectF.top + height;
        } else if (direction == RIGHT) {
            mRectF.right = mWidth + progresses[direction] * unit * -1 + deltaWidth / 2 * -1;
            mRectF.top = mDown[RIGHT] - height / 2f;
            mRectF.left = mRectF.right - width;
            mRectF.bottom = mRectF.top + height;
        } else if (direction == TOP) {
            mRectF.left = mDown[TOP] - width / 2;
            mRectF.top = 0 + progresses[direction] * unit * 1 + deltaHeight / 2 * 1;
            mRectF.right = mRectF.left + width;
            mRectF.bottom = mRectF.top + height;
        } else {
            mRectF.left = mDown[BOTTOM] - width / 2;
            mRectF.bottom = mHeight + progresses[direction] * unit * -1 + deltaHeight / 2 * -1;
            mRectF.top = mRectF.bottom - height;
            mRectF.right = mRectF.left + width;
        }
        canvas.drawBitmap(mBitmaps[direction], null, mRectF, null);
    }

    private void quad(float pathX, float pathY, int direction) {
        float preX = curPathX;
        float preY = curPathY;
        if (direction == LEFT || direction == RIGHT) {
            curPathX = pathX;
            curPathY = pathY;
        } else {
            //noinspection SuspiciousNameCombination
            curPathX = pathY;
            //noinspection SuspiciousNameCombination
            curPathY = pathX;
        }
        mPath[direction].quadTo(preX, preY, (preX + curPathX) / 2, (preY + curPathY) / 2);
    }

    private float curPathX;
    private float curPathY;

    private void updatePaint(int direction) {
        mPaint.setColor(mPaintColor[direction]);
        float alphaProgress = progresses[direction];
        if (alphaProgress < 0.25f) {
            alphaProgress = 0.25f;
        } else if (alphaProgress > 0.75f) {
            alphaProgress = 0.75f;
        }
        mPaint.setAlpha((int) (alphaProgress * 255));
    }

    private void animClose() {
        boolean l = animClose(LEFT);
        boolean u = animClose(TOP);
        boolean r = animClose(RIGHT);
        boolean d = animClose(BOTTOM);
        if (l || u || r || d) {
            postInvalidateDelayed(0);
        }
    }

    private boolean animClose(int direction) {
        if (mCloses[direction]) {
            if (progresses[direction] > 0) {
                progresses[direction] -= mStartSpeed[direction];
                if (progresses[direction] <= 0) {
                    progresses[direction] = 0;
                    mCloses[direction] = false;
                }
                mStartSpeed[direction] += 0.1;
                return true;
            }
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mDownX = ev.getX();
            mDownY = ev.getY();
            mIsStart[LEFT] = mEnabled[LEFT] && mDrawables[LEFT] != null && !isOpen(LEFT) && mDownX <= mEdgeSizes[LEFT];
            mIsStart[TOP] = mEnabled[TOP] && mDrawables[TOP] != null && !isOpen(TOP) && mDownY <= mEdgeSizes[TOP];
            mIsStart[RIGHT] = mEnabled[RIGHT] && mDrawables[RIGHT] != null && !isOpen(RIGHT) && mDownX >= getWidth() - mEdgeSizes[RIGHT];
            mIsStart[BOTTOM] = mEnabled[BOTTOM] && mDrawables[BOTTOM] != null && !isOpen(BOTTOM) && mDownY >= getHeight() - mEdgeSizes[BOTTOM];
            mIsEdgeStart = mIsStart[LEFT] || mIsStart[TOP] || mIsStart[RIGHT] || mIsStart[BOTTOM];
            if (mIsEdgeStart) {
                mStartDirection = -1;
            }
            return true;
        }
        if (mIsEdgeStart) {
            if (action == MotionEvent.ACTION_MOVE) {
                mCurrentX = ev.getX();
                mCurrentY = ev.getY();
                if (mStartDirection == -1) {
                    float deltaX = mCurrentX - mDownX;
                    float deltaY = mCurrentY - mDownY;
                    float disX = Math.abs(deltaX);
                    float disY = Math.abs(deltaY);
                    if (disX > mTouchSlop || disY > mTouchSlop) {
                        if (disX >= disY) {
                            if (mIsStart[LEFT] && deltaX > 0) {
                                decideDirection(LEFT);
                            } else if (mIsStart[RIGHT] && deltaX < 0) {
                                decideDirection(RIGHT);
                            }
                        } else {
                            if (mIsStart[TOP] && deltaY > 0) {
                                decideDirection(TOP);
                            } else if (mIsStart[BOTTOM] && deltaY < 0) {
                                decideDirection(BOTTOM);
                            }
                        }
                    }
                }
                if (mStartDirection != -1) {
                    float preProgress = preProgresses[mStartDirection];
                    preProgresses[mStartDirection] = progresses[mStartDirection];
                    progresses[mStartDirection] = calculateProgress();
                    if (Math.abs(preProgress - progresses[mStartDirection]) > 0.01) {
                        postInvalidate();
                    } else {
                        preProgresses[mStartDirection] = preProgress;
                    }
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mStartDirection != -1) {
                    mCurrentX = ev.getX();
                    mCurrentY = ev.getY();
                    progresses[mStartDirection] = calculateProgress();
                    if (isOpen(mStartDirection)) {
                        if (mListener != null) {
                            mListener.onFullSwipe(mStartDirection);
                        }
                    } else {
                        close(mStartDirection);
                    }
                }
            }
        }
        return true;
    }

    private void decideDirection(int direction) {
        if (direction == LEFT || direction == RIGHT) {
            if (mIsCenter[direction]) {
                mDown[direction] = mHeight / 2f;
            } else {
                if (mDownY < halfSize) {
                    mDown[direction] = halfSize;
                } else if (mDownY >= mHeight - halfSize) {
                    mDown[direction] = mHeight - halfSize;
                } else {
                    mDown[direction] = mDownY;
                }
            }
        } else {
            if (mIsCenter[direction]) {
                mDown[direction] = mWidth / 2f;
            } else {
                if (mDownX < halfSize) {
                    mDown[direction] = halfSize;
                } else if (mDownX >= mWidth - halfSize) {
                    mDown[direction] = mWidth - halfSize;
                } else {
                    mDown[direction] = mDownX;
                }
            }
        }
        mStartDirection = direction;
        if (mPath[direction] == null) {
            mPath[direction] = new Path();
        }
        preProgresses[direction] = 0;
        cancelChildViewTouch();
        requestDisallowInterceptTouchEvent(true);
    }

    private float calculateProgress() {
        if (mStartDirection == LEFT) {
            float deltaX = mCurrentX - mDownX;
            if (deltaX <= 0) return 0;
            return Math.min(deltaX / mLimit, 1);
        } else if (mStartDirection == TOP) {
            float deltaY = mCurrentY - mDownY;
            if (deltaY <= 0) return 0;
            return Math.min(deltaY / mLimit, 1);
        } else if (mStartDirection == RIGHT) {
            float deltaX = mCurrentX - mDownX;
            if (deltaX >= 0) return 0;
            return Math.min(-deltaX / mLimit, 1);
        } else {
            float deltaY = mCurrentY - mDownY;
            if (deltaY >= 0) return 0;
            return Math.min(-deltaY / mLimit, 1);
        }
    }

    private void cancelChildViewTouch() {
        final long now = SystemClock.uptimeMillis();
        final MotionEvent cancelEvent = MotionEvent.obtain(now, now,
                MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).dispatchTouchEvent(cancelEvent);
        }
        cancelEvent.recycle();
    }

    private static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static final Object sLock = new Object();

    private static TypedValue sTempValue;

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(id);
        } else if (Build.VERSION.SDK_INT >= 16) {
            return context.getResources().getDrawable(id);
        } else {
            final int resolvedId;
            synchronized (sLock) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
    }

    public interface OnFullSwipeListener {
        void onFullSwipe(@Direction int direction);
    }
}
