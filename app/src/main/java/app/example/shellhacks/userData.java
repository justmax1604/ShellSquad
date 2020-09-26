package app.example.shellhacks;

public class userData {
    private static userData mInstance= null;

    public String userId;

    protected userData(){}

    public static synchronized userData getInstance() {
        if(null == mInstance){
            mInstance = new userData();
        }
        return mInstance;
    }
}