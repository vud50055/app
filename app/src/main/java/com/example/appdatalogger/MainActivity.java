package com.example.appdatalogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.appdatalogger.pages.chart_page_layout;
import com.example.appdatalogger.pages.file_page;
import com.example.appdatalogger.pages.home_page_layout;
import com.example.appdatalogger.pages.setting_page;
import com.example.appdatalogger.pages.shutdown_page;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements setting_page.Seting_Page_Callback,
        View.OnKeyListener, chart_page_layout.Chart_Page_Callback, shutdown_page.Power_Page_Callback, home_page_layout.Home_Page_Callback {
//public class MainActivity extends AppCompatActivity implements View.OnKeyListener
    String TAG = "AppDataLogger";
    boolean DEBUG = true;
    Button bt_batdau, bt_dung;
    ImageButton ibt_home, ibt_setting, ibt_chart, ibt_file, ibt_shutdown;
    TextView tv_thoigiando, tv_sensordata;
    Spinner sp_selectsensordisplay;

    /*************** Service ************/
    Intent DataloggerWifiIntent;
    DataloggerWifiService mWifiService;
    boolean mWifiBound;
    /***********************************/

    /******** Fragment FramePage ********/
    home_page_layout homepage;
    setting_page settingpage;
    chart_page_layout chartpage;
    file_page filepage;
    shutdown_page shutdowpage;

    /************************************/

    /********** Seting info **********/
    Settings_Configure_Class setting_info;

    Handler timer_Measure;
    /********************************/

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /********** Seting info **********/
        timer_Measure = new Handler();
        setting_info = new Settings_Configure_Class();
        //Setting Page
        setting_info.Setting_SetSampleRate(200);
        setting_info.Setting_SetTimeMeasure(0);
        //Chart Page
        setting_info.Chart_SetDisplayMode("Đồ thị");
        setting_info.Chart_SetAxisXSelect("N");
        setting_info.Chart_SetAxisXType("Zoom");

        /********************************/

        /************* Button ************/
        bt_batdau = findViewById(R.id.bt_BatDau);
        bt_dung = findViewById(R.id.bt_Dung);
        tv_thoigiando = findViewById(R.id.tv_ThoiGianDo);
        tv_sensordata = findViewById(R.id.tv_SesorData);
        sp_selectsensordisplay = findViewById(R.id.sp_SelectSensorDisplay);

        ibt_home = findViewById(R.id.ibt_Home);
        ibt_setting = findViewById(R.id.ibt_Setting);
        ibt_chart = findViewById(R.id.ibt_Chart);
        ibt_file = findViewById(R.id.ibt_File);
        ibt_shutdown = findViewById(R.id.ibt_Shutdown);

        /*********************************/


        /******** Start Service and bound *********/
        DataloggerWifiIntent = new Intent(this, DataloggerWifiService.class);
        startService(DataloggerWifiIntent);
        bindService(DataloggerWifiIntent, myconnection, Context.BIND_AUTO_CREATE);

        //Start Listning Intent from Service
        registerReceiver(mBoardcastFromWifiService, new IntentFilter(DataloggerWifiService.BROADCAST_ACTION));
        /*****************************************/


        /************* Button Event ******************/
        bt_batdau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*************** Setup timer ******************/
                int timemeasure_ms = 0;
                if(setting_info != null)
                    timemeasure_ms = setting_info.Setting_GetTimeMeasure();
                if(timemeasure_ms > 0){
                    if(timer_Measure != null){
                        timer_Measure.removeCallbacksAndMessages(null);
                        timer_Measure = null;
                    }
                    timer_Measure = new Handler();
                    Log.i(TAG, "Setting timer to stop measuring");
                    timer_Measure.postDelayed(timer_Measure_Handler, setting_info.Setting_GetTimeMeasure());
                }
                /**********************************************/

                handle_batdau_function();
            }
        });

        bt_dung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*************** Setup timer ******************/
                if(timer_Measure != null){
                    timer_Measure.removeCallbacksAndMessages(null);
                    timer_Measure = null;
                }

                /**********************************************/

                handle_dung_function();
            }
        });

        /********************************************/

        /************* Image Button Press Handle **************/
        ibt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(1);
            }
        });
        ibt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(2);
            }
        });
        ibt_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(3);
            }
        });
        ibt_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(4);
            }
        });
        ibt_shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(5);
            }
        });
        /*****************************************************/
    }

    @Override
    protected void onStop() {
        stopService(DataloggerWifiIntent);
        if(mWifiBound == true){
            unbindService(myconnection);
            mWifiBound = false;
        }

        if(mBoardcastFromWifiService != null) {
            unregisterReceiver(mBoardcastFromWifiService);
        }
        super.onStop();
    }

    /************ Implement method from Fragment ***********/

    @Override
    public void displayDataPeriod(String item, int data){
        String item_selected = (String) sp_selectsensordisplay.getSelectedItem();

        Log.i(TAG, "Select sensor = " + item_selected + ", item send data = " + item + ", data = " + String.valueOf(data));
        if(item.equals(item_selected) == true){
            tv_sensordata.setText(String.valueOf(data));
        }
    }

    @Override
    public void Power_Scan_SensorEvent(){
        SensorData sensors[];
        sensors = mWifiService.GetSensorsAvailable_v2();

        Log.i(TAG, "Callback request scan Sensor from Scan Button");
            for(int i = 0; i < mWifiService.GetMaxSensorWifiSupport(); i++){
                if(sensors[i] != null){
                    sensors[i].Sensor_SetConnected(true);
                }
            }

    }
    @Override
    public void Chart_Save_Event(String display_mode, String axisx_select, String axisx_type){
        if(DEBUG){
            Log.i(TAG, "Callback from Chart Page: display_mode = " + String.valueOf(display_mode)  + ", axisx_select = " + String.valueOf(axisx_select)
                    + ", axisx_type = " + String.valueOf(axisx_type));
        }
        setting_info.Chart_SetDisplayMode(display_mode);
        setting_info.Chart_SetAxisXSelect(axisx_select);
        setting_info.Chart_SetAxisXType(axisx_type);
    }
    @Override
    public void Setting_Save_Event(int samplemode, int samplerate, int timemeasure) {
        if(DEBUG)
            Log.i(TAG, "Callback from Setting: samplemode = " + String.valueOf(samplemode)  + ", samplerate = " + String.valueOf(samplerate)
                + ", timemeasure = " + String.valueOf(timemeasure));

        setting_info.Setting_SetSampleMode(samplemode);
        setting_info.Setting_SetSampleRate(samplerate);
        setting_info.Setting_SetTimeMeasure(timemeasure * 1000);
        if(timemeasure == 0){
            tv_thoigiando.setText("None");
        }else {
            tv_thoigiando.setText(String.valueOf(timemeasure));
        }
    }
    public void handle_dung_function(){
        if(homepage == null)
            homepage = new home_page_layout(mWifiService);

        homepage.displayDataOnGraph_v2(false);
    }
    public void handle_batdau_function(){
        if(homepage == null)
            homepage = new home_page_layout(mWifiService);
        switchFragment(1);
        homepage.displayDataOnGraph_v2(true);
    }

    private Runnable timer_Measure_Handler = new Runnable() {
        public void run() {
            Log.i(TAG, "Timeout Happen ==>> Stop measuring");
            handle_dung_function();
        }
    };

    private void switchFragment(int which){
        Fragment fragment = null;

        switch (which){
            case 2:{//Setting Page
                if(settingpage == null)
                    settingpage = new setting_page(mWifiService);
                fragment = settingpage;
                break;
            }
            case 3:{//Chart Page
                if(chartpage == null)
                    chartpage = new chart_page_layout();
                fragment = chartpage;
                break;
            }
            case 4:{//File Page
                if(filepage == null)
                    filepage = new file_page();
                fragment = filepage;
                break;
            }
            case 5:{//Shutdown Page
                if(shutdowpage == null)
                    shutdowpage = new shutdown_page();
                fragment = shutdowpage;
                break;
            }
            case 1://Home Page
            default: {
                if (homepage == null)
                    homepage = new home_page_layout(mWifiService);
                /******** Pass arg into HOME PAGE Fragment **********/
                Bundle bundle = new Bundle();
                bundle.putString("Chart_DisplayUpdate_MS", String.valueOf(mWifiService.getSampleRate()));
                bundle.putString("Chart_DisplayMode", setting_info.Chart_GetDisplayMode());
                bundle.putString("Chart_AxisXSelect", setting_info.Chart_GetAxisXSelect());
                bundle.putString("Chart_AxisXType", setting_info.Chart_GetAxisXType());
                homepage.setArguments(bundle);

                /***************************************************/
                fragment = homepage;
                break;
            }
        }
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    /********************************************************/

    /*********** Implement method from Keyevent **************/
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent =======: " + String.valueOf(event.getKeyCode()));
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_BUTTON_START://Start
                /*************** Setup timer ******************/
                Log.i(TAG, "Handle Button START PRESS");
                int timemeasure_ms = 0;
                if(setting_info != null)
                    timemeasure_ms = setting_info.Setting_GetTimeMeasure();
                if(timemeasure_ms > 0){
                    if(timer_Measure != null){
                        timer_Measure.removeCallbacksAndMessages(null);
                        timer_Measure = null;
                    }
                    timer_Measure = new Handler();
                    Log.i(TAG, "Setting timer to stop measuring");
                    timer_Measure.postDelayed(timer_Measure_Handler, setting_info.Setting_GetTimeMeasure());
                }
                /**********************************************/

                handle_batdau_function();
                break;
            case KeyEvent.KEYCODE_BREAK://Use KEYCODE for STOP
                Log.i(TAG, "Handle Button STOP PRESS");
                /*************** Setup timer ******************/
                if(timer_Measure != null){
                    timer_Measure.removeCallbacksAndMessages(null);
                    timer_Measure = null;
                }

                /**********************************************/

                handle_dung_function();
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.i(TAG, "onKey =======: " + String.valueOf(keyCode));
        return false;
    }


    /********************************************************/

    /****************** Update Item list ********************/
    private void updateItemListSensorConnected(){
        List<String> list = new ArrayList<String>();
        SensorData sensors[];
        SensorsList sList = new SensorsList();

        sensors = mWifiService.GetSensorsAvailable_v2();
        for(int i = 0; i < mWifiService.GetMaxSensorWifiSupport(); i++) {
            if (sensors[i] != null) {
                String item = sList.getSensorName(sensors[i].Sensor_ID) + "(" + String.valueOf(sensors[i].Sensor_ID) + ")";
                list.add(item);
                Log.i(TAG, "Add item - " + item);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_selectsensordisplay.setAdapter(dataAdapter);


    }



    /********************************************************/



    /********************* Register Intent from DataloggerWifiService *********/
    private void showDialogRequestConnection(SensorData sensor, int ID){
        AlertDialog.Builder builder;
        SensorsList sList = new SensorsList();
        handle_dung_function();//add to debug
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo kết nối");
        builder.setMessage("Phát hiện Sensor " + sList.getSensorName(ID) + " yêu cầu kết nối");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /***** Init Series for sensor and add to display *****/
//                        Log.i(TAG, "Create series for sensor: " + String.valueOf(sensor.Sensor_GetID()));
                        mWifiService.SetConnected(ID, true);
                        mWifiService.CreateGraphSeriesForEachSensor();
                        homepage.homePage_AddSeries(sensor.Sensor_GraphSeries);


                        updateItemListSensorConnected();//Update list sensor connected to Item Spiner
                        /*****************************************************/
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mWifiService.SetConnected(ID, false);
//                        sensor.Sensor_SetConnected(false);
                        sensor.Sensor_SetStartStoreAndDisplay(false);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ServiceConnection myconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataloggerWifiService.DataloggerWifiServiceBinder binder = (DataloggerWifiService.DataloggerWifiServiceBinder) service;
            mWifiService = binder.getService();
            mWifiBound = true;

            switchFragment(1);//Switch Home Page when Init success
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mWifiBound = false;
        }
    };

    private BroadcastReceiver mBoardcastFromWifiService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int sensorID = intent.getIntExtra("SensorID", -1);

            if(DEBUG)
                Log.i(TAG, "Activity Received Intent from Service, " + String.valueOf(sensorID));
            if(sensorID >= 0){
                SensorData sensor = mWifiService.Sensor_GetFromID(sensorID);
                Log.i(TAG, "Activity received request connection from Sensor (" + String.valueOf(sensorID) + ")");
                showDialogRequestConnection(sensor, sensorID);
            }
        }
    };

    /**************************************************************************/


}