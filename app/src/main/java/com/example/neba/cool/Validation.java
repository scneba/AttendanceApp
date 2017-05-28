package com.example.neba.cool;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

 /**
  * Purpose: Validation class*/
public class Validation {
 
    // Error Messages
    private static final String codeWord = "Code must be greater than 4 characters";
    private static final String passWord = "Password must be greater than 6 characters";


 
 
 

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean isCodeWord(EditText editText,Context context) {
 
        String text = editText.getText().toString().trim();
        editText.setError(null);
 
        // length 0 means there is no text
        if (text.length() <5) {
            editText.setError(codeWord);
           
            return false;
        }
 
        return true;
    }



    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean isPassword(EditText editText,Context context) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() <6) {
            editText.setError(passWord);

            return false;
        }

        return true;
    }
     // check the input field has any text or not
     // return true if it contains text otherwise false
     public static boolean isKey(EditText editText,Context context) {

         String text = editText.getText().toString().trim();
         editText.setError(null);

         // length 0 means there is no text
         if (text.length() <9) {
             editText.setError("Key must be above 8 characters");

             return false;
         }

         return true;
     }
     public static boolean isRepeatTime(EditText editText,Context context) {

         String text = editText.getText().toString().trim();
         editText.setError(null);

         // length 0 means there is no text
         if (text.length()==0) {
             editText.setError("Please enter a value here");

             return false;
         }
         else if( (Integer.parseInt(editText.getText().toString()))>5){
             editText.setError("This field must be less than 5");

             return false;
         }

         return true;
     }

     // check the input field has any text or not
     // return true if it contains text otherwise false
     public static boolean isSSID(EditText editText,Context context) {

         String text = editText.getText().toString().trim();
         editText.setError(null);

         // length 0 means there is no text
         if (text.length() <3) {
             editText.setError("SSID must be greater than 3 characters");

             return false;
         }

         return true;
     }
    
}

