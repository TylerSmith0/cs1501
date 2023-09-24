public class MyLZW{
    private static final int R = 256;       // number of input chars
    private static int W = 9;              // codeword width; removed the "final" keyword to allow them to change
    private static int L = 512;             // number of codewords = 2^W; removed "final" keyword

    public static void compress(char user) { 
        String input = BinaryStdIn.readString();
        if (user != 'n' && user != 'r' && user != 'm')
            user = 'n'; // assume do-nothing mode if no proper character was put in
        BinaryStdOut.write(user,8);
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF! Note this when resetting the codebook!

        switch (user){
            case 'n':
                while (input.length() > 0) {
                    String s = st.longestPrefixOf(input);  // Find max prefix match s.
                    BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                    int t = s.length();
                    if (code == L && W < 16){ // only increase the size of the codeword if we haven't reached 16 bits yet!
                        W++; // adds 1 to codeword length
                        L=(int)Math.pow(2, W); // increases the size of the codebook max
                    }
                    if (t < input.length() && code < L){
                        st.put(input.substring(0, t + 1), code++);  // Add s to symbol table. <----- Fills that last opening! THEN checks!
                    } 
                    input = input.substring(t);            // Scan past s in input.
                }
                BinaryStdOut.write(R, W);
                BinaryStdOut.close();
                break;

            case 'r':
                while (input.length() > 0) {
                    String s = st.longestPrefixOf(input);  // Find max prefix match s.
                    BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                    int t = s.length();

                    // Full codebook checks
                    if (code == L && W < 16){ // only increase the size of the codeword if we haven't reached 16 bits yet!
                        W++; // adds 1 to codeword length
                        L=(int)Math.pow(2, W); // increases the size of the codebook max
                    } else if (code == L && W == 16) {
                        //System.err.println("resizing");
                        W = 9;
                        L = 512;
                        st = new TST<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R+1;  // R is codeword for EOF! Note this when resetting the codebook!
                    }

                    if (t < input.length() && code < L){
                        st.put(input.substring(0, t + 1), code++);  // Add s to symbol table.
                    } 
                    input = input.substring(t);            // Scan past s in input.
                }
                BinaryStdOut.write(R, W);
                BinaryStdOut.close();
                break;

            case 'm':
            float readBits = 0; // bits read in from the file! BUT REMEMBER WE READ SOMETHING EARLIER
            float writeBits = 0; // bits written out to the compression!
            float oldRatio = 0; // compression ratio once the codebook fills
            float newRatio = 0;
            boolean firstFull = false;

            while (input.length() > 0) {
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                writeBits = writeBits + W;
                int t = s.length();
                readBits = readBits + t*8;
                newRatio = readBits / writeBits;

                // Full codebook checks
                if (code == L && W < 16){ // only increase the size of the codeword if we haven't reached 16 bits yet!
                    W++; // adds 1 to codeword length
                    if (W == 16){
                        firstFull = true;
                    }
                    L=(int)Math.pow(2, W); // increases the size of the codebook max
                } else if (code == L && W == 16) {
                    if (firstFull == true){
                        oldRatio = newRatio; // keeps the ratio before the codebook was reset!
                        firstFull = false; // resets a flag so that we don't re-enter this check
                        readBits = 0; // decided to reset here as well because it was giving me better compression overall, but 
                        writeBits = 0;  // not with all.tar :-( can't figure that one out...
                    }
                    if (oldRatio / newRatio > 1.1){
                        // System.err.println("resizing");
                        W = 9;
                        L = 512;
                        st = new TST<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R+1;  // R is codeword for EOF! Note this when resetting the codebook!
                        oldRatio = 0;
                        newRatio = 0;
                        readBits = 0;
                        writeBits = 0;
                    }   
                }

                if (t < input.length() && code < L){
                    st.put(input.substring(0, t + 1), code++);  // Add s to symbol table.
                } 
                input = input.substring(t);            // Scan past s in input.
            }
            BinaryStdOut.write(R, W);
            BinaryStdOut.close();
            break;
        }
    } 
 

    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        char user = (char) BinaryStdIn.readInt(8);
        int codeword = BinaryStdIn.readInt(W);
        float readBits = W;
        float writeBits = 0;
        float oldRatio = 0;
        float newRatio = 0;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        switch (user){
            case 'n':
                while (true) {
                    BinaryStdOut.write(val);

                    if (i == L && W < 16){
                        W++;
                        String[] temp = new String[(int) Math.pow(2, W)];
                        for (int j = 0; j < L; j++){
                            temp[j] = st[j];
                        }
                        st = temp;
                        temp = null;
                        L = (int) Math.pow(2, W);
                    }
                    codeword = BinaryStdIn.readInt(W);                    
                    if (codeword == R) break;
                    String s = st[codeword];
                    if (i == codeword) s = val + val.charAt(0);   // special case hack
                    if (i < L) st[i++] = val + s.charAt(0);                   
                    val = s;
                }
                BinaryStdOut.close();
                break;

            case 'r':
                while (true) {
                    BinaryStdOut.write(val);          
                    // Check for full codebook
                    if (i == L && W == 16){ // means we're just about to get to the last index available!
                        W = 9;
                        L = 512;
                        st = new String[L];
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = ""; // i should be 257 now! 
                    } else if (i == L && W < 16){
                        W++;
                        String[] temp = new String[(int) Math.pow(2, W)];
                        for (int j = 0; j < L; j++){
                            temp[j] = st[j];
                        }
                        st = temp; temp = null;
                        L = (int) Math.pow(2, W);
                    }
                    codeword = BinaryStdIn.readInt(W); 
                    if (codeword == R) break;
                    String s = st[codeword];
                    if (i == codeword) s = val + val.charAt(0);   // special case hack
                    if (i < L) st[i++] = val + s.charAt(0);
                    val = s;
                }
                BinaryStdOut.close();
                break;

            case 'm':
                boolean firstFull = false;
                //boolean codebookFull = false;
                while (true) {
                    BinaryStdOut.write(val);
                    writeBits = writeBits + val.length()*8;
                    readBits = readBits + W;
                    newRatio = writeBits/readBits;

                    if (i == L && W == 16){ // means we're just about to get to the last index available!
                        //codebookFull = true;
                        if (firstFull == true){
                            oldRatio = newRatio; // keeps the numbers the same as the compress
                            // System.err.println("readBits:\t"+readBits+"\twriteBits:\t"+writeBits);
                            // System.err.println("oldRatio:\t"+oldRatio);
                            firstFull = false;
                            readBits = 0; // see compress() function as to why I decided to resize here... just overall
                            // better results were achieved...
                            writeBits = 0;
                        }
                        if (oldRatio/newRatio > 1.1){
                            // System.err.println("Resetting in MONITOR mode");
                            W = 9;
                            L = 512;
                            st = new String[L];
                            for (i = 0; i < R; i++)
                                st[i] = "" + (char) i;
                            st[i++] = ""; // i should be 257 now!
                            readBits = 0;
                            writeBits = 0; // resets the read and write counts
                            oldRatio = 0;
                            newRatio = 0;
                            //codebookFull = false;
                        }
                    } else if (i == L && W < 16){
                        W++;
                        String[] temp = new String[(int) Math.pow(2, W)];
                        for (int j = 0; j < L; j++){
                            temp[j] = st[j];
                        }
                        st = temp; temp = null;
                        L = (int) Math.pow(2, W);
                        if (W == 16){
                            firstFull = true;
                        }
                    }
                    
                    codeword = BinaryStdIn.readInt(W);
                    if (codeword == R) break;
                    String s = st[codeword];
                    if (i == codeword) s = val + val.charAt(0);   // special case hack
                    if (i < L){
                        st[i++] = val + s.charAt(0);
                    }
                    val = s;
                }
                BinaryStdOut.close();
                break;
        }
    }

    public static void main(String[] args){
        if      (args[0].equals("-")) compress(args[1].charAt(0));
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }// end main()
} // end MyLZW