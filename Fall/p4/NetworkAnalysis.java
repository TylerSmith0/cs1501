import java.util.*;
import java.io.*;
public class NetworkAnalysis{
    private static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    private static Scanner kb = null;

    public static void loadGraph(String entry){
        Scanner scanner = null;
        try{
            scanner = new Scanner(new File(entry));
            int vertNum = Integer.parseInt(scanner.nextLine()); // Gets the total number of vertices, then creates them.
            for (int i = 0; i < vertNum; i++){
                Vertex temp = new Vertex(i);
                vertices.add(temp);
                //System.out.println("Adding "+temp.getName()+" to vertices.");
            }
        } catch (FileNotFoundException e){
            System.out.println("Error: Could not find file in directory. Exiting program.");
            System.exit(0);
        }
        while (scanner.hasNext()){
            StringTokenizer token = new StringTokenizer(scanner.nextLine(), " ");
            int src = Integer.parseInt(token.nextToken());
            int dst = Integer.parseInt(token.nextToken());
            String type = token.nextToken();
            int band = Integer.parseInt(token.nextToken());
            int length = Integer.parseInt(token.nextToken());
            Edge tmp1 = new Edge(src, dst, type, band, length);
            vertices.get(src).addEdge(tmp1); // adds initial direction edge
            Edge tmp2 = new Edge(dst, src, type, band, length);
            vertices.get(dst).addEdge(tmp2); // adds reverse direction edge
            //System.out.println("Added edge as follows to Vertex "+vertices.get(src).getName()+":\n"+src+" "+dst+" "+type+" "+band+" "+length);
        }
        scanner.close();
        /*System.out.println("Some stats on the vertices loaded in:");
        for (int i = 0; i < vertices.size(); i++){
            System.out.println("Vertex "+i+" has "+vertices.get(i).getEdgeNum()+" edges.");
        }*/
    } // END loadGraph()

    public static void lowestLatencyPath(){
        System.out.println("\n");
        int src = -1;
        int dst = -1;
        while (true){
            System.out.print("What vertex would you like to begin the path at?\t");
            try{
                src = Integer.parseInt(kb.nextLine());
                if (src < vertices.size()){
                    break;
                } else {
                    System.out.println("Must select a vertex between 0 and "+(vertices.size()-1)+".\n");
                }
            } catch (NumberFormatException e){
                System.out.println("Error: Must enter an integer between 0 and "+(vertices.size()-1)+".\n");
            }  
        }
        while (true){
            System.out.print("What vertex would you like to end at?\t");
            try{
                dst = Integer.parseInt(kb.nextLine());
                if (dst < vertices.size()){
                    break;
                } else {
                    System.out.println("Must select a vertex between 0 and "+(vertices.size()-1)+".\n");
                }
            } catch (NumberFormatException e){
                System.out.println("Error: Must enter an integer between 0 and "+(vertices.size()-1)+".\n");
            }  
        }

        if (src == dst){
            System.out.println("\nSame vertex. Time is 0 seconds.");
            return;
        }

        //System.out.println("Looking to get a path from "+src+" to "+dst+".");
        ArrayList<LinkedList<Edge>> paths = new ArrayList<LinkedList<Edge>>();
        int[] visited = new int[vertices.size()];
        LinkedList<Edge> current = new LinkedList<Edge>(); // This is where I'll iterate a path
        paths = lowestLatencyPath(src, dst, current, paths, visited);

        //System.out.println("Paths has "+ paths.size() + " different paths to the destination.");
        double minTime = 99999999999999999.9;
        int index = -1;
        for (int i = 0; i < paths.size(); i++){
            double temp = 0.0;
            for (int j = 0; j < paths.get(i).size(); j++){
                temp = temp + paths.get(i).get(j).getTime();
            }
            if (temp < minTime){
                minTime = temp;
                index = i;
            }
        }
        if (index != -1){
            System.out.println("\nThe minimum time to get from Vertex "+src+" to Vertex "+dst+" is "+minTime+" seconds.");
            System.out.println("\nPATH:");
            int band = 999999999;
            //System.out.println("The size of the path chosen is "+paths.get(index).size());
            for (int i = 0; i < paths.get(index).size(); i++){
                if (band > paths.get(index).get(i).getBandwidth()){
                    band = paths.get(index).get(i).getBandwidth();
                }
                System.out.print("  "+paths.get(index).get(i).getFrom()+"-"+paths.get(index).get(i).getTo()+" ("+paths.get(index).get(i).getTime()+" sec)\n");
            }
            System.out.println("");
            System.out.println("The bandwidth allowed along the path is "+band+" Mbps.");
        } else {
            System.out.println("There is no path from Vertex "+src+" to Vertex "+dst+".");
        }
    }

