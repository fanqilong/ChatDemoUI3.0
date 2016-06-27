package com.hyphenate.easeui.widget.chatrow;

import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class EaseChatRowVoice extends EaseChatRowFile {

    private ProgressBar voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStutausView;

    private ImageView voicePauseView;
    private ImageView voicePlayView;


    float voiceLen;
    private EaseChatRowVoicePlayClickListener easeChatRowVoicePlayClickListener;


    public EaseChatRowVoice(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);

    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_voice : R.layout.ease_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ProgressBar) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStutausView = (ImageView) findViewById(R.id.iv_unread_voice);

        //自己添加的跟语音有关的 播放的按钮 暂停的按钮
        voicePauseView = ((ImageView) findViewById(R.id.voice_pause));
        voicePlayView = (ImageView) findViewById(R.id.voice_play);


    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        int differentStyleChoose = DifferentStyleChooseUtil.differentStyleChoose(message, position, adapter);
        //==================判断三种布局的设置问题

        switch (differentStyleChoose) {
            case 1:
                if (message.direct() == EMMessage.Direct.SEND) {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_send_progress_horizontal_first));
                } else {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_received_progress_horizontal_first));
                }
                break;
            case 2:
                if (message.direct() == EMMessage.Direct.SEND) {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_send_progress_horizontal_mid));
                } else {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_received_progress_horizontal_mid));
                }
                break;
            case 3:
                if (message.direct() == EMMessage.Direct.SEND) {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_send_progress_horizontal_last));
                } else {
                    voiceImageView.setProgressDrawable(getResources().getDrawable(R.drawable.shape_voice_received_progress_horizontal_last));
                }
                break;
        }

        //==========================================
        int len = voiceBody.getLength();
        if (len > 0) {
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
            //这只语音条的长度
            if (len < 2) {
                voiceLen = 80f;
            } else if (len >= 2 && len < 5) {
                voiceLen = 100f;
            } else if (len >= 5 && len < 10) {
                voiceLen = 120f;
            } else if (len >= 10 && len < 15) {
                voiceLen = 180f;
            } else {
                voiceLen = 200f;
            }
            ViewGroup.LayoutParams layoutParams = voiceImageView.getLayoutParams();
            layoutParams.width = DensityUtil.dip2px(context, voiceLen);
            voiceImageView.setLayoutParams(layoutParams);

        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }


        // FIXME: 16-6-22  存在异步更新进度条的错乱的问题  ？会不会导致handler泄露需要检测  好像解决了
        if (EaseChatRowVoicePlayClickListener.playMsgId != null
                && EaseChatRowVoicePlayClickListener.playMsgId.equals(message.getMsgId()) && EaseChatRowVoicePlayClickListener.isPlaying) {
            if (easeChatRowVoicePlayClickListener != null) {
                Message obtain = Message.obtain();
                obtain.what = EaseChatRowVoicePlayClickListener.UPDATE_PROGRESS;
                obtain.obj = message.getMsgId();
                easeChatRowVoicePlayClickListener.voiceHandler.sendMessage(obtain);

            }
        } else {
            if (easeChatRowVoicePlayClickListener != null) {
                easeChatRowVoicePlayClickListener.voiceHandler.removeCallbacksAndMessages(null);
                voiceImageView.setProgress(0);
            }else {
                voiceImageView.setProgress(0);
            }

        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                readStutausView.setVisibility(View.INVISIBLE);
            } else {
                readStutausView.setVisibility(View.VISIBLE);
            }
            EMLog.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {

                progressBar.setVisibility(View.VISIBLE);
                voicePlayView.setVisibility(View.INVISIBLE);
                setMessageReceiveCallback();
            } else {

                progressBar.setVisibility(View.INVISIBLE);
                voicePlayView.setVisibility(View.VISIBLE);
            }


            return;
        }

        // until here, deal with send voice msg
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {

        easeChatRowVoicePlayClickListener = new EaseChatRowVoicePlayClickListener(message, voiceImageView, readStutausView, adapter, activity);
        easeChatRowVoicePlayClickListener.onClick(bubbleLayout);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EaseChatRowVoicePlayClickListener.currentPlayListener != null && EaseChatRowVoicePlayClickListener.isPlaying) {
            // 停止语音播放
            EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
            easeChatRowVoicePlayClickListener.voiceHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void handleSendMessage() {
        super.handleSendMessage();
        switch (message.status()) {
            case SUCCESS:
                voicePlayView.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                voicePlayView.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                break;
            default:
                break;
        }
    }

//    private int differentStyleChoose(EMMessage message,int position,BaseAdapter adapter) {
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
