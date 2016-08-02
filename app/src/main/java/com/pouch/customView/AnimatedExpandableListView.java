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
 *  �� �̰� �۵��ϴ°�.
 * Animating the ExpandableListView was no easy task.
 * ������ �ƴ�.
 * The way that this
 * class does it is by exploiting how an ExpandableListView works.
 *
 * Normally when {@link ExpandableListView#collapseGroup(int)} or
 * {@link ExpandableListView#expandGroup(int)} is called,
 * �Ϲ������� collapseGroup(int) ,#expandGroup(int) �� �ҷ����� view�� ���¸� �����Ѵ�.
 * the view toggles
 * the flag for a group and calls notifyDataSetChanged to cause the ListView
 * to refresh all of it's view.
 * view�� ���¸� �����ϸ�, notifyDataSetChanged�� ȣ���Ѵ�. �� �Լ��� listview�� ��� ����� ���� �����ش�.
 *
 * This time however, depending on whether a group is expanded or collapsed,
 * certain childViews will either be ignored or added to the list.
 * ���� groupt�� Ȯ��Ǵ��� �ı��Ǵ��Ŀ� �����ִ�.
 * Ư�� childview���� ���õǴ��� list�� ���������� �̴�.

 * Knowing this, we can come up with a way to animate our views.
 * �츮�� �ִϸ��̼� ȿ���� �츮�� view�� ó���ϴ°� �˾Ƶֶ�.
 * For instance for group expansion,
 * ������� �׷��� ��â�ϸ�
 * we tell the adapter to animate the children of a certain group. We then expand the group which causes the
 * �츮�� �����Ϳ��� Ư�� �׷��� �ڽĵ��� ���������Ѵ� �츮�� expandableListview�� ��� ��ũ������ ����� refresh�ϱ⶧���� �׷���
 * ��â�Ѵ�.
 * ExpandableListView to refresh all views on screen.
 *
 * The way that ExpandableListView does this is by calling getView() in the adapter.
 * expandableListView�� getview(��;���) ������ �̷������.
 * However since the adapter knows that we are animating a certain group,
 * ������ ��ʹ� �츮�� Ư���׷쿡 ���ϸ��̼��� ���Ϸ����� �𸣱� ������
 * instead of returning the real views for the children of the group being
 * ��ſ� ���� view�� �׷��� �ڽĿ� ����ȿ��ϸ��̼� realview�� ��ȯ�ؾ��Ѵ�.
 * animated,
 * it will return a fake dummy view.
 * ���� �װ��� fake dummy view�� ��ȯ�Ѵ�.
 * This dummy view will then
 * draw the real child views within it's dispatchDraw function.
 * �׸��� �� dummy view�� �츮�� ���ϴ� ����� �並 dispatchdraw function�� �Ա� �׷�����.
 * The reason we do this is so that we can animate all of it's children by simply animating the dummy view.
 * �ֳ��ϸ� �츮�� �̷��� ������ �ܼ��� dummy view�� ȿ���� ���� ��� childrenview�� animate�� �� �ִ�.
 * After we complete the animation, we tell the adapter to stop animating the group and call notifyDataSetChanged.
 * ȿ���� ������ �츮�� adapter���� animating�� ������� �ϰ� group�� �����Ͱ� ��������� �˷��ش�.
 * Now the ExpandableListView is forced to refresh it's views again,
 * ���� ExpandableListViw�� �װ��� view���� �ٽ� �ʱ�ȭ �Ϸ��� �Ұ��̴�.
 * except this time, it will get the real views for the expanded group.
 * �̹��ѹ��� �����ϰ�� �װ��� expanded group�� ���� real view�� ��Եɰ��̴�.
 * So, to list it all out, when {@link #expandGroupWithAnimation(int)} is
 * called the following happens:
 * �����غ��ڸ� ������ ���� ������ ���.
 * 1. The ExpandableListView tells the adapter to animate a certain group.
 * ExpandableListView �� adapter���� Ư�� �信 ȿ���� �ش�.
 * 2. The ExpandableListView calls expandGroup.
 * ExpandableListView�� expandGroup�� ȣ���Ѵ�.
 * 3. ExpandGroup calls notifyDataSetChanged.
 * ExpandableListView�� DataSet�� �����Ѵ�.
 * 4. As an result, getChildView is called for expanding group.
 * ���� getchildView�� expanding group�� ȣ���Ѵ�.
 * 5. Since the adapter is in "animating mode", it will return a dummy view.
 * ������ adapter�� animationg mode�̴�. �̰��� dummy view�� ��ȯ�Ѵ�.
 * 6. This dummy view draws the actual children of the expanding group.
 * �� dummy view�� Ȯ��� �׷��� ���� �ڽĵ��̴�.
 * 7. This dummy view's height is animated from 0 to it's expanded height.
 * ���̺���� ���̴� 0���� Ȯ��� ���̱��� animate���Ѵ�.
 * 8. Once the animation completes, the adapter is notified to stop
 *    animating the group and notifyDataSetChanged is called again.
 ȿ���� �Ϸ�Ǹ� ����Ϳ��� ȿ���� ���߶�� �ϰ� notifyDataSetChanged�� �ٽ��ѹ� ȣ��ȴ�.
 * 9. This forces the ExpandableListView to refresh all of it's views again.
 �̷��� ������ ExpandableListView���� ��� ����� �ٽ��ѹ� �ʱ�ȭ�Ѵ�.
 * 10.This time when getChildView is called, it will return the actual
 *    child views.
 * ���� getchildView�� �ҷ����� �װ��� ���� child view���� ��ȯ�Ѵ�.
 *
 * �ر��� �� ���߿�..
 *
 * For animating the collapse of a group is a bit more difficult since we
 * can't call collapseGroup from the start as it would just ignore the
 * child items,
 * �׷쿡���� �ر��� �ϱ����� �ִϸ��̼�ȿ���� ���� ��ƴ� �ֳ��ϸ�
 * �츰 collapseGroup�� ���۽� ȣ���� ���̴� ���� child item�� ���������ν�
 *
 * giving up no chance to do any sort of animation.
 * Instead what we have to do is play the animation first and call collapseGroup
 * after the animation is done.
 * So, to list it all out, when {@link #collapseGroupWithAnimation(int)} is
 * called the following happens:
 *
 * 1. The ExpandableListView tells the adapter to animate a certain group.
 * ExpandableListView �� Ư���� �׷쿡 animate�϶�� �Ѵ�.
 * 2. The ExpandableListView calls notifyDataSetChanged.
 * ExpandableListView�� dataset�� ��ȭ��Ų��.
 * 3. As an result, getChildView is called for expanding group.
 * �׷��� getchildView�� group�� Ȯ���Ѵ�.
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
    private boolean isTest= false;


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
     * �ִϸ��̼� ȿ���� �Բ� �׷��� Ȯ���Ѵ�.
     *
     * @param groupPos The position of the group to expand
     * @return Returns true if the group was expanded. False if the group was
     * already expanded.
     * �׷��� Ȯ��Ǹ� true �̹� ���ȴٸ� false
     */
    @SuppressLint("NewApi")
    public boolean expandGroupWithAnimation(int groupPos) {
        boolean lastGroup = groupPos == adapter.getGroupCount() - 1;

        if (lastGroup && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return expandGroup(groupPos, true);
        }
        // flat??
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));

        //����ó��.
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
        // ���������� GroupInfo�� ���� �� �����Ѵ�. .
        adapter.startExpandAnimation(groupPos, 0);
        // Finally call expandGroup (note that expandGroup will call
        // notifyDataSetChanged so we don't need to)
        return expandGroup(groupPos);
    }

    /**
     * �޴��� �ı��ϴ� animation.
     * Collapses the given group with an animation.
     *
     * @param groupPos The position of the group to collapse
     * @return Returns true if the group was collapsed. False if the group was
     * already collapsed.
     * ���������� childview���� ����� true �̹� ����������� false .
     */
    public boolean collapseGroupWithAnimation(int groupPos) {
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if(childIndex >= 0 && childIndex <getChildCount()){
                // get the view for the group it is on screen
                //��ũ����
                View v = getChildAt(childIndex);
                if(v.getBottom()>=getBottom()){
                    // If the user is not going to be able to see the animation
                    // we just collapse the group without an animation.
                    // ���� ������ animation�� ��������ʴٸ� �츮�� �׳� �׷��� �ر��Ѵ�.
                    // animation����.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen
                    // �̰Ϳ� ���� �ذ��� getchildView�� ȣ����� �ʴ´�.
                    // ���� �׷��� �ڽĵ��� ��ũ���� ���ٸ�.
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
         * ���̺��� ���̰��� �����ϰ��ִ�.
         * We save this information so that if the user collapses a group
         * before it fully expands,
         * <p>
         * the collapse animation will start from the
         * �޴��� �������� ȿ���� ���� ���̺��� ���̷κ��� �����Ѵ�.
         * ��������
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
                // ������ ���� �����.
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


                return 0;
            } else {
                // If we are not animating this group, then we will add 1 to
                // the type it has so that no type id conflicts will occur
                // unless getRealChildType() returns MAX_INT
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
            if (info.animating) {
                if (convertView instanceof DummyView == false) {;
                    convertView = new DummyView(parent.getContext());
                    convertView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                }


                if (childPosition < info.firstChildPosition) {
                    // The reason why we do this is to support the collapse
                    // this group when the group view is not visible but the
                    // children of this group are. When notifyDataSetChanged is called,
                    // notifyDataSetChanged�� �Ҹ�������
                    // the ExpandableListView tries to keep the list position the same by saving the first visible item
                    // ExpandableListView Ŭ������ list position�� �����Ϸ����Ѵ�. list position�� ó�� �������� ������ �並 �����ϰ�,
                    // and jumping back to that item after the views have been  refreshed.
                    // �׸��� �������� ���ư��� item��  ����̰��� �͵��� �ʱ�ȭ�Ѵ�.
                    // Now the problem is, if a group has 2 items
                    // ���� ������ ���� �׷��� 2�� �̻��� item�� ������ �ִ°��� .
                    // and the first visible item is the 2nd child of the group
                    // �׸��� ó�� ������ �������� �� �׷��� 2��° �ڽ��̴�.
                    // and this group is collapsed, then the dummy view will be
                    // �׸��� �̱׷��� �� �ı��ϸ� dummy�׷��� �� �׷��� ���� ���ɰ��̴�.
                    // used for the group. But now the group only has 1 item
                    // ������ �׷��� ���� ���� �Ѱ��� �����۸��� �������ִ�.
                    // which is the dummy view, thus when the ListView is trying
                    //���̺�.. �׷��Ƿ� listView�� ��ũ���� ��ġ�� ȸ���Ϸ� �Ұ��̴�.
                    // to restore the scroll position,
                    //
                    // it will try to jump to the second item of the group.
                    // But this group no longer has a second item, so it is forced to jump to the next
                    // group.
                    //
                    // This will cause a very ugly visual glitch.
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

                /* group�� ���� �ڽ��� ������ŭ
                 * �並 �׸� parameter���� ���ϴ� �ݺ����̴�.
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
                        // ����� ����� �׸����̴�.. �ٺ����� �������� ��������
                        dummyView.addFakeView(childView);
                    } else {
                        dummyView.addFakeView(childView);

                        // if this group has too many views, we don't want to
                        // calculate the height of everything... just do a light
                        // approximation and break
                        // �ʹ� ����� �׷��� ���̰����� �뷫�����θ� ����ϰ� �ߴ��Ѵ�.
                        int averageHeight = totalHeight / (i + 1);
                        totalHeight += (len - i - 1) * averageHeight;
                        break;
                    }
                }

                Object o;

                // ���� ���� �±� ���� �� ����ִ°��� �ٸ������ ���°��� ����.
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
                            // ExpandableListView�� DataSet�� �����Ѵ�.
                            // animation �� ������ data�� �����ϰ� tag�� �����Ѵ�.
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
         * ���� ���� ������ ���� ��ü���� �ϳ��� fake view�� �����.
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
            view.requestLayout(); // ?? parentlayout ��û ?
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t){
            super.applyTransformation(interpolatedTime,t);

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
