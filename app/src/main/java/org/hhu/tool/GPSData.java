package org.hhu.tool;

public class GPSData {


    /**
     * longitude :
     * latitude :
     * heading :
     * speed :
     * online :
     * visible :
     */

    private double longitude;
    private double latitude;
    private double heading;
    private double speed;
    private int online;
    private int visible;


    public GPSData() {}

    public GPSData(double latitude, double longitude) {
		setLatitude(latitude);
		setLongitude(longitude);
	}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }
}
