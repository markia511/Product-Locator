package com.ko.lct.common.bean;

public class ReportAPIUsage {
    String clientKey;
    String clientName;
    int countOfQueries;
    public String getClientKey() {
        return clientKey;
    }
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public int getCountOfQueries() {
        return countOfQueries;
    }
    public void setCountOfQueries(int countOfQueries) {
        this.countOfQueries = countOfQueries;
    }

}
