package com.pareto.reflector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static Context m_context;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        List<PackageInfo> info = getInstalledApps();
        Log.v("hello world" , String.valueOf(info.size()));
        for(int i = 0 ; i < info.size() ; i++) {
            String tmp = info.get(i).packageName;
            Log.v("print PackageName : ", tmp);
        }

        tv.setText(stringFromJNI());
    }



    private static Context getAppContext() {
        if (m_context == null) {
            synchronized (MainActivity.class) {
                if (m_context == null) {
                    try {
                        Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                        Method method = ActivityThread.getMethod("currentActivityThread");
                        //获取到当前线程
                        Object currentActivityThread = method.invoke(ActivityThread);
                        //获取Context
                        Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                        m_context = (Context) method2.invoke(currentActivityThread);
                    } catch (Exception e) {
                        m_context = null;
                    }
                }
            }
        }
        return m_context;
    }

    public static List<PackageInfo> getInstalledApps() {
        Context appContext = getAppContext();
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = appContext.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo pak : paklist) {
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
