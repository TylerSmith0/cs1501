import java.util.*;
import java.lang.*;
import java.io.*;


public class NetworkAnalysis{
    static final int COPPER_SPEED = 230000000; // meters per second
    static final int FIBER_SPEED = 200000000; // meters per second
    static LinkedList<Vertex>[] graph; // each vertex is going to be put at it's respective position in index, and neighbors and in LL :-)
    static LinkedList<Vertex>[] cGraph; // copper-only version; helps copper-connected check

    public static void main(String[] args){
        // read in graph
        if (args.length > 0) readGraph(args[0]);
        else{System.out.println("Please enter a filename in the command."); System.exit(0);}

        Scanner kbd = new Scanner(System.in); // open Scanner for user entry
        String uEntry = null;
        char entry = 'a'; // initialize the entry char
        while (entry != 'x' && entry != '4'){
            System.out.print("\n\tWhat would you like to do? Press 'h' if you'd like a list of options.\t\t--> ");
            uEntry = kbd.nextLine();
            if (uEntry != null && uEntry.length() > 0){
                entry = uEntry.charAt(0);
            } else {
                entry = ' ';
            }
            
            switch (entry){
                case '1':
                    int st = 0; int end = 0;
                    System.out.print("\n\t\tWhere would you like to start from?\t\t--> ");
                    uEntry = kbd.nextLine();
                    if (uEntry != null){
                        try {
                           st = Integer.parseInt(uEntry);
                        } catch (NumberFormatException e){
                            System.out.print("\n\t\tERROR: Must enter an integer.\n");
                            break;
                        }
                    } else {
                        System.out.print("\n\t\tYou must enter a starting vertex to find the Lowest Latency Path.\n");
                        break;
                    }
                    System.out.print("\n\t\tWhere would you like to start from?\t\t--> ");
                    uEntry = kbd.nextLine();
                    if (uEntry != null){
                        try {
                           end = Integer.parseInt(uEntry);
                        } catch (NumberFormatException e){
                            System.out.print("\n\t\tERROR: Must enter an integer.\n");
                            break;
                        }
                    } else {
                        System.out.print("\n\t\tYou must enter a starting vertex to find the Lowest Latency Path.\n");
                        break;
                    }
                    if (st >= graph.length || end >= graph.length){
                        System.out.print("\n\t\tYou must enter a valid vertex between 0 and "+(graph.length-1)+".\n");
                        break;
                    } else if (st < 0 || end < 0){
                        System.out.print("\n\t\tYou must enter a valid vertex between 0 and "+(graph.length-1)+".\n");
                        break;
                    }
                    getLowestLatency(st, end);
                    break;
                case '2':
                    if (isCopperConnected()){ System.out.println("\n\t\tThe graph is copper-connected.\n"); }
                    else { System.out.println("\n\t\tThe graph is NOT copper-connected.\n"); }
                    break;
                case '3':
                    boolean flag = true;
                    int v1 = 0; int v2 = 0;
                    LinkedList<Integer> wrong = new LinkedList<Integer>();
                    if (graph.length <= 2){
                        System.out.println("\n\t\tThe graph experiences a disconnect if "+v1+" and "+v2+" fail.\n"+
                                            "\t\tTherefore it is NOT connected upon any 2 vertex failures, since it's 2 or less vertices.\n");
                        break;
                    }

                    for (int i = 0; i < graph.length - 1; i++){
                        //System.out.println("~ BEGINNING AT I = "+i+" ~");
                        for (int j = i+1; j < graph.length; j++){
                            //System.out.println("~ BEGINNING AT J = "+j+" ~");
                            if (!isConnected(i, j)){
                                // If we get here, something is not connected
                                flag = false;
                                wrong.add(i); wrong.add(j);
                            }
                        }
                    }

                    if (flag){
                        System.out.println("\n\t\tThe graph is connected, even upon any 2 vertex failures.\n");
                    } else {
                        System.out.println("\n\t\tThe graph is NOT connected upon 2 vertex failures. The following cause disconnect:\n");
                        for (int i = 0; i < wrong.size(); i=i+2){
                            System.out.println("\t\t\tVertices ["+wrong.get(i)+", "+wrong.get(i+1)+"]");
                        }
                        System.out.print("\n");
                    }
                    break;
                case 'c':
                    System.out.println("The graph is connected:\t"+isConnected());
                    break;
                case 'p':
                    printGraph();
                    break;
                case 'h':
                    System.out.print("\n\t\t1. Find Lowest Latency Path"+
                                     "\n\t\t2. Check if Copper-Connected"+
                                     "\n\t\t3. Check if Remains Connected on Failure"+
                                     "\n\t\t4. Exit the Program"+
                                     "\n\t\th. Display options\n\n");
                    break;
                case 'x':
                case '4':
                    System.out.println("\n\t\tGoodbye!");
                    kbd.close();
                    System.exit(0);
            } // end switch(entry)

        } // end while(entry)

    } // end main()