    public static ArrayList<LinkedList<Edge>> lowestLatencyPath(int src, int dst, LinkedList<Edge> current, ArrayList<LinkedList<Edge>> paths, int[] visited){
        visited[src] = 1;
        for (int i = 0; i < vertices.get(src).getEdgeNum(); i++){ // Loops through ALL edges
            if (src == dst){ // IF we find the vertex!
                //System.out.println("Found the vertex!");
                //System.out.println("Adding "+current.getFirst().getFrom()+"-"+current.getLast().getTo()+" to paths.");
                LinkedList<Edge> pathAdded = new LinkedList<Edge>();
                for (int j = 0; j < current.size(); j++){
                    pathAdded.add(current.get(j));
                }
                paths.add(pathAdded);
                /*current.add(vertices.get(index).getEdge(i));
                paths.add(current);
                current.remove(vertices.get(index).getEdge(i));*/
                visited[src] = 0;
                return paths;
            } else if (visited[vertices.get(src).getEdge(i).getTo()] == 0) { // ONLY if we haven't visited already!
                //System.out.println("Adding edge "+vertices.get(src).getEdge(i).getFrom()+vertices.get(src).getEdge(i).getTo()+".");
                current.add(vertices.get(src).getEdge(i)); // adds first edge to current
                paths = lowestLatencyPath(vertices.get(src).getEdge(i).getTo(), dst, current, paths, visited); // recursively call itself
                current.remove(vertices.get(src).getEdge(i));
                //System.out.println("Removed an edge from current.");
            }
        }
        // if it gets outside the for loop, then there are no paths to return to, so return.
        visited[src] = 0;
        return paths;
    } // END lowestLatencyPath()

    public static boolean copperOnlyConnection(){
        return copperPath(vertices.get(0));
    } // END copperOnlyConnection()

    public static boolean copperPath(Vertex src){
        int[] checks = new int[vertices.size()];
        Vertex current = src;
        checks[current.getName()] = 1; // Sets current vertex to "seen"
        Queue<Edge> edgeQ = new LinkedList<Edge>();
        Edge temp = new Edge(src.getName(), src.getName(), "copper", 1, 1);
        edgeQ.add(temp);
        while (!edgeQ.isEmpty()){
            current = vertices.get(edgeQ.remove().getTo()); // changes the current vertex to the next in the BFS
            for (int i = 0; i < current.getEdgeNum(); i++){
                if (current.getEdge(i).getType().equals("copper") && checks[current.getEdge(i).getTo()] == 0){
                    edgeQ.add(current.getEdge(i)); // Adds all edges to the queue
                    checks[current.getEdge(i).getTo()] = 1; // Flags the TO vertex to say it's been reached
        }   }   }
        for (int i = 0; i < checks.length; i++){
            if (checks[i] == 0) return false;
        }
        return true;
    }

