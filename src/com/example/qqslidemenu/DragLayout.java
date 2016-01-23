package com.example.qqslidemenu;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DragLayout extends ViewGroup {

	private View redView;
	private View greenView;

	private ViewDragHelper mViewDragHelper;// 父view控制子view移动的核心类

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}

	// 初始化数据
	private void init() {
		mViewDragHelper = ViewDragHelper.create(this, callBack);
	}

	// 事件拦截,交给mViewDragHelper来处理
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	// 拦截之后，自己处理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);
		return true;
	}

	// 给mViewDragHelper加回调
	ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {// 这个方法是用来捕获view
			return child == redView || child == greenView;
		}

		// 水平方向的移动回调
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (left <= 0) {
				left = 0;
			} else if (left >= DragLayout.this.getMeasuredWidth()
					- child.getMeasuredWidth()) {
				left = DragLayout.this.getMeasuredWidth()
						- child.getMeasuredWidth();
			}
			return left;
		}

		// 竖直方向的移动回调
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (top <= 0) {
				top = 0;
			} else if (top >= DragLayout.this.getMeasuredHeight()
					- child.getMeasuredHeight()) {
				top = DragLayout.this.getMeasuredHeight()
						- child.getMeasuredHeight();
			}
			return top;
		}

		// 当view发生移动的时候走这个方法
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if (changedView == greenView) {
				int newLeft = redView.getLeft() + dx;
				int newTop = redView.getTop() + dy;
				if (newLeft <= 0) {
					newLeft = 0;
				} else if (newLeft >= DragLayout.this.getMeasuredWidth()
						- redView.getMeasuredWidth()) {
					newLeft = DragLayout.this.getMeasuredWidth()
							- redView.getMeasuredWidth();
				}
				if (newTop <= 0) {
					newTop = 0;
				} else if (newTop >= DragLayout.this.getMeasuredHeight()
						- redView.getMeasuredHeight()) {
					newTop = DragLayout.this.getMeasuredHeight()
							- redView.getMeasuredHeight();
				}

				redView.layout(newLeft, newTop,
						newLeft + redView.getMeasuredWidth(),
						newTop + redView.getMeasuredHeight());
			}
			// 当一个view位置发生变化时，另一个view的做一个动画效果
			float percent = greenView.getLeft()
					* 1.0f
					/ (DragLayout.this.getMeasuredWidth() - greenView
							.getMeasuredWidth());
			executeAnima(percent);
		}

		// 当松手的时候走这个方法
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int centerX = DragLayout.this.getMeasuredWidth() / 2
					- releasedChild.getMeasuredWidth() / 2;
			// System.out.println("============"+centerX);
			if (releasedChild.getLeft() <= centerX) {
				// System.out.println("向左慢慢滑动");
				mViewDragHelper.smoothSlideViewTo(releasedChild, 0,
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			} else if (releasedChild.getLeft() > centerX) {
				// System.out.println("向右慢慢滑动");
				mViewDragHelper.smoothSlideViewTo(
						releasedChild,
						DragLayout.this.getMeasuredWidth()
								- releasedChild.getMeasuredWidth(),
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
		}
	};

	// 上面的慢慢滑动必须配合这个方法一起使用
	public void computeScroll() {
		if (mViewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	};

	// 执行动画，用ViewHelper来做
	protected void executeAnima(float percent) {
		// //用ViewHelper来设置动画
		// ViewHelper.setTranslationX(redView, 50*percent);
		// ViewHelper.setAlpha(redView, 1-percent);
		// ViewHelper.setRotationY(redView, 360f*percent);
		// ViewHelper.setScaleX(redView, 1-percent);
		// 设置渐变色
		redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(percent,
				Color.RED, Color.GREEN));
	}

	/**
	 * 解析完布局文件中的子view的时候走这个方法
	 */
	@Override
	protected void onFinishInflate() {
		redView = this.getChildAt(0);
		greenView = this.getChildAt(1);
	}

	/**
	 * 先测量完才知道要显示的控件的大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);// 测量父view的宽高

		int redViewWidthSpec = MeasureSpec.makeMeasureSpec(
				redView.getLayoutParams().width, MeasureSpec.EXACTLY);
		int redViewHeightSpec = MeasureSpec.makeMeasureSpec(
				redView.getLayoutParams().height, MeasureSpec.EXACTLY);
		redView.measure(redViewWidthSpec, redViewHeightSpec);// 测量子view的宽高
		greenView.measure(redViewWidthSpec, redViewHeightSpec);

	}

	/**
	 * 再布局，才知道要显示在什么位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// int left=0;//设置在左边
		int left = this.getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;// 设置在中间
		int top = 0;
		int right = left + redView.getMeasuredWidth();
		int bottom = top + redView.getMeasuredHeight();

		redView.layout(left, top, right, bottom);
		greenView.layout(left, bottom, right,
				bottom + greenView.getMeasuredHeight());
	}

}
