package com.example.piechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {
    private List<PieBean> pieBeanList = new ArrayList<>();
    private Paint paint;

    private float centerX; //中心点坐标 x
    private float centerY; //中心点坐标 y

    private float radius;//未选中半径
    private float sRadius;//选中半径
    private float curRadius;
    private float sweep;

    private OnPositionChangeListener onPositionChangeListener;

    public void setOnPositionChangeListener(OnPositionChangeListener onPositionChangeListener) {
        this.onPositionChangeListener = onPositionChangeListener;
    }

    public void setPieBeanList(List<PieBean> pieBeanList) {
        this.pieBeanList = pieBeanList;
    }

    public void setsRadius(float sRadius) {
        this.sRadius = sRadius;
    }

    public PieChartView(Context context) {
        this(context,null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint=new Paint();
        paint.setTextSize(25);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float total = 0;
        for (int i = 0; i < pieBeanList.size(); i++) {
            total += pieBeanList.get(i).getNumber();
        }

        centerX = getPivotX();
        centerY = getPivotY();

        if (sRadius == 0) {
            sRadius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
        }

        radius=sRadius-10;

        float startc=0;//从0角度开始画
        for (int i = 0; i < pieBeanList.size(); i++) {
            //计算扇形扫过的角度
            if(total<=0){
                sweep =360/pieBeanList.size();
            }else{
                sweep =360*pieBeanList.get(i).getNumber()/total;
            }
            //设置当前扇形的颜色
            paint.setColor(getResources().getColor(pieBeanList.get(i).getColorRes()));
            //设置当前扇形的半径
            if(pieBeanList.get(i).isSelected()){
                curRadius =sRadius;
            }else{
                curRadius =radius;
            }

            //画扇形
            drawArc(canvas,paint,startc);

            //画扇形外围的 短线和百分数值。

            drawLineAndPercent(canvas,paint,startc,total,i);

            //记录每个扇形起始和结束角度，待会点击需要判断在哪个扇形里需要用到
            pieBeanList.get(i).setStartC(startc);
            pieBeanList.get(i).setEndC(startc+sweep);
            //上一个扇形的结束角度作为下一个扇形的开始角度
            startc=startc+sweep;

        }

    }

    private void drawLineAndPercent(Canvas canvas,Paint paint,float startc,float total,int i) {
        if ((pieBeanList.get(i).getNumber() > 0 && total > 0)) {

            float arcCenterC = startc + sweep / 2; //当前扇形弧线的中间点和圆心的连线 与 起始角度的夹角
            float arcCenterX = 0;  //当前扇形弧线的中间点 的坐标 x  以此点作为短线的起始点
            float arcCenterY = 0;  //当前扇形弧线的中间点 的坐标 y

            float arcCenterX2 = 0; //这两个点作为短线的结束点
            float arcCenterY2 = 0;
            //百分百数字的格式
            DecimalFormat numberFormat = new DecimalFormat("00.00");
            paint.setColor(Color.BLACK);

            //分象限 利用三角函数 来求出每个短线的起始点和结束点，并画出短线和百分比。
            //具体的计算方法看下面图示介绍
            if (arcCenterC >= 0 && arcCenterC < 90) {
                arcCenterX = (float) (centerX + curRadius * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY = (float) (centerY + curRadius * Math.sin(arcCenterC * Math.PI / 180));
                arcCenterX2 = (float) (arcCenterX + 20 * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY2 = (float) (arcCenterY + 20 * Math.sin(arcCenterC * Math.PI / 180));
                canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                if (total <= 0) {
                    canvas.drawText(numberFormat.format(0) + "%", arcCenterX2, arcCenterY2 + paint.getTextSize() / 2, paint);
                } else {
                    canvas.drawText(numberFormat.format(pieBeanList.get(i).getNumber() / total * 100) + "%", arcCenterX2, arcCenterY2 + paint.getTextSize() / 2, paint);
                }
            } else if (arcCenterC >= 90 && arcCenterC < 180) {
                arcCenterC = 180 - arcCenterC;
                arcCenterX = (float) (centerX - curRadius * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY = (float) (centerY + curRadius * Math.sin(arcCenterC * Math.PI / 180));
                arcCenterX2 = (float) (arcCenterX - 20 * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY2 = (float) (arcCenterY + 20 * Math.sin(arcCenterC * Math.PI / 180));
                canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                if (total <= 0) {
                    canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                } else {
                    canvas.drawText(numberFormat.format(pieBeanList.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                }
            } else if (arcCenterC >= 180 && arcCenterC < 270) {
                arcCenterC = 270 - arcCenterC;
                arcCenterX = (float) (centerX - curRadius * Math.sin(arcCenterC * Math.PI / 180));
                arcCenterY = (float) (centerY - curRadius * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterX2 = (float) (arcCenterX - 20 * Math.sin(arcCenterC * Math.PI / 180));
                arcCenterY2 = (float) (arcCenterY - 20 * Math.cos(arcCenterC * Math.PI / 180));
                canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                if (total <= 0) {
                    canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                } else {
                    canvas.drawText(numberFormat.format(pieBeanList.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                }
            } else if (arcCenterC >= 270 && arcCenterC < 360) {
                arcCenterC = 360 - arcCenterC;
                arcCenterX = (float) (centerX + curRadius * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY = (float) (centerY - curRadius * Math.sin(arcCenterC * Math.PI / 180));
                arcCenterX2 = (float) (arcCenterX + 20 * Math.cos(arcCenterC * Math.PI / 180));
                arcCenterY2 = (float) (arcCenterY - 20 * Math.sin(arcCenterC * Math.PI / 180));
                canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                if (total <= 0) {
                    canvas.drawText(numberFormat.format(0) + "%", arcCenterX2, arcCenterY2, paint);
                } else {
                    canvas.drawText(numberFormat.format(pieBeanList.get(i).getNumber() / total * 100) + "%", arcCenterX2, arcCenterY2, paint);
                }
            }
        }
    }

    private void drawArc(Canvas canvas, Paint paint,float startc) {
        RectF rectF = new RectF(centerX - curRadius, centerY - curRadius, centerX + curRadius, centerY + curRadius);
        canvas.drawArc(rectF,startc,sweep,true,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX;
        float touchY;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX=event.getX();
                touchY=event.getY();

                //判断点击是否在圆内
                if(Math.pow(touchX-centerX,2)+Math.pow(touchY-centerY,2)<=Math.pow(radius,2)){
                    //获取touch点和圆心的连线 与 x轴正方向的夹角
                    float sweep = getSweep(touchX, touchY);
                    for (int i = 0; i < pieBeanList.size(); i++) {
                        if(sweep>=pieBeanList.get(i).getStartC()&&sweep<=pieBeanList.get(i).getEndC()){
                            onPositionChangeListener.onPositionChange(i);
                            pieBeanList.get(i).setSelected(true);
                        }else{
                            pieBeanList.get(i).setSelected(false);
                        }
                    }
                    invalidate();
                }else{
                   //圆外不做处理
                }

                break;
        }
        return true;
    }

    private float getSweep(float touchX, float touchY) {
        float xZ = touchX - centerX;
        float yZ = touchY - centerY;
        float a = Math.abs(xZ);
        float b = Math.abs(yZ);
        double c = Math.toDegrees(Math.atan(b / a));
        if (xZ >= 0 && yZ >= 0) {//第一象限
            return (float) c;
        } else if (xZ <= 0 && yZ >= 0) {//第二象限
            return 180 - (float) c;
        } else if (xZ <= 0 && yZ <= 0) {//第三象限
            return (float) c + 180;
        } else {//第四象限
            return 360 - (float) c;
        }

    }

    public interface OnPositionChangeListener{
        void onPositionChange(int position);
    }
}
