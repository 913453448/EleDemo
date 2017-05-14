package com.yasin.eledemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yasin.eledemo.swipe.MaterialProgressDrawable;
import com.yasin.eledemo.swipe.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity implements Animator.AnimatorListener {
    private AppCompatImageView mImageView, mShopCart;
    private float screenW, screenH;
    private RelativeLayout container;

    private ImageView imgTest;
    private SwipeRefreshLayout swipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics outMeric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMeric);
        screenW = outMeric.widthPixels;
        screenH = outMeric.heightPixels;

        setContentView(R.layout.activity_main);
        mImageView = (AppCompatImageView) findViewById(R.id.id_image);
        container = (RelativeLayout) findViewById(R.id.activity_main);
        mShopCart = (AppCompatImageView) findViewById(R.id.id_shopcart);
        imgTest= (ImageView) findViewById(R.id.id_image_test);
        VectorDrawableCompat drawable = (VectorDrawableCompat) mImageView.getDrawable();
        drawable.setTint(Color.RED);



        MaterialProgressDrawable drawable1=new MaterialProgressDrawable(this,container);
        drawable1.showArrow(true);
        drawable1.setArrowScale(1);
        drawable1.setColorSchemeColors(Color.RED,Color.GREEN);
        drawable1.setAlpha(255);
        mShopCart.setImageDrawable(drawable1);
//        drawable1.start();
        drawable1.setStartEndTrim(0f, 0.8f);
        drawable1.setArrowScale(Math.min(1f, 1));
        drawable1.setProgressRotation(0.1f);
        drawable1.start();




        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int[] childCoordinate = new int[2];
                int[] parentCoordinate = new int[2];
                int[] shopCoordinate = new int[2];
                //1.分别获取被点击View、父布局、购物车在屏幕上的坐标xy。
                mImageView.getLocationInWindow(childCoordinate);
                container.getLocationInWindow(parentCoordinate);
                mShopCart.getLocationInWindow(shopCoordinate);

                //2.自定义ImageView 继承ImageView
                MoveImageView img = new MoveImageView(MainActivity.this);
                img.setImageResource(R.drawable.ic_circle);
                //3.设置img在父布局中的坐标位置
                img.setX(childCoordinate[0] - parentCoordinate[0]);
                img.setY(childCoordinate[1] - parentCoordinate[1]);
                //4.父布局添加该Img
                container.addView(img);

                //5.利用 二次贝塞尔曲线 需首先计算出 MoveImageView的2个数据点和一个控制点
                PointF startP = new PointF();
                PointF endP = new PointF();
                PointF controlP = new PointF();
                //开始的数据点坐标就是 addV的坐标
                startP.x = childCoordinate[0] - parentCoordinate[0];
                startP.y = childCoordinate[1] - parentCoordinate[1];
                //结束的数据点坐标就是 shopImg的坐标
                endP.x = shopCoordinate[0] - parentCoordinate[0];
                endP.y = shopCoordinate[1] - parentCoordinate[1];
                //控制点坐标 x等于 购物车x；y等于 addV的y
                controlP.x = endP.x;
                controlP.y = startP.y;

                //启动属性动画
                ObjectAnimator animator = ObjectAnimator.ofObject(img, "mPointF",
                        new PointFTypeEvaluator(controlP), startP, endP);
                animator.setDuration(1000);
                animator.addListener(MainActivity.this);
                animator.start();
            }
        });



        swipe= (SwipeRefreshLayout) findViewById(R.id.id_swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                    }
                },3000);
            }
        });
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //动画结束后 父布局移除 img
        Object target = ((ObjectAnimator) animation).getTarget();
        container.removeView((View) target);
        target=null;
        //shopImg 开始一个放大动画
        ScaleAnimation scaleAnim=new ScaleAnimation(1f,1.2f,1f,1.2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnim.setDuration(300);
        mShopCart.clearAnimation();
        mShopCart.startAnimation(scaleAnim);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    /**
     * 自定义估值器
     */
    public class PointFTypeEvaluator implements TypeEvaluator<PointF> {
        /**
         * 每个估值器对应一个属性动画，每个属性动画仅对应唯一一个控制点
         */
        PointF control;
        /**
         * 估值器返回值
         */
        PointF mPointF = new PointF();

        public PointFTypeEvaluator(PointF control) {
            this.control = control;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            return getBezierPoint(startValue, endValue, control, fraction);
        }

        /**
         * 二次贝塞尔曲线公式
         *
         * @param start   开始的数据点
         * @param end     结束的数据点
         * @param control 控制点
         * @param t       float 0-1
         * @return 不同t对应的PointF
         */
        private PointF getBezierPoint(PointF start, PointF end, PointF control, float t) {
            mPointF.x = (1 - t) * (1 - t) * start.x + 2 * t * (1 - t) * control.x + t * t * end.x;
            mPointF.y = (1 - t) * (1 - t) * start.y + 2 * t * (1 - t) * control.y + t * t * end.y;
            return mPointF;
        }
    }
}
