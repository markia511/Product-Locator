package com.ko.lct.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public static String getLoginPage(@RequestParam(value = "error", required = false) boolean error,
	    @RequestParam(value = "accessdenied", required = false) boolean accessdenied,
	    ModelMap model) {
	logger.debug("Received request to show login page");
	if (error == true) {
	    // Assign an error message
	    model.put("error", "You have entered an invalid username or password!");
	} else {
	    model.put("error", "");
	}
	if (accessdenied) {
	    logger.info("Access denied");
	    model.put("accessdenied", "Access denied!");
	} else {
	    model.put("accessdenied", "");
	}
	return "login";
    }

}
