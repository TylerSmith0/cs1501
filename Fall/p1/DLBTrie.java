import java.util.*;
public class DLBTrie extends Exception{
    private Node root = new Node();

    private static class Node{
       private Node next = null;
       private Node child = null;
       public char value;
       private Node(){
       }
       private Node(char elem){
           value = elem;
       } 
    }// End Node class

    public DLBTrie(){
    }// End Generic Contrustor
    
    public void add(String word){
        if (word != null){
            add(root, word+'^', 0);
        }
        return;
    }// End add(String)
    private void add(Node node, String word, int index){
        char[] chars = word.toCharArray();
        char current;
        if (index < word.length()){
            current = chars[index];
        } else {
            return;
        }
        Node pre = node; //need to initialize in case node is null immediately
        if (index == word.length()){
            return;
        }        
        while (node != null){ //Progresses along the level
            if(node.value == current){
                break;
            }
            pre = node;
            node = node.next;
        }
        if (node == null){ //Need to make sure that the is a node
            pre.next = new Node();
            node = pre.next;
            node.value = chars[index];
        }
        if (node.child == null){// NOW i can check if there's a child... could return true if node OR child are null
            node.child = new Node();
        }
        add(node.child, word, index+1);     
        return;
    }// End add(Node, String, int)


    public String delete(String word){
        String result = delete(root, word+'^', 0);
        return result;
    }
    private String delete(Node node, String word, int index){
        char[] chars = word.toCharArray();
        String result = "";
        while (node != null && index < word.length()){
            if (node.value != chars[index]){
                node = node.next;
            } else if (node.value == chars[index] && index < word.length()-1){
                result = result+chars[index];
                node = node.child;
                index++;
            } else {

                //prev.child = node.next;
                //node.next = null;
                node.value = '\u0000';
                index++;
            }
        }
        if (node == null){
            return null;
        }
        return result;
        
    }

    public String find(String word){
        String result = find(root, word+'^', 0);
        return result;
    }
    private String find(Node node, String word, int index){
        char[] chars = word.toCharArray();
        String result = "";
        while (node != null && index < word.length()){
            if (node.value != chars[index]){
                node = node.next;
            } else if (node.value == chars[index] && index < word.length()-1){
                result = result+chars[index];
                node = node.child;
                index++;
            } else {
                index++;
            }
        }
        if (node == null){
            return null;
        }
        return result;
    }

    public Node searchFor(StringBuilder partialWord){
        Node result = null;
        Node node = root;
        int index = 0;
        char[] chars = partialWord.toString().toCharArray();
        do{
            if (node.value == chars[index]){
                result = node;
                node = node.child;
                index++;
            } else {
                node = node.next;
                result = null;
            }
        } while (node != null && index < partialWord.length());
        return result;
    }

    public void complete(Node node, StringBuilder partialWord, ArrayList<String> guesses){
       
       
        if (node != null){
            // NEED TO FIND THE '^' CHARS maybe based off of the least "distance"?
            Node tempChild = node.child.next; // We're starting at node, need to go .next.next.next and .child.child.child 
        
                
            do{
                if (tempChild == null){
                    // No valid word down this chain, move over
                    complete(node.next, partialWord, guesses);
                    partialWord.deleteCharAt(partialWord.length()-1);
                    return;
                } else if (tempChild.value == '^'){
                    // Valid word!
                    if (guesses.contains(partialWord.toString())){
                        // Don't want to add the same word twice!
                    } else if (guesses.size() < 5){
                        guesses.add(partialWord.toString());
                    } else{
                        // means guesses is probably full!
                        partialWord.deleteCharAt(partialWord.length()-1);
                        return;
                    }
                } else if (tempChild.value == '\u0000'){
                    // No valid word down this chain
                    complete(node.next, partialWord, guesses);
                    return;
                } else {
                        complete(tempChild, partialWord.append(tempChild.value), guesses);
                        // maybe make tempChild next here?
                }

                // Do we try to move tempChild to the next?
                tempChild = tempChild.next;


            } while (tempChild != null && guesses.size() < 5);
            partialWord.deleteCharAt(partialWord.length()-1);
            return;
        } else {
            return;
        }
    }
}