    private static void MALST(){
        Comparator compare = new sortWeights();
        PriorityQueue<Edge> allEdges = new PriorityQueue<Edge>(1, compare);
        for (int i = 0; i < vertices.size(); i++){
            for (int j = 0; j < vertices.get(i).getEdgeNum(); j++){
                //System.out.println("Adding edge ("+vertices.get(i).getEdge(j).getFrom()+"-"+vertices.get(i).getEdge(j).getTo()+")");
                allEdges.add(vertices.get(i).getEdge(j)); // adds EVERY edge to the PQ based on weight
            }
        }

        int[] check = new int[vertices.size()]; // creates check array to see what vertices have been seen
        LinkedList<Edge> MST = new LinkedList<Edge>(); // creates a linked list where I'll keep the path
        //System.out.println("The Edge at the top of this PQ is "+allEdges.peek().getFrom()+" "+allEdges.peek().getTo()+" "+allEdges.peek().getType()+" "+allEdges.peek().getBandwidth()+" "+allEdges.peek().getLength()+".");
        //System.out.println("Looping through the addition "+allEdges.size()+" times.");
        int size = allEdges.size();
        for (int i = 0; i < size; i++){
            Edge temp = allEdges.remove(); // removes the lowest weight from the PQ
            //System.out.println("Checking Edge ("+temp.getFrom()+"-"+temp.getTo()+")");
            if (i == 0){ // need to mark both the FROM and TO vertices if i = 0
                check[temp.getFrom()] = 1; check[temp.getTo()] = 1; // Marks both the from and to vertices as seen
                //System.out.println("For i=0, Adding Edge "+temp.getFrom()+"-"+temp.getTo()+" to MST.");
                MST.add(temp); // adds temp to the MST
            } else if (check[temp.getTo()] == 1){ // eliminates the first edge getting double-added, and ensures the vertex is UNVISITED
                //System.out.println("Edge ("+temp.getFrom()+"-"+temp.getTo()+") already in MST. Vertex "+temp.getTo()+"already seen.");
            } else {
                check[temp.getTo()] = 1;
                //System.out.println("Adding Edge "+temp.getFrom()+"-"+temp.getTo()+" to MST.");
                MST.add(temp);
            }
        } // Loops through all edges, then we need to check!

        for (int i = 0; i < check.length; i++){
            if (check[i] == 1){
                //System.out.println("Checking "+i);
            } else { // if any of the check 
                System.out.println("\nThere is no spanning tree for these given edges.");
                return;
            }
        }
        double totalTime = 0;
        for (int i = 0; i < MST.size(); i++){
            totalTime = totalTime + MST.get(i).getTime(); // adds the time of each edge to TOTAL TIME
        }
        totalTime = totalTime / MST.size(); // averages totalTime over ALL edges

        System.out.println("\nThe minimum average latency spanning tree has an average of "+totalTime+" Mbps per edge.");
        System.out.println("The spanning tree is as follows:\n");
        for (int i = 0; i < MST.size(); i++){
            System.out.println("\t("+MST.get(i).getFrom()+"-"+MST.get(i).getTo()+")\t"+MST.get(i).getTime()+" sec");
        }
        return;

    }

    public static void vertexFailureCheck(){
        boolean flag = vertexFailureCheck(vertices.get(0));
        if (flag == true){
            System.out.println("This graph is still connected with any two vertex failures.");
        } else {
            System.out.println("This graph is NOT connected with any two vertex failures.");
        }
    }

    public static boolean vertexFailureCheck(Vertex src){
        System.out.println("");

        int[] checks = new int[vertices.size()];
        Vertex current = vertices.get(0);
        for (int failA = 0; failA < vertices.size()-1; failA++){ // will loop through from 0 to size-2 vertices
            for (int failB = failA+1; failB < vertices.size(); failB++){ // will start one after failA and go to the end
                checks = new int[vertices.size()];
                for (int i = 0; i < vertices.size(); i++){
                    if (i != failA && i != failB){ // Start somewhere that's NOT a fail point
                        current = vertices.get(i);
                        //System.out.println("Starting at vertex "+i+" when failA is "+failA+" and failB is "+failB);
                        break;
                    }
                }
                //System.out.println("Setting checks at index "+current.getName()+" to 1.");
                checks[current.getName()] = 1; // Sets current vertex to "seen"
                Queue<Edge> edgeQ = new LinkedList<Edge>();
                Edge temp = new Edge(current.getName(), current.getName(), "copper", 1, 1);
                edgeQ.add(temp);
                while (!edgeQ.isEmpty()){
                    current = vertices.get(edgeQ.remove().getTo()); // changes the current vertex to the next in the BFS
                    for (int i = 0; i < current.getEdgeNum(); i++){
                        if (current.getEdge(i).getFrom() != failA && current.getEdge(i).getFrom() != failB){ // cannot go FROM failA or failB
                            if (current.getEdge(i).getTo() != failA && current.getEdge(i).getTo() != failB && checks[current.getEdge(i).getTo()] != 1){
                                //System.out.println("Adding ("+current.getEdge(i).getFrom()+"-"+current.getEdge(i).getTo()+") to queue");
                                edgeQ.add(current.getEdge(i)); // Adds all edges to the queue
                            }
                            checks[current.getEdge(i).getTo()] = 1; // Flags the TO vertex to say it's been reached
                }   }   }
                for (int i = 0; i < checks.length; i++){
                    if (checks[i] == 0){
                        //System.out.println("Failing at "+i);
                        return false;
                    } 
                }


            }
        }
       
        return true;
    }

