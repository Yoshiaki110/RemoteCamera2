package jp.ito.camera;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

//
public class SensorView extends View {
//    private Map<String, Integer>	_map = new TreeMap<String, Integer>();
    public volatile int _x = 0;
    public volatile int _y = 0;
    public volatile int _z = 0;
    public volatile int _diffX = 0;
    public volatile int _diffY = 0;
    public volatile int _diffZ = 0;
//    public int _level = 6;

    //
    public SensorView(Context context) {
        super(context);
        setBackgroundColor(Color.LTGRAY);
    }
    
    //
    @Override 
    protected void onDraw(Canvas canvas) {
        //
        Paint paint=new Paint();       
        paint.setAntiAlias(true);
        paint.setTextSize(12);
        
        //
        canvas.drawText("X軸加速度 : "+_x+" ("+_diffX+")",10,14*1,paint);
        canvas.drawText(getBar((int)_x), 10, 14*2, paint);
        canvas.drawText("Y軸加速度 : "+_y+" ("+_diffY+")",10,14*3,paint);
        canvas.drawText(getBar((int)_y), 10, 14*4, paint);
        canvas.drawText("Z軸加速度 : "+_z+" ("+_diffZ+")",10,14*5,paint);
        canvas.drawText(getBar((int)_z), 10, 14*6, paint);
//        canvas.drawText("方位::"    +_orientation[0], 10,14*4,paint);
//        canvas.drawText("ピッチ:"   +_orientation[1], 10,14*5,paint);
//        canvas.drawText("ロール:"   +_orientation[2], 10,14*6,paint);
    }

    private String getBar(int level){
    	if ( level < -5 ) {
    		return "□□□□□□";
    	} else if ( level < -4 ) {
    		return "　□□□□□";
    	} else if ( level < -3 ) {
    		return "　　□□□□";
    	} else if ( level < -2 ) {
    		return "　　　□□□";
    	} else if ( level < -1 ) {
    		return "　　　　□□";
    	} else if ( level < 0 ) {
    		return "　　　　　□";
    	} else if ( level < 1 ) {
    		return "　　　　　　■";
    	} else if ( level < 2 ) {
    		return "　　　　　　■■";
    	} else if ( level < 3 ) {
    		return "　　　　　　■■■";
    	} else if ( level < 4 ) {
    		return "　　　　　　■■■■";
    	} else if ( level < 5 ) {
    		return "　　　　　　■■■■■";
    	} else if ( level < 6 ) {
    		return "　　　　　　■■■■■■";
    	} else if ( level < 7 ) {
    		return "　　　　　　■■■■■■■";
    	} else if ( level < 8 ) {
    		return "　　　　　　■■■■■■■■";
    	} else if ( level < 9 ) {
    		return "　　　　　　■■■■■■■■■";
    	}
    	return "　　　　　　■■■■■■■■■■";
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
