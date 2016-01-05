package org.dync.teameeting.ui.adapter;

import java.util.List;

import org.dync.teameeting.R;
import org.dync.teameeting.utils.ChatMessage;
import org.dync.teameeting.utils.ChatMessage.Type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author zhulang <br/>
 *         <p>
 *         下午3:18:45
 */
public class ChatMessageAdapter extends CommonAdapter<ChatMessage>
{
    public ChatMessageAdapter(Context context, List<ChatMessage> datas)
    {
        super(context, datas);
    }

    /**
     * Receive the message is 1　，send the messagea is 0
     */
    @Override
    public int getItemViewType(int position)
    {
        ChatMessage msg = mDatas.get(position);
        return msg.getType() == Type.INPUT ? 1 : 0;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ChatMessage chatMessage = mDatas.get(position);

        ViewHolder mHolder = null;
        if (convertView == null)
        {
            if (chatMessage.getType() == Type.INPUT)
            {
                convertView = mInflater.inflate(R.layout.chat_input_msg, null);
            } else
            {
                convertView = mInflater.inflate(R.layout.chat_output_msg, null);
            }
            mHolder = mHolder.fromValues(convertView);
            convertView.setTag(mHolder);

        } else
        {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvContent.setText(chatMessage.getContent());
        // mHolder.tvSendName.setText(chatMessage.getContent());
        // mHolder.tvTime.setText(chatMessage.getDate().toString());

        return convertView;
    }

    private static class ViewHolder
    {
        public LinearLayout llChatLayout; // 设置聊天背景颜色
        public TextView tvContent;
        public TextView tvSendName;
        public TextView tvTime;

        private ViewHolder(LinearLayout llChatLayout, TextView tvContent, TextView tvSendName,
                           TextView tvTime)
        {
            super();
            this.llChatLayout = llChatLayout;
            this.tvContent = tvContent;
            this.tvSendName = tvSendName;
            this.tvTime = tvTime;
        }

        public static ViewHolder fromValues(View view)
        {
            return new ViewHolder((LinearLayout) view.findViewById(R.id.ll_chat_layout),
                    (TextView) view.findViewById(R.id.tv_chat_content),
                    (TextView) view.findViewById(R.id.tv_send_name),
                    (TextView) view.findViewById(R.id.tv_send_time));
        }
    }

}
