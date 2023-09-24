public class LinkedList{

    private Node head;
    private int length;

    /*
        Method Summary:
            



    */

    public LinkedList(){
        head = null;
        length = 0;
    }

    public LinkedList(char uChar){
        this(new Node(uChar));
    }

    public LinkedList(Node uHead){
        head = uHead;
        length = 1;
    }

    public int getLength(){
        return this.length;
    }

    public Node getHead(){
        return this.head;
    }

    
    public void setHead(char uChar){
        Node temp = new Node(uChar);
        setHead(temp);
        return;
    } //////////////////////////////
    private void setHead(Node uHead){
        if (head == null){
            length++;
        }
        head = uHead;
        return;
    }


    public Node remove(char uChar){
        Node temp = new Node(uChar);
        return remove(temp);
    } /////////////////////////////
    private Node remove(Node uNode){
        Node result = null;
        if (head == null){ // LL is empty; can return null
            // No need for any code here
        } else if (head.getValue() == uNode.getValue()){ // found the uNode! make head the next one, and return the oldhead
            result = head;
            length--;
            head = head.getNext(); // Relinks the head of the LL to the next in line (if there is one)
        } else if (!contains(uNode.getValue())){
            return null; // not in the list!
        } else { // LL is not empty, and 
            Node prev = head;
            Node temp = head.getNext();
            for (int i = 1; i < length; i++){
                if (temp.getValue() == uNode.getValue()){
                    result = temp;
                    prev.setNext(temp.getNext()); // sets the PREVIOUS node's next to the TEMP'S next
                    temp = null; // allow garbage collection
                    length--;
                    break;
                }
                temp = temp.getNext();
                prev = prev.getNext();
            }
        }
        return result;
    }


    public boolean add(char uChar){
        Node temp = new Node(uChar);
        return add(temp);
    } //////////////////////////////
    public boolean add(Node uNode){
        if (head == null){
            setHead(uNode);
            return true;
        } else if (!contains(uNode)){
            Node temp = head;
            for (int i = 1; i < length; i++){
                temp = temp.getNext();
            }
            temp.setNext(uNode);
            length++;
            return true;
        } else {
            //System.out.println("LL already has "+uNode.getValue());
            return false;
        }
    }



    public boolean contains(char uChar){
        Node temp = new Node(uChar);
        return contains(temp);
    } ///////////////////////////////////
    private boolean contains(Node uNode){
        if (head == null){
            return false;
        } else {
            Node temp = head;
            for (int i = 0; i < length; i++){
                if (temp.getValue() == uNode.getValue()){
                    return true;
                }
                temp = temp.getNext();
            }
            return false;
        }
    }



    public Node get(char uChar){
        if (length == 0){
            return null;
        } else {
            Node temp = head;
            for (int i = 0; i < length; i++){
                if (temp.getValue() == uChar){
                    return temp; // return the node that the char is at
                }
                temp = temp.getNext(); // increment to next in list
            }
            return null;
        }
    }


}