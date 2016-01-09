package org.dync.teameeting.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhulang on 2016/1/7 0007.
 */
public class CRUDChat
{

    public static List<ChatEnity> selectChatLsit(Context context, String meetingId)
    {
        DaoSession sesion = getSession(context);
        ChatEnityDao chatEnity = sesion.getChatEnityDao();
        //List<ChatEnity> list = chatEnity.loadAll();
        List<ChatEnity> list = new ArrayList<ChatEnity>();
        list = chatEnity.queryBuilder().where(ChatEnityDao.Properties.MeetingId.eq(meetingId)).list();

        Log.i("CRUDChat", "testLoadAll");
        for (ChatEnity p : list)
        {
            Log.i("CRUDChat", p.getName());
        }
        return list;
    }

    /**
     * insert
     * @param context
     * @param chatEnity
     */
    public static void queryInsert(Context context, ChatEnity chatEnity)
    {
        DaoSession sesion = getSession(context);
        ChatEnityDao dao = sesion.getChatEnityDao();
        dao.insert(chatEnity);
    }


    public static DaoSession getSession(Context context)
    {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context,
                "CHAT.db", null).getWritableDatabase();
        DaoMaster dm = new DaoMaster(db);
        DaoSession sesion = dm.newSession();
        return sesion;
    }
}
