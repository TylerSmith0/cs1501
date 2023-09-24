import java.util.*;
import java.io.*;
import java.lang.*;

public class CarTracker{

    private static int cars = 401; // max number of cars

    // Set up the initial 'overall' heaps for price and mileage using IndexMinPQ:
    private static MyMinPQ1 priceHeap = new MyMinPQ1(cars, new PriceComparator()); // Created a PQ of size 401 to ensure that we would have more than enough space to work with
    private static MyMinPQ1 mileHeap = new MyMinPQ1(cars, new MileageComparator()); // Same as above, but for mileage
    private static LinkedList[] priceMap = new LinkedList[401]; // Created an array of LinkedLists up to size 401
    private static LinkedList[] mileMap = new LinkedList[401]; // Created an array of LinkedLists up to size 401

    // Set up the input technique for the program... remember to close at the end!
    static Scanner kbd = new Scanner(System.in); // initializes the keyboard as the input

    public static void main(String[] args){

        String line = null;
        BufferedReader txtfile = null;

        try {
            txtfile = new BufferedReader(new FileReader(new File("cars.txt")));
            line = txtfile.readLine(); // gets rid of beginning line
            line = txtfile.readLine(); // begins with the information!
        } catch (FileNotFoundException e){
            System.out.println("\n\nCould not find file. Beginning Car Tracker with empty database.\n\n");
        } catch (IOException i){
            System.out.println("\n\nEmpty file. Beginning Car Tracker with empty database.");
            line = null;
        }

        while (line != null){
            String[] info = line.split(":"); // splits the line into an array of strings NOT including the : char
            Car car = new Car();
            car.setVIN(info[0]);
            car.setMake(info[1]);
            car.setModel(info[2]);
            car.setPrice(Integer.parseInt(info[3]));
            car.setMileage(Integer.parseInt(info[4]));
            car.setColor(info[5]);
            addCar(car); // adds car to the database
            try {
                line = txtfile.readLine();
            } catch (IOException e){ line = null; }
            if (line == null){
                try {
                    txtfile.close();
                } catch (IOException e){}
                break;
            }
        }

        // at this point, all information should be loaded into PQs and ready to rock!

        System.out.println("\n\nWelcome to the Car Tracker 3000. Your options are as follows:\n\n"+
                            "\t1. Add a Car\n"+
                            "\t2. Update a Car\n"+
                            "\t3. Remove a Car\n"+
                            "\t4. Get Lowest Price\n"+
                            "\t5. Get Lowest Mileage\n"+
                            "\t6. Get Lowest Price by Model\n"+
                            "\t7. Get Lowest Mileage by Model\n"+
                            "\th. More Information\n"+
                            "\tx. Exit Program");

        String uEntry = null;
        char uChar = '0';
        while (true) {
            System.out.print("\n\nPlease enter a selection:\t");
            uEntry = kbd.nextLine(); // takes in the whole line the user enters
            uChar = uEntry.charAt(0); // only looks at the very first character of what was entered

            switch (uChar){
                case '1':
                    //System.out.println("\n\nAdded the following car:\n");
                    addCar();
                    break;
                case '2':
                    //System.out.println("\n\nUpdated the following car:\n");
                    updateCar();
                    break;
                case '3':
                    //System.out.println("\n\nRemoved the following car:\n");
                    removeCar();
                    break;
                case '4':
                    //System.out.println("\n\nLowest Price Car is:\n");
                    getLowestPrice();
                    break;
                case '5':
                    //System.out.println("\n\nLowest Mileage Car is:\n");  
                    getLowestMileage();
                    break;  
                case '6':
                    System.out.print("\nEnter the Make of car:\t");
                    String make = kbd.nextLine();
                    System.out.print("Enter the Model of car:\t");
                    String model = kbd.nextLine();
                    getLowestPrice(model);
                    break;
                case '7':
                    System.out.print("\nEnter the Make of car:\t");
                    String make1 = kbd.nextLine();
                    System.out.print("Enter the Model of car:\t");
                    String model1 = kbd.nextLine();
                    getLowestMileage(model1);
                    break;
                case 'h':
                    System.out.print("\n1.\tAdd a car\n\t\tInsert a new car into the database.");
                    System.out.print("\n2.\tUpdate a car\n\t\tFinds a car in the database and updates certain information.");
                    System.out.print("\n3.\tRemove a car\n\t\tRemoves a specific car from the database.");
                    System.out.print("\n4.\tRetrieve lowest price\n\t\tReturns the lowest-priced car in the database.");
                    System.out.print("\n5.\tRetrieve lowest mileage\n\t\tReturns the lowest-mileage car in the database.");
                    System.out.print("\n6.\tRetrieve lowest mileage by model\n\t\tReturns the lowest-priced car of a certain model in the database.");
                    System.out.print("\n7.\tRetrieve lowest mileage by model\n\t\tReturns the lowest-mileage car of a certain model in the database.");
                    System.out.print("\nx.\tExit the program\n");
                    break;
                case 'p':
                    printHeaps();
                    break;
                case 'P':
                    System.out.print("\nEnter the Model of car:\t\t");
                    String model2 = kbd.nextLine();
                    printHeaps(model2);
                    break;
                case 'x':
                    System.out.println("\n\nThanks for using the Car Tracker 3000. We hope to see you again soon.\n\n");
                    kbd.close();
                    System.exit(0);
            } // end switch case statement
        } // end while(true) loop
    } // end main()