    public static void getLowestLatency(int start, int end){

        if (start == end){
            System.out.println("\n\t\t\tTime to get from "+start+" to "+end+" is 0 ns, since they're the same point.");
            return;
        }

        int[] visited = new int[graph.length]; // builds visited array for my reference
        LinkedList<Integer> edges = new LinkedList<Integer>(); // Creates a queue for my use when doing bfs
        float[] time = new float[graph.length]; // builds a time array to see how long it takes to get to a spot
        for (int i = 0; i < graph.length; i++){
            time[i] = 999999; // assigns a big number to the time[] array
        }
        int[] via = new int[graph.length]; // builds a 'via' array so I can backtrack and see how we got there

        if (start >= graph.length) return; // returns -1 if the start vertex is outside the range of acceptable vertices
        if (end >= graph.length) return; // returns -1 if the end vertex is outside the range of acceptable vertices

        visited[start] = 1; // marks the initial vertex as visited
        time[start] = 0; // assigns a time of 0 to get to the same vertex
        via[start] = start; // assigns the same vertex to that index

        //System.out.println("Starting at vertex "+start);
        //System.out.println("~ Marked vertex "+start+" as visited. ~");
        //System.out.println("~ Marked via["+start+"] = "+start+" ~");

        if (graph[start].size() <= 0) {
            //System.out.println("~ Need to handle no neighbors here! uh ohhhh");
            return;
        }


        // may be able to start loop here ????????????????????????????????????

        Vertex neighbor = null; // temp variable to make code easier
        float min = 999999;
        int next = -1;
        // for (int i = 0; i < graph[start].size(); i++){
        //     neighbor = graph[start].get(i); // loops through every neighbor
        //     if (time[neighbor.getName()] > neighbor.getTime()) time[neighbor.getName()] = neighbor.getTime(); // assigns the time to get to the vertex
        //     via[neighbor.getName()] = start; // got to this vertex from the start!

        //     System.out.println("~ Set time["+neighbor.getName()+"] = "+neighbor.getTime()+" ~");
        //     System.out.println("~ Set via["+neighbor.getName()+"] = "+start+" ~");
        // }

        
        // for (int i = 0; i < time.length; i++){
        //     if (visited[i] == 0 && time[i] < min){
        //         min = time[i]; // keeps track of what the minimum distance is
        //         next = i; // keeps track of what vertex the min is, which will be the next in line
        //     }
        // }
        next = start;
        //System.out.println("~~ Next up to visit: vertex "+next+" ~~");
        visited[next] = 1; // assigns visited to 1 so we don't keep revisiting the same vertex

        while (next != end){ // if next equals end, that means we found it and we're done! woohoo!



            for (int i = 0; i < graph[next].size(); i++){
                neighbor = graph[next].get(i); // loops through every neighbor
                if (visited[neighbor.getName()] == 0 && time[neighbor.getName()] > (time[next] + neighbor.getTime()) ){

                    time[neighbor.getName()] = time[next] + neighbor.getTime(); // assigns the time to get to the vertex
                    via[neighbor.getName()] = next; // got to this vertex from the next!
                   // System.out.println("~ Set time["+neighbor.getName()+"] = "+(time[next]+neighbor.getTime())+" ~");
                    //System.out.println("~ Set via["+neighbor.getName()+"] = "+next+" ~");

                } else if (visited[neighbor.getName()] != 0){
                    //System.out.println("~ Already visited vertex "+neighbor.getName()+" ~");
                } else {
                    //System.out.println("time["+neighbor.getName()+"] = "+time[neighbor.getName()]+"   <   "+(time[next]+neighbor.getTime())+"");
                }
                
            }

            min = 999999;
            next = -1;
            for (int i = 0; i < time.length; i++){
                if (visited[i] == 0 && time[i] < min){
                    min = time[i]; // keeps track of what the minimum distance is
                    next = i; // keeps track of what vertex the min is, which will be the next in line
                }
            }
            //System.out.println("~~ Next up to visit: vertex "+next+" ~~");
            if (next == -1){
                System.out.println("\n\t\t\tGraph is not connected between vertices "+start+" and "+end+".");
                return;
            }
            visited[next] = 1; // assigns visited to 1 so we don't keep revisiting the same vertex    
        }
        // here I need to output the edge progression!
        while (next != start){
            edges.add(0, next);
            edges.add(0, via[next]);
            next = via[next];
        }

        
        System.out.println("\n\t\t\tThe lowest latency path from "+start+" to "+end+" is as follows:\n");
        float temp = 0;
        int low = 999999999; // sets a really big number to be the temporary low
        for (int i = 0; i < edges.size(); i=i+2){
            for (int j = 0; j < graph[edges.get(i)].size(); j++){
                if ( graph[edges.get(i)].get(j).getName() == edges.get(i+1) ){
                    // found the edge! now check if bandwidth is a minimum...
                    if ( graph[edges.get(i)].get(j).getBand() < low ){
                        low = graph[edges.get(i)].get(j).getBand();
                    }
                }
            }
            System.out.println("\t\t\t["+edges.get(i)+", "+edges.get(i+1)+"]  =  "+(1e9*(time[edges.get(i+1)] - temp))+" ns");
            temp = time[edges.get(i+1)];
        }
        System.out.println("\t\t\t----------------------------------------------");
        System.out.println("\t\t\tTotal Time:\t"+(1e9*time[end])+" ns");
        System.out.println("\t\t\tThe minimum bandwidth along this path is "+low+" mbps.");
        return;
    }

