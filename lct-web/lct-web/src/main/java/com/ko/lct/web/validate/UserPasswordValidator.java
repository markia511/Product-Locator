package com.ko.lct.web.validate;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ko.lct.web.UserCreateForm;

@Component
public class UserPasswordValidator extends UserValidator implements Validator {

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
	UserCreateForm form = (UserCreateForm) target;
	String oldPassword = form.getOldPassword();

	if (!StringUtils.hasText(oldPassword)) {
	    errors.rejectValue("oldPassword", "password.empty");
	}
	else {
	    String password = form.getPassword();

	    if (oldPassword.equals(password)) {
		errors.rejectValue("password", "password.cannotBeTheSame");
	    }
	    else {
		String userName = form.getUserName();
		String confirmPassword = form.getConfirmPassword();
		validateUserName(errors, userName);
		validatePassword(errors, password, confirmPassword, userName);
	    }
	}
    }

}
