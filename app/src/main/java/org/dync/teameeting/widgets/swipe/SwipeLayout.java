package org.dync.teameeting.widgets.swipe;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 
 * 
 * @author PoplarTang H_lang 修改 2015年12月1日14:08:13
 * 
 */
public class SwipeLayout extends FrameLayout implements SwipeLayoutInterface
{

	private static final String TAG = "SwipeLayout";
	private View mFrontView;
	private View mBackView;
	private int mDragDistance;
	private ShowEdge mShowEdge = ShowEdge.Right;
	private Status mStatus = Status.Close;
	private ViewDragHelper mDragHelper;
	private SwipeListener mSwipeListener;
	private GestureDetectorCompat mGestureDetector;

	public static enum Status
	{
		Close, Swiping, Open
	}

	public static enum ShowEdge
	{
		Left, Right
	}

	public static interface SwipeListener
	{
		void onClose(SwipeLayout swipeLayout);

		void onOpen(SwipeLayout swipeLayout);

		void onStartClose(SwipeLayout swipeLayout);

		void onStartOpen(SwipeLayout swipeLayout);

	}

	public SwipeLayout(Context context)
	{
		this(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mDragHelper = ViewDragHelper.create(this, mCallback);
		mGestureDetector = new GestureDetectorCompat(context, mOnGestureListener);

	}

	private SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener()
	{

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{

			return Math.abs(distanceX) >= Math.abs(distanceY);
		}
	};

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback()
	{

		@Override
		public boolean tryCaptureView(View child, int pointerId)
		{

			return child == getFrontView() || child == getBackView();
		}

		@Override
		public int getViewHorizontalDragRange(View child)
		{

			return mDragDistance;
		};

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx)
		{
			// The latest amendments suggested values ​​left and offset dx
			// generated to give a final position on the left lateral slip
			int newLeft = left;
			if (child == mFrontView)
			{

				switch (mShowEdge)
				{
				case Left:
					if (newLeft < 0)
						newLeft = 0;
					else if (newLeft > mDragDistance)
						newLeft = mDragDistance;

					break;
				case Right:
					if (newLeft < 0 - mDragDistance)
						newLeft = 0 - mDragDistance;
					else if (newLeft > 0)
						newLeft = 0;

					break;
				}
			} else if (child == mBackView)
			{

				switch (mShowEdge)
				{
				case Left:
					if (newLeft < 0 - mDragDistance)
						newLeft = 0 - mDragDistance;
					else if (newLeft > 0)
						newLeft = 0;
					break;
				case Right:
					if (newLeft < getMeasuredWidth() - mDragDistance)
					{
						newLeft = getMeasuredWidth() - mDragDistance;
					} else if (newLeft > getMeasuredWidth())
					{
						newLeft = getMeasuredWidth();
					}
					break;
				}
			}

			return newLeft;
		};

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
		{

			if (changedView == mFrontView)
			{

				getBackView().offsetLeftAndRight(dx);
			} else if (changedView == mBackView)
			{

				getFrontView().offsetLeftAndRight(dx);
			}

			updateStatus();

			invalidate();
		};

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{

			Log.d(TAG, "xvel: " + xvel + " mShowEdge: " + mShowEdge);
			if (releasedChild == getFrontView())
			{
				processFrontViewRelease(xvel, yvel);
			} else if (releasedChild == getBackView())
			{
				processBackViewRelease(xvel, yvel);
			}
			invalidate();
		};

	};
	private float mDownX;

	protected void processBackViewRelease(float xvel, float yvel)
	{
		switch (mShowEdge)
		{
		case Left:
			if (xvel == 0)
			{
				if (getBackView().getLeft() > (0 - mDragDistance * 0.5f))
				{
					open();
					return;
				}
			} else if (xvel > 0)
			{
				open();
				return;
			}
			break;
		case Right:
			if (xvel == 0)
			{
				if (getBackView().getLeft() < (getMeasuredWidth() - mDragDistance * 0.5f))
				{
					open();
					return;
				}
			} else if (xvel < 0)
			{
				open();
				return;
			}
			break;
		}
		close();
	}

	@Override
	public Status getCurrentStatus()
	{
		int left = getFrontView().getLeft();
		if (left == 0)
		{
			return Status.Close;
		}
		if ((left == 0 - mDragDistance) || (left == mDragDistance))
		{
			return Status.Open;
		}
		return Status.Swiping;
	}

	protected void processFrontViewRelease(float xvel, float yvel)
	{
		switch (mShowEdge)
		{
		case Left:
			if (xvel == 0)
			{
				if (getFrontView().getLeft() > mDragDistance * 0.5f)
				{
					open();
					return;
				}
			} else if (xvel > 0)
			{
				open();
				return;
			}
			break;
		case Right:
			if (xvel == 0)
			{
				if (getFrontView().getLeft() < 0 - mDragDistance * 0.5f)
				{
					open();
					return;
				}
			} else if (xvel < 0)
			{
				open();
				return;
			}
			break;
		}
		close();
	}

	public void close()
	{
		close(true);
	}

	public void close(boolean isSmooth)
	{
		close(isSmooth, true);
	}

