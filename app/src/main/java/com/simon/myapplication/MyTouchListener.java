package com.simon.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MyTouchListener extends DefaultMapViewOnTouchListener {
    public MyTouchListener(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return super.onTouch(view, event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        android.graphics.Point screenPoint = new android.graphics.Point(
                Math.round(e.getX()),
                Math.round(e.getY()));
        Point mapPoint = mMapView.screenToLocation(screenPoint);
        Log.e("simonfong", "onSingleTapUp----x=" + mapPoint.getX()+"---y="+mapPoint.getY());
        return super.onSingleTapUp(e);
    }
}
