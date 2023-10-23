package org.devio.hi.ui.refresh;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 原生GestureDetector的子类，只实现原生GestureDetector的部分方法
 */
public class HiGestureDetector implements GestureDetector.OnGestureListener {
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
