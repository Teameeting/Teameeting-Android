package org.dync.teameeting.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.dync.teameeting.db.chatdao.ChatCacheEntity;
import org.dync.teameeting.db.chatdao.ChatCacheEntityDao;
import org.dync.teameeting.db.chatdao.DaoMaster;
import org.dync.teameeting.db.chatdao.DaoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhulang on 2016/1/9 0009.
 */
public class CRUDChat {
    public static List<ChatCacheEntity> selectChatLsit(Context context, String meetingId) {

        DaoSession sesion = getSession(context);
        ChatCacheEntityDao chatEnity = sesion.getChatCacheEntityDao();
        List<ChatCacheEntity> list = new ArrayList<ChatCacheEntity>();
        list = chatEnity.queryBuilder().where(ChatCacheEntityDao.Properties.Meetingid.eq(meetingId)).list();
        Log.i("CRUDChat", "testLoadAll");
        for (ChatCacheEntity p : list) {
            Log.i("CRUDChat-Content", p.getContent());
        }
        return list;
    }

    /**
     * insert
     *
     * @param context
     * @param chatEnity
     */
    public static void queryInsert(Context context, ChatCacheEntity chatEnity) {
        DaoSession sesion = getSession(context);
        ChatCacheEntityDao dao = sesion.getChatCacheEntityDao();
        dao.insert(chatEnity);
    }


    public static DaoSession getSession(Context context) {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context,
                "CHAT.db", null).getWritableDatabase();
        DaoMaster dm = new DaoMaster(db);
        DaoSession sesion = dm.newSession();
        return sesion;
    }

}
