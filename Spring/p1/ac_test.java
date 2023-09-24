import java.io.*;
import java.util.Scanner;
public class ac_test{
    
    public static void main(String[] args){
        
        // First want to try to read in the dictionary
        //File dictionaryFile = new File("dictionary.txt");
        DLB testing = new DLB();
        DLB user_history = new DLB();
        int open = 0;
        long totalTime = 0;
        int charEntered = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("dictionary.txt"));
        } catch (FileNotFoundException e){
            System.out.println("Error: File not found. Exiting program.");
            System.exit(0);
        }
        String word = null;
        int count = 0;
        do {
            try {
                word = reader.readLine();
                
                if (word != null){
                    count++;
                    //System.out.println(word);
                    testing.add(word);
                }
                //testing.add(word);
            } catch (IOException e){
                System.out.println("Error in file");
            }
        } while (word != null);
        
        try {
            reader.close();
        } catch (IOException e){
            System.out.println("Reader already terminated, or never opened to begin with.");
        }

        try {
            reader = new BufferedReader(new FileReader("user_history.txt"));

            word = null;
            count = 0;
            do {
                try {
                    word = reader.readLine();
                    
                    if (word != null){
                        count++;
                        //System.out.println(word);
                        user_history.add(word);
                    }
                    //testing.add(word);
                } catch (IOException e){
                    System.out.println("Error in file");
                }
            } while (word != null);
            try {
                reader.close();
            } catch (IOException e){
                System.out.println("Reader already terminated, or never opened to begin with.");
            }
        } catch (FileNotFoundException e){
            //System.out.println("Error: File not found. Exiting program.");
            //System.exit(0);
            File userFile = new File("user_history.txt");
        }

        char uChar = '\0';
        word = null; // resets word to null
        Scanner user = new Scanner(System.in);
        StringBuilder total = new StringBuilder();
        String[] predictions = {null, null, null, null, null}; // initialize to all nulls
        int num = 0;
        long start = 0;
        long time = 0;
        System.out.print("Please enter the first letter of a word:\t");
        while (uChar != '!'){
            word = user.nextLine();
            start = System.nanoTime();
            uChar = word.charAt(0); // only takes first character
            switch (uChar){
                case '!':
                    long avgTime = totalTime / charEntered;
                    System.out.println("\n\nAverage Time:\t"+avgTime*10e-6+" msec");
                    System.out.println("Exiting Program.");
                    System.exit(0);
                case '$':
                    if (total.toString() != null){
                        //System.out.println("Adding word to user history.");
                        user_history.add(total.toString());
                        writeOut(total.toString());
                        total = new StringBuilder();
                    } else {
                        //System.out.println("No word recognized. Cannot add an empty String.");
                    }
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '1':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[0]+"\n");
                    user_history.add(predictions[0]);
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '2':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[1]+"\n");
                    user_history.add(predictions[1]);
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '3':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[2]+"\n");
                    user_history.add(predictions[2]);
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '4':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[3]+"\n");
                    user_history.add(predictions[3]);
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '5':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[4]+"\n");
                    user_history.add(predictions[4]);
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                default:
                    charEntered++;
                    for (int i = 0; i < 5; i++){
                        predictions[i] = null; // reset the predictions
                    }
                    total.append(uChar);
                    open = 5;
                    if (user_history.getRoot() != null){ // ensures there is a user history (if not, skips)
                            predictions = user_history.predict(total.toString(), 5, predictions);
                        for (int i = 0; i < 5; i++){
                            if (predictions[i] != null){
                                open--;
                            }
                        }
                    }
            
                    predictions = testing.predict(total.toString(), open, predictions);
                    open = 0; // resets the number open
                    for (int j = 0; j < 5; j++){
                        if (predictions[j] != null){
                            num++;
                        }
                    }
                    time = System.nanoTime() - start;
                    totalTime = totalTime + time;
                    System.out.println("\n("+time*10e-6+") msec\n\nPredictions are as follows:\n");
                    if (num == 0){
                        System.out.print("-- no predictions found --");
                    } else {
                        for (int i = 1; i < num+1; i++){
                            System.out.print(i+") "+predictions[i-1]+"\t\t");
                        }
                    } 
                    num = 0;
                    System.out.print("\n\n\n\nEnter a character:\t");
            }
        }
    }//main()

    public static void writeOut(String word){
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter("user_history.txt", true));
            output.append(word); // This will keep copies but I don't really care, the add() in the DLB catches that
            output.append("\n");
            output.close();
        } catch (IOException e){
            System.out.println("Error: Cannot write to file.");
        }
        return;
    }
}//ac_test()