package com.jj.investigation.openfire.smack;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 播放音频管理类
 * Created by ${R.js} on 2018/1/15.
 */

public class RecordManager {

    /**
     * 播放小文件音频，使用SoundPool
     * @param filePath 音频文件地址
     */
    public static void playAudio(Context context, String filePath) {
        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int soundId = soundPool.load(filePath, 1);
        // 添加监听，加载完成后播放
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                soundPool.play(soundId, volume,volume, 1, 0, 1);
            }
        });
    }

}
