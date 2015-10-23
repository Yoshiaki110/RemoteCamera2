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
import android.widget.EditText;

public class NoiseSetting extends Activity {
	private static final String TAG = NoiseSetting.class.getSimpleName();
	static final String LEVEL = "Noise.level";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.noisesetting);
        setTitle("見守り君 - ノイズ設定");

        SharedPreferences pref = getSharedPreferences("jp.ito.mimamori",MODE_PRIVATE);
		String strAddress = pref.getString(LEVEL, "6");
		EditText editAddress = (EditText)findViewById( R.id.editNoiseLevel );
		editAddress.setText(strAddress);
		Button btn  = (Button) findViewById(R.id.button);
        btn.setOnClickListener(btnListener);	//���X�i�̓o�^
    }
    private OnClickListener btnListener = new OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.button:
                SharedPreferences pref = getSharedPreferences("jp.ito.mimamori",MODE_PRIVATE);
        		Editor e = pref.edit();
        		e.putString(LEVEL, ((EditText)findViewById( R.id.editNoiseLevel )).getText().toString());
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
