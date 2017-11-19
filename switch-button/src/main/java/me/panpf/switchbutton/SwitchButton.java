package me.panpf.switchbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Scroller;

/**
 * 开关按钮
 */
public class SwitchButton extends CompoundButton {
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private int buttonLeft;  //按钮在画布上的X坐标
    private int buttonTop;  //按钮在画布上的Y坐标
    private int tempSlideX = 0; //X轴当前坐标，用于动态绘制图片显示坐标，实现滑动效果
    private int tempMinSlideX = 0;  //X轴最小坐标，用于防止往左边滑动时超出范围
    private int tempMaxSlideX = 0;  //X轴最大坐标，用于防止往右边滑动时超出范围
    private int tempTotalSlideDistance;   //滑动距离，用于记录每次滑动的距离，在滑动结束后根据距离判断是否切换状态或者回滚
    private int duration = 200; //动画持续时间
    private int touchMode; //触摸模式，用来在处理滑动事件的时候区分操作
    private int touchSlop;
    private int withTextInterval = 16;   //文字和按钮之间的间距
    private float touchX;   //记录上次触摸坐标，用于计算滑动距离
    private float minChangeDistanceScale = 0.2f;   //有效距离比例，例如按钮宽度为 100，比例为 0.3，那么只有当滑动距离大于等于 (100*0.3) 才会切换状态，否则就回滚
    private Paint paint;    //画笔，用来绘制遮罩效果
    private RectF buttonRectF;   //按钮的位置
    private Drawable frameDrawable; //框架层图片
    private Drawable stateDrawable;    //状态图片
    private Drawable stateMaskDrawable;    //状态遮罩图片
    private Drawable sliderDrawable;    //滑块图片
    private SwitchScroller switchScroller;  //切换滚动器，用于实现平滑滚动效果
    private PorterDuffXfermode porterDuffXfermode;//遮罩类型

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * 初始化
     *
     * @param attrs 属性
     */
    private void init(AttributeSet attrs) {
        setGravity(Gravity.CENTER_VERTICAL);
        paint = new Paint();
        paint.setColor(Color.RED);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        switchScroller = new SwitchScroller(getContext(), new AccelerateDecelerateInterpolator());
        buttonRectF = new RectF();

        if (attrs != null && getContext() != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
            if (typedArray != null) {
                withTextInterval = (int) typedArray.getDimension(R.styleable.SwitchButton_withTextInterval, 0.0f);
                setDrawables(
                        typedArray.getDrawable(R.styleable.SwitchButton_frameDrawable),
                        typedArray.getDrawable(R.styleable.SwitchButton_stateDrawable),
                        typedArray.getDrawable(R.styleable.SwitchButton_stateMaskDrawable),
                        typedArray.getDrawable(R.styleable.SwitchButton_sliderDrawable)
                );
                typedArray.recycle();
            }
        }

        ViewConfiguration config = ViewConfiguration.get(getContext());
        touchSlop = config.getScaledTouchSlop();
        setChecked(isChecked());
        setClickable(true); //设置允许点击，当用户点击在按钮其它区域的时候就会切换状态
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算宽度
        int measureWidth;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:   // 如果 widthSize 是当前视图可使用的最大宽度
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            case MeasureSpec.EXACTLY:   // 如果 widthSize 是当前视图可使用的绝对宽度
                measureWidth = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED:   // 如果 widthSize 对当前视图宽度的计算没有任何参考意义
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            default:
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
        }

