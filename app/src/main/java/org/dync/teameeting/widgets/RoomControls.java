/**
 * RoomControls.java [V 1.0.0]
 * classes:cn.zldemo.touchitem.view.RoomControls
 * Zlang Create at 2015-12-20.下午12:41:12 
 */
package org.dync.teameeting.widgets;

import org.dync.teameeting.ui.helper.Anims;
import org.dync.teameeting.utils.ScreenUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * cn.zldemo.touchitem.view.RoomControls
 * 
 * @author ZLang <br/>
 *         create at 2015-12-20 下午12:41:12
 */
public class RoomControls extends LinearLayout
{

	public boolean mAvailable;

	public RoomControls(Context paramContext)
	{
		super(paramContext);
		init(paramContext, null);
	}

	public RoomControls(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		init(paramContext, paramAttributeSet);
	}

	public RoomControls(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext, paramAttributeSet);
	}

	private void init(Context paramContext, AttributeSet paramAttributeSet)
	{
		this.mAvailable = true;
	}

	public void hide()
	{
		this.mAvailable = false;
		Anims.animateBottomMarginTo(this, ScreenUtils.dpToP(getResources(), -90), 300L,
				Anims.ACCELERATE);
		makeInvisible();
	}

	public void makeInvisible()
	{
		Anims.fadeOut(this, 300L);
	}

	public void show()
	{
		this.mAvailable = true;
		Anims.animateBottomMarginTo(this, ScreenUtils.dpToP(getResources(), 20), 300L,
				Anims.EASE_OUT);
		makeVisible();
	}

	public void makeVisible()
	{
		Anims.fadeIn(this, 300L, 0L);
	}

}