    private static boolean addCar(Car car){
        boolean flag = priceHeap.insert(car);
        mileHeap.insert(car);
        // need to hash the model and get it!
        int index = hash(car.getModel());
        if (priceMap[index] == null || priceMap[index].size() == 0){
            priceMap[index] = new LinkedList();
            priceMap[index].add(new MyMinPQ1(cars, new PriceComparator())); // builds a pq in the new LL at index
            ((MyMinPQ1)priceMap[index].get(0)).setModel(car.getModel());
        }
        if (mileMap[index] == null || mileMap[index].size() == 0){
            mileMap[index] = new LinkedList();
            mileMap[index].add(new MyMinPQ1(cars, new MileageComparator())); // builds a pq
            ((MyMinPQ1)mileMap[index].get(0)).setModel(car.getModel());
        }
        // MIGHT HAVE LL BUT NO MODEL:
        // need to traverse for model here
        if (flag == true){ // only want to do this if the insert was initially successful
            MyMinPQ1 pq1 = (MyMinPQ1)priceMap[index].get(0);
            MyMinPQ1 pq2 = null;
            for (int i = 0; i < priceMap[index].size(); i++){
                pq1 = (MyMinPQ1)priceMap[index].get(i);
                if (pq1.getModel().equals(car.getModel())){
                    pq2 = (MyMinPQ1)mileMap[index].get(i);
                    break;
                } // found the right pq!
            }
            if (pq2 == null){ // means there wasn't any entry in the LL
                pq1 = new MyMinPQ1(cars, new PriceComparator());
                pq1.setModel(car.getModel());
                pq2 = new MyMinPQ1(cars, new MileageComparator());
                pq2.setModel(car.getModel());
                priceMap[index].add(pq1);
                mileMap[index].add(pq2);
            }
            pq1.insert(car);
            pq2.insert(car);
        }
        return flag;
    } // end addCar(Car)

    private static void addCar(){
        System.out.print("\nEnter VIN of car:\t\t");
        String VIN = kbd.nextLine();
        System.out.print("Enter the Make of car:\t\t");
        String make = kbd.nextLine();
        System.out.print("Enter the Model of car:\t\t");
        String model = kbd.nextLine();
        System.out.print("Enter the Price of car:\t\t$");
        String price = kbd.nextLine();
        System.out.print("Enter the Mileage of car:\t");
        String mileage = kbd.nextLine();
        System.out.print("Enter the Color of car:\t\t");
        String color = kbd.nextLine();
        Car car = new Car();
        car.setVIN(VIN);
        car.setMake(make);
        car.setModel(model);
        car.setPrice(Integer.parseInt(price));
        car.setMileage(Integer.parseInt(mileage));
        car.setColor(color);
        if (addCar(car)){
            System.out.println("\nAdded the following car:\n");
            System.out.println("\t"+car.getMake()+" "+car.getModel()+" ("+car.getColor()+") -- "+car.getVIN()+"\n"+
                                    "\tMiles:  "+car.getMileage()+"\t\tPrice:  $"+car.getPrice()+"\n");
        } else {
            System.out.println("\n\n\tCar already in database.\n");
        }
        
    } // end addCar()

