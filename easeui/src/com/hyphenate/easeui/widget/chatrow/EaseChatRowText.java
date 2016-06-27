package com.hyphenate.easeui.widget.chatrow;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.exceptions.HyphenateException;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class EaseChatRowText extends EaseChatRow {

    private TextView contentView;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    public void onSetUpView() {

        //==============================设置不同的shape样式

        int differentStyleChoose = DifferentStyleChooseUtil.differentStyleChoose(message, position, adapter);

        switch (differentStyleChoose) {
            case 1:
                if (message.direct() == EMMessage.Direct.SEND) {
                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_send_common_first));
                } else {
                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_received_common_first));
                }
                break;
            case 2:
                if (message.direct() == EMMessage.Direct.SEND) {

                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_send_common_mid));
                } else {
                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_received_common_mid));

                }
                break;
            case 3:
                if (message.direct() == EMMessage.Direct.SEND) {

                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_send_common_last));
                } else {
                    contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_message_received_common_last));

                }
                break;

        }

        //================================================
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);

        handleTextMessage();
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
//                sendMsgInBackground(message);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        // TODO Auto-generated method stub

    }

//    private int differentStyleChoose(EMMessage message, int position, BaseAdapter adapter) {
//        //==============================设置不同的shape样式
//        int ret = 0;
//        if (position == 0) {
//            //消息的方向
//            if (message.direct() == EMMessage.Direct.SEND) {
//                ret = 1;
//            }
//        } else {
//            if (position + 1 < adapter.getCount()) {
//
//                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
//                EMMessage nextMessage = (EMMessage) adapter.getItem(position + 1);
//
//                if (prevMessage != null && nextMessage != null) {
//                    // 和上 下 来源相同
//                    if (message.direct() == prevMessage.direct() &&
//                            message.direct() == nextMessage.direct()) {
//                        // 和 上下的名字相同
//                        if (message.getFrom().equals(prevMessage.getFrom()) &&
//                                message.getFrom().equals(nextMessage.getFrom())) {
//                            //
//                            ret = 2;
//                        }
//                        // 和上一条不同  和下一条相同
//                        if (!message.getFrom().equals(prevMessage.getFrom()) &&
//                                message.getFrom().equals(nextMessage.getFrom())) {
//                            ret = 1;
//                        }
//                        //和上一条相同  和下一条不同
//                        if (message.getFrom().equals(prevMessage.getFrom()) &&
//                                !message.getFrom().equals(nextMessage.getFrom())) {
//                            ret = 3;
//                        }
//                        //和上一条不同  和吓一跳不同
//                        if (!message.getFrom().equals(prevMessage.getFrom()) &&
//                                !message.getFrom().equals(nextMessage.getFrom())) {
//                            ret = 2;
//                        }
//
//                    }
//                    if (message.direct() != prevMessage.direct() &&
//                            message.direct() != nextMessage.direct()) {
//                        //上下来源都不同
//                        ret = 2;
//                    }
//                    if (message.direct() == prevMessage.direct() &&
//                            message.direct() != nextMessage.direct()) {
//                        //上来源相同 下来源不同
//                        ret = 3;
//
//                    }
//                    if (message.direct() != prevMessage.direct() &&
//                            message.direct() == nextMessage.direct()) {
//                        //上 来源不同 下 来源相同
//                        ret = 1;
//                    }
//
//                }
//
//            } else {
//                //最后一条
//                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
//                if (prevMessage != null &&
//                        message.direct() == prevMessage.direct() &&
//                        message.getFrom().equals(prevMessage.getFrom())) {   //方向相同  名字相同
//
//                    ret = 3;
//                }
//                if (prevMessage != null &&
//                        message.direct() != prevMessage.direct() &&
//                        !message.getFrom().equals(prevMessage.getFrom())) {//方向不同 名字不同
//
//                    ret = 1;
//                }
//
//                if (prevMessage != null &&
//                        message.direct() == prevMessage.direct() &&
//                        !message.getFrom().equals(prevMessage.getFrom())) {//（群聊）方向相同 名字不同
//
//                    ret = 1;
//                }
//            }
//
//
//        }
//        return ret;
//        //================================================
//    }


}
