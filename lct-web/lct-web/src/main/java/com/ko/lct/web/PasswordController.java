package com.ko.lct.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ko.lct.web.bean.User;
import com.ko.lct.web.dao.UserDao;
import com.ko.lct.web.util.LocatorUtility;
import com.ko.lct.web.validate.UserPasswordValidator;

@Controller
@Secured({ "IS_AUTHENTICATED_ANONYMOUSLY", "ROLE_ADMIN", "ROLE_USER" })
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private UserDao dao;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;

    @Autowired
    private LocatorUtility utility;

    @RequestMapping(value = "/change_password", method = RequestMethod.POST, params = "!edit")
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public String changePassword(HttpServletRequest request, Principal principal, Model model) {
	User user = getUser(principal);
	if (user == null) {
	    return "redirect:/login?error=true";
	}
	UserCreateForm userChangePasswordForm = new UserCreateForm();
	userChangePasswordForm.setUserName(user.getUserName());
	userChangePasswordForm.setRole(user.getRole());
	userChangePasswordForm.setEnabled(user.isEnabled());
	model.addAttribute("userForm", userChangePasswordForm);
	return "user_change_password";
    }
    
    @RequestMapping(value = "/change_password", method = RequestMethod.POST, params = "edit=1")
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public String changePassword(
	    @ModelAttribute(value = "userForm") UserCreateForm userChangePasswordForm,
	    BindingResult bindingResult,
	    Principal principal,
	    Model model) {
	User user = getUser(principal);
	if (user == null) {
	    return "redirect:/login?error=true";
	}
	return validateAndChangeUserPassword(user.getUserName(), userChangePasswordForm, bindingResult, model);
    }
    

    @RequestMapping(value = "/password_expired/{userName}", method = RequestMethod.GET, params = "!edit")
    public String userPasswordExpired(
	    @PathVariable("userName") String name,
	    HttpServletRequest request,
	    Model model) {
	User user = getUser(name);
	if (user == null) {
	    return "redirect:/login?error=true";
	}
	UserCreateForm userChangePasswordForm = new UserCreateForm();
	userChangePasswordForm.setUserName(user.getUserName());
	userChangePasswordForm.setRole(user.getRole());
	userChangePasswordForm.setEnabled(user.isEnabled());
	model.addAttribute("userForm", userChangePasswordForm);
	return "user_change_password";
    }

    @RequestMapping(value = "/password_expired/{userName}", method = RequestMethod.POST, params = "edit=1")
    public String userChangePassword(
	    @PathVariable("userName") String name,
	    @ModelAttribute(value = "userForm") UserCreateForm userChangePasswordForm,
	    BindingResult bindingResult,
	    Model model) {
	User user = getUser(name);
	if (user == null) {
	    return "redirect:/login?error=true";
	}
	return validateAndChangeUserPassword(user.getUserName(), userChangePasswordForm, bindingResult, model);
    }

    private User getUser(Principal principal) {
    	String userName = (principal == null) ? null : principal.getName();
    	return getUser(userName);
    }

    private User getUser(String userName) {
	User user = null;
	if (StringUtils.hasText(userName)) {
	    user = dao.getUser(userName);
	}
	return user;
    }
    
    
    private String validateAndChangeUserPassword(
	    String userName,
	    UserCreateForm userChangePasswordForm,
	    BindingResult bindingResult,
	    Model model) {

	this.userPasswordValidator.validate(userChangePasswordForm, bindingResult);
	if (bindingResult.hasErrors()) {
	    model.addAttribute("userForm", userChangePasswordForm);
	    return "user_change_password";
	}

	Authentication authentication = this.utility.authenticateUser(userName, userChangePasswordForm.getOldPassword());

	if (authentication != null && authentication.isAuthenticated()) {
	    if (this.dao.updateUserPassword(userName, this.passwordEncoder.encodePassword(userChangePasswordForm.getPassword(), null))) {
		logger.info("User [" + userName + "] - password changed");
	    }
	    return "redirect:/login";
	}
	else {
	    bindingResult.rejectValue("oldPassword", "password.invalid");
	    return "user_change_password";
	}
    }
}
