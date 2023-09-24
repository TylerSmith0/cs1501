//import java.lang.StringBuilder.*;
// MUST REMOVE THE ABOVE LINE!!!!!!!!


public class DLB{

    private LinkedList root;
    private int totalWords;
    
    public DLB(){
        root = null;
        totalWords = 0;
    }

    public int getNumWords(){
        return totalWords;
    }

    public LinkedList getRoot(){
        return root;
    }

    public boolean add(String uWord){
        if (uWord == null){
            return false;
        }
        char[] word = new char[uWord.length() + 1];
        char temp[] = uWord.toCharArray();
        for (int i = 0; i < uWord.length(); i++){
            word[i] = temp[i];
        }
        word[uWord.length()] = '^'; // set the "Final Word" indicator, TOTAL WORD IN CHARS NOW
        LinkedList list = root;
        LinkedList oldList = null;
        Node tmp = null;
        for (int i = 0; i < (uWord.length() + 1); i++){
            if (list == null){
                list = new LinkedList();
                if (oldList == null){
                    root = list;
                } else {
                    oldList.get(word[i-1]).setChild(list); // gets the old list, finds the last added node of the previous iteration, and sets it's child to this new ll
                }
            }

            tmp = list.get(word[i]); // tries to find the node w/ the same value; if not, returns NULL
            if (tmp == null){
                list.add(word[i]); // adds to the list
                tmp = list.get(word[i]); // tmp is now the node we just added!
            }
            oldList = list;
            list = tmp.getChild(); // gets the child of the node we found / created and makes it the NEW list... then loop through that one...
        }
        totalWords++;
        return true;
    }

    public String remove(String uWord){
        if (uWord == null){
            return null;
        }
        char[] word = new char[uWord.length() + 1];
        char temp[] = uWord.toCharArray();
        for (int i = 0; i < uWord.length(); i++){
            word[i] = temp[i];
        }
        word[uWord.length()] = '^'; // set the "Final Word" indicator, TOTAL WORD IN CHARS NOWs
        LinkedList list = root;
        StringBuilder result = new StringBuilder();
        //LinkedList oldList = null;
        Node tmp = null;
        for (int i = 0; i < (uWord.length() + 1); i++){
            if (list == null){
                return null;
            }
            tmp = list.get(word[i]); // tries to find the node w/ the same value; if not, returns NULL
            if (tmp == null){
                //System.out.println("String not found. Tmp was null.");
                return null;
            } else if (tmp.getValue() == '^'){
                // found the end carrot! It's here! need to remove it from the LL and decrement the total num of words
                list.remove('^');
                totalWords--;
                result.append(word[i]);
            } else {
                // we found it! get it's kid and keep looping
                result.append(word[i]);
            }
            list = tmp.getChild(); // gets the child of the node we found / created and makes it the NEW list... then loop through that one...
        }
        return result.toString();
    }


    public String[] predict(String uWord, int spotsOpen, String[] predictions){ // WANT TO RETURN THE STRING ARRAY
        // find the number of strings in predictions
        // spotsOpen now has HOW MANY WORDS WE NEED
        LinkedList list = root; // starting at the root
        Node temp = null; // initialize the letter node to null
        StringBuilder result = new StringBuilder();
        result.append(uWord); // starts at the uWord
        char[] word = uWord.toCharArray(); // user's given word, need to iterate to the node of the last character!
        for (int i = 0; i < uWord.length(); i++){
            temp = list.get(word[i]); // Gets the given letter node (if possible)
            if (temp == null){ // letter not in the set! Give up
                return predictions;
            }
            // If we get here, we found the letter and are at temp.
            list = temp.getChild(); // the new list is going to be the LL that's the CHILD of the given letter!

            // Once we get to the last child, list will be the next set and / or null!
        }
        // At this point, TEMP is the last character node in uWord, and LIST is the child LL (if there is one, null otherwise)
        if (list == null){ // no children! Therefore there's no possible words to predict...
            //System.out.println("unable to find a child for "+temp.getValue()+", returning.");
            return predictions;
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // now we search...
        temp = list.getHead();
        for (int i = 0; i < list.getLength(); i++){
            if (temp.getValue() == '^'){ // we found a word! yay!
                boolean flag = true; // word is not already in predictions
                for (int j = 0; j < 5; j++){
                    if (result.toString().equals(predictions[j])){
                        flag = false;
                    }
                }
                if (flag){
                    predictions[5-spotsOpen] = result.toString(); // assigns the result to the predictions array
                    spotsOpen--;
                }
                 // Decrements the spots open
                result = new StringBuilder(uWord); // re-initialize a new builder
            } else { // want to recursively move down the chain
                result.append(temp.getValue()); // adds the character to the end of the word
                //System.out.println("Jumping into another predict with "+result.toString());
                predictions = predict(result.toString(), spotsOpen, predictions); // recursively call itself!
                // need to update spots open!
                spotsOpen = 0;
                for (int j = 0; j < 5; j++){
                    if (predictions[j] == null){
                        spotsOpen++; // increment spots open in pred[j] is null
                    }
                }
                //System.out.print(result.toString()+" --> ");
                result.delete(result.length()-1, result.length()); // removes the last letter in the result
                //System.out.println(result.toString());
            }
            if (spotsOpen == 0){
                //System.out.println("Returning because out of predictions!");
                return predictions; // end the loop early! get out of there!
            }
            
            temp = temp.getNext(); // moves on to the next in line, UNTIL THERE ARE NONE LEFT (meaning we've hit the .getLength() limit)
        }
        //System.out.println("Returning; Ran out of for loop");
        return predictions; // nothing else we can do! need to return


        
    }














} // End DLB