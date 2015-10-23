package jp.ito.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SensorSetting extends Activity {
	private static final String TAG = SensorSetting.class.getSimpleName();
	static final String LEVELX = "Sensor.level.x";
	static final String LEVELY = "Sensor.level.y";
	static final String LEVELZ = "Sensor.level.z";
	static final String LEVEL = "Sensor.level";
	static final String BEEP = "Sensor.beep";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.sensorsetting);
        setTitle("見守り君 - 揺れ設定");

        SharedPreferences pref = getSharedPreferences("jp.ito.mimamori",MODE_PRIVATE);
		String str = pref.getString(LEVELX, "0");
		EditText editLevelX = (EditText)findViewById( R.id.editSensorLevelX );
		editLevelX.setText(str);
		str = pref.getString(LEVELY, "0");
		EditText editLevelY = (EditText)findViewById( R.id.editSensorLevelY );
		editLevelY.setText(str);
		str = pref.getString(LEVELZ, "0");
		EditText editLevelZ = (EditText)findViewById( R.id.editSensorLevelZ );
		editLevelZ.setText(str);
		str = pref.getString(LEVEL, "1");
		EditText editLevel = (EditText)findViewById( R.id.editSensorLevel );
		editLevel.setText(str);

		boolean beep = pref.getBoolean(BEEP, false);
		CheckBox checkBox = (CheckBox)findViewById( R.id.checkBoxSensorBeep );
		checkBox.setChecked(beep);
		Button btn  = (Button) findViewById(R.id.button);
        btn.setOnClickListener(btnListener);	//���X�i�̓o�^
    }
    private OnClickListener btnListener = new OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.button:
                SharedPreferences pref = getSharedPreferences("jp.ito.mimamori",MODE_PRIVATE);
        		Editor e = pref.edit();
        		String str = ((EditText)findViewById( R.id.editSensorLevelX )).getText().toString();
        		e.putString(LEVELX, str);
        		str = ((EditText)findViewById( R.id.editSensorLevelY )).getText().toString();
        		e.putString(LEVELY, str);
        		str = ((EditText)findViewById( R.id.editSensorLevelZ )).getText().toString();
        		e.putString(LEVELZ, str);
        		str = ((EditText)findViewById( R.id.editSensorLevel )).getText().toString();
        		e.putString(LEVEL, str);
        		boolean beep = ((CheckBox)findViewById( R.id.checkBoxSensorBeep )).isChecked();
        		e.putBoolean(BEEP, beep);
        		e.commit();
        		Intent i = new Intent();
        		setResult(RESULT_OK, i);
        		finish();
                break;
            }
        }
    };
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
    }

    @Override
    protected void onPause(){
		super.onPause();
		Log.d(TAG, "onPause()");
	}

    //
    @Override
    protected void onStop() {
        super.onStop();
		Log.d(TAG, "onStop()");
    }    

    @Override
    protected void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}
}