    public static boolean isConnected(int v1, int v2){

        // Want to create a "visited" array and use bfs to see if we can see all the vertices

        // arbitrarily starting at 0, because I fucking say so

        int[] visited = new int[graph.length]; // Creates a 'visited' array for my reference
        LinkedList<Integer> q = new LinkedList<Integer>();
        int seen = 1; // how many vertices we've seen
        // Setting v1 and v2 to -1 to say they "failed"
//visited[v1] = -1; visited[v2] = -1;

        // need to decide where to start
        for (int i = 0; i < graph.length; i++){
            if (i != v1 && i != v2){
                visited[i] = 1;
                // already initialized "seen" as 1
                //System.out.println("~ Marking "+i+" as seen for the STARTING POINT ~");
                for (int j = 0; j < graph[i].size(); j++){
                    if (graph[i].get(j).getName() != v1 && graph[i].get(j).getName() != v2){
                        q.add(graph[i].get(j).getName()); // Adds the neighbors of v0 to the queue if they are NOT the failed vertices
                        //System.out.println("~ Adding "+graph[i].get(j).getName()+" to the Queue ~");
                    }
                    else {
                        if (graph[i].get(j).getName() == v1){
                            if (visited[v1] == 0){
                                visited[v1] = 1; // assume it's visited but do not add it's neighbors
                                seen++;
                                if (seen == graph.length) return true;
                            }
                            //System.out.println("~ Assinging "+v1+" to visited, since it's a failure ~");
                        } else {
                            if (visited[v2] == 0){
                                visited[v2] = 1; // assume it's visited but do not add it's neighbors
                                seen++;
                                if (seen == graph.length) return true;
                            }                            
                            //System.out.println("~ Assinging "+v2+" to visited, since it's a failure ~");

                        }
                    }
                }
                break;
            }
        }
        
        // at this point, all neighbors of v0 should be in q
        if (q.size() == 0 && graph.length > 1){ // no vertices in q, AND there are more than 1 vertices
            return false; // nope, not connected
        } else if (graph.length == 1){ return true; } // if.f the graph is of size 1, then obviously it's connected
        
        while (q.size() > 0){ // while the q still has buddies in it
            int v = q.remove(); // v is the vertex we're visiting now
            //System.out.println("~~ POPPING "+v+" FROM Q ~~");
            if (visited[v] == 0){ // ONLY checks if it is 0!

                // Means we haven't been here yet!
                visited[v] = 1; // set this vertex to 'seen'
                //System.out.println("~ Marking "+v+" as seen ~");
                seen++; // increments seen by 1
                if (seen == graph.length) return true; // we've found them all! go team!

                // adding all neighbors to queue
                for (int i = 0; i < graph[v].size(); i++){

                    if (graph[v].get(i).getName() != v1 && graph[v].get(i).getName() != v2){
                        q.add(graph[v].get(i).getName()); // Adds the neighbors of v0 to the queue if they are NOT the failed vertices
                        //System.out.println("~ Adding "+graph[v].get(i).getName()+" to the Queue ~");
                    }
                    else {
                        if (graph[v].get(i).getName() == v1){
                            if (visited[v1] == 0){
                                visited[v1] = 1; // assume it's visited but do not add it's neighbors
                                seen++;                    
                                //System.out.println("~ Assinging "+v1+" to visited, since it's a failure ~");
                                if (seen == graph.length) return true;
                            }
                        } else {
                            if (visited[v2] == 0){
                                visited[v2] = 1; // assume it's visited but do not add it's neighbors
                                seen++;                          
                                //System.out.println("~ Assinging "+v2+" to visited, since it's a failure ~");
                                if (seen == graph.length) return true;
                            }
                        }
                    }
// q.add(graph[v].get(i).getName()); // adds all the neighbors of vertex v to the q
// System.out.println("~ Adding "+graph[v].get(i).getName()+" to the Queue ~");
                }
                // once we get here, we've updated the visited array and added all the neighbors to the queue, so we should be good to just end
            } else {
                //System.out.println("~ Already visited "+v+" ~");
                // We've been here already, dammit

                // don't really need to do anything at this point, we'll just move on to the next one

            }
        }

        return false;
    }

