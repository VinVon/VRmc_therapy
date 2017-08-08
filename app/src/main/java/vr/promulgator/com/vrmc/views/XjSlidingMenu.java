package vr.promulgator.com.vrmc.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

/**
 * Created by xws on 2017/8/1.
 */

public class XjSlidingMenu extends HorizontalScrollView {
    private View menuView,contentView;
    private int current;//滑动的距离
    private int distance = 62;
    private int menuWidth;
    private GestureDetector gestureDetector;
    private boolean menuState = false;
    private boolean isIntercepter = false;
    private int dip2px(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,i,getResources().getDisplayMetrics());
    }

    public XjSlidingMenu(Context context) {
        this(context,null);
    }

    public XjSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public XjSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        distance = dip2px(62);
        menuWidth = getScreenWidth()-distance;
         gestureDetector = new GestureDetector(getContext(),simpleOnGestureListener);
    }

    /**
     * 布局解析完毕调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup childAt = (ViewGroup) getChildAt(0);
        if (childAt.getChildCount() !=2){
            throw  new RuntimeException("只能放置2个子View");
        }
        /**
         * 菜单页
         */
         menuView = childAt.getChildAt(0);
        ViewGroup.LayoutParams layoutParams = menuView.getLayoutParams();
        layoutParams.width = menuWidth;
            menuView.setLayoutParams(layoutParams);
        /**
         * 内容页
         */
         contentView = childAt.getChildAt(1);
        ViewGroup.LayoutParams layoutParam = contentView.getLayoutParams();
        layoutParam.width = getScreenWidth();
        contentView.setLayoutParams(layoutParam);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(menuWidth,0);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercepter = false;
            if (menuState){
                float cyrrentX = ev.getX();
             if (cyrrentX >menuWidth){
                 Log.e("TAG","dispatchTouchEvent关闭菜单");
                 closeMenu();
                 isIntercepter = true;
                 return  true;
             }
            }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("TAG","onTouchEvent");
        if (isIntercepter){
            return true;
        }
        boolean b = this.gestureDetector.onTouchEvent(ev);
        if (b){
            return b;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP){
                 current = getScrollX();
            if (current > menuWidth/2){
                //关闭菜单
                closeMenu();
            }else{
                //打开菜单
                openMenu();
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = 1f*l/menuWidth;
        //内容缩放最小为0.7
        float rightScale = 0.7f+0.3f*scale;
        ViewCompat.setPivotX(contentView,0);
        ViewCompat.setPivotY(contentView,contentView.getMeasuredHeight()/2);

        ViewCompat.setScaleX(contentView,rightScale);
        ViewCompat.setScaleY(contentView,rightScale);
        //菜单的缩放
        float leftalpha = 0.7f*(1-scale)+0.7f;
        float leftScale = 0.3f*(1-scale)+0.7f;
        ViewCompat.setAlpha(menuView,leftalpha);
        ViewCompat.setScaleX(menuView,leftScale);
        ViewCompat.setScaleY(menuView,leftScale);
        ViewCompat.setTranslationX(menuView,0.3f*l);
    }

    public void openMenu() {
        menuState = true;
        smoothScrollTo(0,0);

    }

    public void closeMenu() {
        menuState = false;
        smoothScrollTo(menuWidth,0);
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    private int getScreenWidth(){
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    /**
     * 快速滑动
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
        private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX>0&& !menuState){
                    Log.e("TAG","开发菜单");
                    openMenu();
                    return true;
                }else if (velocityX>0&& menuState){
                    Log.e("TAG","关闭菜单");
                    closeMenu();
                    return true;
                }
                return false;
            }
        };


}
