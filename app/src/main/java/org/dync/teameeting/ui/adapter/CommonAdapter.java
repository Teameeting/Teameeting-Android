/**
 * CommonAdapter.java [V 1.0.0]
 * classes:com.example.menu.adapter.CommonAdapter
 * Zlang Create at 2015-12-1.上午10:15:47 
 */
package org.dync.teameeting.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * com.example.menu.adapter.CommonAdapter
 * 
 * @author ZLang <br/>
 * 
 *         create at 2015-12-1 上午10:15:47 <br/>
 *         versions 1.0 viersion 2.0
 * @param <T>
 * 
 */
public abstract class CommonAdapter<T> extends BaseAdapter
{

	protected final Context mContext;
	protected final List<T> mDatas;
	protected LayoutInflater mInflater;
	protected int itemLayoutId;

	public CommonAdapter(Context context, List<T> mDatas)
	{
		this(context, mDatas, 0);
	}

	/**
	 * 
	 * @param context
	 * @param mDatas
	 * 
	 * @param itemLayoutId
	 * 
	 */
	public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId)
	{
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mDatas = mDatas;
		this.itemLayoutId = itemLayoutId;

	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public T getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
