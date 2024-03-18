package com.example.appdatalogger;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataloggerWifiService extends Service {

    String TAG = "AppDataLogger";
    boolean DEBUG =true;
    IBinder mBinder;
    Intent mIntentWifiSensorConnect;
    public static final String BROADCAST_ACTION = "com.example.dataloggerwifi";

    /********** Sensor Data *******/
    private int sampleRate = 100;//Default SampleRate
    private static SensorData sensors[];
    private final int SENSOR_DATA_BUFFER_SIZE = 1024;
    /******************************/

    /********* For Socket Server *******/
    public static final int SOCKET_CLIENT_SUPPORT_MAX = 10;
    public int currentClient = 0;
    private static final int SOCKET_SERVER_PORT = 8010;
    Thread SocketServerThread;
    ServerSocket sServer = null;
    SocketClientThread mSockClients[];
    /**********************************/

    public DataloggerWifiService() {
    }

    public class DataloggerWifiServiceBinder extends Binder {
        DataloggerWifiService getService() {
            return DataloggerWifiService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");

        sensors = new SensorData[SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal()];
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            sensors[i] = null;
        }
        mBinder = new DataloggerWifiServiceBinder();
        mIntentWifiSensorConnect = new Intent(BROADCAST_ACTION);

        /************* Start Socket Server *****/
        mSockClients = new SocketClientThread[SOCKET_CLIENT_SUPPORT_MAX];
        SocketServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainServerSocketThread();
                } catch (InterruptedIOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        SocketServerThread.start();
        /***************************************/


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");


        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");

        super.onDestroy();

        if(sServer != null){
            try {
                sServer.close();
            } catch (IOException e) {
                Log.e(TAG, "Can't close SocketServer");
            }
        }
        /******* Close Socket and thread ******/
        if(SocketServerThread != null){
            SocketServerThread.interrupt();
            SocketServerThread = null;
        }
        /*************************************/

    }

    /******************* Main Socket Server ****/
    private void mainServerSocketThread() throws InterruptedIOException {
        Socket s;
        try {
            sServer = new ServerSocket(SOCKET_SERVER_PORT);

            while(!Thread.currentThread().isInterrupted()){
                if(currentClient <= SOCKET_CLIENT_SUPPORT_MAX){
                    s = null;
                    try {
                        s = sServer.accept();
                    } catch (IOException e){
                        Thread.currentThread().interrupt();
                        break;
                    }
                    Log.i(TAG, "Accept client connect >> Start thread");
                    //Warning: doan nay dang de so sensor tuong duong so thread >> co ban la co nguy co: giai phap: thread send intent cho service de service assign vao mang
                    mSockClients[currentClient] = new SocketClientThread(s, handlerMsgThread);
                    mSockClients[currentClient].start();
                    currentClient++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected Handler handlerMsgThread = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageTypeHandle.MSG_TYPE_REQUEST_CONNECTION:
                    SensorData sensor = (SensorData) msg.obj;
                    requestActionFromUser(sensor);
                    if(DEBUG)
                        Log.i(TAG, "Received new sensor connection request from Thread: " + String.valueOf(sensor.Sensor_GetID()));
                    break;
                default:
                    if(DEBUG)
                        Log.i(TAG, "Got a new message: " + (String) msg.obj);
                    break;
            }
        }
    };

    public void requestActionFromUser(SensorData sSensor){
        boolean isAvailable = false;
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(DEBUG)
                Log.i(TAG, "Check slot available:  " + ((sensors[i] == null)?"OK":"NOT Available") + "(" + String.valueOf(i) + ")");
            if(sensors[i] == null){
                isAvailable = true;
                sensors[i] = sSensor;
                sensors[i].Sensor_SetSampleRate(sampleRate);
                break;
            }
        }
        if(DEBUG)
           Log.i(TAG, " available?:  " + String.valueOf(isAvailable));
        if(isAvailable == true){
            if(DEBUG)
                Log.i(TAG, "Send Intent");
            mIntentWifiSensorConnect.putExtra("SensorID", (int) sSensor.Sensor_GetID());
            sendBroadcast(mIntentWifiSensorConnect);

        }
    }


    /*******************************************************/

    /************* Handle Server data ************/
    public SensorData Sensor_GetFromID(int ID){
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                if(sensors[i].Sensor_GetID() == ID)
                    return sensors[i];
            }
        }
        return null;
    }

    public void CreateGraphSeriesForEachSensor(){
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                if(sensors[i].Sensor_GraphSeries == null){
                    sensors[i].Sensor_GraphSeries = new LineGraphSeries<DataPoint>();
                };
            }
        }
    }

    public SensorData[] GetSensorsAvailable_v2(){
        SensorData sTemp[] = new SensorData[SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal()];
        int length = 0;
        sTemp = new SensorData[SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal()];

        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
//            Log.i(TAG, "Service check Sensor available ==>> " + ((sensors[i] == null)?"null":"available"));
            if(sensors[i] != null){
                sTemp[length] = sensors[i];
                length++;
            }
        }
//        if(length > 0){
//            /*********** Debug only *********/
//            for(int i = 0; i < length; i++){
//                Log.i(TAG, "Service Re-check Sensor available ==>> " + ((sTemp[i] == null)?"null":"available"));
//            }
//            /***************************/
//        }else{
//            Log.i(TAG, "Service check Sensor available ==>> NO AVAILABLE");
//        }
        return sTemp;
    }

    public int GetMaxSensorWifiSupport(){
        return SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal();
    }

    public SensorData GetSensorsInstance(int i){
        if(i >= SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal())
            return null;
        return sensors[i];
    }

    public void UpdateSampleRate(int sRate){
        sampleRate = sRate;
        if(DEBUG)
            Log.i(TAG, "Update SampleRate to All Connected Sensor");
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                sensors[i].Sensor_SetSampleRate(sRate);
            }
        }
    }

    public int getSampleRate(){
        return sampleRate;
    }

    public void AddData(int index, int dat) {
        if(DEBUG)
            Log.i(TAG, "Sensor " + String.valueOf(index) + ": Adding data = " + String.valueOf(dat));
        sensors[index].Sensor_AddData(dat);
    }
    public void RemoveData(int index) {
        sensors[index].Sensor_RemoveData();
    }
    public int GetData(int index, int i) {
        return sensors[index].Sensor_GetData(i);

    }
    public boolean IsConnected(int ID) {
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                if(sensors[i].Sensor_ID == ID){
                    return sensors[i].Sensor_GetConnected();
                }
            }
        }
        return false;
    }
    public void SetConnected(int ID, boolean connected) {
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                if(sensors[i].Sensor_ID == ID){
                    sensors[i].Sensor_SetConnected(connected);
                }
            }
        }
    }
    public void SetStartStoreAndDisplay(boolean on) {
        for(int i = 0; i < SensorsList.SensorWifiList.MAX_SUPPORT_SENSOR_WIFI.ordinal(); i++){
            if(sensors[i] != null){
                    sensors[i].Sensor_SetStartStoreAndDisplay(on);
            }
        }
    }
    public int GetSize(int index) {
        return sensors[index].Sensor_GetBufferSize();
    }

    /**********************************************/


}