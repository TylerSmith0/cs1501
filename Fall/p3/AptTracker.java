import java.util.*;
import java.lang.*;
import java.io.*;

public class AptTracker{
    /* Create OVERALL PQs */
    static SqFootageComparator sfComp = new SqFootageComparator();
    static RentComparator rentComp = new RentComparator();
    static MaxPQ<Apartment> sfAparts = new MaxPQ<Apartment>(sfComp); // OVERALL sfAparts
    static MinPQ<Apartment> rentAparts = new MinPQ<Apartment>(rentComp); // OVERALL rentAparts
    static ArrayList<City> cityList = new ArrayList<City>();
    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args){ 
        readInFile();    
        String userEntry = "x";
        System.out.println("Hello! Welcome to the Apartment Selector 5000.");
        System.out.println("If you need help at any time for commands, just type '-h' for assistance.");
        System.out.println("--------------------------------------------------------------------------\n\n");
        while (!userEntry.equals("quit")){
            System.out.print("What would you like to do?:\t");
            try{
                userEntry = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            switch (userEntry){
                case "-h": 
                    printHelp(); break;
                case "add":
                    addApt(); break;
                case "update": 
                    /*updateApt();*/ System.out.println("updateApt() is not working at this time."); break;
                case "rent": 
                    getLowestRent(); break;
                case "rent-city":
                case "rent -city": 
                    cityGetLowestRent(); break;
                case "sqft":
                    getHighestSqFt(); break;
                case "sqft-city":
                case "sqft -city": 
                    cityGetHighestSqFt(); break;
                case "remove": 
                    System.out.println("removeApart() is not working at this time.");
                    break;
                    /*Apartment temp = removeApart();
                    if (temp != null){
                        System.out.println("You removed "+temp.getAddress()+", "+temp.getCity()+" from consideration.");
                    }*/
                default:
            }
            System.out.println("\n");
        }

        //System.out.println("Size of RentPQ: "+rentAparts.size());
        //System.out.println("Size of sfAparts: " + sfAparts.size());
        for (int i = 0; i < cityList.size(); i++){
            //System.out.println("Size of "+cityList.get(i).name + "RentPQ: "+cityList.get(i).RentPQ.size());
            //System.out.println("Size of "+cityList.get(i).name + "SqFtPQ: "+cityList.get(i).SqFtPQ.size());
        }
    } // END main()

    public static void printHelp(){
        System.out.println("LIST OF COMMANDS:\n"+
                            "-h\t\tLists all commands that can be used.\n\n"+
                            "add\t\tPrompts user for information on the apartment, then adds it to both the global and city databases.\n\n"+
                            "remove\t\tPrompts user for information on the apartment, and verifies that apartment is the one to be removed.\n"+
                            "\t\tThen, removes apartment from the global database and the city databases.\n\n"+
                            "update\t\tPrompts the user for information on the apartment, and verifies that apartent is the one to be updated.\n"+
                            "\t\tThen, prompts the user for a new rent value and updates accordingly.\n\n"+
                            "rent\t\tDisplays the lowest rent of all the apartments listed.\n\n"+
                            "rent -city\tPrompts user for a city, then displays the lowest rent found in that city.\n\n"+
                            "sqft\t\tDisplays the highest square footage apartment of all the apartments listed.\n\n"+
                            "sqft -city\tPrompts user for a city, then displays the highest square footage found in that city.\n\n"+
                            "quit\t\tExits the program.\n\n"+
                            "You can always force quit the program with ^C if you need to.\n\n");

    }

    public static Apartment removeApart(){

        /**
         * This is where the user input goes
         */
        String temp1 = null;
        while (temp1 == null){
            System.out.print("What is the street address?\t");
            try{
                temp1 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }
        String temp2 = null;
        while (temp2 == null){
            System.out.print("\nWhat is the Unit Number?\t");
            try{
                temp2 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }

        String temp4 = "x";
        int int4 = 0;
        while(true){
            System.out.print("\nWhat is the ZIP Code?\t");
            try{
                temp4 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            try{
                int4 = Integer.parseInt(temp4);
                break;
            } catch (NumberFormatException e){
                System.out.println("Error: Invalid entry. Must be valid number, WITHOUT commas.");
            }
        }
// ////////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Finding index in Global PQ to remove from rentAparts
         */
        int indexRent = -1;
        Iterator<Apartment> iterator = rentAparts.iterator();
        Apartment temp = null;
        boolean flag = false;
        while (iterator.hasNext()){
            indexRent++;
            temp = iterator.next();
            if (int4 == temp.getZIP()){
                if (temp2.equals(temp.getUnit())){
                    if (temp1.equals(temp.getAddress())){
                        flag = true;
                        System.out.println("Found "+temp.getAddress()+" in rentAparts.");
                        break;
                    }}}}

        /**
         * Finding index in Global PQ to remove from sfAparts
         */
        int indexSqFt = -1;
        iterator = sfAparts.iterator();
        temp = null;
        flag = false;
        while (iterator.hasNext()){
            indexSqFt++;
            temp = iterator.next();
            if (int4 == temp.getZIP()){
                if (temp2.equals(temp.getUnit())){
                    if (temp1.equals(temp.getAddress())){
                        flag = true;
                        System.out.println("Found "+temp.getAddress()+" in sfAparts.");
                        break;
                    }}}}
    
        /**
         * Removing IF.F it was found! 
        */
        if (flag == true){
            //sfAparts.removeIndex(indexSqFt);
            rentAparts.removeIndex(indexRent);

            for (int i = 0; i < cityList.size(); i++){
                if (temp.getCity().equals(cityList.get(i).name)){
                    // NEED AN INDEX HERE
                    indexRent = 0;
                    Iterator<Apartment> cityIterator = cityList.get(i).RentPQ.iterator();
                    Apartment tempA = null;
                    flag = false;
                    while (cityIterator.hasNext()){
                        tempA = cityIterator.next();
                        if (tempA.getZIP() == temp.getZIP() && tempA.getUnit().equals(temp.getUnit()) && tempA.getAddress().equals(temp.getAddress())){
                            System.out.println("Foudn in CityPQ at index "+indexRent);
                            cityList.get(i).RentPQ.removeIndex(indexRent);
                            break;
                        }
                        indexRent++;
                    }

                    /*
                    cityIterator = cityList.get(i).SqFtPQ.iterator();
                    temp = null;
                    flag = false;
                    while (cityIterator.hasNext()){
                        temp = cityIterator.next();
                        if (int4 == temp.getZIP() && temp2.equals(temp.getUnit()) && temp1.equals(temp.getAddress())){
                            flag = true;
                            break;
                        }
                        indexSqFt++;
                    }*/
                    System.out.println("removing from rentpq");
                    if (cityList.get(i).RentPQ.size() == 0){
                        cityList.remove(i);
                    }
                }
            }
            //System.out.println("Removed successfully.");
        } else {
            temp = null;
            System.out.println("Could not find the Apartment specified. Returning to Home screen.");
        }
        return temp;
    }

    public static void addApt(){
        Apartment temp = new Apartment();
        String temp1 = null;
        while (temp1 == null){
            System.out.print("What is the street address?\t");
            try{
                temp1 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }
        String temp2 = null;
        while (temp2 == null){
            System.out.print("\nWhat is the Unit Number?\t");
            try{
                temp2 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }
        String temp3 = null;
        while (temp3 == null){
            System.out.print("\nWhat is the City?\t");
            try{
                temp3 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }
        String temp4 = "x";
        while(true){
            System.out.print("\nWhat is the ZIP Code?\t");
            try{
                temp4 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            try{
                int int4 = Integer.parseInt(temp4);
                break;
            } catch (NumberFormatException e){
                System.out.println("Error: Invalid entry. Must be valid number, WITHOUT commas.");
            }
        }
        String temp5 = "x";
        while(true){
            System.out.print("\nWhat is the Price of Rent per month?\t$");
            try{
                temp5 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            try{
                int int5 = Integer.parseInt(temp5);
                break;
            } catch (NumberFormatException e){
                System.out.println("Error: Invalid entry. Must be valid number, WITHOUT commas.");
            }
        }
        String temp6 = "x";
        while(true){
            System.out.print("\nWhat is the Square Footage?\t");
            try{
                temp6 = keyboard.readLine();
            }  catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            try{
                int int6 = Integer.parseInt(temp6);
                break;
            } catch (NumberFormatException e){
                System.out.println("Error: Invalid entry. Must be valid number, WITHOUT commas.");
            }
        }
        String entry = temp1 + ":" + temp2 + ":" + temp3 + ":" + temp4 + ":" + temp5 + ":" + temp6;
        formatInfo(entry, temp);
    }

    public static void updateApt(){
        String temp1 = null;
        while (temp1 == null){
            System.out.print("What is the street address?\t");
            try{
                temp1 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }
        String temp2 = null;
        while (temp2 == null){
            System.out.print("\nWhat is the Unit Number?\t");
            try{
                temp2 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
        }

        String temp4 = "x";
        int int4 = 0;
        while(true){
            System.out.print("\nWhat is the ZIP Code?\t");
            try{
                temp4 = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Unexpected IOException. Exiting program.");
                System.exit(0);
            }
            try{
                int4 = Integer.parseInt(temp4);
                break;
            } catch (NumberFormatException e){
                System.out.println("Error: Invalid entry. Must be valid number, WITHOUT commas.");
            }
        }
        Iterator<Apartment> iterator = rentAparts.iterator();
        Apartment temp = null;
        boolean flag = false;
        while (iterator.hasNext()){
            temp = iterator.next();
            if (int4 == temp.getZIP()){
                if (temp2.equals(temp.getUnit())){
                    if (temp1.equals(temp.getAddress())){
                        flag = true;
                        break;
                    }
                }
            }
        }

        if (flag == true){
            System.out.print("Would you like to update the rent for "+temp.getAddress()+" "+temp.getUnit()+" "+temp.getCity()+" "+temp.getZIP()+"?(y/n)\t");
            String entry = "x";
            int int6 = 0;
            while (!entry.equals("y") && !entry.equals("n")){
                try{
                    entry = keyboard.readLine();
                } catch (IOException e){
                    System.out.println("Error: Unexpected error. Exiting the program.");
                    System.exit(0);
                }
                if (entry.equals("y")){
                    while (true){
                        System.out.print("What is the new rent?\t");
                        try{
                            entry = keyboard.readLine();
                            int6 = Integer.parseInt(entry);
                            break;
                        } catch (IOException e){
                            System.out.println("Error: Unexpected error. Exiting the program.");
                            System.exit(0);
                        } catch (NumberFormatException n){
                            System.out.print("Please enter an integer without commas for the rent per month:\t");
                        }
                    }
                    temp.setRent(int6);
                    break;
                }
                else if (!entry.equals("n")){
                    System.out.print("Please enter either a 'y' to change the rent or an 'n' to exit the update screen.");
                }
            }
        } else {
            System.out.println("No record of the apartment found. Exiting to Home screen.");
        }
    } // END updateApt()


    public static void getLowestRent(){
        Apartment apart = rentAparts.min();
        System.out.println("The lowest rent is " + apart.getAddress() + ", Unit " + apart.getUnit() + ", " + apart.getCity() + " " + apart.getZIP() + " at $" + apart.getRent() + " per month.");
    }

    public static void cityGetLowestRent(){
        City temp = null;
        String city = "x";
        System.out.print("What city would you like to search in?\t");
        boolean flag = false;
        while (flag == false){
            try{
                city = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Error: Unexpected response.");
                System.exit(0);
            }
            for (int i = 0; i < cityList.size(); i++){
                if (city.equals(cityList.get(i).name)){
                    temp = cityList.get(i);
                    flag = true;
                    break;
                }
            }
            if (flag == false){
                System.out.print("Could not find an apartment in the city. Please enter another:\t");
            }
        }
        Apartment apart = temp.RentPQ.min();
        System.out.println("The lowest rent is " + apart.getAddress() + ", Unit " + apart.getUnit() + ", " + apart.getCity() + " " + apart.getZIP() + " at $" + apart.getRent() + " per month.");
    }

    public static void getHighestSqFt(){
        Apartment apart = sfAparts.max();
        System.out.println("The highest sq. footage is " + apart.getAddress() + ", Unit " + apart.getUnit() + ", " + apart.getCity() + " " + apart.getZIP() + " at " + apart.getSqFootage() + " sq ft.");
    }

    public static void cityGetHighestSqFt(){
        City temp = null;
        String city = "x";
        System.out.print("What city would you like to search in?\t");
        boolean flag = false;
        while (flag == false){
            try{
                city = keyboard.readLine();
            } catch (IOException e){
                System.out.println("Error: Unexpected response.");
                System.exit(0);
            }
            for (int i = 0; i < cityList.size(); i++){
                if (city.equals(cityList.get(i).name)){
                    temp = cityList.get(i);
                    flag = true;
                    break;
                }
            }
            if (flag == false){
                System.out.print("Could not find an apartment in the city. Please enter another:\t");
            }
        }
        Apartment apart = temp.SqFtPQ.max();
        System.out.println("The highest sq. footage is " + apart.getAddress() + ", Unit " + apart.getUnit() + ", " + apart.getCity() + " " + apart.getZIP() + " at " + apart.getSqFootage() + " sq ft.");
        }

    public static void readInFile(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("apartments.txt"));
        } catch (FileNotFoundException e){
            System.out.println("Error: File not found. Exiting program.");
            System.exit(0);
        }

        if (scanner != null){
            String ew = scanner.nextLine();
            while (scanner.hasNextLine()){
                String entry = scanner.nextLine();
                Apartment temp = new Apartment();
                formatInfo(entry, temp);
            }
            scanner.close();
        } else{
            System.out.println("Scanner was null.");
        }  

    } // END readInFile()

    public static void formatInfo(String entry, Apartment temp){
        String[] vals = entry.split(":");
        String address = vals[0];
        temp.setAddress(address);
        String unit = vals[1];
        temp.setUnit(unit);
        String city = vals[2];
        temp.setCity(city);
        int ZIP = Integer.parseInt(vals[3]);
        temp.setZIP(ZIP);
        int rent = Integer.parseInt(vals[4]);
        temp.setRent(rent);
        int sqFootage = Integer.parseInt(vals[5]);
        temp.setSqFootage(sqFootage);
        // Add to overall PQs
        sfAparts.insert(temp);
        rentAparts.insert(temp);

        // Adds to City PQs
        boolean flag = false; // assume it's not there
        for (int i = 0; i < cityList.size(); i++){
            if ((cityList.get(i).name).equals(city)){
                flag = true;
                cityList.get(i).RentPQ.insert(temp);
                cityList.get(i).SqFtPQ.insert(temp);
                break;
            }
        }
        if (flag == false){
            City newCity = new City(city);
            cityList.add(newCity);
            newCity.RentPQ.insert(temp);
            newCity.SqFtPQ.insert(temp);
        }    
    }
} // END class AptTracker


class Apartment{
    private String name;
    private int rent;
    private String unit;
    private String address;
    private String city;
    private int ZIP;
    private int sqFootage;

    public Apartment(){
        this(null);
    }
    public Apartment(String entry){
        name = entry;
    }

    // SETTERS
    public void setRent(int num){
        rent = num;
    }
    public void setUnit(String entry){
        unit = entry;
    }
    public void setAddress(String entry){
        address = entry;
    }
    public void setCity(String entry){
        city = entry;
    }
    public void setZIP(int num){
        ZIP = num;
    }
    public void setSqFootage(int num){
        sqFootage = num;
    }

    // GETTERS
    public int getRent(){
        return rent;
    }
    public String getUnit(){
        return unit;
    }
    public String getAddress(){
        return address;
    }
    public String getCity(){
        return city;
    }
    public int getZIP(){
        return ZIP;
    }
    public int getSqFootage(){
        return sqFootage;
    }

    
    // COMPARISON STUFF

}

class RentComparator implements Comparator<Apartment>{
    public int compare(Apartment a1, Apartment a2){
        if (a1 != null && a2 != null){
            if (a1.getRent() < a2.getRent()) return -1;
            else if (a1.getRent() > a2.getRent()) return 1;
            else return 0;
        }
        else{
            return 0;
        }
    }
}

class SqFootageComparator implements Comparator<Apartment>{
    public int compare(Apartment a1, Apartment a2){
        if (a1 != null && a2 != null){ 
            if (a1.getSqFootage() < a2.getSqFootage()) return -1;
            else if (a1.getSqFootage() > a2.getSqFootage()) return 1;
            else return 0;
        }
        else{
            return 0;
        }
    }
}

class City{
    public MinPQ<Apartment> RentPQ;
    public MaxPQ<Apartment> SqFtPQ;
    public String name;

    public City(String n){
        name = n;
        RentComparator rc = new RentComparator();
        SqFootageComparator sc = new SqFootageComparator();
        RentPQ = new MinPQ<Apartment>(rc);
        SqFtPQ = new MaxPQ<Apartment>(sc);
    }
}