package org.hhu.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * 三维姿态检测控件
 * Created by Dexter on 2017/12/29.
 */

public class CompassView extends View {

    private Paint mCirclePaint;//外圆画笔
    private Paint mTextPaint;//方位画笔
    private Paint mLinepaint;//刻度画笔
    private float mDegree = 0f;//指针度
    private int mTextHight;//字高
    private Paint mDegreePain;//指针度画笔
    private Paint mSmallCirclePaint;//内圆画笔
    private Paint mSmallerCirclePaint;//最小内圆画笔
    private Paint mPointerPaint;//指针画笔
    private Paint mBorderPaint;//外圆边框画笔

    public CompassView(Context context) {
        super(context);
        initView();
    }
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    public float getmDegree() {
        return mDegree;
    }

    public void setmDegree(float mDegree) {
        this.mDegree = mDegree;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        int d = Math.min(width, height);
        setMeasuredDimension(d, d);
    }

    protected int measure(int measureSpec){
        int size = 0;
        int measureMode = MeasureSpec.getMode(measureSpec);
        if(measureMode == MeasureSpec.UNSPECIFIED){
            size = 250;
        }else{
            size = MeasureSpec.getSize(measureSpec);
        }
        return size;

    }

    protected void initView(){
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.WHITE);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(getResources().getColor(R.color.player_black));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(2f);
        mCirclePaint.setStyle(Style.STROKE);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.bootstrap_gray));
        mTextPaint.setTextSize(35f);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mLinepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinepaint.setColor(getResources().getColor(R.color.bootstrap_gray_dark));
        mDegreePain = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreePain.setColor(getResources().getColor(R.color.bootstrap_gray_light));
        mDegreePain.setTextSize(16f);
        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint.setColor(getResources().getColor(R.color.player_blue));
        mSmallerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallerCirclePaint.setColor(Color.WHITE);
        mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerPaint.setColor(getResources().getColor(R.color.player_red));
        mPointerPaint.setStrokeWidth(2f);
        mTextHight = (int) mLinepaint.measureText("NN");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int px = getMeasuredWidth()/2;
        int py = getMeasuredHeight()/2;
        int radius = Math.min(px, py);
        int smallRadius = (int)(radius * 0.5);
        int smallerRadius =(int)(radius * 0.2);
        canvas.drawCircle(px, py, radius,mBorderPaint);//外圆边框
        canvas.drawCircle(px, py, radius,mCirclePaint);//外圆
        canvas.drawCircle(px, py, smallRadius,mSmallCirclePaint);//小圆
        canvas.drawCircle(px, py, smallerRadius,mSmallerCirclePaint);//最小圆
        canvas.drawText(String.valueOf((int)mDegree)+"°", px-mTextHight*2, py+mTextHight, mTextPaint);//指针度数
        canvas.save();
        canvas.rotate(-mDegree, px, py);
        for(int i = 0;i<24;i++){
            int linePy = py-radius+(radius/20);
            int textPy = linePy + mTextHight+10;
            canvas.drawLine(px, py - radius, px, linePy, mLinepaint);//短刻度
            canvas.save();
            if (i % 6 == 0) {
                String location = null;
                switch (i) {
                    case 0:
                        location = "N";
                        canvas.drawLine(px, py-smallRadius, px, textPy+20, mPointerPaint);//指针
                        canvas.drawLine(px, py-mTextHight, px, py-smallerRadius, mPointerPaint);//指针
                        break;
                    case 6:
                        location = "W";
                        break;
                    case 12:
                        location = "S";
                        break;
                    case 18:
                        location = "E";
                        break;
                }
                canvas.drawLine(px, py - radius, px, linePy*2, mLinepaint);//长刻度
                canvas.drawText(location, px-mTextHight/2-6, textPy+15, mTextPaint);//方位
            }else if(i%3 == 0){
                canvas.drawText(String.valueOf(i*15), px-mTextHight, textPy, mDegreePain);//刻度文字
            }
            canvas.restore();
            canvas.rotate(-15, px, py);
        }
        canvas.restore();
    }
}
