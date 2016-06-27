package com.hyphenate.chatuidemo.widget;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.chatrow.DifferentStyleChooseUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatRowVoiceCall extends EaseChatRow{

    private TextView contentvView;
    private ImageView iconView;

    public ChatRowVoiceCall(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_received_voice_call : R.layout.ease_row_sent_voice_call, this);
        // 视频通话
        }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_received_video_call : R.layout.ease_row_sent_video_call, this);
        }
    }

    @Override
    protected void onFindViewById() {
        contentvView = (TextView) findViewById(R.id.tv_chatcontent);
        iconView = (ImageView) findViewById(R.id.iv_call_icon);
    }

    @Override
    protected void onSetUpView() {
        //设置不同的shape
        int differentStyleChoose = DifferentStyleChooseUtil.differentStyleChoose(message, position, adapter);
        switch (differentStyleChoose) {
            case 1:
                if (message.direct()== EMMessage.Direct.SEND){
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_send_frist));
                }else {
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_received_frist));
                }
                break;
            case 2:
                if (message.direct()== EMMessage.Direct.SEND){
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_send_mid));
                }else {
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_received_mid));
                }
                break;
            case 3:
                if (message.direct()== EMMessage.Direct.SEND){
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_send_last));
                }else {
                    bubbleLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_call_received_last));
                }
                break;
        }

        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        //// TODO: 2016/6/24  处理通话显示的文字的颜色  时间的什么鬼的东西 达到和设计样子
        contentvView.setText(txtBody.getMessage());
    }
    
    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onBubbleClick() {
        // TODO: 2016/6/24 这里是可以做到回拨
    }

  

}
