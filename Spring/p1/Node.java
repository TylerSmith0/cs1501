public class Node{

    /* Initialize a "next" reference and a "child" reference
       as well as a value for the Node itself! */

    private char value;
    private Node next;
    private LinkedList child;

    /*
        #############################################################################################
        #############################################################################################
                                        Constructors
        #############################################################################################
        #############################################################################################
    */

    /* Constructor
        public Node()
        Initializes a new Node with all "null" values for references and the "null" character... Whatever it's called... */
    public Node(){
        next = null;
        child = null;
        value = '\0';
        return;
    }

    /* Constructor
        public Node( char )
        Initializes a new Node with all "null" values for references, but assigns the values of the node to the given character */
    public Node(char uVal){
        next = null;
        child = null;
        value = uVal;
        return;
    }



    /*
        #############################################################################################
        #############################################################################################
                                        Getters / Setters
        #############################################################################################
        #############################################################################################
    */

    public char getValue(){
        return this.value;
    }

    public boolean setValue(char uVal){
        this.value = uVal;
        return true;
    }

    public Node getNext(){
        return this.next;
    }

    public boolean setNext(Node uNext){
        this.next = uNext;
        return true;
    }

    public LinkedList getChild(){
        return this.child;
    }

    public boolean setChild(LinkedList uChild){
        this.child = uChild;
        return true;
    }

}