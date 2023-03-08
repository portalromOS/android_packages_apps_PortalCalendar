package com.portal.calendar.Utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {
    private final GestureDetector gestureDetector;
    private Context context;

    public enum swipeDirection {
        ALL,
        HORIZONTAL,
        VERTICAL
    }


    public OnSwipeTouchListener(Context context, swipeDirection direction) {
        super();
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener(direction));
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeTop() {}
    public void onSwipeBottom() {}

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 60;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        private swipeDirection direction;

        public GestureListener(swipeDirection direction) {
            this.direction = direction;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 != null && e2 != null){
                float diffY = e2.getRawY() - e1.getRawY();
                float diffX = e2.getRawX() - e1.getRawX();

                switch(direction){
                    case ALL:
                        if ((Math.abs(diffX) > Math.abs(diffY))) {
                            isHorizontalSwipping(diffX, velocityX);
                        } else {
                            isVerticalSwipping(diffY, velocityY);
                        }
                        break;
                    case HORIZONTAL:
                        isHorizontalSwipping(diffX, velocityX);
                        break;
                    case VERTICAL:
                        isVerticalSwipping(diffY, velocityY);
                        break;
                }
            }
            return true;
        }

        private void isHorizontalSwipping(float diffX, float velocityX){
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
        }
        private void isVerticalSwipping(float diffY, float velocityY){
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
            }
        }

    }
}