package com.ko.lct.web.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ko.lct.web.UserCreateForm;
import com.ko.lct.web.bean.UserRole;
import com.ko.lct.web.dao.UserDao;

@Component
public class UserCreateValidator extends UserValidator implements Validator {

    @Autowired
    private UserDao userDao;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
	return UserCreateForm.class.isAssignableFrom(clazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object target, Errors errors) {
	UserCreateForm userCreateForm = (UserCreateForm) target;

	String userName = userCreateForm.getUserName();
	String password = userCreateForm.getPassword();
	String confirmPassword = userCreateForm.getConfirmPassword();
	UserRole role = userCreateForm.getRole();

	validateUserName(errors, userName);
	validatePassword(errors, password, confirmPassword, userName);
	validateRole(errors, role);

	if (!errors.hasErrors()) {
	    if (this.userDao.isUserExists(userName)) {
		errors.rejectValue("userName", "userName.exists");
	    }
	}

    }

}
