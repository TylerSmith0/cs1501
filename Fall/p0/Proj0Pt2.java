public class Proj0Pt2 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter some arguments");
        } else {
            System.out.println("You entered " + args.length + " arguments:");
            for (String arg : args) {
                System.out.println(arg);
            }
        }
    }
}

