package com.pouch.customView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.data.ActionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickActionBar 전반적인 부분.
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
private ImageView mArrowUp;
    private ImageView mArrowDown;
    private Animation mTrackAnim;
    private LayoutInflater inflater;
    private ViewGroup mTrack;
    private OnActionItemClickListener mItemClickListener;
    private OnDismissListener mDismissListener;

    private List<ActionItem> mActionItemList = new ArrayList<ActionItem>();

    private boolean mDidAction;
    private boolean mAnimateTrack;

    private int mChildPos;
    private int mAnimStyle;

    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_AUTO = 4;

    private int selectedParent = 0;
    public int getSelectedParent() {
        return selectedParent;
    }

    public void setSelectedParent(int selectedParent) {
        this.selectedParent = selectedParent;
    }


    public QuickAction(Context context) {
        super(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTrackAnim = AnimationUtils.loadAnimation(context, R.anim.rail);

        //애니메이션 효과 설정.
        mTrackAnim.setInterpolator(new Interpolator() {
            public float getInterpolation(float t) {
                //pushes past the target area , then snaps back into place
                // equation for graphing: 1.2-(x*1.6-1.1)^2;

                final float inner = (t * 1.55f) - 1.1f;
                return 1.2f - inner * inner;

            }
        });

        setRootViewId(R.layout.quick_action);

        mAnimStyle = ANIM_AUTO;
        mAnimateTrack = true;
        mChildPos = 0;
    }


    @Override
    public void onDismiss() {
        if(!mDidAction && mDismissListener != null){
            mDismissListener.onDismiss();
        }
    }
    public interface OnActionItemClickListener {
        public abstract void onItemClick(QuickAction source, int pos, int actionId);
    }

    /**
     * Listener for window dismiss
     *
     */
    public interface OnDismissListener {
        public abstract void onDismiss();
    }

    public ActionItem getActionItem(int index){
        return mActionItemList.get(index);
    }

    public void setRootViewId(int id){
        // 여기부터
        mRootView   = (ViewGroup)inflater.inflate(id,null);
        mTrack       = (ViewGroup)mRootView.findViewById(R.id.tracks);
        mArrowDown  = (ImageView)mRootView.findViewById(R.id.arrow_down);
        mArrowUp    = (ImageView)mRootView.findViewById(R.id.arrow_up);

        //This was previously defined on show() method, moved here to prevent force close that occured
        //when tapping fastly on a view to show quickaction dialog.
        //Thanx to zammbi (github.com/zammbi)

//        mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setContentView(mRootView);
    }

    /**
     * Animate track.
     *
     * @param mAnimateTrack flag to animate track
     */
    public void mAnimateTrack(boolean mAnimateTrack) {
        this.mAnimateTrack = mAnimateTrack;
    }
    /**
     * Set animation style.
     *
     * @param mAnimStyle animation style, default is set to ANIM_AUTO
     */
    public void setAnimStyle(int mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }
    public void addActionItem(ActionItem action){
        mActionItemList.add(action);

        String title = action.getTitle();
        Drawable icon = action.getIcon();
        View container = (View)inflater.inflate(R.layout.quick_action_item,null);
        ImageView img = (ImageView)container.findViewById(R.id.iv_icon);
        //TextView text = (TextView)container.findViewById(R.id.tv_title);

        if (icon != null) {
            img.setImageDrawable(icon);
        } else {
            img.setVisibility(View.GONE);
        }
/*
        if (title != null) {
            text.setText(title);
        } else {
            text.setVisibility(View.GONE);
        }
*/
        final int pos = mChildPos;
        final int actionId = action.getActionId();
        //TODO : 버튼을 클릭할 때 마다. title에 따라 다른 Intent를해줘야됨.
        //
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(QuickAction.this,pos,actionId);
                }

                if (!getActionItem(pos).isSticky()) {
                    mDidAction = true;

                    //workaround for transparent background bug
                    //thx to Roman Wozniak <roman.wozniak@gmail.com>
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }

            }
        });

        container.setFocusable(true);
        container.setClickable(true);

        mTrack.addView(container, mChildPos);
        mChildPos++;
    }

    public void setOnActionItemClickListener(OnActionItemClickListener listener) {
        mItemClickListener = listener;
    }
    public void setOnDismissListener(QuickAction.OnDismissListener listener){
        setOnDismissListener(this);
        mDismissListener = listener;
    }
    /**
     * GridLayout에 맞추기 위해 수정.
     */
    public void show(View anchor,int columns,int size){
        preShow();
        int[] location 		= new int[2];
        mDidAction 			= false;

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0],location[1],
                location[0]+anchor.getWidth(),location[1]+anchor.getHeight()
                );

        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


        int rootWidth  = mRootView.getMeasuredWidth();
        int rootHeight = mRootView.getMeasuredHeight();

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        int xPos    =columns *size;
        int yPos    =anchorRect.top - rootHeight;

        boolean onTop = true;

        /*어떤 버튼이 눌려진경우 위쪽과 아랫쪽 둘중 하나를 그릴건지에 대한 선택. */
        //display on bottom
        if(rootHeight>anchor.getTop()){
            yPos = anchorRect.bottom;
            onTop	= false;
        }

        showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX(),columns,size);

        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        // 화살방향, 애니메이션 효과가지 전부 새팅.
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

        if (mAnimateTrack) mTrack.startAnimation(mTrackAnim);
    }


    public void show(View anchor){
        preShow();
        int[] location 		= new int[2];
        mDidAction 			= false;

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0],location[1],
                location[0]+anchor.getWidth(),location[1]+anchor.getHeight()
        );

        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


        int rootWidth  = mRootView.getMeasuredWidth();
        int rootHeight = mRootView.getMeasuredHeight();

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();