    public static boolean isCopperConnected(){

        // Want to create a "visited" array and use bfs to see if we can see all the vertices

        // arbitrarily starting at 0, because I fucking say so

        int[] visited = new int[cGraph.length]; // Creates a 'visited' array for my reference
        LinkedList<Integer> q = new LinkedList<Integer>();
        visited[0] = 1; // shows that we visited vertex 0
        for (int i = 0; i < cGraph[0].size(); i++){
            q.add(cGraph[0].get(i).getName()); // Adds the neighbors of vertex 0 to the queue!
            //System.out.println("~ Adding "+cGraph[0].get(i).getName()+" to the Queue ~");
        }
        // at this point, all neighbors of v0 should be in q
        if (q.size() == 0 && cGraph.length > 1){ // no vertices in q, AND there are more than 1 vertices
            return false; // nope, not connected
        } else if (cGraph.length == 1){ return true; } // if.f the graph is of size 1, then obviously it's connected
        int seen = 1; // how many vertices we've seen
        while (q.size() > 0){ // while the q still has buddies in it
            int v = q.remove(); // v is the vertex we're visiting now
            //System.out.println("~~ POPPING "+v+" FROM Q ~~");
            if (visited[v] == 0){

                // Means we haven't been here yet!
                visited[v] = 1; // set this vertex to 'seen'
                //System.out.println("~ Marking "+v+" as seen ~");
                seen++; // increments seen by 1
                if (seen == cGraph.length) return true; // we've found them all! go team!

                // adding all neighbors to queue
                for (int i = 0; i < cGraph[v].size(); i++){
                    q.add(cGraph[v].get(i).getName()); // adds all the neighbors of vertex v to the q
                    //System.out.println("~ Adding "+cGraph[v].get(i).getName()+" to the Queue ~");
                }
                // once we get here, we've updated the visited array and added all the neighbors to the queue, so we should be good to just end
            } else {
                //System.out.println("~ Already visited "+v+" ~");
                // We've been here already, dammit

                // don't really need to do anything at this point, we'll just move on to the next one

            }
        }

        return false;
    }

    public static boolean isConnected(){

        // Want to create a "visited" array and use bfs to see if we can see all the vertices

        // arbitrarily starting at 0, because I fucking say so

        int[] visited = new int[graph.length]; // Creates a 'visited' array for my reference
        LinkedList<Integer> q = new LinkedList<Integer>();
        visited[0] = 1; // shows that we visited vertex 0
        for (int i = 0; i < graph[0].size(); i++){
            q.add(graph[0].get(i).getName()); // Adds the neighbors of vertex 0 to the queue!
            //System.out.println("~ Adding "+graph[0].get(i).getName()+" to the Queue ~");
        }
        // at this point, all neighbors of v0 should be in q
        if (q.size() == 0 && graph.length > 1){ // no vertices in q, AND there are more than 1 vertices
            return false; // nope, not connected
        } else if (graph.length == 1){ return true; } // if.f the graph is of size 1, then obviously it's connected
        int seen = 1; // how many vertices we've seen
        while (q.size() > 0){ // while the q still has buddies in it
            int v = q.remove(); // v is the vertex we're visiting now
            //System.out.println("~~ POPPING "+v+" FROM Q ~~");
            if (visited[v] == 0){

                // Means we haven't been here yet!
                visited[v] = 1; // set this vertex to 'seen'
                //System.out.println("~ Marking "+v+" as seen ~");
                seen++; // increments seen by 1
                if (seen == graph.length) return true; // we've found them all! go team!

                // adding all neighbors to queue
                for (int i = 0; i < graph[v].size(); i++){
                    q.add(graph[v].get(i).getName()); // adds all the neighbors of vertex v to the q
                    //System.out.println("~ Adding "+graph[v].get(i).getName()+" to the Queue ~");
                }
                // once we get here, we've updated the visited array and added all the neighbors to the queue, so we should be good to just end
            } else {
                //System.out.println("~ Already visited "+v+" ~");
                // We've been here already, dammit

                // don't really need to do anything at this point, we'll just move on to the next one

            }
        }

        return false;
    }

