package com.ko.lct.job.common.businessobjects;

public enum ConvertedStepEnum {

    ZERO_STEP(0), SEPARATE_FIRST_DIGIT_FROM_LETTER(1), SPLIT_COMPLEX_CITY_NAME(2), CHANGE_ADDRESS_LINE(3),
    FIRST_WEIGHTY_WORD(4), WITHOUT_POSTAL_CODE(5), WITHOUT_CITY(6), OUTLET_IN_TOP_ADDRESS(7),
    ONLY_OUTLET_ADDRESS(8), WITHOUT_ADDRESS_LINE(9);

    private int step;

    private ConvertedStepEnum(int step) {
	this.step = step;
    }

    public int getStep() {
	return step;
    }
}
