package com.ko.lct.web.security;

class LoginAttempt {
    int count;
    long lockTime;

    public int getCount() {
	return this.count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public long getLockTime() {
	return this.lockTime;
    }

    public void setLockTime(long lockTime) {
	this.lockTime = lockTime;
    }

}