        //计算高度
        int measureHeight;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:   // 如果 heightSize 是当前视图可使用的最大宽度
                measureHeight = (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            case MeasureSpec.EXACTLY://如果heightSize是当前视图可使用的绝对宽度
                measureHeight = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED:   // 如果 heightSize 对当前视图宽度的计算没有任何参考意义
                measureHeight = (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            default:
                measureHeight = (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (measureWidth < getMeasuredWidth()) {
            measureWidth = getMeasuredWidth();
        }

        if (measureHeight < getMeasuredHeight()) {
            measureHeight = getMeasuredHeight();
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Drawable[] drawables = getCompoundDrawables();
        int drawableRightWidth = 0;
        int drawableTopHeight = 0;
        int drawableBottomHeight = 0;
        if (drawables != null) {
            if (drawables.length > 1 && drawables[1] != null) {
                drawableTopHeight = drawables[1].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
            if (drawables.length > 2 && drawables[2] != null) {
                drawableRightWidth = drawables[2].getIntrinsicWidth() + getCompoundDrawablePadding();
            }
            if (drawables.length > 3 && drawables[3] != null) {
                drawableBottomHeight = drawables[3].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
        }

        buttonLeft = (getWidth() - (frameDrawable != null ? frameDrawable.getIntrinsicWidth() : 0) - getPaddingRight() - drawableRightWidth);
        buttonTop = (getHeight() - (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0) + drawableTopHeight - drawableBottomHeight) / 2;
        int buttonRight = buttonLeft + (frameDrawable != null ? frameDrawable.getIntrinsicWidth() : 0);
        int buttonBottom = buttonTop + (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0);
        buttonRectF.set(buttonLeft, buttonTop, buttonRight, buttonBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 保存图层并全体偏移，让 paddingTop 和 paddingLeft 生效
        canvas.save();
        canvas.translate(buttonLeft, buttonTop);

        // 绘制状态层
        if (stateDrawable != null && stateMaskDrawable != null) {
            Bitmap stateBitmap = getBitmapFromDrawable(stateDrawable);
            if (stateMaskDrawable != null && stateBitmap != null && !stateBitmap.isRecycled()) {
                // 保存并创建一个新的透明层，如果不这样做的话，画出来的背景会是黑的
                int src = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
                // 绘制遮罩层
                stateMaskDrawable.draw(canvas);
                // 绘制状态图片按并应用遮罩效果
                paint.setXfermode(porterDuffXfermode);
                canvas.drawBitmap(stateBitmap, tempSlideX, 0, paint);
                paint.setXfermode(null);
                // 融合图层
                canvas.restoreToCount(src);
            }
        }

        // 绘制框架层
        if (frameDrawable != null) {
            frameDrawable.draw(canvas);
        }

        // 绘制滑块层
        if (sliderDrawable != null) {
            Bitmap sliderBitmap = getBitmapFromDrawable(sliderDrawable);
            if (sliderBitmap != null && !sliderBitmap.isRecycled()) {
                canvas.drawBitmap(sliderBitmap, tempSlideX, 0, paint);
            }
        }

        // 融合图层
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            eventType = event.getActionMasked();
        } else {
            eventType = event.getAction() & MotionEvent.ACTION_MASK;
        }
        switch (eventType) {
            case MotionEvent.ACTION_DOWN: {
                // 如果按钮当前可用并且按下位置正好在按钮之内
                if (isEnabled() && buttonRectF.contains(event.getX(), event.getY())) {
                    touchMode = TOUCH_MODE_DOWN;
                    tempTotalSlideDistance = 0; // 清空总滑动距离
                    touchX = event.getX();  // 记录X轴坐标
                    setClickable(false);    // 当用户触摸在按钮位置的时候禁用点击效果，这样做的目的是为了不让背景有按下效果
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                switch (touchMode) {
                    case TOUCH_MODE_IDLE: {
                        break;
                    }
                    case TOUCH_MODE_DOWN: {
                        final float x = event.getX();
                        if (Math.abs(x - touchX) > touchSlop) {
                            touchMode = TOUCH_MODE_DRAGGING;
                            // 禁值父View拦截触摸事件
                            // 如果不加这段代码的话，当被ScrollView包括的时候，你会发现，当你在此按钮上按下，
                            // 紧接着滑动的时候ScrollView会跟着滑动，然后按钮的事件就丢失了，这会造成很难完成滑动操作
                            // 这样一来用户会抓狂的，加上这句话呢ScrollView就不会滚动了
                            if (getParent() != null) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            touchX = x;
                            return true;
                        }
                        break;
                    }
                    case TOUCH_MODE_DRAGGING: {
                        float newTouchX = event.getX();
                        tempTotalSlideDistance += setSlideX(tempSlideX + ((int) (newTouchX - touchX)));    // 更新X轴坐标并记录总滑动距离
                        touchX = newTouchX;
                        invalidate();
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                setClickable(true);

                //结尾滑动操作
                if (touchMode == TOUCH_MODE_DRAGGING) {// 这是滑动操作
                    touchMode = TOUCH_MODE_IDLE;
                    // 如果滑动距离大于等于最小切换距离就切换状态，否则回滚
                    if (Math.abs(tempTotalSlideDistance) >= Math.abs(frameDrawable.getIntrinsicWidth() * minChangeDistanceScale)) {
                        toggle();   //切换状态
                    } else {
                        switchScroller.startScroll(isChecked());
                    }
                } else if (touchMode == TOUCH_MODE_DOWN) { // 这是按在按钮上的单击操作
                    touchMode = TOUCH_MODE_IDLE;
                    toggle();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                setClickable(true);
                if (touchMode == TOUCH_MODE_DRAGGING) {
                    touchMode = TOUCH_MODE_IDLE;
                    switchScroller.startScroll(isChecked()); //回滚
                } else {
                    touchMode = TOUCH_MODE_IDLE;
                }
                break;
            }
        }

        super.onTouchEvent(event);
        return isEnabled();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if (frameDrawable != null) frameDrawable.setState(drawableState);  //更新框架图片的状态
        if (stateDrawable != null) stateDrawable.setState(drawableState); //更新状态图片的状态
        if (stateMaskDrawable != null) stateMaskDrawable.setState(drawableState); //更新状态遮罩图片的状态
        if (sliderDrawable != null) sliderDrawable.setState(drawableState); //更新滑块图片的状态
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == frameDrawable || who == stateDrawable || who == stateMaskDrawable || who == sliderDrawable;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void jumpDrawablesToCurrentState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.jumpDrawablesToCurrentState();
            if (frameDrawable != null) frameDrawable.jumpToCurrentState();
            if (stateDrawable != null) stateDrawable.jumpToCurrentState();
            if (stateMaskDrawable != null) stateMaskDrawable.jumpToCurrentState();
            if (sliderDrawable != null) sliderDrawable.jumpToCurrentState();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if (changed) {
            if (getWidth() > 0 && switchScroller != null) {   //如果已经绘制完成
                switchScroller.startScroll(checked);
            } else {
                setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX);  //直接修改X轴坐标，因为尚未绘制完成的时候，动画执行效果不理想，所以直接修改坐标，而不执行动画
            }
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        //重写此方法实现让文本提前换行，避免当文本过长时被按钮给盖住
        int padding = super.getCompoundPaddingRight() + (frameDrawable != null ? frameDrawable.getIntrinsicWidth() : 0);
        if (!TextUtils.isEmpty(getText())) {
            padding += withTextInterval;
        }
        return padding;
    }

    /**
     * 设置图片
     *
     * @param frameBitmap       框架图片
     * @param stateDrawable     状态图片
     * @param stateMaskDrawable 状态遮罩图片
     * @param sliderDrawable    滑块图片
     */
    public void setDrawables(Drawable frameBitmap, Drawable stateDrawable, Drawable stateMaskDrawable, Drawable sliderDrawable) {
        if (frameBitmap == null || stateDrawable == null || stateMaskDrawable == null || sliderDrawable == null) {
            throw new IllegalArgumentException("ALL NULL");
        }

        this.frameDrawable = frameBitmap;
        this.stateDrawable = stateDrawable;
        this.stateMaskDrawable = stateMaskDrawable;
        this.sliderDrawable = sliderDrawable;

        this.frameDrawable.setBounds(0, 0, this.frameDrawable.getIntrinsicWidth(), this.frameDrawable.getIntrinsicHeight());
        this.frameDrawable.setCallback(this);
        this.stateDrawable.setBounds(0, 0, this.stateDrawable.getIntrinsicWidth(), this.stateDrawable.getIntrinsicHeight());
        this.stateDrawable.setCallback(this);
        this.stateMaskDrawable.setBounds(0, 0, this.stateMaskDrawable.getIntrinsicWidth(), this.stateMaskDrawable.getIntrinsicHeight());
        this.stateMaskDrawable.setCallback(this);
        this.sliderDrawable.setBounds(0, 0, this.sliderDrawable.getIntrinsicWidth(), this.sliderDrawable.getIntrinsicHeight());
        this.sliderDrawable.setCallback(this);

        this.tempMinSlideX = (-1 * (stateDrawable.getIntrinsicWidth() - frameBitmap.getIntrinsicWidth()));  //初始化X轴最小值
        setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX);  //根据选中状态初始化默认坐标

        requestLayout();
    }

    /**
     * 设置图片
     *
     * @param frameDrawableResId     框架图片ID
     * @param stateDrawableResId     状态图片ID
     * @param stateMaskDrawableResId 状态遮罩图片ID
     * @param sliderDrawableResId    滑块图片ID
     */
    public void setDrawableResIds(int frameDrawableResId, int stateDrawableResId, int stateMaskDrawableResId, int sliderDrawableResId) {
        if (getResources() != null) {
            setDrawables(
                    getResources().getDrawable(frameDrawableResId),
                    getResources().getDrawable(stateDrawableResId),
                    getResources().getDrawable(stateMaskDrawableResId),
                    getResources().getDrawable(sliderDrawableResId)
            );
        }
    }

    /**
     * 设置动画持续时间
     *
     * @param duration 动画持续时间
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置有效距离比例
     *
     * @param minChangeDistanceScale 有效距离比例，例如按钮宽度为100，比例为0.3，那么只有当滑动距离大于等于(100*0.3)才会切换状态，否则就回滚
     */
    public void setMinChangeDistanceScale(float minChangeDistanceScale) {
        this.minChangeDistanceScale = minChangeDistanceScale;
    }

    /**
     * 设置按钮和文本之间的间距
     *
     * @param withTextInterval 按钮和文本之间的间距，当有文本的时候此参数才能派上用场
     */
    public void setWithTextInterval(int withTextInterval) {
        this.withTextInterval = withTextInterval;
        requestLayout();
    }

    /**
     * 设置X轴坐标
     *
     * @param newSlideX 新的X轴坐标
     * @return Xz轴坐标增加的值，例如newSlideX等于100，旧的X轴坐标为49，那么返回值就是51
     */
    private int setSlideX(int newSlideX) {
        //防止滑动超出范围
        if (newSlideX < tempMinSlideX) newSlideX = tempMinSlideX;
        if (newSlideX > tempMaxSlideX) newSlideX = tempMaxSlideX;
        //计算本次距离增量
        int addDistance = newSlideX - tempSlideX;
        this.tempSlideX = newSlideX;
        return addDistance;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof DrawableContainer) {
            return getBitmapFromDrawable(drawable.getCurrent());
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }

    /**
     * 切换滚动器，用于实现滚动动画
     */
    private class SwitchScroller implements Runnable {
        private Scroller scroller;

        public SwitchScroller(Context context, android.view.animation.Interpolator interpolator) {
            this.scroller = new Scroller(context, interpolator);
        }

        /**
         * 开始滚动
         *
         * @param checked 是否选中
         */
        public void startScroll(boolean checked) {
            scroller.startScroll(tempSlideX, 0, (checked ? tempMinSlideX : tempMaxSlideX) - tempSlideX, 0, duration);
            post(this);
        }

        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                setSlideX(scroller.getCurrX());
                invalidate();
                post(this);
            }
        }
    }
}

