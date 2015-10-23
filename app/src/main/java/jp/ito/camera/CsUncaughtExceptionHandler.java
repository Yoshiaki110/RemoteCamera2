package jp.ito.camera;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class CsUncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private static Context sContext = null;
    private static final String BUG_FILE = "BugReport";
    private static final UncaughtExceptionHandler sDefaultHandler
            = Thread.getDefaultUncaughtExceptionHandler();

    public CsUncaughtExceptionHandler(Context context){
        sContext = context;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        PrintWriter pw = null;
        try {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            String path = sdcard + File.separator + BUG_FILE;
//            pw = new PrintWriter(sContext.openFileOutput(path, Context.MODE_WORLD_READABLE));
            pw = new PrintWriter(new FileOutputStream(path, true));
            ex.printStackTrace(pw);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) pw.close();
        }
        sDefaultHandler.uncaughtException(thread, ex);
    }

    public static void SendBugReport(final Activity activity) {
        final File bugfile = activity.getFileStreamPath(BUG_FILE);
        if (!bugfile.exists()) {
            return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("ERROR");
        alert.setMessage("send mail");
        alert.setPositiveButton("Post", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendMail(activity,bugfile);
            }});
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    private static void SendMail(final Activity activity,File bugfile){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(bugfile));
            String str;
            while((str = br.readLine()) != null){
                sb.append(str +"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + "xxx@xxxx.xx.xx"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "ÅyBugReportÅz" + R.string.app_name );
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        activity.startActivity(intent);
        bugfile.delete();
    }

}