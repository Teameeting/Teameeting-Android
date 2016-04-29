package org.dync.teameeting.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.db.chatdao.ChatCacheEntity;
import org.dync.teameeting.db.chatdao.ChatCacheEntityDao;
import org.dync.teameeting.db.chatdao.DaoMaster;
import org.dync.teameeting.db.chatdao.DaoSession;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhulang on 2016/1/9 0009.
 */
public class CRUDChat {

    /*******
     * select
     */
    public static List<ChatCacheEntity> selectChatList(Context context, String meetingId) {

        DaoSession session = getSession(context);
        ChatCacheEntityDao chatEnity = session.getChatCacheEntityDao();
        List<ChatCacheEntity> list = new ArrayList<ChatCacheEntity>();
        list = chatEnity.queryBuilder().where(ChatCacheEntityDao.Properties.Meetingid.eq(meetingId)).list();
        closeDB(session);
        return list;

    }

    public static long selectLoadListSize(Context context, String meetingId) {
        DaoSession session = getSession(context);
        ChatCacheEntityDao chatEnity = session.getChatCacheEntityDao();
        long count = chatEnity.queryBuilder().where(ChatCacheEntityDao.Properties.Meetingid.eq(meetingId)).count();

        Log.i("CRUDChat", "count" + count);
        closeDB(session);
        return count;
    }

    public static List<ChatCacheEntity> setectAllList(Context context) {
        DaoSession session = getSession(context);
        ChatCacheEntityDao chatEnity = session.getChatCacheEntityDao();
        List<ChatCacheEntity> list = chatEnity.loadAll();
        closeDB(session);
        return list;
    }


    public static long selectIsReadSize(Context context, String meetingId) {

        DaoSession session = getSession(context);
        ChatCacheEntityDao chatEnity = session.getChatCacheEntityDao();
        QueryBuilder<ChatCacheEntity> where = chatEnity.queryBuilder().where(ChatCacheEntityDao.Properties.Meetingid.eq(meetingId));
        long isReadSize = (long) where.count();
        closeDB(session);
        return isReadSize;
    }

    public static ChatCacheEntity selectTopChatMessage(Context context, String meetingId) {

        DaoSession session = getSession(context);
        String tablename = session.getChatCacheEntityDao().getTablename();

        String sql = "select *from " + tablename + " where _id = (select max(_id) from "
                + tablename + " where meetingid ='" + meetingId + "')";
        Cursor cursor = session.getDatabase().rawQuery(sql, null);

        if (cursor.moveToFirst() == true) {
            String meeting = cursor.getString(cursor.getColumnIndex(ChatCacheEntity.MEETINGID));
            String sendtime = cursor.getString(cursor.getColumnIndex(ChatCacheEntity.SENDTIME));
            String userId = cursor.getString(cursor.getColumnIndex(ChatCacheEntity.USERID));
            String content = cursor.getString(cursor.getColumnIndex(ChatCacheEntity.CONTENT));
            boolean isRead = cursor.getInt(cursor.getColumnIndex(ChatCacheEntity.ISREAD)) == 0 ? false : true;
            cursor.close();
            return new ChatCacheEntity(meetingId, userId, content, sendtime, isRead);
        }
        return null;
    }


    /*******
     * Insert
     */

    public static void queryInsert(Context context, ReqSndMsgEntity resquest) {
        DaoSession session = getSession(context);
        ChatCacheEntityDao dao = session.getChatCacheEntityDao();
        if (resquest != null) {
            dao.insert(chatCacheEntityFractoty(resquest));
        }

    }


    /*******
     * delete
     */

    public static void deleteByMeetingId(Context context, String meetingId) {

        DaoSession session = getSession(context);
        ChatCacheEntityDao personDao = session.getChatCacheEntityDao();
        QueryBuilder<ChatCacheEntity> qb = personDao.queryBuilder();
        DeleteQuery<ChatCacheEntity> bd = qb.where(ChatCacheEntityDao.Properties.Meetingid.eq(meetingId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
        closeDB(session);
    }


    public static DaoSession getSession(Context context) {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context,
                "CHAT.db", null).getWritableDatabase();
        DaoMaster dm = new DaoMaster(db);

        DaoSession sesion = dm.newSession();
        return sesion;
    }

    private static ChatCacheEntity chatCacheEntityFractoty(ReqSndMsgEntity reqSndMsgEntity) {
        ChatCacheEntity chatCacheEntity = new ChatCacheEntity(
                reqSndMsgEntity.getRoom(),
                reqSndMsgEntity.getFrom(),
                reqSndMsgEntity.getCont(),
                reqSndMsgEntity.getNtime() + "",
                false
        );
        return chatCacheEntity;
    }

    private static void closeDB(DaoSession session) {
        session.getDatabase().close();
    }


}
