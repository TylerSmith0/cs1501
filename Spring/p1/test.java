import java.io.*;
import java.util.*;

public class test{
    
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
        
        //count--;
        System.out.println("We made it through the close! Woohoo! There are "+count+" words.");
        try {
            reader.close();
        } catch (IOException e){
            System.out.println("Reader already terminated, or never opened to begin with.");
        }






/*
        try {
            reader = new BufferedReader(new FileReader("dictionary.txt"));
        } catch (FileNotFoundException e){
            System.out.println("Error: File not found. Exiting program.");
            System.exit(0);
        }
        word = null;
        do {
            try {
                word = reader.readLine();
                
                if (word != null){
                    count--;
                    //System.out.println(word);
                    testing.remove(word);
                }
                //testing.add(word);
            } catch (IOException e){
                System.out.println("Error in file");
            }
        } while (word != null);
        
        //count--;
        System.out.println("We made it through the removal! Woohoo! There are "+count+" words.");
        try {
            reader.close();
        } catch (IOException e){
            System.out.println("Reader already terminated, or never opened to begin with.");
        }
*/
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
                        System.out.println("Adding word to user history.");
                        user_history.add(total.toString());
                        total = new StringBuilder();
                    } else {
                        System.out.println("No word recognized. Cannot add an empty String.");
                    }
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '1':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[0]+"\n");
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '2':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[1]+"\n");
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '3':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[2]+"\n");
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '4':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[3]+"\n");
                    total = new StringBuilder();
                    System.out.print("Please enter the first letter of the next word:\t");
                    break;
                case '5':
                    System.out.println("\n\nWORD COMPLETED:\t"+predictions[4]+"\n");
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
        


        
        // Node node1 = new Node();
        // Node node2 = new Node('a');
        // Node node3 = new Node('a');

        // System.out.println("node1 has a value of "+node1.getValue()+", a next of "+node1.getNext()+", and a child of "+node1.getChild()+".");
        // System.out.println("node2 has a value of "+node2.getValue()+", a next of "+node2.getNext()+", and a child of "+node2.getChild()+".");
        // System.out.println("node3 has a value of "+node3.getValue()+", a next of "+node3.getNext()+", and a child of "+node3.getChild()+".");

        // node1.setValue('a');
        // node2.setValue('b');
        // node3.setValue('^');

        // System.out.println("node1 has a value of "+node1.getValue()+", a next of "+node1.getNext()+", and a child of "+node1.getChild()+".");
        // System.out.println("node2 has a value of "+node2.getValue()+", a next of "+node2.getNext()+", and a child of "+node2.getChild()+".");
        // System.out.println("node3 has a value of "+node3.getValue()+", a next of "+node3.getNext()+", and a child of "+node3.getChild()+".");

        // node1.setNext(node2);
        // node2.setNext(node3);
        // node3.setNext(node1);

        // System.out.println("node1 has a value of "+node1.getValue()+", a next of "+node1.getNext()+", and a child of "+node1.getChild()+".");
        // System.out.println("node2 has a value of "+node2.getValue()+", a next of "+node2.getNext()+", and a child of "+node2.getChild()+".");
        // System.out.println("node3 has a value of "+node3.getValue()+", a next of "+node3.getNext()+", and a child of "+node3.getChild()+".");

        // LinkedList LL1 = new LinkedList('C');
        // LinkedList LL2 = new LinkedList('B');
        // LinkedList LL3 = new LinkedList('A');

        // node1 = new Node('A');
        // node2 = new Node('B');
        // node3 = new Node('C');

        // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1 = new LinkedList(node1);
        // LL2 = new LinkedList(node2);
        // LL3 = new LinkedList(node3);

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1.setHead('D');
        // LL2.setHead('E');
        // LL3.setHead('F');

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1.add('A');
        // LL2.add('A');
        // LL3.add('A');

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1.add('B');
        // LL2.add('B');
        // LL3.add('B');

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1.add('C');
        // LL2.add('C');
        // LL3.add('C');

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");

        // LL1.remove('A');
        // LL2.remove('A');
        // LL3.remove('A');
        // LL1.remove('B');
        // LL2.remove('B');
        // LL3.remove('B');
        // LL1.remove('C');
        // LL2.remove('C');
        // LL3.remove('C');
        // LL1.remove('D');
        // LL2.remove('E');
        // LL3.remove('F');


        // Node nodeA = new Node('A');
        // Node nodeB = new Node('B');
        // Node nodeC = new Node('C');
        // Node nodeD = new Node('D');

        // LL1.add(nodeA);
        // LL1.add(nodeB);
        // LL1.add(nodeC);
        // LL1.add(nodeD);

        // // System.out.println("LL1 has a head of "+LL1.getHead().getValue()+" and a length of "+LL1.getLength()+".");
        // // System.out.println("LL2 has a head of "+LL2.getHead().getValue()+" and a length of "+LL2.getLength()+".");
        // // System.out.println("LL3 has a head of "+LL3.getHead().getValue()+" and a length of "+LL3.getLength()+".");
       
        // System.out.println("NodeA:\t"+nodeA);
        // System.out.println("NodeB:\t"+nodeB);
        // System.out.println("NodeC:\t"+nodeC);
        // System.out.println("NodeD:\t"+nodeD);

        // System.out.println("Getting nodes for LL1:\n");
        // char temp[] = new char[4];
        // temp[0] = 'A';
        // temp[1] = 'B';
        // temp[2] = 'C';
        // temp[3] = 'D';
        // for (int i = 0; i < LL1.getLength(); i++){
        //     System.out.println("Node "+i+":\t"+LL1.get(temp[i]));
        // }

        
        // testing.add("hello");
        // testing.add("hell");
        // testing.add("he");


    }
}