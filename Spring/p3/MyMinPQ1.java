/******************************************************************************
 *  Compilation:  javac MyMinPQ1.java
 *  Execution:    java MyMinPQ1 < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/24pq/tinyPQ.txt
 *  
 *  Generic min priority queue implementation with a binary heap.
 *  Can be used with a comparator instead of the natural order.
 *
 *  % java MyMinPQ1 < tinyPQ.txt
 *  E A E (6 left on pq)
 *
 *  We use a one-based array to simplify parent and child calculations.
 *
 *  Can be optimized by replacing full exchanges with half exchanges
 *  (ala insertion sort).
 *
 ******************************************************************************/


import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 *  The {@code MyMinPQ1} class represents a priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with methods for peeking at the minimum Car,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  This implementation uses a <em>binary heap</em>.
 *  The <em>insert</em> and <em>delete-the-minimum</em> operations take
 *  &Theta;(log <em>n</em>) amortized time, where <em>n</em> is the number
 *  of elements in the priority queue. This is an amortized bound
 *  (and not a worst-case bound) because of array resizing operations.
 *  The <em>min</em>, <em>size</em>, and <em>is-empty</em> operations take
 *  &Theta;(1) time in the worst case.
 *  Construction takes time proportional to the specified capacity or the
 *  number of items used to initialize the data structure.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param  the generic type of Car on this priority queue
 */
