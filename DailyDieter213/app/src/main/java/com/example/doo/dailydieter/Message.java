package com.example.doo.dailydieter;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;
    public static final int TYPE_SEND = 3;
    public static final int TYPE_GET = 4;

    private int mType;
    private String mMessage;
    private String mUsername;
    private String mDate;
    private String mTime;
    private String mGroupID;

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() {
        return mUsername;
    };

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getGroupID() {
        return mGroupID;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mDate;
        private String mTime;
        private String mGroupID;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder time(String time) {
            mTime = time;
            return this;
        }

        public Builder groupID(String groupID) {
            mGroupID = groupID;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mDate = mDate;
            message.mTime = mTime;
            message.mGroupID = mGroupID;
            return message;
        }
    }
}
