package com.dada.marsframework.widget.actionbar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dada.marsframework.R;

import java.util.LinkedList;

/**
 * Created by laidayuan on 2018/2/12.
 */

public class ActionBar extends RelativeLayout implements View.OnClickListener {

    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView mLogoView;
    private View mBackIndicator;
    // private View mHomeView;
    private TextView mTitleView;
    private LinearLayout mActionsView;
    private RelativeLayout mHomeLayout;
    private ProgressBar mProgress;
    private View mLine;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(
                R.layout.layout_actionbar, null);
        addView(mBarView);

        mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeLayout = (RelativeLayout) mBarView
                .findViewById(R.id.actionbar_home_bg);
        mBackIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView
                .findViewById(R.id.actionbar_actions);

        mProgress = (ProgressBar) mBarView
                .findViewById(R.id.actionbar_progress);

        mLine=(View)mBarView.findViewById(R.id.line);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);

		/*
		 * CharSequence title = a.getString(R.styleable.ActionBar_title); if
		 * (title != null) { setTitle(title); }
		 */
        // CharSequence title = a.getString(R.styleable.ActionBar_title);
        // if (title != null) {
        // setTitle(title);
        // }

        a.recycle();
    }

    public void setHomeAction(final Action action) {

        if (action.getText() != null) {
            TextView labelView = (TextView) mHomeLayout
                    .findViewById(R.id.actionbar_home_text);
            labelView.setText(action.getText());
            labelView.setVisibility(View.VISIBLE);
            labelView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    action.performAction(view);
                }
            });
        } else {
            ImageButton labelView = (ImageButton) mHomeLayout
                    .findViewById(R.id.actionbar_home_btn);
            labelView.setImageResource(action.getDrawable());
            labelView.setVisibility(View.VISIBLE);
            labelView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    action.performAction(view);
                }
            });
        }

        // mHomeLayout.setTag(action);
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the provided logo to the left in the action bar.
     *
     * This is meant to be used instead of the setHomeAction and does not draw a
     * divider to the left of the provided logo.
     *
     * @param resId
     *            The drawable resource id
     */
    public void setHomeLogo(int resId) {
        // TODO: Add possibility to add an IntentAction as well.
        mLogoView.setImageResource(resId);
        mLogoView.setVisibility(View.VISIBLE);
        mHomeLayout.setVisibility(View.GONE);
    }

    /*
     * Emulating Honeycomb, setdisplayHomeAsUpEnabled takes a boolean and
     * toggles whether the "home" view should have a little triangle indicating
     * "up"
     */
    public void setDisplayHomeAsUpEnabled(boolean show) {
        mBackIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    public void setTitleColor(ColorStateList color){
        mTitleView.setTextColor(color);
    }

    public void setTitleColor(int color){
        mTitleView.setTextColor(color);
    }

    public void setGoneLine(){
        mLine.setVisibility(View.GONE);
    }

    public void setProgressBarVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }


    public int getProgressBarVisibility() {
        return mProgress.getVisibility();
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener
     *            the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     *
     * @param actionList
     *            the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action
     *            the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action
     *            the action to add
     * @param index
     *            the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index
     *            position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action
     *            The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                Object tag = view.getTag();
                View button = view.findViewById(R.id.actionbar_item_btn);
                if (tag == null && button != null) {
                    tag = button.getTag();
                }

                if (tag instanceof Action && tag.equals(action)) {

                    mActionsView.removeView(view);

                }
            }
        }

    }

    /**
     * Returns the number of actions currently registered with the action bar.
     *
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action
     *            the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.layout_actionbar_item,
                mActionsView, false);

        if (action.getText() != null) {
            TextView labelView = (TextView) view
                    .findViewById(R.id.actionbar_item_text);
            labelView.setText(action.getText());
            labelView.setVisibility(View.VISIBLE);
            labelView.setTextColor(action.getTextColor());
            view.setTag(action);
            view.setOnClickListener(this);

        } else {
            ImageButton labelView = (ImageButton) view
                    .findViewById(R.id.actionbar_item_btn);
            labelView.setImageResource(action.getDrawable());
            labelView.setVisibility(View.VISIBLE);

            labelView.setTag(action);
            labelView.setOnClickListener(this);
        }

        return view;
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public int getDrawable();

        public String getText();

        public int getTextColor();

        public void performAction(View view);
    }

    public static class IntentAction extends AbstractAction {
        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
            mContext = context;
            mIntent = intent;
        }

        @Override
        public void performAction(View view) {
            try {
                mContext.startActivity(mIntent);
            } catch (ActivityNotFoundException e) {

            }
        }
    }

	/*
	 * public static abstract class SearchAction extends AbstractAction { public
	 * SearchAction() { super(R.drawable.actionbar_search); } }
	 */
}
