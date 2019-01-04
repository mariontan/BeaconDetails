package com.aic.beacondetails;

public class BeaconState {
    private String m_id;
    private String m_name;
    private int m_age;
    private String m_gender;
    private String m_gpsTime;
    private String m_gpsDate;
    private int m_fix;
    private int m_quality;
    private String m_message;
    private float m_latitude;
    private float m_longitude;
    private float m_altitude;

    public String getM_id() { return m_id; }

    public void setM_id(String p_id) { this.m_id = p_id;}

    public String getM_name() { return m_name; }

    public void setM_name(String p_name) { this.m_name = p_name; }

    public int getM_age() { return m_age;}

    public void setM_age(int p_age) { this.m_age = p_age;}

    public String getM_gender() { return m_gender;}

    public void setM_gender(String p_gender) { this.m_gender = p_gender;}

    public String getM_gpsTime() {
        return m_gpsTime;
    }

    public void setM_gpsTime(String p_gpsTime) {
        this.m_gpsTime = p_gpsTime;
    }

    public String getM_gpsDate() {
        return m_gpsDate;
    }

    public void setM_gpsDate(String p_gpsDate) {
        this.m_gpsDate = p_gpsDate;
    }

    public int getM_fix() {
        return m_fix;
    }

    public void setM_fix(int p_fix) {
        this.m_fix = p_fix;
    }

    public int getM_quality() {
        return m_quality;
    }

    public void setM_quality(int p_quality) {
        this.m_quality = p_quality;
    }

    public String getM_message() {
        return m_message;
    }

    public void setM_message(String p_message) {
        this.m_message = p_message;
    }

    public float getM_latitude() {
        return m_latitude;
    }

    public void setM_latitude(float p_latitude) {
        this.m_latitude = p_latitude;
    }

    public float getM_longitude() {
        return m_longitude;
    }

    public void setM_longitude(float p_longitude) {
        this.m_longitude = p_longitude;
    }

    public float getM_altitude() {
        return m_altitude;
    }

    public void setM_altitude(float p_altitude) {
        this.m_altitude = p_altitude;
    }
}
