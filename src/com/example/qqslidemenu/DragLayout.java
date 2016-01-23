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

	private ViewDragHelper mViewDragHelper;// ��view������view�ƶ��ĺ�����

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

	// ��ʼ������
	private void init() {
		mViewDragHelper = ViewDragHelper.create(this, callBack);
	}

	// �¼�����,����mViewDragHelper������
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	// ����֮���Լ�����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);
		return true;
	}

	// ��mViewDragHelper�ӻص�
	ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {// �����������������view
			return child == redView || child == greenView;
		}

		// ˮƽ������ƶ��ص�
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

		// ��ֱ������ƶ��ص�
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

		// ��view�����ƶ���ʱ�����������
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
			// ��һ��viewλ�÷����仯ʱ����һ��view����һ������Ч��
			float percent = greenView.getLeft()
					* 1.0f
					/ (DragLayout.this.getMeasuredWidth() - greenView
							.getMeasuredWidth());
			executeAnima(percent);
		}

		// �����ֵ�ʱ�����������
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int centerX = DragLayout.this.getMeasuredWidth() / 2
					- releasedChild.getMeasuredWidth() / 2;
			// System.out.println("============"+centerX);
			if (releasedChild.getLeft() <= centerX) {
				// System.out.println("������������");
				mViewDragHelper.smoothSlideViewTo(releasedChild, 0,
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			} else if (releasedChild.getLeft() > centerX) {
				// System.out.println("������������");
				mViewDragHelper.smoothSlideViewTo(
						releasedChild,
						DragLayout.this.getMeasuredWidth()
								- releasedChild.getMeasuredWidth(),
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
		}
	};

	// ���������������������������һ��ʹ��
	public void computeScroll() {
		if (mViewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	};

	// ִ�ж�������ViewHelper����
	protected void executeAnima(float percent) {
		// //��ViewHelper�����ö���
		// ViewHelper.setTranslationX(redView, 50*percent);
		// ViewHelper.setAlpha(redView, 1-percent);
		// ViewHelper.setRotationY(redView, 360f*percent);
		// ViewHelper.setScaleX(redView, 1-percent);
		// ���ý���ɫ
		redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(percent,
				Color.RED, Color.GREEN));
	}

	/**
	 * �����겼���ļ��е���view��ʱ�����������
	 */
	@Override
	protected void onFinishInflate() {
		redView = this.getChildAt(0);
		greenView = this.getChildAt(1);
	}

	/**
	 * �Ȳ������֪��Ҫ��ʾ�Ŀؼ��Ĵ�С
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);// ������view�Ŀ��

		int redViewWidthSpec = MeasureSpec.makeMeasureSpec(
				redView.getLayoutParams().width, MeasureSpec.EXACTLY);
		int redViewHeightSpec = MeasureSpec.makeMeasureSpec(
				redView.getLayoutParams().height, MeasureSpec.EXACTLY);
		redView.measure(redViewWidthSpec, redViewHeightSpec);// ������view�Ŀ��
		greenView.measure(redViewWidthSpec, redViewHeightSpec);

	}

	/**
	 * �ٲ��֣���֪��Ҫ��ʾ��ʲôλ��
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// int left=0;//���������
		int left = this.getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;// �������м�
		int top = 0;
		int right = left + redView.getMeasuredWidth();
		int bottom = top + redView.getMeasuredHeight();

		redView.layout(left, top, right, bottom);
		greenView.layout(left, bottom, right,
				bottom + greenView.getMeasuredHeight());
	}

}
