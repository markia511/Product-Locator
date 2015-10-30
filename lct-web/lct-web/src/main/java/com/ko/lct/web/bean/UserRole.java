package com.ko.lct.web.bean;

public enum UserRole {
    ROLE_USER, ROLE_ADMIN;

    public static UserRole parse(String value) {
	if (value != null) {
	    for (UserRole role : UserRole.values()) {
		if (value.equalsIgnoreCase(role.name())) {
		    return role;
		}
	    }
	}
	return null;
    }

    @Override
    public String toString() {
	return super.toString().substring(5);
    }

}
