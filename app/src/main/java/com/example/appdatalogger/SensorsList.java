package com.example.appdatalogger;

public class SensorsList {
    public enum SensorList{
        TESTING_SENSOR_WIFI,
        TEMPERATURE_SENSOR_WIFI,
        TEMPERATURE_SENSOR_WIRE,
        SOUND_SENSOR_WIFI,
        SOUND_SENSOR_WIRE,
        DISTANCE_SENSOR_WIFI,
        DISTANCE_SENSOR_WIRE,
        SENSOR_SUPPORT_MAX
    }
    public enum SensorWifiList{
        TESTING_SENSOR_WIFI,
        TEMPERATURE_SENSOR_WIFI,
        SOUND_SENSOR_WIFI,
        DISTANCE_SENSOR_WIFI,
        MAX_SUPPORT_SENSOR_WIFI
    }

    public static final int SENSOR_ID_TEST_SENSOR = 101;
    public static final int SENSOR_ID_TEMP_SENSOR = 141;
    public static final int SENSOR_ID_HUMIDITY_SENSOR = 151;
    public static final int SENSOR_ID_DISTANCE_SENSOR = 171;
    public static final int SENSOR_ID_VOICE_SENSOR = 201;

    public String SENSOR_NAME_TEST_SENSOR = "TEST SENSOR";
    public String SENSOR_NAME_TEMP_SENSOR = "TEMPERATURE SENSOR";
    public String SENSOR_NAME_HUMIDITY_SENSOR = "HUMIDITY SENSOR";
    public String SENSOR_NAME_DISTANCE_SENSOR = "DISTANCE SENSOR";
    public String SENSOR_NAME_VOICE_SENSOR = "SOUND SENSOR";

    public static final int MSG_TYPE_REQUEST_CONNECTION = 101;
    public static final int MSG_TYPE_DATATRANFER = 114;

    public boolean isRequestConnection(int msg){
        if(msg == MSG_TYPE_REQUEST_CONNECTION)
            return true;
        else
            return false;
    }

    public boolean isDataTranfer(int msg){
        if(msg == MSG_TYPE_DATATRANFER)
            return true;
        else
            return false;
    }

    public boolean isSensorSupport(int SensorID){
        switch (SensorID){
            case SENSOR_ID_TEST_SENSOR:
            case SENSOR_ID_TEMP_SENSOR:
            case SENSOR_ID_HUMIDITY_SENSOR:
            case SENSOR_ID_DISTANCE_SENSOR:
            case SENSOR_ID_VOICE_SENSOR:
                return true;
            default:
                return false;
        }
    }

    public String getSensorName(int Sensor){
        String sName;
        switch (Sensor){
            case SENSOR_ID_TEST_SENSOR:
                sName = SENSOR_NAME_TEST_SENSOR;
                break;
            case SENSOR_ID_TEMP_SENSOR:
                sName = SENSOR_NAME_TEMP_SENSOR;
                break;
            case SENSOR_ID_VOICE_SENSOR:
                sName = SENSOR_NAME_VOICE_SENSOR;
                break;
            case SENSOR_ID_DISTANCE_SENSOR:
                sName = SENSOR_NAME_DISTANCE_SENSOR;
                break;
            default:
                sName = "None";
        }
        return sName;
    }



}
