package app.example.shellhacks;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateValidatorUsingDateFormat {
    private List<String> dateFormats = new ArrayList<String>();

    private boolean isValid(String dateFormat, String dateStr) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public DateValidatorUsingDateFormat(){
        //Adding elements in the List
        dateFormats.add("MM/dd/yyyy");
        dateFormats.add("MMM dd yyyy");
        dateFormats.add("MM yyyy");
    }



    public Date getParsedDate(String inputString){
        //Adding elements in the List
        dateFormats.add("MM/dd/yyyy");
        dateFormats.add("MMM dd yyyy");
        dateFormats.add("MM yyyy");
        Date expDate = null;

        //String inputParsed = "02/28/2019";
        //Iterating the List element using for-each loop
        for(String df : dateFormats) {
            // if valid format then convert to date;
            if(this.isValid(df, inputString)){
                try{
                    expDate = new SimpleDateFormat(df).parse(inputString);
                    break;
                }
                catch(Exception e){
                    System.out.println("continue\n");
                }

            }
        }

        return expDate;
    }
}