//        int xPos    = (screenWidth - rootWidth)/2;
//        int yPos    = anchorRect.top - rootHeight;
        int xPos    =0;//(screenWidth - rootWidth)/2;
        int yPos    =anchorRect.top;

        Log.v("ypos",String.valueOf(yPos));
        Log.v("anchorRect.top",String.valueOf(anchorRect.top));
        Log.v("anchorRect",anchorRect.toShortString());
        Log.v("rootHeight",String.valueOf(rootHeight));
        boolean onTop = true;

        /*어떤 버튼이 눌려진경우 위쪽과 아랫쪽 둘중 하나를 그릴건지에 대한 선택. */
        //display on bottom
        if(rootHeight>anchor.getTop()){
            yPos = anchorRect.bottom;
            onTop	= false;
        }

        showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX());

        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        // 화살방향, 애니메이션 효과가지 전부 새팅.
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, yPos);

        if (mAnimateTrack) mTrack.startAnimation(mTrackAnim);
    }
    /**
     * Show arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedX distance from left screen
     */

    private void showArrow(int whichArrow,int requestedX,int columns,int width){
        // 현재는 showArrow = down hide는 up
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);

//        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
//        showArrow.setPadding(columns*width,0,0,0);
  //      param.leftMargin = columns*width;

        hideArrow.setVisibility(View.INVISIBLE);
    }
    private void showArrow(int whichArrow,int requestedX){
        // 현재는 showArrow = down hide는 up
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        param.leftMargin = requestedX - arrowWidth;

        hideArrow.setVisibility(View.INVISIBLE);
    }

    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop){
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

        switch (mAnimStyle){
            case ANIM_GROW_FROM_LEFT:
                mWindow.setAnimationStyle((onTop)? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                break;
            case ANIM_GROW_FROM_RIGHT:
                mWindow.setAnimationStyle((onTop)? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                break;
            case ANIM_GROW_FROM_CENTER:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                break;
            case ANIM_AUTO:
                if (arrowPos <= screenWidth/4) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                } else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                } else {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right : R.style.Animations_PopDownMenu_Right);
                }
                break;

        }
    }
}
