/* Tyler Smith (tns17@pitt.edu)
 * Project 1 - AutoComplete Implementation
 * CS 1501 - Algorithm Implementations
 * 
 * In this implementation, I will use a DLB trie to store a dictionary file. This trie will then be
 * used to complete an autofill feature that a user will enter characters one by one. The program 
 * will output 5 or less suggestions based on what the user enters, as well as other corner cases 
 * regarding special characters.
 * 
 * There also will be an output file that checks the user's past words entered. This will ALSO be a 
 * DLB trie, which will be looked at FIRST before the dictionary to try to prioritize the words the 
 * user uses.
 * 
 * Oh baby, this is going to be fun. Let's Rock and Roll.
 */

import java.lang.*;
import java.util.*;
import java.io.*;
public class ac_test{
    public static void main(String[] args){
       DLBTrie dictionary = new DLBTrie();
       DLBTrie user = new DLBTrie();
       ArrayList<String> guesses = new ArrayList<String>();
       StringBuilder partialWord = new StringBuilder();
       ArrayList<String> userWords = new ArrayList<String>();
       char entry;
       String fullWord = null;
        long begin = 0;
        long end = 0;
        long time = 0;
        double timed = 0;
        double total = 0;
        int numTime = 0;
       do{
        dictionary = loadDictionary(dictionary);
        } while (dictionary == null);
        loadUser(user, userWords);
        System.out.print("Enter your first character:   ");
        entry = loadEntry();
       do{
       /* try{
            FileWriter fileOutput = new FileWriter(new File("tyler_diction.txt"));
            fileOutput.write(dictionary.)
        } catch (IOException e){
            System.out.println("Couldn't print output file. IO Error thrown.");
        }*/
        
         if (entry == '1' && guesses.size() > 0){
             fullWord = guesses.get(0);
             System.out.println("\n\nWORD COMPLETED:\t\t"+fullWord);
             user.add(fullWord);
             userWords.add(fullWord);
             partialWord.delete(0, partialWord.length());
             System.out.print("Enter the first character of the next word:  ");

         } else if (entry == '2' && guesses.size() > 1){
             fullWord = guesses.get(1);
             System.out.println("WORD COMPLETED:\t\t"+fullWord);
             user.add(fullWord);
             userWords.add(fullWord);
             partialWord.delete(0, partialWord.length());
             System.out.print("Enter the first character of the next word:  ");

         } else if (entry == '3' && guesses.size() > 2){
             fullWord = guesses.get(2);
             System.out.println("\n\nWORD COMPLETED:\t\t"+fullWord);
             user.add(fullWord);
             userWords.add(fullWord);
             partialWord.delete(0, partialWord.length());
             System.out.print("Enter the first character of the next word:  ");

         } else if (entry == '4' && guesses.size() > 3){
             fullWord = guesses.get(3);
             System.out.println("\n\nWORD COMPLETED:\t\t"+fullWord);
             user.add(fullWord);
             userWords.add(fullWord);
             partialWord.delete(0, partialWord.length());
             System.out.print("Enter the first character of the next word:  ");

         } else if (entry == '5' && guesses.size() > 4) {
             fullWord = guesses.get(4);
             System.out.println("\n\nWORD COMPLETED:\t\t"+fullWord);
             user.add(fullWord);
             userWords.add(fullWord);
             partialWord.delete(0, partialWord.length());
             System.out.print("Enter the first character of the next word:  ");

         } else if (entry == '!'){
             double avgTime = total/(double)numTime;
             System.out.println("\n\n\nAverage Time = "+avgTime);
             System.out.println("Bye!");
             userWrite(userWords);
             System.exit(0);
         } else if (entry == '$'){
             // Need to add a user history and restart the program
            partialWord.deleteCharAt(partialWord.length()-1);
            user.add(partialWord.toString());
            userWords.add(partialWord.toString());
            partialWord.delete(0, partialWord.length());
            System.out.print("Enter the first character of the next word:   ");
         } else{
            partialWord.append(entry);
            guesses = new ArrayList<String>();
            begin = System.nanoTime();
            generateGuess(guesses, dictionary, user, partialWord);
            end = System.nanoTime();
            time = (end - begin);
            timed = (double)time/1000000000;
            total = total + timed;
            numTime++;
            System.out.println("("+timed+" s)");
            System.out.println("Predictions:\n");
            if (guesses.size() == 0){
                System.out.println("No Predictions Found.");
            }
            for (int i = 0; i < guesses.size(); i++){
                System.out.print("("+(i+1)+") "+guesses.get(i)+"\t");
            }
            System.out.println("\n________________________________________\n\n");
            partialWord.append(entry);
            System.out.print("Enter your next character:   ");
         }
         entry = loadEntry();
         System.out.println("\n");
         
       } while (true);
     } //End main() block

     public static DLBTrie loadDictionary(DLBTrie dictionary){
        try{
            File dictFile = new File("dictionary.txt");
            Scanner reader = new Scanner(dictFile);
            while (reader.hasNextLine()){
                String word = reader.nextLine();
                dictionary.add(word);
            }
            reader.close();
        } catch (FileNotFoundException f){
            System.out.println("File not found. Try again.");
            dictionary = null;
        }
        return dictionary;
     }

     public static void loadUser(DLBTrie user, ArrayList<String> userWords){
        try{
            File userFile = new File("user_history.txt");
            Scanner reader = new Scanner(userFile);
            while (reader.hasNextLine()){
                String word = reader.nextLine();
                user.add(word);
                userWords.add(word);
            }
            reader.close();
        } catch (FileNotFoundException f){
            System.out.println("User history not found.");
        }
     }

     public static char loadEntry(){
         String entryS = null;
         char entry = '\u0000';
         Scanner keyboard = new Scanner(System.in);
         do{
            entryS = keyboard.next();
            if (entryS != null){
                char[] chars = entryS.toCharArray();
                entry = chars[0];
            } else{
                System.out.println("Please enter a single character.");
            }
         } while (entryS == null);
         return entry;
     }

     public static void generateGuess(ArrayList<String> guesses, DLBTrie dictionary, DLBTrie user, StringBuilder partialWord){
        StringBuilder userPartialWord = new StringBuilder();
        userPartialWord = userPartialWord.append(partialWord.toString());
        user.complete(user.searchFor(userPartialWord), userPartialWord, guesses);
        dictionary.complete(dictionary.searchFor(partialWord), partialWord, guesses);
     }

     public static void userWrite(ArrayList<String> userWords){
            try{
                System.out.println("Trying to write out");
                FileWriter output = new FileWriter("user_history.txt");
                for (int j = 0; j < userWords.size(); j++){
                    output.write(userWords.get(j)+"\n");
                }
                System.out.println("Finished writing out!");
                output.close();
            } catch (IOException e){
                System.out.println("Error writing out. Closing program.");
            }

     }
} //End class block
