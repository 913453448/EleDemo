package com.yasin.eledemo;

/**
 * Created by leo on 17/5/14.
 */


import android.content.Context;
import android.graphics.PointF;
import android.widget.ImageView;
public class MoveImageView extends ImageView {

    public MoveImageView(Context context) {
        super(context);
    }

    public void setMPointF(PointF pointF) {
        setX(pointF.x);
        setY(pointF.y);
    }
}