    private static void removeCar(String uVIN){
        Car removal1 = priceHeap.remove(uVIN);
        Car removal2 = mileHeap.remove(uVIN);
        if (removal1 == null || removal2 == null){
            System.out.println("\n\n\tError: Car not found.");
            return;
        }
        int index = hash(removal1.getModel());
        int index2 = hash(removal2.getModel());
        if (index != index2){
           // System.out.println("Indices in removeCar(String) did not match.");
            return;
        } // makes sure not to remove something if they are different I guess



        // need to traverse for the model hash

        MyMinPQ1 pq1 = (MyMinPQ1)priceMap[index].get(0);
        MyMinPQ1 pq2 = null;
        for (int i = 0; i < priceMap[index].size(); i++){
            pq1 = (MyMinPQ1)priceMap[index].get(i);
            if (pq1.getModel().equals(removal1.getModel())){
                pq2 = (MyMinPQ1)mileMap[index].get(i);
                break;
            } // found the right pq!
        }
        if (pq2 == null){
            System.out.println("\n\tNo model name found in database.");
            return;
        }

        if (pq1 != null) pq1.remove(uVIN);
        if (pq2 != null) pq2.remove(uVIN);
        System.out.println("\nRemoved the following car:\n");
        System.out.println("\t"+removal1.getMake()+" "+removal1.getModel()+" ("+removal1.getColor()+") -- "+removal1.getVIN()+"\n"+
                                "\tMiles:  "+removal1.getMileage()+"\t\tPrice:  $"+removal1.getPrice()+"\n");
    } // end removeCar(String)

    private static void removeCar(){
        System.out.print("\nEnter VIN of car:\t");
        String VIN = kbd.nextLine();
        if (VIN.length() != 17){
            System.out.println("\nInvalid VIN.\n\n");
            return;
        }
        removeCar(VIN);
    } // end removeCar()

    private static void updateCar(){
        System.out.print("\nEnter VIN of car:\t\t");
        String VIN = kbd.nextLine();
        System.out.print("\nWhat would you like to update?\n\n1.\tPrice of Car\n2.\tMileage of Car\n3.\tColor of Car\n\nEnter entry:\t");
        String num = kbd.nextLine();
        int resp = Integer.parseInt(num);
        String entry = null;
        Car car = null;
        if (resp == 1){
            System.out.print("\nEnter new price of car:\t$");
            entry = kbd.nextLine();
            int price = Integer.parseInt(entry);
            car = new Car();
            car.setVIN(VIN);
            car.setPrice(price);
        } else if (resp == 2){
            System.out.print("\nEnter new mileage of car:\t");
            entry = kbd.nextLine();
            int mileage = Integer.parseInt(entry);
            car = new Car();
            car.setVIN(VIN);
            car.setMileage(mileage);
        } else if (resp == 3){
            System.out.print("\nEnter new color of car:\t");
            entry = kbd.nextLine();
            car = new Car();
            car.setVIN(VIN);
            car.setColor(entry);
        }
        if (entry == null) return;
        if (car == null) return; // I think this is doing the same thing but who cares
        Car temp1 = priceHeap.update(VIN, car, resp);
        Car temp2 = mileHeap.update(VIN, car, resp);
        if (temp1 != null && temp2 != null){
            //System.out.println("Made it to the update in models");
            int index = hash(temp1.getModel());

            // need to traverse for the model hash
            MyMinPQ1 pq1 = (MyMinPQ1)priceMap[index].get(0);
            MyMinPQ1 pq2 = null;
            for (int i = 0; i < priceMap[index].size(); i++){
                pq1 = (MyMinPQ1)priceMap[index].get(i);
                if (pq1.getModel().equals(temp1.getModel())){
                    pq2 = (MyMinPQ1)mileMap[index].get(i);
                    break;
                } // found the right pq!
            }
            if (pq2 == null){
                System.out.println("\n\tNo model name found in database.");
                return;
            }
            if (pq1 != null) pq1.update(VIN, car, resp);
            if (pq2 != null) pq2.update(VIN, car, resp);
        } else {
            System.out.println("\n\n\tError: Car not found in database.\n");
        }
        if (temp1 != null){
            System.out.println("\nUpdated to the following car:\n");
            System.out.println("\t"+temp1.getMake()+" "+temp1.getModel()+" ("+temp1.getColor()+") -- "+temp1.getVIN()+"\n"+
                                    "\tMiles:  "+temp1.getMileage()+"\t\tPrice:  $"+temp1.getPrice()+"\n");
        }
    }