	public void close(boolean isSmooth, boolean isNotify)
	{
		if (isSmooth)
		{
			Rect rect = computeFrontLayout(false);
			if (mDragHelper.smoothSlideViewTo(getFrontView(), rect.left, rect.top))
			{
				ViewCompat.postInvalidateOnAnimation(this);
			}
			;
		} else
		{
			layoutContent(false);
			updateStatus(isNotify);
		}
	}

	public void open()
	{
		open(true, true);
	}

	public void open(boolean isSmooth)
	{
		open(isSmooth, true);
	}

	/**
	 * open layout
	 * 
	 * @param isSmooth
	 *            Whether it is smooth animation。
	 * @param isNotify
	 *            Whether notification callback
	 */
	public void open(boolean isSmooth, boolean isNotify)
	{
		if (isSmooth)
		{
			Rect rect = computeFrontLayout(true);
			if (mDragHelper.smoothSlideViewTo(getFrontView(), rect.left, rect.top))
			{
				ViewCompat.postInvalidateOnAnimation(this);
			}
			;
		} else
		{
			layoutContent(true);
			updateStatus(isNotify);
		}
	}

	private void updateStatus()
	{
		updateStatus(true);
	}

	/**
	 * Update the current status
	 * 
	 * @param isNotify
	 */
	private void updateStatus(boolean isNotify)
	{
		Status lastStatus = mStatus;
		Status status = getCurrentStatus();

		if (status != mStatus)
		{
			mStatus = status;

			if (!isNotify || mSwipeListener == null)
			{
				return;
			}

			if (mStatus == Status.Open)
			{
				mSwipeListener.onOpen(this);
			} else if (mStatus == Status.Close)
			{
				mSwipeListener.onClose(this);
			} else if (mStatus == Status.Swiping)
			{
				if (lastStatus == Status.Open)
				{
					mSwipeListener.onStartClose(this);
				} else if (lastStatus == Status.Close)
				{
					mSwipeListener.onStartOpen(this);
				}
			}
		} else
		{
			mStatus = status;
		}
	}

	@Override
	public void computeScroll()
	{

		if (mDragHelper.continueSettling(true))
		{

			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev)
	{

		return mDragHelper.shouldInterceptTouchEvent(ev) & mGestureDetector.onTouchEvent(ev);
	};

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		switch (MotionEventCompat.getActionMasked(event))
		{
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:

			float deltaX = event.getRawX() - mDownX;
			if (deltaX > mDragHelper.getTouchSlop())
			{

				requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			mDownX = 0;
		default:
			break;
		}

		try
		{
			mDragHelper.processTouchEvent(event);
		} catch (IllegalArgumentException e)
		{
		}

		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mDragDistance = getBackView().getMeasuredWidth();

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);

		layoutContent(false);

	}

	private void layoutContent(boolean isOpen)
	{
		Rect rect = computeFrontLayout(isOpen);
		getFrontView().layout(rect.left, rect.top, rect.right, rect.bottom);
		rect = computeBackLayoutViaFront(rect);
		getBackView().layout(rect.left, rect.top, rect.right, rect.bottom);
		bringChildToFront(getFrontView());
	}

	private Rect computeBackLayoutViaFront(Rect mFrontRect)
	{
		Rect rect = mFrontRect;

		int bl = rect.left, bt = rect.top, br = rect.right, bb = rect.bottom;
		if (mShowEdge == ShowEdge.Left)
		{
			bl = rect.left - mDragDistance;
		} else if (mShowEdge == ShowEdge.Right)
		{
			bl = rect.right;
		}
		br = bl + getBackView().getMeasuredWidth();

		return new Rect(bl, bt, br, bb);
	}

	private Rect computeFrontLayout(boolean isOpen)
	{
		int l = 0, t = 0;
		if (isOpen)
		{
			if (mShowEdge == ShowEdge.Left)
			{
				l = 0 + mDragDistance;
			} else if (mShowEdge == ShowEdge.Right)
			{
				l = 0 - mDragDistance;
			}
		}

		return new Rect(l, t, l + getMeasuredWidth(), t + getMeasuredHeight());
	}

	@Override
	protected void onFinishInflate()
	{
		if (getChildCount() != 2)
		{
			throw new IllegalStateException("At least 2 views in SwipeLayout");
		}

		mFrontView = getChildAt(0);
		if (mFrontView instanceof FrontLayout)
		{
			((FrontLayout) mFrontView).setSwipeLayout(this);
		} else
		{
			throw new IllegalArgumentException("Front view must be an instanceof FrontLayout");
		}

		mBackView = getChildAt(1);

	}

	public View getFrontView()
	{
		return mFrontView;
	}

	public View getBackView()
	{
		return mBackView;
	}

	public void setShowEdge(ShowEdge showEdit)
	{
		mShowEdge = showEdit;
		requestLayout();
	}

	public SwipeListener getSwipeListener()
	{
		return mSwipeListener;
	}

	public void setSwipeListener(SwipeListener mSwipeListener)
	{
		this.mSwipeListener = mSwipeListener;
	}
}
