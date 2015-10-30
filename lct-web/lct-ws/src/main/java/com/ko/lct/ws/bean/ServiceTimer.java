package com.ko.lct.ws.bean;

public class ServiceTimer {
    private long startTime = 0;
    
    public ServiceTimer() {
	startTime = System.currentTimeMillis();
    }
    
    public void start() {
	startTime = System.currentTimeMillis();
    }
    
    public long getCurrentDurationTime() {
	return System.currentTimeMillis() - startTime;
    }
}
