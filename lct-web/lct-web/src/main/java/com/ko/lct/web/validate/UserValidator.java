package com.ko.lct.web.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.validation.Errors;

import com.ko.lct.web.bean.UserRole;
import com.ko.lct.web.dao.UserDao;

abstract class UserValidator {
    
    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;
        
    protected void validateUserName(Errors errors, String userName) {
	if (userName == null || userName.trim().isEmpty()) {
	    errors.rejectValue("userName", "userName.empty");
	}
	else {
	    if (userName.startsWith(" ")) {
		errors.rejectValue("userName", "userName.cantStartSpace");
	    }
	    else if (userName.endsWith(" ")) {
		errors.rejectValue("userName", "userName.cantEndSpace");
	    }
	    else if (userName.length() > 255) {
		errors.rejectValue("userName", "userName.tooLong");
	    }
	}
    }
    
    protected void validatePassword(Errors errors, String password, String confirmPassword, String userName) {
	if (password == null || password.trim().isEmpty()) {
	    errors.rejectValue("password", "password.empty");
	} else {
	    if (password.length() < 8) {
		errors.rejectValue("password", "password.tooShort");
	    }
	    else if (password.startsWith(" ")) {
		errors.rejectValue("password", "password.cantStartSpace");
	    }
	    else if (password.endsWith(" ")) {
		errors.rejectValue("password", "password.cantEndSpace");
	    }

	    if (password.contains(userName)) {
		errors.rejectValue("password", "password.containsUserName");
	    }

	    if (!password.isEmpty() && !Character.isLetter(password.charAt(0))) {
		errors.rejectValue("password", "password.notStartsWithLetter");
	    }

	    if (userDao.isPasswordInHistory(userName, passwordEncoder.encodePassword(password, null))) {
		errors.rejectValue("password", "password.alreadyUsed");
	    }

	    if (!password.equals(confirmPassword)) {
		errors.rejectValue("confirmPassword", "password.incorrectConfirm");
	    }
	    
	    if (!password.equals(confirmPassword)) {
		errors.rejectValue("confirmPassword", "password.incorrectConfirm");
	    }
	    
	    String[] patterns = {".*[0-9].*", ".*[a-z].*", ".*[A-Z].*", ".*[@#$%^&+=].*"};
	    int score =0;
		
	    for (String p :patterns) {
		score += password.matches(p) ? 1 : 0;
	    }
	    if (score < 3) {
		errors.rejectValue("password", "password.notEnoughComplexity");
	    }	
	}
	
    }
    
    protected void validateRole(Errors errors, UserRole roleValue) {
	if (roleValue == null) {
	    errors.rejectValue("role", "role.empty");
	}
    }

}
