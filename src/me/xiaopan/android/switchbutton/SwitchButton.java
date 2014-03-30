package me.xiaopan.android.switchbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Scroller;

/**
 * 开关按钮
 */
public class SwitchButton extends CompoundButton {
    private int buttonDrawX;  //按钮在画布上的X坐标
    private int buttonDrawY;  //按钮在画布上的Y坐标
    private int tempSlideX = 0; //X轴当前坐标，用于动态绘制图片显示坐标，实现滑动效果
    private int tempMinSlideX = 0;  //X轴最小坐标，用于防止往左边滑动时超出范围
    private int tempMaxSlideX = 0;  //X轴最大坐标，用于防止往右边滑动时超出范围
    private int tempTotalSlideDistance;   //滑动距离，用于记录每次滑动的距离，在滑动结束后根据距离判断是否切换状态或者回滚
    private int duration = 200; //动画持续时间
    private int withTextInterval;
    private float tempTouchX;   //记录上次触摸坐标，用于计算滑动距离
    private float minChangeDistanceScale = 0.2f;   //有效距离比例，例如按钮宽度为100，比例为0.3，那么只有当滑动距离大于等于(100*0.3)才会切换状态，否则就回滚
    private boolean tempAllowMode;  //是否允许滑动，当按下的位置不在按钮之内的时候就不允许滑动
    private Paint paint;    //画笔，用来绘制遮罩效果
    private RectF buttonRectF;   //按钮的位置
    private Drawable frameDrawable; //框架层图片
    private Drawable stateDrawable;    //状态图片
    private Drawable stateMaskDrawable;    //状态遮罩图片
    private Drawable sliderDrawable;    //滑块图片
    private Drawable sliderMaskDrawable;    //滑块遮罩图片
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
     * @param attrs 属性
     */
    private void init(AttributeSet attrs){
        setGravity(Gravity.CENTER_VERTICAL);
        paint = new Paint();
        paint.setColor(Color.RED);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        switchScroller = new SwitchScroller(getContext(), new AccelerateDecelerateInterpolator());
        buttonRectF = new RectF();

        if(attrs != null && getContext() != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
            if(typedArray != null){
                withTextInterval = (int) typedArray.getDimension(R.styleable.SwitchButton_withTextInterval, 0.0f);
                setDrawables(
                    typedArray.getDrawable(R.styleable.SwitchButton_frameDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_stateDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_stateMaskDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_sliderDrawable),
                    typedArray.getDrawable(R.styleable.SwitchButton_sliderMaskDrawable)
                );
                typedArray.recycle();
            }
        }
        setChecked(isChecked());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算宽度
        int measureWidth;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST://如果widthSize是当前视图可使用的最大宽度
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            case MeasureSpec.EXACTLY://如果widthSize是当前视图可使用的绝对宽度
                measureWidth = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED://如果widthSize对当前视图宽度的计算没有任何参考意义
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
            default:
                measureWidth = getCompoundPaddingLeft() + getCompoundPaddingRight();
                break;
        }

        //计算高度
        int measureHeight;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST://如果heightSize是当前视图可使用的最大宽度
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            case MeasureSpec.EXACTLY://如果heightSize是当前视图可使用的绝对宽度
                measureHeight = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED://如果heightSize对当前视图宽度的计算没有任何参考意义
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
            default:
                measureHeight = (frameDrawable != null? frameDrawable.getIntrinsicHeight():0) + getCompoundPaddingTop() + getCompoundPaddingBottom();
                break;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(measureWidth < getMeasuredWidth()){
            measureWidth = getMeasuredWidth();
        }

