package com.jj.investigation.openfire.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.utils.FileManager;
import com.jj.investigation.openfire.utils.ToastRecord;
import com.jj.investigation.openfire.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * 录音按钮
 * Created by ${R.js} on 2017/12/27.
 */

public class VoiceRecordButton extends Button {

    private MediaRecorder mediaRecorder;
    // 开始录音的时间
    private long startTime = 0;
    // 是否正在录音
    private boolean isRecording = true;
    private boolean permissionRecord;
    private boolean permissionWrite;
    private VoiceRecordThread recordThread;
    private File recordFile;
    private boolean permissionRead;


    public VoiceRecordButton(Context context) {
        this(context, null);
    }

    public VoiceRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkMyPermission();
    }

    /**
     * 动态获取权限
     */
    private void checkMyPermission() {
        permissionRecord = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        permissionRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!permissionRecord || !permissionWrite || !permissionRead) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                    "android.permission.RECORD_AUDIO", // 音量权限
                    "android.permission.WRITE_EXTERNAL_STORAGE", // 读写权限
                    "android.permission.READ_EXTERNAL_STORAGE"}, 1);
        } else {
            permissionRecord = true;
            permissionWrite = true;
            permissionRead = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 每次录制要要检查权限
        checkMyPermission();
        if (permissionRecord && permissionWrite) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 开始录音
                    startRecording();
                    break;
                case MotionEvent.ACTION_UP: // 取消录音
                case MotionEvent.ACTION_CANCEL:
                    stopRecording();
                    break;
                default:
                    break;
            }
        } else {
            ToastUtils.showLongToast("请赋予录音和读写权限");
        }
        return super.onTouchEvent(event);
    }

    /**
     * 开始录音:
     * 使用android自带的录音功能
     */
    private void startRecording() {
        // 创建录音对象
        mediaRecorder = new MediaRecorder();
        // 指定音源，使用麦克风录音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 指定录音文件的格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 设置录音的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        // 指定录音文件保存的目录
        final File file = FileManager.createFile("voice");
        long mill = System.currentTimeMillis();
        // 录音文件
        recordFile = new File(file, "voice_" + mill);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        // 开启录音
        try {
            // 加载资源
            mediaRecorder.prepare();
            // 开始录音
            startTime = System.currentTimeMillis();
            mediaRecorder.start();
            isRecording = true;
            // 录音是耗时的，开启子线程来录音
            recordThread = new VoiceRecordThread();
            recordThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音并释放资源
     */
    private void stopRecording() {
        isRecording = false;
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();

            long allTime = (System.currentTimeMillis() - startTime) / 1000;
            if (allTime < 1) {
                ToastUtils.showLongToast("录制时间过短");
            } else {
                if (onVoiceRecordListener != null) {
                    onVoiceRecordListener.onRecordEnd(recordFile, allTime);
                }
            }
        }
    }

    /**
     * 通过handler显示音量大小的Toast
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastRecord.showToast(getContext(), msg.what);
        }
    };

    /**
     * 录音线程：
     * 监听麦克风音量大小
     */
    public class VoiceRecordThread extends Thread {
        @Override
        public void run() {
            super.run();
            // 如果正在录音则获取麦克风的音量
            while (isRecording) {
                try {
                    // 每隔100毫秒获取一次音量大小
                    Thread.sleep(100);
                    // 获取声音分贝
                    // 根据这个相对值，不断替换显示的图片
                    int x = 0;
                    try {
                        // 可能会报这个错误：java.lang.RuntimeException: getMaxAmplitude failed.
                        // 但是该错误出现有很大的不确定性，一会有一会没有，而且要跟机型有关，所以捕获这个异常
                        x = mediaRecorder.getMaxAmplitude();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // if (x != 0) {
                    // 根据分贝大小计算得到一个音量的相对值
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (x != 0) {
                        if (f < 10) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_1);
                        } else if (f < 15) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_2);
                        } else if (f < 20) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_3);
                        } else if (f < 25) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_4);
                        } else if (f < 30) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_5);
                        } else if (f < 35) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_6);
                        } else if (f < 40) {
                            handler.sendEmptyMessage(R.drawable.recording_indicator_voice_7);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 录音监听
     */
    public interface OnVoiceRecordListener {
        /**
         *
         * @param recordFile 录音文件
         * @param duration 录音文件的时长
         */
        void onRecordEnd(File recordFile, long duration);
    }
    private OnVoiceRecordListener onVoiceRecordListener;
    public  void setOnVoiceRecordListener(OnVoiceRecordListener onVoiceRecordListener) {
        this.onVoiceRecordListener = onVoiceRecordListener;
    }
}
