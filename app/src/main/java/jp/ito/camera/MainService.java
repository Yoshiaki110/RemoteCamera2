package jp.ito.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	private static final String TAG = MainService.class.getSimpleName();
	public static final String BR_ACTION = "action";
	public static final String BR_TIMER = "timer";
	public static final String BR_NOISE = "noise";
	public static final String BR_NOISE_DATA = "noise.data";
	public static final String BR_CAMERA = "camera";
	public static final String BR_SENSOR = "sensor";
	public static final String BR_SENSOR_DATA_X = "sensor.data.x";
	public static final String BR_SENSOR_DATA_Y = "sensor.data.y";
	public static final String BR_SENSOR_DATA_Z = "sensor.data.z";
	public static final String BR_SENSOR_DATA_DIFF_X = "sensor.data.diff.x";
	public static final String BR_SENSOR_DATA_DIFF_Y = "sensor.data.diff.y";
	public static final String BR_SENSOR_DATA_DIFF_Z = "sensor.data.diff.z";
	public static final String BR_TOAST = "toast";
	public static final String BR_TOAST_MESSAGE = "toast.message";
	private static NoiseService		_noiseService = null;
	private static SensorService	_sensorService = null;
//    private static MobileServiceClient _mClient = null;
    private static int _maxNoise = 0;
    private static int _sensorCnt = 0;

	class CameraBinder extends Binder {
		MainService getService() {
			return MainService.this;
		}
	}
	
	public static final String ACTION = "Camera Service";
	private Timer _timer;

	public MainService(){
		Log.d(TAG, "MainService()");
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate()");
		super.onCreate();
//		Toast toast = Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT);
//		toast.show();
		if ( _noiseService == null ){
			_noiseService = new NoiseService(this);
			_noiseService.recordStart();
		}
		if ( _sensorService == null ){
			_sensorService = new SensorService(this);
		}
        if ( _timer == null ){
            schedule(1000 * 60 * 60);
//            schedule(1000 * 60);
        }
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart()");
		super.onStart(intent, startId);
//		Toast toast = Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT);
//		toast.show();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
//		Toast toast = Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT);
//		toast.show();
		if (_timer != null) {
			_timer.cancel();
			_timer = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind()");
//		Toast toast = Toast.makeText(getApplicationContext(), "onBind()", Toast.LENGTH_SHORT);
//		toast.show();
		return new CameraBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onRebind()");
//		Toast toast = Toast.makeText(getApplicationContext(), "onRebind()", Toast.LENGTH_SHORT);
//		toast.show();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind()");
//		Toast toast = Toast.makeText(getApplicationContext(), "onUnbind()", Toast.LENGTH_SHORT);
//		toast.show();
		return true; //
	}
	
	//
	public void toast(String str){
		Log.d(TAG, "toast(" + str + ")");
		Intent intent = new Intent(ACTION);
		intent.putExtra(BR_ACTION, BR_TOAST);
		intent.putExtra(BR_TOAST_MESSAGE, str);
		sendBroadcast(intent);
	}
	public void setNoise(int data){
//		Log.d(TAG, "setNoise()");
		Intent intent = new Intent(ACTION);
		intent.putExtra(BR_ACTION, BR_NOISE);
		intent.putExtra(BR_NOISE_DATA, data);
		sendBroadcast(intent);
        if (_maxNoise < data) {
            _maxNoise = data;
        }
	}
	public void setSensor(int x, int y, int z, int diffX, int diffY, int diffZ){
//		Log.d(TAG, "setNoise()");
		Intent intent = new Intent(ACTION);
		intent.putExtra(BR_ACTION, BR_SENSOR);
		intent.putExtra(BR_SENSOR_DATA_X, x);
		intent.putExtra(BR_SENSOR_DATA_Y, y);
		intent.putExtra(BR_SENSOR_DATA_Z, z);
		intent.putExtra(BR_SENSOR_DATA_DIFF_X, diffX);
		intent.putExtra(BR_SENSOR_DATA_DIFF_Y, diffY);
		intent.putExtra(BR_SENSOR_DATA_DIFF_Z, diffZ);
		sendBroadcast(intent);
	}
    public void setSensor(){
        ++_sensorCnt;
    }
//	public void setCamera(Camera camera){
//		Log.d(TAG, "setCamera()");
//	}
	public void schedule(long delay) {
		Log.d(TAG, "schedule()");
		if (_timer != null) {
			_timer.cancel();
		}
		_timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			public void run() {
                Log.d(TAG, "TimerTask()");
                send();
//				Intent intent = new Intent(ACTION);
//				intent.putExtra(BR_ACTION, BR_TIMER);
//				sendBroadcast(intent);
			}
		};
		_timer.schedule(timerTask, delay, delay);
	}
//	public void getNoiseMessage(){
//		//String str = _noiseService.getMessage();
//	}
	public void sensorSetting(int levelX, int levelY, int levelZ, int level, boolean beep){
		_sensorService._levelX = levelX;
		_sensorService._levelY = levelY;
		_sensorService._levelZ = levelZ;
		_sensorService._level = level;
		_sensorService._beep = beep;
	}
	public void OnSaveState(Bundle outState){
	}
	public void OnLoadState(Bundle savedInstanceState){
	}
	public void sensorInit(SensorManager sensorManager){
		_sensorService.init(sensorManager);
		_sensorService.start();
	}
    public void send() {
        String urlString = "http://mimamorikun.azure-mobile.net/tables/Item";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd HH:mm");
        String str = sdf2.format(date);
        try {
            URL url = new URL(urlString);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);//POST可能にする

            uc.setRequestProperty("Accept", "application/json"); // ヘッダを設定
            uc.setRequestProperty("Content-Type", "application/json"); // ヘッダを設定
            uc.setRequestProperty("X-ZUMO-APPLICATION", "YWnkYqPoSShuMZwUdzilHizNIMtgvK92"); // アプリケーションキーを設定
            OutputStream os = uc.getOutputStream();//POST用のOutputStreamを取得

            String strDate = sdf.format(date);
            Log.d(TAG, "Insert " + strDate + " _maxNoise:" + _maxNoise + " sensorCnt:" + _sensorCnt);
            String postStr = "{\"Text\":\"" + strDate + "\",\"Noise\":\"" + _maxNoise + "\",\"Sensor\":\"" + _sensorCnt + "\"}";//POSTするデータ
            PrintStream ps = new PrintStream(os);
            ps.print(postStr);//データをPOSTする
            ps.close();

            InputStream is = uc.getInputStream();//POSTした結果を取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = reader.readLine()) != null) {
                Log.d(TAG, s);
            }
            reader.close();
            _maxNoise = 0;
            _sensorCnt = 0;
            str += " 通信成功";
        } catch (MalformedURLException e) {
            str += " 通信失敗";
            Log.e(TAG, "Invalid URL format: " + urlString);
        } catch (IOException e) {
            str += " 通信失敗";
            Log.e(TAG, "Can't connect to " + urlString);
        }
        toast(str);
    }
}
