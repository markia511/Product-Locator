package com.ko.lct.web;

public class UserCreateForm extends UserForm {
    private String oldPassword;
    private String password;
    private String confirmPassword;

    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return this.oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getPassword() {
	return this.password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getConfirmPassword() {
	return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

}
