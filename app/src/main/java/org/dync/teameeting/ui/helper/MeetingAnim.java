/**
 * MettingAnim.java [V 1.0.0]
 * classes:org.dync.teammeeting.ui.anim.MettingAnim
 * Zlang Create at 2015-12-10.下午2:25:21 
 */
package org.dync.teameeting.ui.helper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.view.View;
import android.widget.ImageButton;

/**
 * 
 * Meeting control animator helper
 * 
 * @author ZLang <br/>
 *         create at 2015-12-10 下午2:25:21
 */
public class MeetingAnim
{
	/**
	 * 
	 * @param view
	 * @param startAlpha
	 * @param endAlpha
	 * @param time
	 */
	public void alphaAnimator(View view, float startAlpha, float endAlpha, int time)
	{

		PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", startAlpha, endAlpha);
		ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, alpha);
		objectAnimator.setDuration(time).start();

	}

	/**
	 * button translation or Alpha Animator
	 * 
	 * @param view
	 * @param startX
	 * @param endX
	 * @param time
	 * @param alphaFlag
	 */
	public void translationAlphaAnimator(View view, float startX, float endX, int time,
			final boolean alphaFlag)
	{

		PropertyValuesHolder alpha;
		if (alphaFlag)
		{
			alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0F);
		} else
		{
			alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0F);
		}

		PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", startX,
				endX);
		ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, alpha,
				translationX);
		objectAnimator.setDuration(time).start();

		objectAnimator.addListener(new AnimatorListener()
		{

			@Override
			public void onAnimationStart(Animator arg0)
			{

			}

			@Override
			public void onAnimationRepeat(Animator arg0)
			{

			}

			@Override
			public void onAnimationEnd(Animator arg0)
			{
				if (!alphaFlag && animationEndListener != null)
				{
					animationEndListener.onAnimationEnd(arg0);
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0)
			{

			}
		});

	}

	AnimationEndListener animationEndListener;

	public interface AnimationEndListener
	{
		public void onAnimationEnd(Animator arg0);
	}

	public void setAnimEndListener(AnimationEndListener listener)
	{
		animationEndListener = listener;
	}

	/**
	 * @param cameraButton
	 * @param meetingCameraFlag
	 */
	public void rotationOrApaha(View view, boolean meetingCameraFlag)
	{
		int values = 360;
		if (meetingCameraFlag)
		{
			values = 360;
		} else
		{
			values = -360;
		}
		ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0, values);
		ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		AnimatorSet set = new AnimatorSet();
		set.play(alpha).with(rotation);
		set.setDuration(300);
		set.start();
	}

}
