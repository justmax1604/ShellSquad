package app.example.shellhacks;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateValidatorUsingDateFormat {
    private List<String> dateFormats = new ArrayList<>();

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
        dateFormats.add("dd MMM yyyy");
        dateFormats.add("MMM dd yy");
        dateFormats.add("MMMdd");
    }



    public Date getParsedDate(String inputString){
        //Adding elements in the List
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

    public Date fixYear(Date date) {
        Date currDate = new Date();
        if (date.before(currDate)) {
            if (currDate.getMonth() > date.getMonth()) {
                date.setYear(currDate.getYear() + 1);
            }
            else {
                date.setYear(currDate.getYear());
            }
        }

        return date;
    }

    public static String FormatDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                .withZone(ZoneId.of("UTC")).withLocale(Locale.US);
        return formatter.format(date.toInstant());
    }
}
