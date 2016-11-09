package spit.postyourevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by DELL on 14/10/2016.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{


    public interface OnClickItemInterface{
        void onItemClick(View view,int position);
    }

    private OnClickItemInterface onClickItemInterface;
    GestureDetector gestureDetector;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if(childView != null && onClickItemInterface != null
                && gestureDetector.onTouchEvent(e)){
            onClickItemInterface.onItemClick(childView,rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    public RecyclerItemClickListener(Context context,OnClickItemInterface listener){

        onClickItemInterface =listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
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
        });
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}
