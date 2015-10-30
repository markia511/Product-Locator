package com.ko.lct.job.common.businessobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeocodeString {

    public static final String COMPLEX_CITY_SIGN = "-";
    public static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]+");
    private static final int FIRST_WORD_POSITION = 0;
    private static final int MIN_LETTERS_WEIGHTY_WORD = 3;
    private static final int MIN_WORDS_FOR_SPLITING_ADDRESS = 3;
    private static final String NOT_DIGIT_REGEXP = "[^0-9]+";
    private static final String STREET_NUMBER_SUFFIX = "th";
    private static final int MIN_CHARS_FOR_STREET_NUMBER_SUFFIX = 3;
    private static final int MIN_CHARS_INCORRECT_STREET_NUMBER = 6;
    private static final int DEFAULT_INDEX_SPLIT_DIGITS = 5;
    public static final int COUNT_WORD_CUT_COMPLEX_CITY = 2;
    private static final String HIGHWAY = "HWY";
    private static final String AVENUE = "AVE";
    private static final String BOX = "BOX";
    private static final String BOULEVARD = "BLVD";
    private static final String WAY = "WAY";
    private String value;
    private List<String> words;

    public GeocodeString(String value) {
	this.value = value;
	initWords();
    }

    private void initWords() {
	if (this.value != null
		&& this.value.trim().length() > 0) {
	    words = new ArrayList<String>();
	    this.value = this.value.replace("-", " ");
	    this.value = this.value.replace("#", " ");
	    String[] splitValues = this.value.split(" ");
	    for (String splitValue : splitValues) {
		if (splitValue.trim().length() > 0) {
		    words.add(splitValue);
		}
	    }
	}
    }

    public String getFirstWord() {
	if (words != null
		&& !words.isEmpty()) {
	    return words.get(FIRST_WORD_POSITION);
	}
	return null;
    }

    public String separateFirstDigitsFromLetters() {
	/* TODO: add logic for separate string as '1234567ave' */
	if (isStartWithNotSeparatedDigits()) {
	    int position = indexOf(this.value, NOT_DIGIT_REGEXP);
	    String[] splitAddress = this.value.split(" ");
	    if (position != -1
		    && splitAddress.length > 0) {
		if (splitAddress[0].length() >= MIN_CHARS_FOR_STREET_NUMBER_SUFFIX) {
		    String streetNumber = this.value.substring(0, position);
		    if (streetNumber.length() >= MIN_CHARS_INCORRECT_STREET_NUMBER) {
			position = MIN_CHARS_INCORRECT_STREET_NUMBER - 1;
			streetNumber = this.value.substring(0, position);
		    }
		    int countCharsWithoutSuffix = splitAddress[0].length() - STREET_NUMBER_SUFFIX.length();
		    if (STREET_NUMBER_SUFFIX.equalsIgnoreCase(splitAddress[0].substring(countCharsWithoutSuffix))) {
			if (countCharsWithoutSuffix < MIN_CHARS_INCORRECT_STREET_NUMBER) {
			    return null;
			} else {
			    return this.value.substring(0, DEFAULT_INDEX_SPLIT_DIGITS) + " " +
				    this.value.substring(DEFAULT_INDEX_SPLIT_DIGITS) + " ";
			}
		    } else {
			return streetNumber + " " + this.value.substring(position) + " ";
		    }
		}
	    }
	}
	return null;
    }

    private boolean isStartWithNotSeparatedDigits() {
	String firstWord = getFirstWord();
	if (firstWord != null) {
	    Matcher matcher = DIGIT_PATTERN.matcher(String.valueOf(firstWord.charAt(0)));
	    if (matcher.matches()) {
		matcher = DIGIT_PATTERN.matcher(firstWord);
		if (!matcher.matches()
			|| firstWord.length() >= MIN_CHARS_INCORRECT_STREET_NUMBER) {
		    return true;
		}
	    }
	}
	return false;
    }

    private static int indexOf(String str, String regexp) {
	Pattern pattern = Pattern.compile(regexp);
	Matcher matcher;
	int position = -1;
	for (int i = 0; i < str.length(); i++) {
	    matcher = pattern.matcher(String.valueOf(str.charAt(i)));
	    if (matcher.matches()) {
		position = i;
		break;
	    }
	}
	return position;
    }

    public boolean isStartFromDigits() {
	String firstWord = getFirstWord();
	if (firstWord != null) {
	    Matcher matcher = DIGIT_PATTERN.matcher(firstWord);
	    if (matcher.matches()) {
		return true;
	    }
	}
	return false;
    }

    public String injectWords(String value, int position) {
	String result = null;
	if (this.words != null
		&& this.words.size() > 1
		&& value != null
		&& !value.isEmpty()) {
	    result = value;
	    position++;
	    if (position < 0) {
		position = 0;
	    }
	    for (int i = position; i < words.size(); i++) {
		result += (" " + words.get(i));
	    }
	}
	return result;
    }

    public String getFirstWeightyWord() {
	String result = null;
	if (words != null
		&& words.size() >= MIN_WORDS_FOR_SPLITING_ADDRESS
		&& isStartFromDigits()) {
	    int index = FIRST_WORD_POSITION + 1;
	    while (index < words.size()) {
		if (isWeightyWord(words.get(index))) {
		    break;
		}
		index++;
	    }
	    if (index < (words.size() - 1)) {
		result = "";
		for (int i = 0; i <= index; i++) {
		    result += (words.get(i) + " ");
		}
		if (result.length() > 0) {
		    result = result.substring(0, result.length() - 1);
		}
	    }
	}
	return result;
    }

    public String getFirstOnlyWeightyWords() {
	String result = null;
	if (words != null
		&& !words.isEmpty()) {
	    int index = FIRST_WORD_POSITION;
	    result = words.get(index);
	    while (words.size() > (index + 1)
		    && isNotSmallNotDigitWord(words.get(++index))) {
		result += (" " + words.get(index));
	    }
	}
	return result;
    }

    private static boolean isNotSmallNotDigitWord(String word) {
	if (word.length() >= MIN_LETTERS_WEIGHTY_WORD
		&& !word.contains("#")) {
	    Matcher matcher = DIGIT_PATTERN.matcher(String.valueOf(word.charAt(0)));
	    if (!matcher.matches()) {
		return true;
	    }
	}
	return false;
    }

    private static boolean isWeightyWord(String word) {
	if (isNotSmallNotDigitWord(word)
		&& !word.equalsIgnoreCase(AVENUE)
		&& !word.equalsIgnoreCase(HIGHWAY)
		&& !word.equalsIgnoreCase(BOX)
		&& !word.equalsIgnoreCase(BOULEVARD)
		&& !word.equalsIgnoreCase(WAY)) {
	    return true;
	}
	return false;
    }

    @Override
    public String toString() {
	return value;
    }
}
