package org.dync.teameeting.bean;

import java.util.List;

/**
 * Created by zhangqilu on 2016/1/14.
 */
public class MessageList {


    /**
     * requestid : 1452759226326
     * code : 200
     * messageList : [{"id":56,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"a2835c4583ec7bab","sendtime":1452758647241,"message":"法国红"},{"id":55,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758601747,"message":"5689"},{"id":54,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758599738,"message":"6788"},{"id":53,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758597730,"message":"4688"},{"id":52,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758595723,"message":"Rtyio"},{"id":51,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758593719,"message":"4689"},{"id":50,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758545715,"message":"R67ii"},{"id":49,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758529195,"message":"Fuikk"},{"id":48,"messagetype":0,"meetingid":"400000000491","sessionid":"e4cd6dad85c08adb73a2f9b9d7547386","userid":"dcd54ddf2ee1df11","sendtime":1452758526573,"message":"467ii"},{"id":42,"messagetype":0,"meetingid":"400000000491","sessionid":"bba290c85923267b576c3a4fe973b84b","userid":"c1054ba8cb43f9a7","sendtime":1452757821878,"message":"Ssddf"},{"id":19,"messagetype":0,"meetingid":"400000000491","sessionid":"9ea9e3163b65358ac4f421d9d649cddb","userid":"cc1f0115b54c46a1","sendtime":1452736231994,"message":"名"},{"id":18,"messagetype":0,"meetingid":"400000000491","sessionid":"9ea9e3163b65358ac4f421d9d649cddb","userid":"cc1f0115b54c46a1","sendtime":1452736220929,"message":"八点"},{"id":17,"messagetype":0,"meetingid":"400000000491","sessionid":"19244870eb90d608e18d7e6576b96eaa","userid":"dcd54ddf2ee1df11","sendtime":1452736218149,"message":"Fhkk"},{"id":16,"messagetype":0,"meetingid":"400000000491","sessionid":"19244870eb90d608e18d7e6576b96eaa","userid":"dcd54ddf2ee1df11","sendtime":1452736210610,"message":",ryuu"},{"id":15,"messagetype":0,"meetingid":"400000000491","sessionid":"fda8cb80d02035c729fda6d3a859c25a","userid":"cc1f0115b54c46a1","sendtime":1452736208843,"message":"兔兔"},{"id":14,"messagetype":0,"meetingid":"400000000491","sessionid":"fda8cb80d02035c729fda6d3a859c25a","userid":"cc1f0115b54c46a1","sendtime":1452736204388,"message":" 兔兔"},{"id":13,"messagetype":0,"meetingid":"400000000491","sessionid":"fda8cb80d02035c729fda6d3a859c25a","userid":"cc1f0115b54c46a1","sendtime":1452736199956,"message":"扣扣"}]
     * message : get meeting room message list success
     */

    private long requestid;
    private int code;
    private String message;

    private List<MessageListEntity> messageList;

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageList(List<MessageListEntity> messageList) {
        this.messageList = messageList;
    }

    public long getRequestid() {
        return requestid;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<MessageListEntity> getMessageList() {
        return messageList;
    }

}
