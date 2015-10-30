package com.ko.lct.web;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ko.lct.common.uri.LctUriUtils;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.web.bean.User;
import com.ko.lct.web.dao.UserDao;
import com.ko.lct.web.util.LocatorUtility;
import com.ko.lct.web.validate.UserCreateValidator;
import com.ko.lct.web.validate.UserEditValidator;
import com.ko.lct.web.validate.UserPasswordValidator;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao dao;

    @Autowired
    private UserCreateValidator userCreateValidator;

    @Autowired
    private UserEditValidator userEditValidator;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;

    @Autowired
    private LocatorUtility utility;

    /*
     * @InitBinder protected void initBinder(WebDataBinder binder) { binder.setValidator(this.userValidator); }
     */

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String usersList(/* Locale locale, */Model model, HttpSession session) {
	String forward;
	model.addAttribute("users", this.dao.getUserList());
	forward = "user_list";
	return forward;
    }

    @RequestMapping(method = RequestMethod.POST, params = { "operation=remove" }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody boolean deleteUser(@RequestParam(value = "userName") String name) {
	return this.dao.deleteUser(LctUriUtils.unEscapeParam(name));
    }

    @RequestMapping(method = RequestMethod.POST, params = { "!create", "operation=create" })
    public String userCreate(Model model) {
	model.addAttribute("userForm", new UserCreateForm());
	model.addAttribute("create", "1");
	model.addAttribute("operation", "create");
	return "user_create";
    }

    @RequestMapping(method = RequestMethod.POST, params = { "create=1", "operation=create" })
    public String userCreate(
	    @ModelAttribute(value = "userForm") UserCreateForm userCreateForm,
	    BindingResult bindingResult,
	    Model model) {
	this.userCreateValidator.validate(userCreateForm, bindingResult);
	if (bindingResult.hasErrors()) {
	    model.addAttribute("create", "1");
	    model.addAttribute("operation", "create");
	    return "user_create";
	}
	else {
	    if (!this.dao.addUser(
		    userCreateForm.getUserName(),
		    this.passwordEncoder.encodePassword(userCreateForm.getPassword(), null),
		    userCreateForm.getRole(),
		    userCreateForm.isEnabled())) {
		bindingResult.reject("general_exception");
		return "user_create";
	    }
	    return "redirect:/user";
	}
    }

    @RequestMapping(method = RequestMethod.POST, params = { "!edit", "operation=edit" })
    public String userEdit(@RequestParam(value = "userName") String userName, Model model) {
	User user = this.dao.getUser(userName);
	if (user == null) {
	    return "redirect:/user";
	}
	UserCreateForm userCreateForm = new UserCreateForm();
	userCreateForm.setUserName(user.getUserName());
	userCreateForm.setRole(user.getRole());
	userCreateForm.setEnabled(user.isEnabled());
	model.addAttribute("userForm", userCreateForm);
	model.addAttribute("edit", "1");
	model.addAttribute("operation", "edit");
	return "user_create";
    }

    @RequestMapping(method = RequestMethod.POST, params = { "edit=1", "operation=edit" })
    public String userEdit(
	    @RequestParam(value = "userName") String userName,
	    @ModelAttribute(value = "userForm") UserCreateForm userCreateForm,
	    BindingResult bindingResult,
	    Model model,
	    Principal principal) {
	this.userEditValidator.validate(userCreateForm, bindingResult);
	if (principal != null && principal.getName() != null && principal.getName().equalsIgnoreCase(userCreateForm.getUserName()) && !bindingResult.hasErrors()) {
	    this.userPasswordValidator.validate(userCreateForm, bindingResult);
	    if (!bindingResult.hasErrors()) {
		Authentication authentication = this.utility.authenticateUser(userName, userCreateForm.getOldPassword());
		if (authentication == null || !authentication.isAuthenticated()) {
		    bindingResult.rejectValue("oldPassword", "password.invalid");
		}
	    }
	}
	if (bindingResult.hasErrors()) {
	    model.addAttribute("userForm", userCreateForm);
	    model.addAttribute("edit", "1");
	    model.addAttribute("operation", "edit");
	    return "user_create";
	}
	else {
	    this.dao.updateUser(
		    userCreateForm.getUserName(),
		    this.passwordEncoder.encodePassword(userCreateForm.getPassword(), null),
		    userCreateForm.getRole(),
		    userCreateForm.isEnabled());
	    return "redirect:/user";
	}
    }

}
