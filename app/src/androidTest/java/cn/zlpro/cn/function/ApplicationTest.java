package cn.zlpro.cn.function;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.dync.teameeting.db.CRUDChat;
import org.dync.teameeting.db.chatdao.ChatCacheEntity;
import org.dync.teameeting.db.chatdao.ChatCacheEntityDao;
import org.dync.teameeting.db.chatdao.DaoMaster;
import org.dync.teameeting.db.chatdao.DaoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

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

    public static DaoSession getSession(Context context) {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context,
                "CHAT.db", null).getWritableDatabase();
        DaoMaster dm = new DaoMaster(db);
        DaoSession sesion = dm.newSession();
        return sesion;
    }

}