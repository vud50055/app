package com.example.appdatalogger;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;

public class SensorData {
    String TAG = "AppDataLogger";
    boolean DEBUG = false;
    /*
     * Sensor_ID:   de phan biet cac loai Sensor Khac nhau
     */
    public int Sensor_ID;
    /*
    Sensor_ConnectionType: Kieu ket noi: WIFI hoac WIRE
     */
    public int Sensor_ConnectionType;
    /*
    Sensor_data_count:  So data dang luu trong mang
     */
    public int Sensor_data_count;
    /*
    Sensor_display_index:   vi tri data dang duoc doc va hien thi
     */
    public int Sensor_display_index;
    /*
    Sensor_data_size:   So luong toi da data co the luu trong mang: thay doi dong
     */
    public int Sensor_data_size;
    /*
    Sensor_data[]:  Mang de luu data
     */
    public int Sensor_data[];
    public DataInfo Sensor_datas[];
    /*
    Sensor_SampleRate:  chu ky lay mau
     */
    public int Sensor_SampleRate;
    /*
    Sensor_Connected:   Hien thi trang thai cua Sensor da ket noi voi DataLogger hay chua
     */
    public boolean Sensor_Connected;
    /*
    Sensor_StartStoreAndDisplay:    Hien thi trang thai co dang hien thi data cua sensor len Graph khong
     */
    public boolean Sensor_StartStoreAndDisplay;
    /*
    Sensor_GraphSeries: de hien thi data cua sensor len graph
     */
    public LineGraphSeries<DataPoint> Sensor_GraphSeries;

    public static final int SENSOR_CONNECTION_TYPE_WIFI = 0;
    public static final int SENSOR_CONNECTION_TYPE_WIRE = 1;

    public static final int SENSOR_DATA_FORMAT_START = 0x42;
    public static final int SENSOR_DATA_FORMAT_END = 0x45;
    /*
    Moi lan tang thi se tang GROW_BUFFER_SIZE phan tu de luu data
     */
    public int GROW_BUFFER_SIZE = 500;

    public boolean Sensor_GetConnected() {
        return false;
    }

    public class DataInfo{
        int timeGet;
        int data;
    }

    public SensorData(int ID, int size)
    {
        Sensor_ID = ID;
        Sensor_data_count = 0;
        Sensor_data_size = size;
        Sensor_data = new int[size];
    }

    public SensorData()
    {
        Sensor_data_count = 0;
        Sensor_display_index = 0;
        Sensor_SampleRate = 500;//Khoi tao chu ky lay mau mac dinh la 100ms
//        Sensor_PendingAction = false;
    }

    public void Sensor_AddData(int dat)
    {
        if(DEBUG)
            Log.i(TAG, "Add data: " + String.valueOf(dat));
        if (Sensor_data_count == Sensor_data_size)
        {
            Sensor_GrowDataSize();
        }

        Sensor_data[Sensor_data_count] = dat;
        Sensor_data_count++;
    }

    public void Sensor_AddData(int time, int dat)
    {
        if(DEBUG)
            Log.i(TAG, "Add data: " + String.valueOf(dat));
        if (Sensor_data_count == Sensor_data_size)
        {
            Sensor_GrowDataSize();
        }

        Sensor_data[Sensor_data_count] = dat;
        Sensor_data_count++;
    }

    public void Sensor_RemoveData()
    {
        Arrays.fill(Sensor_data, 0);
        Sensor_data_count = 0;
    }

    public void Sensor_GrowDataSize() {
        int temp[] = null;

        if(DEBUG)
            Log.i(TAG, "Grow data size");
        if (Sensor_data_count == Sensor_data_size) {
            temp = new int[Sensor_data_size + GROW_BUFFER_SIZE];
            {
                for (int i = 0; i < Sensor_data_size; i++) {
                    temp[i] = Sensor_data[i];
                }
                Sensor_data = temp;
                Sensor_data_size = Sensor_data_size + GROW_BUFFER_SIZE;
            }
        }
    }

    //Get data by index
    public int Sensor_GetData(int index) {
        if(index > Sensor_data_count)
            return Sensor_data[Sensor_data_count];
        return Sensor_data[index];
    }

    public int Sensor_DisplayData() {
        if(Sensor_data_count > 0){
            if(Sensor_display_index < Sensor_data_count){
                Sensor_display_index++;
                return Sensor_data[Sensor_display_index - 1];
            }else{
                return Sensor_data[Sensor_data_count - 1];
            }
        }else{
            return -1;
        }
    }

    public void Sensor_SetConnected(boolean connected){
        if(DEBUG)
            Log.i(TAG, "Set connection to " + String.valueOf(connected));
        Sensor_Connected = connected;
    }

    //Doc so data da luu
    public int Sensor_GetBufferSize(){
        return Sensor_data_count;
    }

    public void Sensor_SetID(int sID){
        Sensor_ID = sID;
    }

    public int Sensor_GetID(){
        return Sensor_ID;
    }

    public void Sensor_InitBufferSize(int size){
        Sensor_data_size = size;
        Sensor_data = new int[size];
    }

    public void Sensor_SetConnectionType(int cType){
        Sensor_ConnectionType = cType;
    }
    public int Sensor_GetConnectionType(){
        return Sensor_ConnectionType;
    }

    public void Sensor_SetStartStoreAndDisplay(boolean on){
        if(DEBUG)
            Log.i(TAG, "Set Start Store and Display to " + String.valueOf(on));
        Sensor_StartStoreAndDisplay = on;
    }
    public boolean Sensor_GetStartStoreAndDisplay(){
        return Sensor_StartStoreAndDisplay;
    }

//    public void Sensor_SetPendingAction(boolean on){
//        Sensor_PendingAction = on;
//    }
//    public boolean Sensor_GetPendingAction(){
//        return Sensor_PendingAction;
//    }

    public void Sensor_SetSampleRate(int sample){
        Sensor_SampleRate = sample;
    }
    public int Sensor_GetSampleRate(){
        return Sensor_SampleRate;
    }

}
