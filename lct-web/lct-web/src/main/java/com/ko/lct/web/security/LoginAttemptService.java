package com.ko.lct.web.security;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class LoginAttemptService {
    private static final int MAX_LOGIN_ATTEMPT = 10;
    private static final long LOCK_TIME_IN_MILLS = 1000 * 60 * 30; // 30 minutes
    private static final int MAX_ITEMS_COUNT = 200;

    private static ConcurrentHashMap<String, LoginAttempt> loginAttempts =
	    new ConcurrentHashMap<String, LoginAttempt>();

    protected LoginAttempt getLoginAttemptByUserName(final String userName) {
	if (userName == null) {
	    return null;
	}
	return loginAttempts.get(userName);
    }

    public boolean isUserLocked(final String userName) {
	if (userName != null) {
	    LoginAttempt loginAttempt = getLoginAttemptByUserName(userName);
	    if (loginAttempt != null) {
		if (loginAttempt.count > MAX_LOGIN_ATTEMPT) {
		    if (Calendar.getInstance().getTimeInMillis() - loginAttempt.getLockTime() > LOCK_TIME_IN_MILLS) {
			// Unlock
			loginAttempts.remove(userName);
		    }
		    else {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public void successfilLogin(final String userName) {
	if (userName != null) {
	    loginAttempts.remove(userName);
	}
    }

    public void failedLogin(final String userName) {
	if (userName != null) {
	    LoginAttempt loginAttempt = getLoginAttemptByUserName(userName);
	    if (loginAttempt == null) {
		loginAttempt = new LoginAttempt();
		loginAttempts.put(userName, loginAttempt);
	    }
	    if (loginAttempt.count < Integer.MAX_VALUE) {
		loginAttempt.count++;
	    }
	    loginAttempt.setLockTime(Calendar.getInstance().getTimeInMillis());
	    if (loginAttempt.count > MAX_ITEMS_COUNT) {
		cleanUpLoginAttempts();
	    }
	}
    }

    private static void cleanUpLoginAttempts() {
	synchronized (loginAttempts) {
	    long currentTime = Calendar.getInstance().getTimeInMillis();
	    for (String key : loginAttempts.keySet()) {
		LoginAttempt loginAttempt = loginAttempts.get(key);
		if (loginAttempt != null) {
		    if (currentTime - loginAttempt.getLockTime() > LOCK_TIME_IN_MILLS) {
			loginAttempts.remove(key);
		    }
		}
	    }
	}
    }

}
