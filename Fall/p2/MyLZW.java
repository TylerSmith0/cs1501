/*************************************************************************
 * 
 * NOTE: This code was not originally written by Tyler Smith. The basic LZW function was provided by
 * Sedgewick and Wayne in the textbook that I read. I simply added some functionality to the algorithm,
 * such as a "Do Nothing Mode", a "Reset" mode, and a "Monitor" mode. This all has to do with how the 
 * codebook is maintained as it approaches its capacity. I also included a variable width capability for
 * the codewords.
 * 
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int W = 9;                // codeword width initialized to start at 9, can go up to 16
    private static int L = 512;             // number of codewords = 2^W; must remember to update when W increases!
    private static char mode = 'n';
    private static double totalSize = 0;
    private static double compressedSize = 0;
    private static double oldRatio = 1;
    private static double newRatio = 1;
    private static boolean resetFlag = false;
    

    public static void compress() { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
        BinaryStdOut.write(mode);

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            totalSize = s.length() * 8 + totalSize; // counting the total number of BITS being processed in the original file
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            compressedSize = W + compressedSize; // adds the bits to the compressed total
            int t = s.length();
            newRatio = totalSize / compressedSize;
            if (resetFlag == false){
                oldRatio = newRatio;
            }
            if (t < input.length() && code == L && W < 16){
                W++;
                L=L*2;
            }
            // checks if codebook is full full
            if (code == L && W == 16){
                if (mode == 'r'){
                    W = 9;
                    L = 512;
                    st = new TST<Integer>();
                    for (int j = 0; j < R; j++){
                        st.put("" + (char) j, j);
                    }
                    code = R+1;
                } else if (mode == 'm'){
                    resetFlag = true;
                    if (oldRatio / newRatio > 1.1){
                        // reset the codebook!
                        //System.err.println("Resetting the codebook. Ratio is "+(oldRatio/newRatio));
                        W = 9;
                        L = 512;
                        st = new TST<Integer>();
                        for (int j = 0; j < R; j++){
                            st.put("" + (char) j, j);
                        }
                        code = R+1;
                        oldRatio = 1;
                        newRatio = 1;
                        resetFlag = false;
                    }
                }
                // otherwise we do nothing!
            }
            //

            if (t < input.length() && code < L){    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value
        mode = (char)BinaryStdIn.readInt(8);
        //System.err.println("Mode read in was "+mode);

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val); // Write to file
            totalSize = val.length() * 8 + totalSize;
            compressedSize = W + compressedSize;
            newRatio = totalSize / compressedSize;
            if (resetFlag == false){
                oldRatio = newRatio;
            }

            if (i == L && W < 16){
                W++; L=L*2; // Increment width and total
                String[] arr = new String[L]; // resize array
                for (int j = 0; j < st.length; j++){
                    arr[j] = st[j];
                }
                st = arr; arr = null;
            }

            if (i == L && W == 16){
                if (mode == 'r'){
                    W = 9; L = 512;
                    st = new String[L];
                    // Initialize with the chars
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                } else if (mode == 'm'){
                    resetFlag = true;
                    //System.err.println("Compression Ratio is "+(oldRatio/newRatio));
                    if (oldRatio / newRatio > 1.1){
                        // reset the codebook!
                        //System.err.println("Resetting the codebook.");
                        W = 9;
                        L = 512;
                        st = new String[L];
                        //System.err.println("Reset codebook at "+oldRatio/newRatio);
                        // Initialize with the chars
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";
                        oldRatio = 1;
                        newRatio = 1;
                        resetFlag = false;
                    }
                }
            }

            codeword = BinaryStdIn.readInt(W); // Read in the next code word to look at
            if (codeword == R) break; // if EOF break
            String s = st[codeword]; // Finds the equivalent string that the codeword equals
            if (i == codeword){
                s = val + val.charAt(0);   // special case hack
            }
            if (i < L){
                st[i++] = val + s.charAt(0);
            }
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if (args.length == 2){
            char[] userTemp = args[1].toCharArray();
            if (userTemp[0] == 'r'){
                mode = 'r';
            } else if (userTemp[0] == 'm'){
                mode = 'm';
            } else{
                mode = 'n';
            }
        }
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");

    }

}