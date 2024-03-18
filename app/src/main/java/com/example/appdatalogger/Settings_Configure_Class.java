package com.example.appdatalogger;

public class Settings_Configure_Class {
    public int Setting_SampleMode;
    public int Setting_SampleRate;
    public int Setting_TimeMeasure;

    public String Chart_DisplayMode;
    public String Chart_AxisXSelect;
    public String Chart_AxisXType;

    public int Setting_GetSampleMode(){
        return Setting_SampleMode;
    }
    public void Setting_SetSampleMode(int mode){
        Setting_SampleMode = mode;
    }
    public int Setting_GetSampleRate(){
        return Setting_SampleRate;
    }
    public void Setting_SetSampleRate(int mode){
        Setting_SampleRate = mode;
    }
    public int Setting_GetTimeMeasure(){
        return Setting_TimeMeasure;
    }
    public void Setting_SetTimeMeasure(int mode){
        Setting_TimeMeasure = mode;
    }

    public String Chart_GetDisplayMode(){
        return Chart_DisplayMode;
    }
    public void Chart_SetDisplayMode(String mode){
        Chart_DisplayMode = mode;
    }
    public String Chart_GetAxisXSelect(){
        return Chart_AxisXSelect;
    }
    public void Chart_SetAxisXSelect(String mode){
        Chart_AxisXSelect = mode;
    }
    public String Chart_GetAxisXType(){
        return Chart_AxisXType;
    }
    public void Chart_SetAxisXType(String mode){
        Chart_AxisXType = mode;
    }
}

