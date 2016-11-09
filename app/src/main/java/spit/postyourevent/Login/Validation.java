package spit.postyourevent.Login;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by DELL on 15/10/2016.
 */

public class Validation {

    public static final String TEXT_REG = "[a-zA-Z]+";
    public static boolean isText(EditText editText){
        String text = editText.getText().toString();
        editText.setError(null);
        boolean check= hasText(editText);
        if(!check)
            return false;
        boolean var = Pattern.matches(TEXT_REG,text);
        if(!var) {
            editText.setError("only alphanumeric!");
            return false;
        }
        return true;
    }
    public static boolean hasText(EditText editText){
        String text=editText.getText().toString();
        editText.setError(null);
        int length;
        length=text.length();
        if(length==0) {
            editText.setError("Enter Your Name");
            return false;
        }
        else
            return true;
    }

}
