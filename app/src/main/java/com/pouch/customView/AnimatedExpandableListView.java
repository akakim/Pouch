package com.pouch.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
/*
 * A detailed explanation for how this class works:
 * 어떻게 이게 작동하는가.
 * Animating the ExpandableListView was no easy task.
 * 쉬운일 아님.
 * The way that this
 * class does it is by exploiting how an ExpandableListView works.
 *
 * Normally when {@link ExpandableListView#collapseGroup(int)} or
 * {@link ExpandableListView#expandGroup(int)} is called,
 * 일반적으로 collapseGroup(int) ,#expandGroup(int) 가 불려지면 view는 상태를 변경한다.
 * the view toggles
 * the flag for a group and calls notifyDataSetChanged to cause the ListView
 * to refresh all of it's view.
 * view가 상태를 변경하면, notifyDataSetChanged를 호출한다. 이 함수는 listview의 모든 뷰들을 새로 고쳐준다.
 *
 * This time however, depending on whether a group is expanded or collapsed,
 * certain childViews will either be ignored or added to the list.
 * 이제 groupt이 확장되느냐 파괴되느냐와 관련있다.
 * 특정 childview들은 무시되느나 list에 더해지느냐 이다.

 * Knowing this, we can come up with a way to animate our views.
 * 우리는 애니메이션 효과를 우리의 view를 처리하는걸 알아둬라.
 * For instance for group expansion,
 * 예를들어 그룹이 팽창하면
 * we tell the adapter to animate the children of a certain group. We then expand the group which causes the
 * 우리는 어탭터에게 특정 그룹의 자식들을 움직여야한다 우리는 expandableListview가 모든 스크린상의 뷰들을 refresh하기때문에 그룹은
 * 팽창한다.
 * ExpandableListView to refresh all views on screen.
 *
 * The way that ExpandableListView does this is by calling getView() in the adapter.
 * expandableListView가 getview(어뎁터안의) 에의해 이루어진다.
 * However since the adapter knows that we are animating a certain group,
 * 하지만 어뎁터는 우리가 특정그룹에 에니메이션을 을하려는지 모르기 때문에
 * instead of returning the real views for the children of the group being
 * 대신에 실제 view를 그룹의 자식에 적용된에니메이션 realview를 반환해야한다.
 * animated,
 * it will return a fake dummy view.
 * 먼저 그것은 fake dummy view를 반환한다.
 * This dummy view will then
 * draw the real child views within it's dispatchDraw function.
 * 그리고 이 dummy view는 우리가 원하는 결과의 뷰를 dispatchdraw function과 함깨 그려진다.
 * The reason we do this is so that we can animate all of it's children by simply animating the dummy view.
 * 왜냐하면 우리는 이러한 과정을 단순히 dummy view의 효과에 의해 모든 childrenview를 animate할 수 있다.
 * After we complete the animation, we tell the adapter to stop animating the group and call notifyDataSetChanged.
 * 효과가 끝나면 우리는 adapter에게 animating을 끝내라고 하고 group에 데이터가 변경됬음을 알려준다.
 * Now the ExpandableListView is forced to refresh it's views again,
 * 이제 ExpandableListViw는 그것의 view들을 다시 초기화 하려고 할것이다.
 * except this time, it will get the real views for the expanded group.
 * 이번한번을 제외하고는 그것은 expanded group을 위한 real view를 얻게될것이다.
 * So, to list it all out, when {@link #expandGroupWithAnimation(int)} is
 * called the following happens:
 * 나열해보자면 다음과 같은 절차를 띈다.
 * 1. The ExpandableListView tells the adapter to animate a certain group.
 * ExpandableListView 는 adapter에게 특정 뷰에 효과를 준다.
 * 2. The ExpandableListView calls expandGroup.
 * ExpandableListView는 expandGroup을 호출한다.
 * 3. ExpandGroup calls notifyDataSetChanged.
 * ExpandableListView는 DataSet을 변경한다.
 * 4. As an result, getChildView is called for expanding group.
 * 따라서 getchildView는 expanding group을 호출한다.
 * 5. Since the adapter is in "animating mode", it will return a dummy view.
 * 때문에 adapter는 animationg mode이다. 이것은 dummy view를 반환한다.
 * 6. This dummy view draws the actual children of the expanding group.
 * 이 dummy view는 확장된 그룹의 실제 자식들이다.
 * 7. This dummy view's height is animated from 0 to it's expanded height.
 * 더미뷰들의 높이는 0부터 확장된 높이까지 animate를한다.
 * 8. Once the animation completes, the adapter is notified to stop
 *    animating the group and notifyDataSetChanged is called again.
 효과가 완료되면 어댑터에게 효과를 멈추라고 하고 notifyDataSetChanged이 다시한번 호출된다.
 * 9. This forces the ExpandableListView to refresh all of it's views again.
 이러한 절차는 ExpandableListView에게 모든 뷰들을 다시한번 초기화한다.
 * 10.This time when getChildView is called, it will return the actual
 *    child views.
 * 이제 getchildView가 불려지고 그것은 실제 child view들을 반환한다.
 *
 * 붕괴는 좀 나중에..
 *
 * For animating the collapse of a group is a bit more difficult since we
 * can't call collapseGroup from the start as it would just ignore the
 * child items,
 * 그룹에대한 붕괴를 하기위한 애니메이션효과는 조금 어렵다 왜냐하면
 * 우린 collapseGroup을 시작시 호출할 것이다 그저 child item을 무시함으로써
 *
 * giving up no chance to do any sort of animation.
 * Instead what we have to do is play the animation first and call collapseGroup
 * after the animation is done.
 * So, to list it all out, when {@link #collapseGroupWithAnimation(int)} is
 * called the following happens:
 *
 * 1. The ExpandableListView tells the adapter to animate a certain group.
 * ExpandableListView 는 특정한 그룹에 animate하라고 한다.
 * 2. The ExpandableListView calls notifyDataSetChanged.
 * ExpandableListView는 dataset을 변화시킨다.
 * 3. As an result, getChildView is called for expanding group.
 * 그래서 getchildView는 group을 확장한다.
 * 4. Since the adapter is in "animating mode", it will return a dummy view.
 * 5. This dummy view draws the actual children of the expanding group.
 * 6. This dummy view's height is animated from it's current height to 0.
 * 7. Once the animation completes, the adapter is notified to stop
 *    animating the group and notifyDataSetChanged is called again.
 * 8. collapseGroup is finally called.
 * 9. This forces the ExpandableListView to refresh all of it's views again.
 * 10.This time when the ListView will not get any of the child views for
 *    the collapsed group.
 */
