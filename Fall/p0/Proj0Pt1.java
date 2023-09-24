import java.util.Scanner;

public class Proj0Pt1 {
    public static void main(String[] args) {
        System.out.println("You should see this message in your terminal!");
        Scanner stdin = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = stdin.nextLine();
        System.out.println("Hello, " + name);
    }
}

