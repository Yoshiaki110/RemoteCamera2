package jp.ito.camera;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

public class SensorService implements SensorEventListener {
	private static final String TAG = SensorService.class.getSimpleName();
    private final ToneGenerator _toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
    private SensorManager _sensorManager;	//
    private Sensor        _accelerometer;	//
    private Sensor        _orientationer;		//
//    private Map<String, Integer>	_map = new TreeMap<String, Integer>();

    private float[] _orientation =new float[3];//
    private int _x = 0;
    private int _y = 0;
    private int _z = 0;
    public volatile int _levelX = 0;
    public volatile int _levelY = 0;
    public volatile int _levelZ = 0;
    public volatile int _level = 1;
    public volatile boolean _beep = false;
	private MainService _mainService = null;

    //
    public SensorService(MainService mainService) {
    	_mainService = mainService;
    }

    public void init(SensorManager sensorManager) {
        _sensorManager = sensorManager;

        //
        List<Sensor> list;
        list = _sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (list.size() > 0) {
        	_accelerometer=list.get(0);
        }
        list = _sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (list.size() > 0) {
        	_orientationer=list.get(0);
        }
    }

    //
    public void onSensorChanged(SensorEvent event) {
    	int x = _x;
    	int y = _y;
    	int z = _z;
        //
        for (int i=0;i<3;i++) {
            int w=(int)(10*event.values[i]);
            event.values[i]=(float)(w/10.0f);
        }
        //
        if (event.sensor == _accelerometer) {
        	float[] acceleration = event.values;
            if ( acceleration[0] < 30 && acceleration[0] > -30 ){
            	x = ((_x*8) + (int)(acceleration[0])*12) / 20;
            }
            if ( acceleration[1] < 30 && acceleration[1] > -30 ){
            	y = ((_y*8) + (int)(acceleration[1])*12) / 20;
            }
            if ( acceleration[2] < 30 && acceleration[2] > -30 ){
            	z = ((_z*8) + (int)(acceleration[2])*12) / 20;
            }
 //   		Log.d(TAG, "x:" + x + " y:" + y + " z:" + z);
            _mainService.setSensor(x, y, z, Math.abs(x-_x), Math.abs(y-_y), Math.abs(z-_z));
            int point = 0;
//            if ( _x > _levelX || _x < (_levelX * -1) ) {
//            	point++;
//            }
//            if ( _y > _levelY || _y < (_levelY * -1) ) {
//            	point++;
//            }
//            if ( _z > _levelZ || _z < (_levelZ * -1) ) {
//            	point++;
//            }
            if ( Math.abs(x - _x) > _levelX ) {
            	point++;
            }
            if ( Math.abs(y - _y) > _levelY ) {
            	point++;
            }
            if ( Math.abs(z - _z) > _levelZ ) {
            	point++;
            }
            if ( point >= _level ) {
            	if ( _beep ){
	            	int i = ToneGenerator.TONE_PROP_BEEP;
	            	_toneGenerator.startTone(i);
            	}
                _mainService.setSensor();
//            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//            	long currentTime = System.currentTimeMillis();
//            	String del = sdf.format(new Date(currentTime - Default.PERIOD));
//            	List<String> list = new ArrayList<String>();
//            	for (Map.Entry<String, Integer> e : _map.entrySet()){
//            		if ( e.getKey().compareTo(del) < 0 ){
//            			list.add(e.getKey());
//            		}
//            	}
//            	for (String s : list) {
//            		_map.remove(s);
//            	}
//            	String key = sdf.format(new Date(currentTime));
//            	_map.put(key, point);
          	}
            _x = x;
            _y = y;
            _z = z;
        }
        //
        if (event.sensor==_orientationer) {
        	_orientation = event.values;
        }
    }
	public void onAccuracyChanged(Sensor sensor,int accuracy) {
	}

    public void start() {
        //
        if (_accelerometer!=null) {
            _sensorManager.registerListener(this, 
        	    _accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (_orientationer!=null) {
            _sensorManager.registerListener(this, 
      	        _orientationer,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    public void stop() {
        _sensorManager.unregisterListener(this);
    }
//    public String getMessage(){
//    	StringBuffer sb = new StringBuffer();
//		sb.append("最近３日間の開閉記録\n");
//    	for (Map.Entry<String, Integer> e : _map.entrySet()){
//    		sb.append("\t" + e.getKey() + "\n");
//    	}
//		sb.append("\n\n");
//    	return sb.toString();
//    }
}