public class MyMinPQ1 implements Iterable {
    private Car[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue
    private Comparator comparator;  // optional comparator
    private LinkedList[] indirect = new LinkedList[401]; // implementing this for the purpose of keys in the table, closed addressing
    private String model; // ONLY USED TO RESOLVE MODEL COLLISIONS

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public MyMinPQ1(int initCapacity) {
        pq = new Car[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public MyMinPQ1() {
        this(1);
    }

    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order in which to compare the keys
     */
    public MyMinPQ1(int initCapacity, Comparator comparator) {
        this.comparator = comparator;
        pq = new Car[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order in which to compare the keys
     */
    public MyMinPQ1(Comparator comparator) {
        this(1, comparator);
    }

    /**
     * Initializes a priority queue from the array of keys.
     * <p>
     * Takes time proportional to the number of keys, using sink-based heap construction.
     *
     * @param  keys the array of keys
     */
    public MyMinPQ1(Car[] keys) {
        n = keys.length;
        pq = new Car[keys.length + 1];
        for (int i = 0; i < n; i++)
            pq[i+1] = keys[i];
        for (int k = n/2; k >= 1; k--)
            sink(k);
        assert isMinHeap();
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Returns a smallest Car on this priority queue.
     *
     * @return a smallest Car on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Car min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Car[] temp = (Car[]) new Object[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
        // no need to adjust the Indirection Table in this case; should be alright :-)
    }

    /**
     * Adds a new Car to this priority queue.
     *
     * @param  x the Car to add to this priority queue
     */
    public boolean insert(Car x) {
        if (x == null) return false;
        int index = hash(x.getVIN());
        LinkedList list = indirect[index];
        if (list != null && list.size() != 0){  // need to see if it exists already 
            String temp = null;
            for (int i = 0; i < list.size(); i++){
                temp = ((Node)list.get(i)).getVIN();
                if (temp.equals(x.getVIN())){
                    //System.out.println("\tCar already in database. Use '2' to update a car.");
                    return false;
                }
            }
        }

        // double size of array if necessary
        if (n == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[++n] = x;
        Node node = new Node();
        node.setVIN(x.getVIN());
        node.setIndex(n);
        //LinkedList list = indirect[hash(x)];
        if (list == null){ // if null or 0 there's no LL!
            list = new LinkedList();
            //list = indirect[hash(x)]; // I think this is unnecessary but it makes me happy
        }
        list.add(node);
        indirect[hash(x)] = list; // sets list to the indirectinon table! :-)
        setSpot(x, n);
        swim(n);
        assert isMinHeap();
        return true;
    }

    /**
     * Updates a Car in the PQ
     * 
     * @param  x the Car to be updated in the pq
     * @param  new the new Car to be put in the pq
     */
    public Car update(String VIN, Car car, int resp){
        // first, hash the VIN
        // then, remove it from the pq and then re-insert with the new Car object
        // finally, sink and swim the Car
        int index = hash(VIN);
        LinkedList list = indirect[index];
        if (list == null || list.size() == 0){ 
            //System.out.println("Error: Car not in database.");
            return null;
        }
        int llIndex = -1; // need to find a way to get the NODE OF INTEREST
        String temp = null;
        for (int i = 0; i < list.size(); i++){
            temp = ((Node)list.get(i)).getVIN();
            if (temp.equals(VIN)){
                llIndex = i;
                break;
            }
        }

        if (llIndex < 0){
            //System.out.println("Car not in database.\n"); // verifies the VIN exists
            return null;
        }

        Car oldCar = pq[((Node)indirect[index].get(llIndex)).getIndex()]; // found the car!

        if (oldCar == null){
            //System.out.println("Car not in database.\n"); // verifies the VIN exists
            return null;
        }
        // NEED TO REPOPULATE THE FIELDS OF THE CAR!!
        if (resp == 1){ // price was updated
            //System.out.println("updated price apparently???  car has a price of "+car.getPrice());

            //car.setVIN(VIN);
            car.setMileage(oldCar.getMileage());
            car.setMake(oldCar.getMake());
            car.setModel(oldCar.getModel());
            car.setColor(oldCar.getColor());
        } else if (resp == 2){ // mileage was updated
            //System.out.println("updated miles apparently???");
            //car.setVIN(VIN);
            car.setPrice(oldCar.getPrice());
            car.setMake(oldCar.getMake());
            car.setModel(oldCar.getModel());
            car.setColor(oldCar.getColor());
        } else { // color was updated
            //System.out.println("updated color apparently???");
            //car.setVIN(VIN);
            car.setMileage(oldCar.getMileage());
            car.setMake(oldCar.getMake());
            car.setModel(oldCar.getModel());
            car.setPrice(oldCar.getPrice());
        }
        pq[((Node)indirect[index].get(llIndex)).getIndex()] = car; // updated the pq to have the new car
        sink(((Node)indirect[index].get(llIndex)).getIndex());
        swim(((Node)indirect[index].get(llIndex)).getIndex());
        return car;
    }

    public Car get(int i){
        return pq[i];
    }

    public Car remove(String VIN){
        int index = hash(VIN);
        LinkedList list = indirect[index];
        if (list == null){
            //System.out.println("\nError: Car not in database.");
            return null;
        }
        // if (list.size() == 0){
        //     System.out.println("Car not in database.");
        //     return null;
        // }
        int llIndex = -1; // need to find a way to get the NODE OF INTEREST
        String temp = null;
        for (int i = 0; i < list.size(); i++){
            temp = ((Node)list.get(i)).getVIN();
            //System.out.println("i is "+i+" temp is "+temp+" and list size is "+list.size());
            if (temp != null && temp.equals(VIN)){
                //System.out.println("found llIndex in the list");
                llIndex = i;
                break;
            }
        }

        if (llIndex < 0){
            //System.out.println("Car not in database bitch boi.");
            return null;
        }

        int locAt = ((Node)list.get(llIndex)).getIndex(); // returns index in pq of car

        //int locAt = indirect[index];
        if (locAt <= 0) return null;
        Car car = pq[locAt]; // finds the car to be removed
        if (car == null){
            System.out.println("\nError: Car not in database.");
            return null;
        }
        if (locAt == n){
            pq[n] = null; // sets the last value to null
            n--;
            list.remove(llIndex);
            return car;
        }
        exch(locAt, n); // swaps the location of car with the end
        pq[n] = null; // sets the last value to null
        n--;
        sink(locAt);
        swim(locAt);

       list.remove(llIndex);

        return car;
    }

    /**
     * Removes and returns a smallest Car on this priority queue.
     *
     * @return a smallest Car on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Car delMin() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Car min = pq[1];
        exch(1, n--);
        sink(1);
        setSpot(pq[n+1], 0); // resets the indirection table to 0 at the one to be removed
        pq[n+1] = null;     // to avoid loiterig and help with garbage collection
        

        if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length / 2);
        assert isMinHeap();
        return min;
    }


   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

   /***************************************************************************
    * Helper functions for compares and swaps.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable) pq[i]).compareTo(pq[j]) > 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        Car swap = pq[i];
        pq[i] = pq[j];
        setSpot(pq[i], i);
        pq[j] = swap;
        setSpot(pq[j], j);        
    }

    // is pq[1..n] a min heap?
    private boolean isMinHeap() {
        for (int i = 1; i <= n; i++) {
            if (pq[i] == null) return false;
        }
        for (int i = n+1; i < pq.length; i++) {
            if (pq[i] != null) return false;
        }
        if (pq[0] != null) return false;
        return isMinHeapOrdered(1);
    }

    // is subtree of pq[1..n] rooted at k a min heap?
    private boolean isMinHeapOrdered(int k) {
        if (k > n) return true;
        int left = 2*k;
        int right = 2*k + 1;
        if (left  <= n && greater(k, left))  return false;
        if (right <= n && greater(k, right)) return false;
        return isMinHeapOrdered(left) && isMinHeapOrdered(right);
    }


    /**
     * Returns an iterator that iterates over the keys on this priority queue
     * in ascending order.
     * <p>
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator {
        // create a new pq
        private MyMinPQ1 copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new MyMinPQ1(size());
            else                    copy = new MyMinPQ1(size(), comparator);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Car next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }

    /**
     *
     *      Indirection Table Functions
     *              Here, I want to be able to hash a VIN :-)
     *              Also I guess there's other shit to figure out
     *    
    **/

    private int hash(Car car){
        int R = 26 + 10; // Letters in Alphabet plus the 0-9 numbers available
        String VIN = car.getVIN();
        int index = 0;
        for (int i = 0; i < 17; i++){
            index = (R * index + VIN.charAt(i)) % 401;
        }
        return index;
        
    }

    private int hash(String VIN){
        int R = 26 + 10;
        int index = 0;
        for (int i = 0; i < 17; i++){
            index = (R * index + VIN.charAt(i)) % 401;
        }
        return index;
    }

    // private int getSpot(Car car){
    //     int index = hash(car); // tells us where in the table we're going to add this!
    //     return indirect[index]; // the location of the Car in the pq!
    // }

    private void setSpot(Car car, int spot){
       // indirect[hash(car)] = spot; // hashes the VIN, goes to that spot, and puts in the index in the pq to the table


        LinkedList list = indirect[hash(car)];
        if (list == null){ // if null or 0 there's no LL!
            //System.out.println("Car not in the system.");
            return;
        }
        int llIndex = 0; // need to find a way to get the NODE OF INTEREST
        String temp = null;
        for (int i = 0; i < list.size(); i++){
            temp = ((Node)list.get(i)).getVIN();
            if (temp != null && temp.equals(car.getVIN())){
                llIndex = i;
                break;
            }
        }
        ((Node)list.get(llIndex)).setIndex(spot); // finds the VIN in the LL, sets the index to 'spot'
    }

    public String getModel(){
        return model;
    }

    public void setModel(String string){
        this.model = string;
        return;
    }
    // public void printInd(){
    //     for (int i = 0; i < 401; i++){
    //         System.out.print(i+") "+indirect[i]+"\t");
    //         if (i % 4 == 0) System.out.print("\n");
    //     }
    // }

}

class Node{
    String VIN = null;
    int index = 0;

    public void setVIN(String VIN){
        this.VIN = VIN;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public String getVIN(){
        return this.VIN;
    }

    public int getIndex(){
        return this.index;
    }
}

class PriceComparator implements Comparator{
    public int compare(Object car1, Object car2){
        return ((Car)car1).getPrice() - ((Car)car2).getPrice();
    }
} // end PriceComparator

class MileageComparator implements Comparator{
    public int compare(Object car1, Object car2){
        return ((Car)car1).getMileage() - ((Car)car2).getMileage();
    }
} // end MileageComparator