    public static void printHelp(){
        System.out.println("\n\n#####################################################\n"+
                           "###################   HELP MENU #####################\n"+
                           "#####################################################\n\n"+
                           "1\tLowest Latency Path\n"+
                           "2\tCopper-Only Connection\n"+
                           "3\tMinimum Average Latency Spanning Tree\n"+
                           "4\tVertex Failure Check\n"+
                           "5\tExit Program\n");
    }

    public static void main(String[] args){
        System.out.println("\n\nHello! Welcome to the program. Enter a command between 1-5 below.\n"+
                            "If you need any help, simply type 'help' and press Enter.");
        loadGraph(args[0]);
        kb = new Scanner(System.in);
        while (true){
            System.out.println("\n");
            System.out.print("Choose a command:\t");
            String userEntry = kb.nextLine();
            switch (userEntry){
                case "1":
                    lowestLatencyPath();
                    break;
                case "2":
                    boolean flag = copperPath(vertices.get(0));
                    if (flag == true){
                        System.out.println("The graph is a copper-connected graph.");
                    } else {
                        System.out.println("The graph is NOT copper-connected.");
                    }
                    break;
                case "3":
                    MALST();
                    break;
                case "4":
                    vertexFailureCheck();
                    break;
                case "5":
                    kb.close();
                    System.out.println("\nThanks for playing! Goodbye!");
                    System.exit(0);
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    System.out.println(userEntry+" not found. Please enter any number from 1-5.");
            }
        }
    } // END main()
} // END CLASS

class Vertex{
    private int name;
    private ArrayList<Edge> edgeList = new ArrayList<Edge>();

    public Vertex(int entry){
        name = entry;
    }

    public boolean edgeTo(int dest){
        for (int i = 0; i < edgeList.size(); i++){
            if (getEdge(i).getTo() == dest){
                return true;
            }
        }
        return false;
    }

    public int getEdgeNum(){
        return edgeList.size();
    } // END getEdgeNum()

    public int getName(){
        return name;
    } // END getName()

    public Edge getEdge(int entry){
        if (edgeList.size() > entry){
            return edgeList.get(entry);
        } else {
            throw new IllegalArgumentException("Error: Adjacency List does not contain index "+entry+".");
        }
    } // END getEdge()

    public void addEdge(Edge edge){
        edgeList.add(edge);
    } // END addEdge()
    
} // END Vertex class

class sortWeights implements Comparator<Edge>{
    public int compare(Edge a, Edge b){
        if (a.getTime() > b.getTime()){
            return 1;
        } else if (a.getTime() == b.getTime()){
            return 0;
        } else {
            return -1;
        }
    }
}

class Edge{
    private int from;
    private int to;
    private String type;
    private int bandwidth;
    private int length;
    private double travelTime;

    public Edge(){
        throw new IllegalArgumentException("Error: Need to include to and from edges, as well as bandwidth.");
    }
    public Edge(int in, int out, String matl, int band, int lgh){
        from = in;
        to = out;
        type  = matl;
        bandwidth = band;
        length = lgh;
        if (type.equals("copper")){
            travelTime = length / 230000000.0000000; // time in seconds
        } else {
            travelTime = length / 200000000.0000000; // time in seconds
        }
    }


    public int getLength(){
        return length;
    } // END getLength()

    public int getBandwidth(){
        return bandwidth;
    } // END getBandwidth()

    public String getType(){
        return type;
    } // END getType()

    public int getFrom(){
        return from;
    } // END getFrom()

    public int getTo(){
        return to;
    } // END getTo()

    public double getTime(){
        return travelTime;
    } // END getTime()
}