        if(measureHeight < getMeasuredHeight()){
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
        if(drawables != null){
            if(drawables.length > 1 && drawables[1] != null){
                drawableTopHeight = drawables[1].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
            if(drawables.length > 2 && drawables[2] != null){
                drawableRightWidth = drawables[2].getIntrinsicWidth() + getCompoundDrawablePadding();
            }
            if(drawables.length > 3 && drawables[3] != null){
                drawableBottomHeight = drawables[3].getIntrinsicHeight() + getCompoundDrawablePadding();
            }
        }

        buttonDrawX = (getWidth() - (frameDrawable!=null?frameDrawable.getIntrinsicWidth():0) - getPaddingRight() - drawableRightWidth);
        buttonDrawY = (getHeight() - (frameDrawable!=null?frameDrawable.getIntrinsicHeight():0) + drawableTopHeight - drawableBottomHeight) / 2;
        buttonRectF.set(buttonDrawX, buttonDrawY, buttonDrawX + (frameDrawable != null ? frameDrawable.getIntrinsicWidth() : 0), buttonDrawY + (frameDrawable != null ? frameDrawable.getIntrinsicHeight() : 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //保存图层并全体偏移，让paddingTop和paddingLeft生效
        canvas.save();
        canvas.translate(buttonDrawX, buttonDrawY);

        //绘制状态层
        if(stateDrawable != null && stateMaskDrawable != null){
            Bitmap stateBitmap = getBitmapFromDrawable(stateDrawable);
            if(stateMaskDrawable != null && stateBitmap != null && !stateBitmap.isRecycled()){
                //保存并创建一个新的透明层，如果不这样做的话，画出来的背景会是黑的
                int src = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
                //绘制遮罩层
                stateMaskDrawable.draw(canvas);
                //绘制状态图片按并应用遮罩效果
                paint.setXfermode(porterDuffXfermode);
                canvas.drawBitmap(stateBitmap, tempSlideX, 0, paint);
                paint.setXfermode(null);
                //融合图层
                canvas.restoreToCount(src);
            }
        }

        //绘制框架层
        if(frameDrawable != null){
            frameDrawable.draw(canvas);
        }

        //绘制滑块层
        if(sliderDrawable != null && sliderMaskDrawable != null){
            Bitmap sliderBitmap = getBitmapFromDrawable(sliderDrawable);
            if(sliderMaskDrawable != null && sliderBitmap != null && !sliderBitmap.isRecycled()){
                //保存并创建一个新的透明层，如果不这样做的话，画出来的背景会是黑的
                int src = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
                //绘制遮罩层
                sliderMaskDrawable.draw(canvas);
                //绘制滑块图片按并应用遮罩效果
                paint.setXfermode(porterDuffXfermode);
                canvas.drawBitmap(sliderBitmap, tempSlideX, 0, paint);
                paint.setXfermode(null);
                //融合图层
                canvas.restoreToCount(src);
            }
        }

        //融合图层
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(isEnabled()){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    tempAllowMode = buttonRectF.contains(event.getX(), event.getY());   //判断按下的位置是否在按钮的范围之内
                    if(!tempAllowMode){
                        setPressed(true);   //激活按下状态
                    }
                    tempTotalSlideDistance = 0; //清空总滑动距离
                    tempTouchX = event.getX();  //记录X轴坐标
                    break;
                case MotionEvent.ACTION_MOVE :
                    if(tempAllowMode){
                        float newTouchX = event.getX();
                        tempTotalSlideDistance += setSlideX(tempSlideX + ((int) (newTouchX - tempTouchX)));    //更新X轴坐标并记录总滑动距离
                        tempTouchX = newTouchX; //记录X轴坐标
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP :
                    setPressed(false);  //取消按下状态
                    if(Math.abs(tempTotalSlideDistance) > 0){//当滑动距离大于0才会被认为这是一次有效的滑动操作，否则就是单机操作
                        if(Math.abs(tempTotalSlideDistance) >= Math.abs(frameDrawable.getIntrinsicWidth() * minChangeDistanceScale)){//如果滑动距离大于等于最小切换距离就切换状态
                            setChecked(!isChecked());   //切换状态
                        }else{
                            switchScroller.startScroll(isChecked());//本次滑动无效，回滚
                        }
                    }else{
                        setChecked(!isChecked());   //单击切换状态
                    }
                    break;
                case MotionEvent.ACTION_CANCEL :
                    setPressed(false);  //取消按下状态
                    switchScroller.startScroll(isChecked()); //回滚
                    break;
                case MotionEvent.ACTION_OUTSIDE :
                    setPressed(false);  //取消按下状态
                    switchScroller.startScroll(isChecked()); //回滚
                    break;
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if(frameDrawable != null){
            frameDrawable.setState(drawableState);  //更新框架图片的状态
        }
        if(stateDrawable != null){
            stateDrawable.setState(drawableState); //更新状态图片的状态
        }
        if(stateMaskDrawable != null){
            stateMaskDrawable.setState(drawableState); //更新状态遮罩图片的状态
        }
        if(sliderDrawable != null){
            sliderDrawable.setState(drawableState); //更新滑块图片的状态
        }
        if(sliderMaskDrawable != null){
            sliderMaskDrawable.setState(drawableState); //更新滑块遮罩图片的状态
        }
        invalidate();
    }

    @Override
     public void setChecked(boolean checked) {
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if(changed){
            if(getWidth() > 0 && switchScroller != null){   //如果已经绘制完成
                switchScroller.startScroll(checked);
            }else{
                setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX);  //直接修改X轴坐标，因为尚未绘制完成的时候，动画执行效果不理想，所以直接修改坐标，而不执行动画
            }
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        //重写此方法实现让文本提前换行，避免当文本过长时被按钮给盖住
        int padding = super.getCompoundPaddingRight() + (frameDrawable!=null?frameDrawable.getIntrinsicWidth():0);
        if (!TextUtils.isEmpty(getText())) {
            padding += withTextInterval;
        }
        return padding;
    }

    /**
     * 设置图片
     * @param frameBitmap 框架图片
     * @param stateDrawable 状态图片
     * @param stateMaskDrawable 状态遮罩图片
     * @param sliderDrawable 滑块图片
     * @param sliderMaskDrawable 滑块遮罩图片
     */
    public void setDrawables(Drawable frameBitmap, Drawable stateDrawable, Drawable stateMaskDrawable, Drawable sliderDrawable, Drawable sliderMaskDrawable){
        if(frameBitmap == null || stateDrawable == null || stateMaskDrawable == null || sliderDrawable == null || sliderMaskDrawable == null){
            throw new IllegalArgumentException("ALL NOT NULL");
        }

        this.frameDrawable = frameBitmap;
        this.stateDrawable = stateDrawable;
        this.stateMaskDrawable = stateMaskDrawable;
        this.sliderDrawable = sliderDrawable;
        this.sliderMaskDrawable = sliderMaskDrawable;

        this.frameDrawable.setBounds(0, 0, this.frameDrawable.getIntrinsicWidth(), this.frameDrawable.getIntrinsicHeight());
        this.frameDrawable.setCallback(this);
        this.stateDrawable.setBounds(0, 0, this.stateDrawable.getIntrinsicWidth(), this.stateDrawable.getIntrinsicHeight());
        this.stateDrawable.setCallback(this);
        this.stateMaskDrawable.setBounds(0, 0, this.stateMaskDrawable.getIntrinsicWidth(), this.stateMaskDrawable.getIntrinsicHeight());
        this.stateMaskDrawable.setCallback(this);
        this.sliderDrawable.setBounds(0, 0, this.sliderDrawable.getIntrinsicWidth(), this.sliderDrawable.getIntrinsicHeight());
        this.sliderDrawable.setCallback(this);
        this.sliderMaskDrawable.setBounds(0, 0, this.sliderMaskDrawable.getIntrinsicWidth(), this.sliderMaskDrawable.getIntrinsicHeight());
        this.sliderMaskDrawable.setCallback(this);

        this.tempMinSlideX = (-1 * (stateDrawable.getIntrinsicWidth() - frameBitmap.getIntrinsicWidth()));  //初始化X轴最小值
        setSlideX(isChecked() ? tempMinSlideX : tempMaxSlideX);  //根据选中状态初始化默认坐标

        requestLayout();
    }

    /**
     * 设置图片
     * @param frameDrawableResId 框架图片ID
     * @param stateDrawableResId 状态图片ID
     * @param stateMaskDrawableResId 状态遮罩图片ID
     * @param sliderDrawableResId 滑块图片ID
     * @param sliderMaskDrawableResId 滑块遮罩图片ID
     */
    public void setDrawableResIds(int frameDrawableResId, int stateDrawableResId, int stateMaskDrawableResId, int sliderDrawableResId, int sliderMaskDrawableResId){
        if(getResources() != null){
            setDrawables(
                getResources().getDrawable(frameDrawableResId),
                getResources().getDrawable(stateDrawableResId),
                getResources().getDrawable(stateMaskDrawableResId),
                getResources().getDrawable(sliderDrawableResId),
                getResources().getDrawable(sliderMaskDrawableResId)
            );
        }
    }

    /**
     * 设置动画持续时间
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置有效距离比例
     * @param minChangeDistanceScale 有效距离比例，例如按钮宽度为100，比例为0.3，那么只有当滑动距离大于等于(100*0.3)才会切换状态，否则就回滚
     */
    public void setMinChangeDistanceScale(float minChangeDistanceScale) {
        this.minChangeDistanceScale = minChangeDistanceScale;
    }

    /**
     * 设置按钮和文本之间的间距
     * @param withTextInterval 按钮和文本之间的间距，当有文本的时候此参数才能派上用场
     */
    public void setWithTextInterval(int withTextInterval) {
        this.withTextInterval = withTextInterval;
        requestLayout();
    }

    /**
     * 设置X轴坐标
     * @param newSlideX 新的X轴坐标
     * @return Xz轴坐标增加的值，例如newSlideX等于100，旧的X轴坐标为49，那么返回值就是51
     */
    private int setSlideX(int newSlideX) {
        //防止滑动超出范围
        if(newSlideX < tempMinSlideX) newSlideX = tempMinSlideX;
        if(newSlideX > tempMaxSlideX) newSlideX = tempMaxSlideX;
        //计算本次距离增量
        int addDistance = newSlideX - tempSlideX;
        this.tempSlideX = newSlideX;
        return addDistance;
    }

    private static Bitmap getBitmapFromDrawable(Drawable drawable){
        if(drawable == null){
            return null;
        }

        if(drawable instanceof DrawableContainer){
            return getBitmapFromDrawable(drawable.getCurrent());
        }else if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else{
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
         * @param checked 是否选中
         */
        public void startScroll(boolean checked){
            scroller.startScroll(tempSlideX, 0, (checked? tempMinSlideX : tempMaxSlideX) - tempSlideX, 0, duration);
            post(this);
        }

        @Override
        public void run() {
            if(scroller.computeScrollOffset()){
                setSlideX(scroller.getCurrX());
                invalidate();
                post(this);
            }
        }
    }
}

