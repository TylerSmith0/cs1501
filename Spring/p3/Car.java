import java.util.Comparator;

public class Car {
    private String VIN;
    private String make;
    private String model;
    private int price;
    private int mileage;
    private String color;

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    //                          INITIALIZERS
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    public Car(){
        VIN = null;
        make = null;
        model = null;
        price = -1;
        mileage = -1;
        color = null;
    } // end Car()

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    //                        GETTERS AND SETTERS
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    public String getVIN(){
        return VIN;
    } // end getVIN()

    public String getMake(){
        return make;
    } // end getMake()

    public String getModel(){
        return model;
    } // end getModel()

    public int getPrice(){
        return price;
    } // end getPrice()

    public int getMileage(){
        return mileage;
    } // end getMileage()

    public String getColor(){
        return color;
    } // end getColor()


    public boolean setVIN(String val){
        if (val.length() != 17){
            throw new IllegalArgumentException("VIN must be 17 digits.");
        }
        char[] temp = val.toCharArray();
        for (int i = 0; i < 17; i++){
            if (temp[i] == 'I' || temp[i] == 'Q' || temp[i] == 'O'){
                throw new IllegalArgumentException("VIN cannot contain 'I', 'Q', or 'O'.");
            } 
        }
        // if we make it here, val should be good!
        VIN = val;
        return true;
    } // end setVIN()

    public boolean setMake(String val){
        if (val == null){
            throw new NullPointerException("Make cannot be null.");
        }
        make = val;
        return true;
    } // end setMake()

    public boolean setModel(String val){
        if (val == null){
            throw new NullPointerException("Model cannot be null.");
        }
        model = val;
        return true;
    } // end setModel()

    public boolean setPrice(int val){
        if (val < 0){
            throw new IllegalArgumentException("Cannot have negative price.");
        }
        price = val;
        return true;        
    } // end setPrice()

    public boolean setMileage(int val){
        if (val < 0){
            throw new IllegalArgumentException("Cannot have negative mileage.");
        }
        mileage = val;
        return true;
    } // end setMileage()

    public boolean setColor(String val){
        if (val == null){
            throw new NullPointerException("Color cannot be null.");
        }
        color = val;
        return true;
    } // end setColor()


    // public int compare(Car car1, Car car2){
    //     return car1.getPrice() - car2.getPrice();
    // }

} // end class Car()