    private static void getLowestPrice(){
        Car temp = priceHeap.get(1);
        if (temp != null){
            System.out.println("\n\nLowest Price car is:\n");
            System.out.println("\t"+temp.getMake()+" "+temp.getModel()+" ("+temp.getColor()+") -- "+temp.getVIN()+"\n"+
                                "\tMiles:  "+temp.getMileage()+"\t\tPrice:  $"+temp.getPrice()+"\n");
        } else {
            System.out.println("\n\n\tCar database is empty.\n");
        }
    } // end getLowestPrice()

    private static void getLowestMileage(){
        Car temp = mileHeap.get(1);
        if (temp != null){
            System.out.println("\n\nLowest Price car is:\n");
            System.out.println("\t"+temp.getMake()+" "+temp.getModel()+" ("+temp.getColor()+") -- "+temp.getVIN()+"\n"+
                                "\tMiles:  "+temp.getMileage()+"\t\tPrice:  $"+temp.getPrice()+"\n");
        } else {
            System.out.println("\n\n\tCar database is empty.\n");
        }
    } // end getLowestMileage()

    private static void getLowestPrice(String model){
        int index = hash(model);
        if (priceMap[index] == null || priceMap[index].size() == 0){
            System.out.println("\n\tNo model name found in database.");
            return;
        }
        System.out.println("\n\nLowest Price "+model+" is:\n");

        // need to traverse for the model hash
        MyMinPQ1 pq1 = (MyMinPQ1)priceMap[index].get(0);
        MyMinPQ1 pq2 = null;
        for (int i = 0; i < priceMap[index].size(); i++){
            pq1 = (MyMinPQ1)priceMap[index].get(i);
            if (pq1.getModel().equals(model)){
                pq2 = (MyMinPQ1)mileMap[index].get(i);
                break;
            } // found the right pq!
        }
        if (pq2 == null){
            System.out.println("\n\tNo model name found in database.");
            return;
        }

        Car temp = pq1.get(1); // returns highest priority of model PQ
        if (temp != null){
            System.out.println("\t"+temp.getMake()+" "+temp.getModel()+" ("+temp.getColor()+") -- "+temp.getVIN()+"\n"+
                               "\tMiles:  "+temp.getMileage()+"\t\tPrice:  $"+temp.getPrice()+"\n");
        } else {
            System.out.println("\tAll "+model+"s removed from database.");
        }
        
    } // end getLowestPrice(model)

    private static void getLowestMileage(String model){
        int index = hash(model);
        if (mileMap[index] == null || mileMap[index].size() == 0){
            System.out.println("\n\tNo model name found in database.");
            return;
        }
        System.out.println("\n\nLowest Mileage "+model+" is:\n");

        // need to traverse for the model hash
        MyMinPQ1 pq1 = null;
        MyMinPQ1 pq2 = (MyMinPQ1)mileMap[index].get(0);
        for (int i = 0; i < mileMap[index].size(); i++){
            pq2 = (MyMinPQ1)mileMap[index].get(i);
            if (pq2.getModel().equals(model)){
                pq1 = (MyMinPQ1)mileMap[index].get(i);
                break;
            } // found the right pq!
        }
        if (pq1 == null){
            System.out.println("\n\tNo model name found in database.");
            return;
        }

        Car temp = pq2.get(1); // returns highest priority of model PQ
        if (temp != null){
            System.out.println("\t"+temp.getMake()+" "+temp.getModel()+" ("+temp.getColor()+") -- "+temp.getVIN()+"\n"+
                               "\tMiles:  "+temp.getMileage()+"\t\tPrice:  $"+temp.getPrice()+"\n");
        } else {
            System.out.println("\tAll "+model+"s removed from database.");
        }
    } // end getLowestMileage(model)

    private static int hash(String model){
        int index = 0;
        int R = 256; // ASCII extended thing
        for (int i = 0; i < model.length(); i++){
            index = (R * index + model.charAt(i)) % 401; // hashing by the size of the model heapArray
        }
        return index;
    }


