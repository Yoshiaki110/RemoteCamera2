package jp.ito.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NoiseView extends View {
	public float _data = -1;
    public String _msg = "";

    //
    public NoiseView(Context context) {
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
        
        canvas.drawText("音量 : " + (int)_data, 10, 18*1, paint);
        String str = "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■";
        if ( _data < 10000){
        	canvas.drawText(str.substring(0,(int)_data / 300 + 1), 10, 18*2, paint);
        }
        canvas.drawText(_msg, 10, 18*4, paint);
    }
}
