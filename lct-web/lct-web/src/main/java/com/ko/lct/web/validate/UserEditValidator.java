package com.ko.lct.web.validate;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ko.lct.web.UserCreateForm;
import com.ko.lct.web.bean.UserRole;

@Component
public class UserEditValidator extends UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
	return UserCreateForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
	UserCreateForm userCreateForm = (UserCreateForm) target;

	String userName = userCreateForm.getUserName();
	String password = userCreateForm.getPassword();
	String confirmPassword = userCreateForm.getConfirmPassword();
	UserRole role = userCreateForm.getRole();

	validateUserName(errors, userName);

	if (StringUtils.hasText(password) || StringUtils.hasText(confirmPassword)) {
	    validatePassword(errors, password, confirmPassword, userName);
	}

	validateRole(errors, role);
    }

}
