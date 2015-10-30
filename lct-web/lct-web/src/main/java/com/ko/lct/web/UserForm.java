package com.ko.lct.web;

import com.ko.lct.web.bean.UserRole;

public class UserForm {

    private String userName;
    private UserRole role;
    private boolean enabled;

    public String getUserName() {
	return this.userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
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

}
