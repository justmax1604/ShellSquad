package app.example.shellhacks;

import java.util.Date;

public class FoodItem {

    private String item_name;
    private Date expiration_date;
    //private String imagePath;

    public FoodItem (){
        //empty constructor
    }

    public FoodItem (String itemName, Date itemDate) {
        this.item_name = itemName;
        this.expiration_date = itemDate;
        //this.imagePath = imagePath;
    }

    /*
    public static List<FoodItem> fromJsonArray (***){
        List<FoodItem> items = new ArrayList<>();
        for (int i = 0; i<; i++){
            movies.add(new FoodItem(***));

        }
        return movies;
    }
    */

    public String getItem_name() {
        return item_name;
    }

    public Date getExpiration_date() {
        return expiration_date;
    }
    /*
    public String getImagePath() {
        return imagePath;
    }
    */
}