public class AnimatedExpandableListView extends ExpandableListView {

    private static final String TAG = "ExpandableListView";

    private static final int ANIMATION_DURATION = 300;

    private AnimatedExpandableListAdapter adapter;
    private boolean isTest= true;


    public AnimatedExpandableListView(Context context) {
        super(context);
    }

    public AnimatedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        // Make sure that the adapter extends AnimatedExpandableListAdapter
        if (adapter instanceof AnimatedExpandableListAdapter) {
            this.adapter = (AnimatedExpandableListAdapter) adapter;
            this.adapter.setParent(this);
        } else {
            throw new ClassCastException(adapter.toString() + " must implement AnimatedExpandableListAdapter");
        }
    }

    /**
     * Expands the given group with an animation.
     * 애니메이션 효과와 함께 그룹을 확장한다.
     *
     * @param groupPos The position of the group to expand
     * @return Returns true if the group was expanded. False if the group was
     * already expanded.
     * 그룹이 확장되면 true 이미 열렸다면 false
     */
    @SuppressLint("NewApi")
    public boolean expandGroupWithAnimation(int groupPos) {
        boolean lastGroup = groupPos == adapter.getGroupCount() - 1;

        if (lastGroup && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return expandGroup(groupPos, true);
        }
        // flat??
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));

        //예외처리.
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if (childIndex < getChildCount()) {
                // Get the view for the group is it is on screen...

                View v = getChildAt(childIndex);
                if (v.getBottom() >= getBottom()) {
                    // If the user is not going to be able to see the animation
                    // we just expand the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen

                    // We need to notify the adapter that the group was expanded
                    // without it's knowledge
                    adapter.notifyGroupExpanded(groupPos);
                    return expandGroup(groupPos);
                }
            }
        }
        // Let the adapter know that we are starting the animation..
        // 실질적으로 GroupInfo의 값들 만 세팅한다. .
        adapter.startExpandAnimation(groupPos, 0);
        // Finally call expandGroup (note that expandGroup will call
        // notifyDataSetChanged so we don't need to)
        return expandGroup(groupPos);
    }

    /**
     * 메뉴를 파괴하는 animation.
     * Collapses the given group with an animation.
     *
     * @param groupPos The position of the group to collapse
     * @return Returns true if the group was collapsed. False if the group was
     * already collapsed.
     * 성공적으로 childview들을 지우면 true 이미 지워진경우라면 false .
     */
    public boolean collapseGroupWithAnimation(int groupPos) {
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if(childIndex >= 0 && childIndex <getChildCount()){
                // get the view for the group it is on screen
                //스크린상에
                View v = getChildAt(childIndex);
                if(v.getBottom()>=getBottom()){
                    // If the user is not going to be able to see the animation
                    // we just collapse the group without an animation.
                    // 만약 유저가 animation을 보고싶지않다면 우리는 그냥 그룹을 붕괴한다.
                    // animation없이.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen
                    // 이것에 대한 해결은 getchildView는 호출되지 않는다.
                    // 만일 그룹의 자식들이 스크린상에 없다면.
                    return collapseGroup(groupPos);
                }
            }else {
                // If the group is offscreen, we can just collapse it without an
                // animation...
                return collapseGroup(groupPos);
            }
        }
        // Get the position of the firstChild visible from the top of the screen
        long packedPos = getExpandableListPosition(getFirstVisiblePosition());
        int firstChildPos = getPackedPositionChild(packedPos);
        int firstGroupPos = getPackedPositionGroup(packedPos);

        // If the first visible view on the screen is a child view AND it's a
        // child of the group we are trying to collapse, then set that
        // as the first child position of the group... see
        // {@link #startCollapseAnimation(int, int)} for why this is necessary
        firstChildPos = firstChildPos == -1 || firstGroupPos != groupPos ? 0 : firstChildPos;

        adapter.startCollapseAnimation(groupPos, firstChildPos);
        adapter.notifyDataSetChanged();
        return isGroupExpanded(groupPos);
    }

    private int getAnimationDuration() {
        return ANIMATION_DURATION;
    }

    private static class GroupInfo {
        /**
         * This variable contains the last known height value of the dummy view.
         * 더미뷰의 높이값을 포함하고있다.
         * We save this information so that if the user collapses a group
         * before it fully expands,
         * <p>
         * the collapse animation will start from the
         * 메뉴가 지워지는 효과는 현재 더미뷰의 높이로부터 시작한다.
         * 완전히히
         * CURRENT height of the dummy view and not from the full expanded
         * height.
         */
        boolean animating = false;
        boolean expanding = false;
        int firstChildPosition;

        int dummyHeight = -1;
    }


    public static abstract class AnimatedExpandableListAdapter extends BaseExpandableListAdapter {
        private SparseArray<GroupInfo> groupInfo = new SparseArray<GroupInfo>();
        private AnimatedExpandableListView parent;

        private static final int STATE_IDLE = 0;
        private static final int STATE_EXPANDING = 1;
        private static final int STATE_COLLAPSING = 2;

        public abstract View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

        public abstract int getRealChildrenCount(int groupPosition);

        private void setParent(AnimatedExpandableListView parent) {
            this.parent = parent;
        }

        public int getRealChildType(int groupPosition, int childPosition) {
            return 0;
        }


        // ??
        public int getRealChildTypeCount() {
            return 1;
        }


        private GroupInfo getGroupInfo(int groupPosition) {
            GroupInfo info = groupInfo.get(groupPosition);
            if (info == null) {
                info = new GroupInfo();
                groupInfo.put(groupPosition, info);
                // 없으면 새로 만든다.
            }
            return info;
        }

        public void notifyGroupExpanded(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.dummyHeight = -1;
        }

        private void startExpandAnimation(int groupPosition, int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = true;
        }

        private void startCollapseAnimation(int groupPosition, int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = false;
        }

        private void stopAnimation(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = false;
        }

        public final int getChildType(int groupPosition, int childPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                // If we are animating this group, then all of it's children
                // are going to be dummy views which we will say is type 0.
                // 만약 우리가 그룹에 에니메이션 효과를 준다면 그것의 자식은 dummy view
                // 가 될것이고 우리는 그걸 type 0라 부르겠다.

                return 0;
            } else {
                // If we are not animating this group, then we will add 1 to
                // the type it has so that no type id conflicts will occur
                // unless getRealChildType() returns MAX_INT
                // 만일 우리가 이그룹에 에니메이션을 안준다면 우리는 그 type이 진 값에 1을 더한다.
                // 그러면 어떤 타입ㅇ idrk 충돌이 발생하지않게된다.
                // getRealChildType가 MAX_INT값을 반환하는 것을 제외하고.
                return getRealChildType(groupPosition, childPosition) + 1;
            }
        }

        public final int getChildrenCount(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                return info.firstChildPosition + 1;
            } else {
                return getRealChildrenCount(groupPosition);
            }
        }

        @Override
        public final int getChildTypeCount() {
            return getRealChildTypeCount() + 1;
        }

        protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        }

        public final View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
            final GroupInfo info = getGroupInfo(groupPosition);

            Log.i(TAG, "getChildView");
            if (info.animating) {
                if (convertView instanceof DummyView == false) {;
                    convertView = new DummyView(parent.getContext());
                    convertView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                }


                if (childPosition < info.firstChildPosition) {
                    // The reason why we do this is to support the collapse
                    //왜 이렇게 하냐면, 우리는 확장된 메뉴 UI를 지우는 것이다.
                    // this group when the group view is not visible but the
                    // 그룹이 보이지않지만 그룹의 자식은 있다.
                    // children of this group are. When notifyDataSetChanged is called,
                    // notifyDataSetChanged가 불리어질때
                    // the ExpandableListView tries to keep the list position the same by saving the first visible item
                    // ExpandableListView 클래스는 list position을 유지하려고한다. list position은 처음 보여지는 아이템 뷰를 저장하고,
                    // and jumping back to that item after the views have been  refreshed.
                    // 그리고 이전으로 돌아가서 item이  뷰들이가진 것들을 초기화한다.
                    // Now the problem is, if a group has 2 items
                    // 이제 문제는 만약 그룹이 2개 이상의 item을 가지고 있는경우다 .
                    // and the first visible item is the 2nd child of the group
                    // 그리고 처음 보여진 아이탬은 이 그룹의 2번째 자식이다.
                    // and this group is collapsed, then the dummy view will be
                    // 그리고 이그룹은 을 파괴하면 dummy그룹이 이 그룹을 위해 사용될것이다.
                    // used for the group. But now the group only has 1 item
                    // 하지만 그룹은 이제 오직 한가지 아이템만을 가지고있다.
                    // which is the dummy view, thus when the ListView is trying
                    //더미뷰.. 그러므로 listView는 스크롤의 위치를 회복하려 할것이다.
                    // to restore the scroll position,
                    //
                    // it will try to jump to the second item of the group.
                    // 그것은 이 그룹의 2번째 아이템으로 가려할것이다.
                    // But this group no longer has a second item, so it is forced to jump to the next
                    // 하지만 더이상 2번째 아이템을 그룹은 가지고있지않다.
                    // 그래서 이것은 다음 그룹으로 건너뛰게끔 할것이다.
                    // group.
                    //
                    // This will cause a very ugly visual glitch.
                    // 이것은 무섭게 보여질것이다.
                    // So the way that we counteract this is by creating as many
                    // dummy views as we need to maintain the scroll position
                    // of the ListView after notifyDataSetChanged has been
                    // called.
                    convertView.getLayoutParams().height = 0;
                    return convertView;
                }

                final ExpandableListView listView = (ExpandableListView) parent;

                final DummyView dummyView = (DummyView) convertView;

                // clear the views that the dummy view draws
                dummyView.clearViews();

                //set the style of the diveider
                dummyView.setDivider(listView.getDivider(), parent.getMeasuredWidth(), parent.getMeasuredHeight());

                //make measure spaces to measure child views

                final int measureSpecW = MeasureSpec.makeMeasureSpec(parent.getWidth(), MeasureSpec.EXACTLY);
                final int measureSpecH = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

                int totalHeight = 0;
                int clipHeight = parent.getHeight();

                final int len = getRealChildrenCount(groupPosition);

                /* group이 가진 자식의 갯수만큼
                 * 뷰를 그릴 parameter들을 구하는 반복문이다.
                 */
                for (int i = info.firstChildPosition; i < len; i++) {
                    View childView = getRealChildView(groupPosition, i, (i == len - 1), null, parent);

                    LayoutParams p = (LayoutParams) childView.getLayoutParams();
                    if (p == null) {
                        p = (LayoutParams) generateDefaultLayoutParams();
                        childView.setLayoutParams(p);
                    }

                    int lpHeight = p.height;

                    int childHeightSpec;
                    if (lpHeight > 0) {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
                    } else {
                        childHeightSpec = measureSpecH;
                    }

                    childView.measure(measureSpecW, childHeightSpec);
                    totalHeight += childView.getMeasuredHeight();

                    if (totalHeight < clipHeight) {
                        // we only need to draw enough views to fool the user...
                        // 충분한 뷰들을 그릴것이다.. 바보같은 유저에게 ㅋㅋㅋㅋ
                        dummyView.addFakeView(childView);
                    } else {
                        dummyView.addFakeView(childView);

                        // if this group has too many views, we don't want to
                        // calculate the height of everything... just do a light
                        // approximation and break
                        // 너무 뷰들을 그룹이 많이가지면 대략적으로만 계산하고 중단한다.
                        int averageHeight = totalHeight / (i + 1);
                        totalHeight += (len - i - 1) * averageHeight;
                        break;
                    }
                }

                Object o;

                // 더미 뷰의 태그 값을 얻어서 비어있는경우와 다른경우의 상태값을 저장.
                int state = (o = dummyView.getTag()) == null ? STATE_IDLE : (Integer) o;

                if (info.expanding && state != STATE_EXPANDING) {
                    ExpandAnimation ani = new ExpandAnimation(dummyView, 0, totalHeight, info);
                    ani.setDuration(this.parent.getAnimationDuration());
                    ani.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //3. ExpandGroup calls notifyDataSetChanged.
                            // ExpandableListView는 DataSet을 변경한다.
                            // animation 이 끝난후 data를 변경하고 tag를 설정한다.
                            stopAnimation(groupPosition);
                            notifyDataSetChanged();
                            dummyView.setTag(STATE_IDLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    dummyView.startAnimation(ani);
                    dummyView.setTag(STATE_EXPANDING);
                } else if (!info.expanding && state != STATE_COLLAPSING) {
                    if(info.dummyHeight == -1){
                        info.dummyHeight = totalHeight;
                    }

                    ExpandAnimation ani = new ExpandAnimation(dummyView, info.dummyHeight, 0, info);
                    ani.setDuration(this.parent.getAnimationDuration());
                    ani.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stopAnimation(groupPosition);
                            listView.collapseGroup(groupPosition);
                            notifyDataSetChanged();
                            info.dummyHeight = -1;
                            dummyView.setTag(STATE_IDLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}

                        @Override
                        public void onAnimationStart(Animation animation) {}

                    });

                    dummyView.startAnimation(ani);
                    dummyView.setTag(STATE_COLLAPSING);
                }
                return convertView;
            }
            else {
                return getRealChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            }
        }
    }


    private static class DummyView extends View {
        private List<View> views = new ArrayList<View>();
        private Drawable divider;
        private int dividerWidth;
        private int dividerHeight;

        public DummyView(Context context) {
            super(context);
        }

        public DummyView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DummyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setDivider(Drawable divider, int dividerWidth, int dividerHeight) {
            if (divider != null) {
                this.divider = divider;
                this.dividerWidth = dividerWidth;
                this.dividerHeight = dividerHeight;

                divider.setBounds(0, 0, dividerWidth, dividerHeight);
            }
        }

        /**
         *  * Add a view for the DummyView to draw.
         * 실제 뷰의 갯수를 새서 전체적인 하나의 fake view를 만든다.
         * @param childView View to draw
         */
        public void addFakeView(View childView) {
            childView.layout(0, 0, getWidth(), childView.getMeasuredHeight());
            views.add(childView);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            final int len = views.size();
            for (int i = 0; i < len; i++) {
                View v = views.get(i);
                v.layout(left, top, left + v.getMeasuredWidth(), top + v.getMeasuredHeight());
            }
        }

        public void clearViews() {
            views.clear();
        }
    }

    private static class ExpandAnimation extends Animation {
        private int baseHeight;
        private int delta;
        private View view;
        private GroupInfo groupInfo;
        private final String TAG= ExpandAnimation.class.getSimpleName();
        private boolean isTest = true;

        private ExpandAnimation(View v, int startHeight, int endHeight, GroupInfo info) {


            baseHeight = startHeight;
            delta = endHeight - startHeight;
            view = v;
            groupInfo = info;

            if(isTest){
                Log.v(TAG,"ExpandAnimation Constructor()");
                Log.v(TAG,"baseHeight : "+String.valueOf(baseHeight));
                Log.v(TAG,"delta : "+String.valueOf(delta));
                Log.v(TAG,"view's name : "+String.valueOf(view.getId()));
                Log.v(TAG,"groupInfo : "+info.toString());
            }
            view.getLayoutParams().height = startHeight;
            view.requestLayout(); // ?? parentlayout 요청 ?
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t){
            super.applyTransformation(interpolatedTime,t);

            //버튼 하나의 값설정. delta의 경우 자식 뷰들의 전체적인
            //변화값을 의미한다.
            if(interpolatedTime<1.0f){
                int val = baseHeight+(int)(delta * interpolatedTime);
                view.getLayoutParams().height = val;
                groupInfo.dummyHeight = val;
                view.requestLayout();
            }else{
                int  val = baseHeight +delta;
                view.getLayoutParams().height = val;
                groupInfo.dummyHeight = val;
                view.requestLayout();
            }
        }
    }
}
