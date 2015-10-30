package com.ko.lct.web.bean;

import java.util.Date;

public class User {
    private String userName;
    private String password;
    private UserRole role;
    private boolean enabled;
    private Date createDt;
    private Date modifyDt;

    public String getUserName() {
	return this.userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getPassword() {
	return this.password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public UserRole getRole() {
	return this.role;
    }

    public void setRole(UserRole role) {
	this.role = role;
    }

    public boolean isEnabled() {
	return this.enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public Date getCreateDt() {
	return this.createDt;
    }

    public void setCreateDt(Date createDt) {
	this.createDt = createDt;
    }

    public Date getModifyDt() {
	return this.modifyDt;
    }

    public void setModifyDt(Date modifyDt) {
	this.modifyDt = modifyDt;
    }

}