    public static void printHeaps(){
        Car temp1 = null;
        Car temp2 = null;
        System.out.println("~~~~~ PRICE HEAP:~~~~~~~~\t\t\t\t\t\t\t\t~~~~~ MILE HEAP:~~~~~~~~");
        if (priceHeap.size() != mileHeap.size()){
            System.out.println("ALERT:::: different size heaps\n\n\n");
            return;
        } 
        for (int i = 0; i <= priceHeap.size(); i++){
            temp1 = priceHeap.get(i);
            temp2 = mileHeap.get(i);
            int R = 26 + 10;
            int index = 0;
            if (temp1 != null && temp2 != null){
                for (int j = 0; j < 17; j++){
                    index = (R * index + temp1.getVIN().charAt(j)) % 401;
                }
                
                System.out.println(i+")\t"+temp1.getMake()+" "+temp1.getModel()+" ("+temp1.getColor()+") -- "+temp1.getVIN()+"\t\t\t\t\t"+
                                   i+")\t"+temp2.getMake()+" "+temp2.getModel()+" ("+temp2.getColor()+") -- "+temp2.getVIN()+"\n"+
                                   "\tMiles:  "+temp1.getMileage()+"\tPrice:  $"+temp1.getPrice()+"\tindex = "+index+"\t\t\t\t\t"+
                                   "\tMiles:  "+temp2.getMileage()+"\tPrice:  $"+temp2.getPrice()+"\tindex = "+index+"\n");
            }
        }


        

        //priceHeap.printInd();

        // System.out.println("~~~~~ MILE HEAP:~~~~~~~~");
        // for (int i = 0; i <= mileHeap.size(); i++){
        //     temp = mileHeap.get(i);
        //     int R = 26 + 10;
        //     int index = 0;
        //     if (temp != null){
        //         for (int j = 0; j < 17; j++){
        //             index = (R * index + temp.getVIN().charAt(j)) % 401;
        //         }
                
        //         System.out.println(i+")\t"+temp.getMake()+" "+temp.getModel()+" ("+temp.getColor()+") -- "+temp.getVIN()+"\n"+
        //                         "\tMiles:  "+temp.getMileage()+"\tPrice:  $"+temp.getPrice()+"\tindex = "+index+"\n");
        //     }
        // }

        //mileHeap.printInd();
    }

    public static void printHeaps(String model){
        int index = hash(model);
        if (priceMap[index] == null || priceMap[index].size() == 0){
            System.out.println("No model name found in database.");
            return;
        }
        
        Car temp1 = null;
        Car temp2 = null;
        System.out.println("~~~~~ PRICE HEAP:~~~~~~~~\t\t\t\t\t\t\t\t~~~~~ MILE HEAP:~~~~~~~~");

        // need to traverse for the model hash
        MyMinPQ1 pq1 = (MyMinPQ1)priceMap[index].get(0);
        MyMinPQ1 pq2 = null;
        for (int i = 0; i < priceMap[index].size(); i++){
            pq1 = (MyMinPQ1)priceMap[index].get(i);
            if (pq1.getModel().equals(model)){
                pq2 = (MyMinPQ1)mileMap[index].get(i);
                break;
            } // found the right pq!
        }
        if (pq2 == null){
            System.out.println("\n\tNo model name found in database.");
            return;
        }
        for (int i = 0; i <= pq1.size(); i++){

            // need to traverse for model hash
            temp1 = pq1.get(i);
            temp2 = pq2.get(i);
            int R = 26 + 10;
            int ind = 0;
            if (temp1 != null && temp2 != null){
                for (int j = 0; j < 17; j++){
                    ind = (R * ind + temp1.getVIN().charAt(j)) % 401;
                }
                
                System.out.println(i+")\t"+temp1.getMake()+" "+temp1.getModel()+" ("+temp1.getColor()+") -- "+temp1.getVIN()+"\t\t\t\t\t"+
                                   i+")\t"+temp2.getMake()+" "+temp2.getModel()+" ("+temp2.getColor()+") -- "+temp2.getVIN()+"\n"+
                                   "\tMiles:  "+temp1.getMileage()+"\tPrice:  $"+temp1.getPrice()+"\tindex = "+ind+"\t\t\t\t\t"+
                                   "\tMiles:  "+temp2.getMileage()+"\tPrice:  $"+temp2.getPrice()+"\tindex = "+ind+"\n");
            }
        }
    }
    
} // end CarTracker()