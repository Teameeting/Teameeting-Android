package org.dync.teameeting.utils;

import org.dync.teameeting.bean.MeetingListEntity;

import java.util.List;

/**
 * Created by Xiao_Bailong on 2016/1/21.
 */
public class MeetingHelper {
    /**
     * if meeting List
     *
     * @param mDatas
     * @param meetingId
     * @return -1 postion null , >0 meetingID on arraylist position
     */
    public static int getMeetingIdPosition(List<MeetingListEntity> mDatas, String meetingId) {

        for (int i = 0; i < mDatas.size(); i++) {
            MeetingListEntity meetingListEntity = mDatas.get(i);
            if (meetingId.equals(meetingListEntity.getMeetingid())) {
                return i;
            }
        }
        return -1;
    }
}
