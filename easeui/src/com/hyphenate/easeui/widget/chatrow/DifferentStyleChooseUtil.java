package com.hyphenate.easeui.widget.chatrow;

import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;

/**
 * Created by fanqilong on 16-6-23.
 */
public final class DifferentStyleChooseUtil {
    private DifferentStyleChooseUtil() {
    }


    public static int differentStyleChoose(EMMessage message, int position, BaseAdapter adapter) {
        //==============================设置不同的shape样式
        int ret = 0;
        if (position == 0) {
            //消息的方向
            if (message.direct() == EMMessage.Direct.SEND) {
                ret = 1;
            }
        } else {
            if (position + 1 < adapter.getCount()) {

                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                EMMessage nextMessage = (EMMessage) adapter.getItem(position + 1);

                if (prevMessage != null && nextMessage != null) {
                    // 和上 下 来源相同
                    if (message.direct() == prevMessage.direct() &&
                            message.direct() == nextMessage.direct()) {
                        // 和 上下的名字相同
                        if (message.getFrom().equals(prevMessage.getFrom()) &&
                                message.getFrom().equals(nextMessage.getFrom())) {
                            //
                            ret = 2;
                        }
                        // 和上一条不同  和下一条相同
                        if (!message.getFrom().equals(prevMessage.getFrom()) &&
                                message.getFrom().equals(nextMessage.getFrom())) {
                            ret = 1;
                        }
                        //和上一条相同  和下一条不同
                        if (message.getFrom().equals(prevMessage.getFrom()) &&
                                !message.getFrom().equals(nextMessage.getFrom())) {
                            ret = 3;
                        }
                        //和上一条不同  和吓一跳不同
                        if (!message.getFrom().equals(prevMessage.getFrom()) &&
                                !message.getFrom().equals(nextMessage.getFrom())) {
                            ret = 2;
                        }

                    }
                    if (message.direct() != prevMessage.direct() &&
                            message.direct() != nextMessage.direct()) {
                        //上下来源都不同
                        ret = 2;
                    }
                    if (message.direct() == prevMessage.direct() &&
                            message.direct() != nextMessage.direct()) {
                        //上来源相同 下来源不同
                        ret = 3;

                    }
                    if (message.direct() != prevMessage.direct() &&
                            message.direct() == nextMessage.direct()) {
                        //上 来源不同 下 来源相同
                        ret = 1;
                    }

                }

            } else {
                //最后一条
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null &&
                        message.direct() == prevMessage.direct() &&
                        message.getFrom().equals(prevMessage.getFrom())) {   //方向相同  名字相同

                    ret = 3;
                }
                if (prevMessage != null &&
                        message.direct() != prevMessage.direct() &&
                        !message.getFrom().equals(prevMessage.getFrom())) {//方向不同 名字不同

                    ret = 1;
                }

                if (prevMessage != null &&
                        message.direct() == prevMessage.direct() &&
                        !message.getFrom().equals(prevMessage.getFrom())) {//（群聊）方向相同 名字不同

                    ret = 1;
                }
            }


        }
        return ret;
        //================================================
    }



}
