package jp.ito.camera;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class NoiseService implements Runnable {
	// Changing the sample resolution changes sample type. byte vs. short.
	private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final String TAG = NoiseView.class.getSimpleName();
//    private Map<String, Integer>	_map = new TreeMap<String, Integer>();

	private volatile int _frequency = 8000;
	private volatile int _channelConfiguration;
	private volatile boolean _isRecording;
	private Thread _recorderThread;
	private MainService _mainService = null;
	
    //
    public NoiseService(MainService mainService) {
    	_mainService = mainService;
		_channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    }

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(_frequency,
				  _channelConfiguration, AUDIOENCODING) * 2;
		AudioRecord recordInstance = new AudioRecord(
				MediaRecorder.AudioSource.MIC, _frequency,
				_channelConfiguration, AUDIOENCODING,bufferSize);
		short[] tempBuffer = new short[bufferSize];
		for (int i = 0; i < 3; i++){
			try {
				recordInstance.startRecording();		// ������IllegalStateException���ł�
				break;
			} catch (IllegalStateException e1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
		while (this._isRecording) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			// Are we paused?
			bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				Log.e(TAG, "read() returned AudioRecord.ERROR_INVALID_OPERATION");
//				continue;
				return;
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				Log.e(TAG, "read() returned AudioRecord.ERROR_BAD_VALUE");
//				continue;
				return;
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				Log.e(TAG, "read() returned AudioRecord.ERROR_INVALID_OPERATION");
//				continue;
				return;
			}

			long sum = 0;
			for(int i = 0; i < bufferSize; i++){
				sum += Math.abs(tempBuffer[i]);
			}
			float avg = sum / bufferSize;
	        _mainService.setNoise((int)avg);

		}
		recordInstance.stop();
		recordInstance.release();
	}

	public void recordStart(){
		if (_recorderThread != null){
			recordStop();
		}
		_recorderThread = new Thread(this);
		_isRecording = true;
		_recorderThread.start();
	}

	public void recordStop(){
		_isRecording = false;
		try {
			if (_recorderThread != null){
				_recorderThread.join();
			}
			_recorderThread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		Log.d(TAG, "record stop!");
	}
}