    // READ IN GRAPH
    private static void readGraph(String filename){
        BufferedReader file = null;
        String line = null;
        // Open BufferedReader to scrape file
        try{
            file = new BufferedReader(new FileReader(new File(filename))); // builds a buffered reader to scrape through the file
        } catch (FileNotFoundException e){
            System.out.print("\n\t\t\tFile not found.\n\n");
            System.exit(0);
        }

        // Take in First Item (should be num of vertices)
        try{
            line = file.readLine();
        } catch (IOException e){
            System.out.println("\n\nError: file.readLine() fatal mistake.");
            System.exit(0);
        }
        

        // Assign num to vertices
        if (line != null){
            int num = Integer.parseInt(line);
            graph = new LinkedList[num];
            cGraph = new LinkedList[num];
            //vertices = new LinkedList<Vertex>();
            for (int i = 0; i < num; i++){
                graph[i] = new LinkedList<Vertex>(); // initializes a LL for every index in the graph
                cGraph[i] = new LinkedList<Vertex>();
                //vertices.addLast(new Vertex(i)); // adds a new Vertex of number i to the end of the LL
            }
        } else {
            System.out.println("Incorrect file format. Expecting Vertices number.");
            System.exit(0);
        }

        // Move line along in file
        try{
            line = file.readLine();
        } catch (IOException e){
            System.out.println("\n\nError: file.readLine() fatal mistake.");
            System.exit(0);
        }
        // Initialize data array for lines
        String[] data;

        // Loop through File
        while (line != null){
            data = line.split(" "); // splits data into respective sections

            //  0       1       2       3       4
            //  v1     v2      type    bps    length
            
            graph[Integer.parseInt(data[0])].add(new Vertex(Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4]))); // Adds Vertex data[1] to the LL at data[0]
            graph[Integer.parseInt(data[1])].add(new Vertex(Integer.parseInt(data[0]), data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4])));

            if (data[2].equals("copper")){
                cGraph[Integer.parseInt(data[0])].add(new Vertex(Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4]))); // Adds Vertex data[1] to the LL at data[0]
                cGraph[Integer.parseInt(data[1])].add(new Vertex(Integer.parseInt(data[0]), data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4])));
            }

            try{
                line = file.readLine();
            } catch (IOException e){
                System.out.println("\n\nError: file.readLine() fatal mistake.");
                System.exit(0);
            }
        }


    } // end readGraph()

    public static void printGraph(){
        System.out.print("\n\n------------------------ ARRAY LIST REPRESENTATION OF GRAPH: ----------------------------\n");
        for (int i = 0; i < graph.length; i++){
            System.out.print("\n graph["+i+"]:  "+i);
            for (int j = 0; j < graph[i].size(); j++){
                System.out.print(" -> "+graph[i].get(j).getName());
            }
        }
        System.out.print("\n\n\n");
    }

} // end NetworkAnalysis class

class Vertex {
    //int from; // Vertex from which you get here
    int name;
    String type; // type of connection (either optical or copper)
    float length; // length of connection (in meters)
    int band; // bandwidth across connection (in MBps)
    float speed; // speed at which the connection allows
    float time; // time it takes to get from 'from' to Vertex in seconds

    public Vertex(/*int fr,*/ int n, String t, int b, float l){
        //this.from = fr;
        this.name = n; // the index 
        this.type = t;
        this.length = l;
        this.band = b;
        if (t.equals("copper")) this.speed = 230000000;
        else this.speed = 200000000;
        this.time = length / speed; // m / (m/s) --> m * s / m --> s to go thru cable
    }

    // Getters

    public int getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public float getLength(){
        return this.length;
    }

    public int getBand(){
        return this.band;
    }

    public float getSpeed(){
        return this.speed;
    }

    public float getTime(){
        return this.time;
    }

} // end Edge class