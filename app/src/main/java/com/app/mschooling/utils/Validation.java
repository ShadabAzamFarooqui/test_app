package com.app.mschooling.utils;


import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPwdFormatValid(String pwd) {
        return pwd.matches("^[0-9a-zA-Z\\@\\#\\!\\$\\%\\^\\&\\*\\(\\)\\_\\+\\=\\-\\~\\`\\/\\.]{6,24}$");
    }

    public static boolean isNameFormatValid(String name) {
        return name.matches("^[\\sa-zA-Z]{2,16}$");
    }

    public static boolean isPhoneFormatValid(String number) {
        number = number.replaceAll("[^ō0-9]", "");
        return number.matches("^[+]?[0-9]{10,11}$");
    }

    public static boolean isCvvValid(String cvv) {
        return cvv.matches("^[+]?[0-9]{3,4}$");
    }

    public static boolean isZipCodeValid(String zipcode) {
        return zipcode.matches("^[+]?[0-9]{5}$");
    }

    public static boolean isExpireDateValid(String expire) {
        return expire.matches("(?:0[1-9]|1[0-2])/[0-9]{2}");
    }

    public static boolean isLicencePlateValid(String info) {
        return info.matches(".{3,8}$");
    }


    public static boolean isValidPhone(EditText editText, String errMsg, boolean required) {
        String s = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher m = p.matcher(s);
        boolean matchBoolean = (m.find() && m.group().equals(s));
        Log.d("BooleanTes", String.valueOf(m));
        Log.d("BooleanTesting", String.valueOf(m.matches()));
        if (matchBoolean) {
            return true;
        } else {
            if (m.regionEnd() >= 11) {
                editText.setError("enter 10 digit valid mobile number");
            } else {
                editText.setError(errMsg);
            }
        }

        return matchBoolean;

    }

    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("");
            return false;
        }

        return true;
    }


//    ===================================================================================


    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object object) {
        return object == null ? true : (object instanceof CharSequence ? ((CharSequence) object).length() == 0 : (object.getClass().isArray() ? Array.getLength(object) == 0 : (object instanceof Collection ? ((Collection) object).isEmpty() : (object instanceof Map ? ((Map) object).isEmpty() : false))));
    }


    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }


}
