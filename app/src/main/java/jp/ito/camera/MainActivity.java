package jp.ito.camera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private SensorView	_sensorView;		//
	private NoiseView	_noiseView;			//
    private static Activity mInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
        mInstance = this;
//		Context context = getApplicationContext();
//		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(context));
		Context context = this.getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(new CsUncaughtExceptionHandler(context));

		SharedPreferences pref = getSharedPreferences("jp.ito.mimamori",MODE_PRIVATE);

		LinearLayout layoutBig = new LinearLayout(this);
		layoutBig.setBackgroundColor(Color.LTGRAY);
		LinearLayout layoutTop = new LinearLayout(this);
		layoutBig.addView(layoutTop, new LinearLayout.LayoutParams(WC,WC));	//
		layoutBig.setOrientation(LinearLayout.VERTICAL);
		
//		_cameraView = new CameraView(this,savedInstanceState);
//ß		layoutTop.addView(_cameraView,new LinearLayout.LayoutParams(100,100));
		_sensorView = new SensorView(this);
		layoutTop.addView(_sensorView, new LinearLayout.LayoutParams(FP,90));	//
		_noiseView=new NoiseView(this);
		_noiseView.setBackgroundColor(Color.LTGRAY);
		layoutBig.addView(_noiseView, new LinearLayout.LayoutParams(FP,FP));

		setContentView(layoutBig);

		//
		Intent intent = new Intent(this, MainService.class);
		startService(intent);
		IntentFilter filter = new IntentFilter(MainService.ACTION);
		registerReceiver(_receiver, filter);    // これは必要削除しないこと
		//
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		int index = 5;
		String[] strs = new String[index];
		String str = strs[index];
    }
	public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		public MyUncaughtExceptionHandler(Context context) {
		}

		@Override
		public void uncaughtException(Thread th, Throwable t) {
			try {
				saveState(t);//ここでスタックトレースを保存
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	private static File BUG_REPORT_FILE = null;
	static {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		String path = sdcard + File.separator + "bug.txt";
		BUG_REPORT_FILE = new File(path);
	}
	private void saveState(Throwable e) throws FileNotFoundException {
		StackTraceElement[] stacks = e.getStackTrace();//スタックトレース
		File file = BUG_REPORT_FILE;//保存先
		PrintWriter pw = null;
		pw = new PrintWriter(new FileOutputStream(file));
		StringBuilder sb = new StringBuilder();
		int len = stacks.length;
		for (int i = 0; i < len; i++) {
			StackTraceElement stack = stacks[i];
			sb.setLength(0);
			sb.append(stack.getClassName()).append("#");//クラス名
			sb.append(stack.getMethodName()).append(":");//メソッド名
			sb.append(stack.getLineNumber());//行番号
			pw.println(sb.toString());//ファイル書出し
		}
		pw.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(_mainService != null)
			_mainService.OnSaveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(_mainService != null)
			_mainService.OnLoadState(savedInstanceState);
	}

    @Override
    protected void onStart() {
        super.onStart();
		Log.d(TAG, "onStart()");
    }
    //
    @Override
    protected void onResume() {
        super.onResume();
		Log.d(TAG, "onResume()");
//        parameterChanged();
    }

    @Override
    protected void onPause(){
		super.onPause();
		Log.d(TAG, "onPause()");
//        _sensorView.stop();
//		_noiseView.recordPause(true);
	}

    //
    @Override
    protected void onStop() {
        super.onStop();
		Log.d(TAG, "onStop()");
    }    

    @Override
    protected void onDestroy(){
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
//		_noiseView.recordStop();

		unbindService(serviceConnection); //
		unregisterReceiver(_receiver);    // これは必要削除しないこと
		if (_mainService != null){
			_mainService.stopSelf(); //
		}
    }

    static final int MENU_NOISE	=	3;
    static final int MENU_SENSOR	=	4;
    static final int MENU_EXIT	=	9;
    static final int INIT_SETTING	=	99;
 //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //
//        menu.add(Menu.NONE, MENU_NOISE, Menu.NONE, "ノイズ設定");
        menu.add(Menu.NONE, MENU_SENSOR, Menu.NONE, "揺れ設定");
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, "アプリ終了");
        return super.onCreateOptionsMenu(menu);
    }
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
        case MENU_NOISE:
            intent = new Intent(MainActivity.this, NoiseSetting.class);
        	break;
        case MENU_SENSOR:
            intent = new Intent(MainActivity.this, SensorSetting.class);
        	break;
        case MENU_EXIT:
    		unbindService(serviceConnection); //
    		unregisterReceiver(_receiver);     // これは必要削除しないこと
    		_mainService.stopSelf(); //
        	moveTaskToBack(true);
        	Process.killProcess(Process.myPid());
        	break;
        default:
        	break;
        }
        try {
        	startActivityForResult(intent, id);
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "画面遷移に失敗しました。", Toast.LENGTH_LONG).show();
    		Log.d(TAG, "menu画面遷移に失敗しました。");
        }
        return true;
    }
    protected void onActivityResult(int id, int resultCode, Intent data) {
        switch (id) {
        case MENU_NOISE:
        	if (resultCode == RESULT_OK) {
        		noiseSetting();
        	}
        	break;
        case MENU_SENSOR:
        	if (resultCode == RESULT_OK) {
        		senssorSetting();
        	}
        	break;
        case INIT_SETTING:
    		noiseSetting();
    		senssorSetting();
        	break;
        default:
        	break;
        }
    }

