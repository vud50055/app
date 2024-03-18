package com.example.appdatalogger;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClientThread extends Thread {
    String TAG = "AppDataLogger";
    boolean DEBUG = true;
    private final int SENSOR_DATA_BUFFER_SIZE = 1024;
    private static final int SOCKET_DATA_BUFFER_ZIE = 1024;

    protected Socket socket;
    protected  SensorData sensor;
    protected Handler thHandler;

    public SocketClientThread(Socket s, Handler handle) {
        this.socket = s;
        this.thHandler = handle;
        this.sensor = new SensorData();
    }

    public void run() {
        super.run();
        while(!Thread.currentThread().isInterrupted()){
            char[] cbuf = new char[SOCKET_DATA_BUFFER_ZIE];
            int buffer_size = 0;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //receive a message
            try {
                buffer_size = in.read(cbuf, 0, SOCKET_DATA_BUFFER_ZIE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(buffer_size <= 0){
                break;
            }

            /*********** Print debug only ***********
            if(DEBUG){
                Log.i(TAG, "received: " + String.valueOf(buffer_size) + " Byte"+ ", SampleRate = " + String.valueOf(sensor.Sensor_GetSampleRate()));
                for(int i = 0; i < buffer_size; i++){
                    Log.i(TAG, String.format("data-%d = 0x%02X", i, (long)cbuf[i]));
                }
            }
            ****************************************/

//            buffer_size = handle_GetNewestData(cbuf, buffer_size);
            buffer_size = handle_GetNewestData_v2(cbuf, buffer_size);

            /*********** Print debug only ***********
            if(DEBUG){
                Log.i(TAG, "After Get New:received: " + String.valueOf(buffer_size) + " Byte"+ ", SampleRate = " + String.valueOf(sensor.Sensor_GetSampleRate()));
                for(int i = 0; i < buffer_size; i++){
                    Log.i(TAG, String.format("data-%d = 0x%02X", i, (long)cbuf[i]));
                }
            }
            ****************************************/

            handle_ReceivedData(cbuf, buffer_size);

            try {
                Thread.sleep(sensor.Sensor_GetSampleRate());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        Log.i(TAG, "Server exit: ");
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDataFormatCorrect(char[] data, int length){
        boolean isCorrect = false;
        /************** Check format ban tin co dung hay khong ****/
        int start_index = -1, end_index = -1;
        for(int i = 0; i < length; i++){
            if((start_index < 0) && ((int)data[i] == SensorData.SENSOR_DATA_FORMAT_START)){
                start_index = i;
            }
            if((end_index < 0) && ((int)data[i] == SensorData.SENSOR_DATA_FORMAT_END)){
                end_index = i;
            }
        }
        if((start_index >= 0) && (end_index >= 0) && (start_index < end_index))
            isCorrect = true;
        /********************************************************/

        return isCorrect;
    }

    /*************** Get data moi nhat ************/
    private int handle_GetNewestData(char[] data, int length){
        char[] temp = new char[10];
        int[] start_index_item = new int[length], end_index_item = new int[length];
        int start_count = 0, end_count = 0, select_item = 0, length_new = 0;
        SensorsList sList = new SensorsList();

        for(int i = 0; i < length; i++){
            if(((int)data[i] == SensorData.SENSOR_DATA_FORMAT_START)){
                if(i + 1 < length){
                    int msgType = (int)data[i + 1];
                    if(sList.isRequestConnection(msgType) || sList.isDataTranfer(msgType)){
                        start_index_item[start_count] = i;
//                        if(DEBUG)
//                            Log.i(TAG, "Storage Start index = " + String.valueOf(i) + ", into index " + String.valueOf(start_count));
                        start_count++;
                    }
                }
            }
            if(((int)data[i] == SensorData.SENSOR_DATA_FORMAT_END)){
                if(i + 1 - MessageTypeHandle.MSG_WIFI_FORMAT_LENGTH >= 0){
                    if(((int)data[i + 1 - MessageTypeHandle.MSG_WIFI_FORMAT_LENGTH] == SensorData.SENSOR_DATA_FORMAT_START)){
                        end_index_item[end_count] = i;
//                        if(DEBUG)
//                            Log.i(TAG, "Storage End index = " + String.valueOf(i) + ", into index " + String.valueOf(end_count));
                        end_count++;
                    }
                }
            }
        }

        if(start_count == end_count){
            select_item = start_count - 1;
        }else if(start_count > end_count){
            select_item = end_count - 1;
        }else {
            select_item = start_count - 1;
        }

//        if(DEBUG){
//            Log.i(TAG, "start_count = " + String.valueOf(start_count) + ", end_count = " + String.valueOf(end_count) + ", select_count = " + String.valueOf(select_item));
//            Log.i(TAG, "START = " + String.valueOf(start_index_item[select_item]) + ", END = " + String.valueOf(end_index_item[select_item]));
//        }
        for(int i = 0; i <= end_index_item[select_item] - start_index_item[select_item]; i++){
            data[i] =  data[start_index_item[select_item] + i];
//            if(DEBUG)
//                Log.i(TAG, String.format("Add follow item to array = 0x%02X", (long)data[start_index_item[select_item] + i]));
            length_new++;
        }
//        if(DEBUG){
//            Log.i(TAG, "length_new = " + String.valueOf(length_new));
//        }

        return length_new;
    }

    private int handle_GetNewestData_v2(char[] data, int length){
        char[] temp = new char[SensorData.SENSOR_DATA_FORMAT_END];
        int[] start_index_item = new int[length], end_index_item = new int[length];
        int start_count = 0, end_count = 0, select_item = 0, length_new = 0;
        SensorsList sList = new SensorsList();

        for(int i = 0; i < length; i++){
            if(((int)data[i] == SensorData.SENSOR_DATA_FORMAT_START)){
                if(i + 1 < length){
                    int msgType = (int)data[i + 1];
                    if(sList.isRequestConnection(msgType) || sList.isDataTranfer(msgType)){//Check byte thu 2
                        if(i + MessageTypeHandle.MSG_WIFI_FORMAT_LENGTH - 1 < length){//Check ky tu cuoi
                            if((int)data[i + MessageTypeHandle.MSG_WIFI_FORMAT_LENGTH - 1] == SensorData.SENSOR_DATA_FORMAT_END){//Correct form
                                temp[0] = data[i];
                                temp[1] = data[i + 1];
                                temp[2] = data[i + 2];
                                temp[3] = data[i + 3];
                                temp[4] = data[i + 4];
                                temp[5] = data[i + 5];
                                temp[6] = data[i + 6];
                            }
                        }
                    }
                }
            }
        }

        data = temp;
        length_new = MessageTypeHandle.MSG_WIFI_FORMAT_LENGTH;
        return length_new;
    }
    /**********************************************/

    /*************** Note ban tin:
     * bit-0: 0x42
     * bit-1: Truyen data hay request ket noi: truyen = 101, request ket noi = 114
     * bit-2: data lenght
     * bit-3: unknow
     * bit-4: low Byte data: hoac la Sensor_ID trong ban tin request ket noi
     * bit-5; high Byte data
     * bit-6:
     * Note: ban tin truyen data dang ko thay co sensor type > lam sao phan biet ban tin nay la cua sensor nao
     */
    private void handle_ReceivedData(char[] data, int length){
//        Log.i(TAG, "Handle message======");

        /********* Doan nay can check dieu kien co phai la sensor send data ko ********/
        if(isDataFormatCorrect(data, length)){
            int msgType = (int)data[1];
            int SensorID = (int)data[4];
            SensorsList sList = new SensorsList();

            if(sList.isRequestConnection(msgType)){
                /********* Send thong bao len man hinh *******/
                //Test only: accept luon
                if(DEBUG)
                    Log.i(TAG, "Sensor request connection");
                sensor.Sensor_SetID(SensorID);
                sensor.Sensor_SetConnectionType(SensorData.SENSOR_CONNECTION_TYPE_WIFI);
                sensor.Sensor_InitBufferSize(SENSOR_DATA_BUFFER_SIZE);

                //Send message to Service
                Message message = this.thHandler.obtainMessage();
                message.obj = sensor;
                message.what = MessageTypeHandle.MSG_TYPE_REQUEST_CONNECTION;
                this.thHandler.sendMessage(message);
                /*********************************************/
            }else{//Sensor Send data
                if(DEBUG)
                    Log.i(TAG, "Connected = " + String.valueOf(sensor.Sensor_GetConnected()) + ", display = " +  String.valueOf(sensor.Sensor_GetStartStoreAndDisplay()));
                if(sensor.Sensor_GetConnected() && sensor.Sensor_GetStartStoreAndDisplay()){
                    int lowByte = 0, upByte = 0, temp = 0;
                    lowByte = (int) data[4];
                    upByte = (int) data[5];
                    temp = (upByte<<8) | lowByte;
                    sensor.Sensor_AddData(temp);
                    if(DEBUG)
                        Log.i(TAG, "Sensor send data = " + String.valueOf(temp));
                }
            }
        }else{
            Log.e(TAG, "Data received is not correct");
        }

        /******************************************************************************/
    }


}