//    public void parameterChanged(){
//    	if (_sensorMenuCalled) {
//    		senssorSetting();
//	    	_sensorMenuCalled = false;
//    	}
//    	if (_mailMenuCalled) {
//    		mailSetting();
//	    	_mailMenuCalled = false;
//    	}
//    	if (_xmppMenuCalled) {
//    		xmppSetting();
//    		_xmppMenuCalled = false;
//    	}
//    }
    private void senssorSetting(){
		if (_mainService != null){
	        SharedPreferences pref = getSharedPreferences("jp.ito.mimamori", MODE_PRIVATE);
			String strX = pref.getString(SensorSetting.LEVELX, "0");
			String strY = pref.getString(SensorSetting.LEVELY, "0");
			String strZ = pref.getString(SensorSetting.LEVELZ, "0");
			String str = pref.getString(SensorSetting.LEVEL, "1");
			boolean beep = pref.getBoolean(SensorSetting.BEEP, false);
	    	_mainService.sensorSetting(Integer.parseInt(strX), Integer.parseInt(strY), Integer.parseInt(strZ), Integer.parseInt(str), beep);
		}
    }
    private void noiseSetting(){
    	;
    }
	private class CameraReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.d(TAG, "onReceive() " + context + " " + intent);
			String action = intent.getStringExtra(MainService.BR_ACTION);
			if ( action == null ){
				Log.d(TAG, "onReceive() " + context + " " + intent);
				return;
			}
			if (action.equals(MainService.BR_TIMER)){
//				Toast toast = Toast.makeText(getApplicationContext(), "Time over!", Toast.LENGTH_LONG);
//				toast.show();
			} else if (action.equals(MainService.BR_NOISE)){
				int data = intent.getIntExtra(MainService.BR_NOISE_DATA, 0);
				_noiseView._data = data;
				_noiseView.invalidate();
			} else if (action.equals(MainService.BR_SENSOR)){
				_sensorView._x = intent.getIntExtra(MainService.BR_SENSOR_DATA_X, 0);
				_sensorView._y = intent.getIntExtra(MainService.BR_SENSOR_DATA_Y, 0);
				_sensorView._z = intent.getIntExtra(MainService.BR_SENSOR_DATA_Z, 0);
				_sensorView._diffX = intent.getIntExtra(MainService.BR_SENSOR_DATA_DIFF_X, 0);
				_sensorView._diffY = intent.getIntExtra(MainService.BR_SENSOR_DATA_DIFF_Y, 0);
				_sensorView._diffZ = intent.getIntExtra(MainService.BR_SENSOR_DATA_DIFF_Z, 0);
				_sensorView.invalidate();
			} else if (action.equals(MainService.BR_TOAST)){
				String str = intent.getStringExtra(MainService.BR_TOAST_MESSAGE);
				Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
				toast.show();
                _noiseView._msg = str;
                _noiseView.invalidate();
			}
		}
	}
	
	private MainService _mainService;
	private final CameraReceiver _receiver = new CameraReceiver();    // これは必要削除しないこと
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
//		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			_mainService = ((MainService.CameraBinder)service).getService();
//			_mainService.schedule(10000);
			senssorSetting();
			noiseSetting();
			_mainService.sensorInit((SensorManager)getSystemService(Context.SENSOR_SERVICE));
		}
//		@Override
		public void onServiceDisconnected(ComponentName className) {
			_mainService = null;
		}
	};

    public static Activity getInstance() {
        return mInstance;
    }
}