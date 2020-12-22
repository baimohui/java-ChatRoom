package javaExps;
import java.util.Scanner;
import java.util.Random;
public class exp1a {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		Random rand = new Random();
		while(true) {
			int answer = rand.nextInt(100);
			int count = 0;
			while(true) {
				System.out.println("Please enter a number to catch the correct answer:");
				int test = scan.nextInt();
				if(test < answer) {
					System.out.println("The number you guess is smaller than that. Try again!");
					count++;
				} else if(test > answer) {
					count++;
					System.out.println("The number you guess is bigger than that. Try again!");
				} else {
					count++;
					System.out.printf("Now you get it by %d times. Congratulation!", count);
					break;
				}
			}
			
			System.out.println("Do you want to play again? Y/N");
			char choose = scan.next().charAt(0);
			if(choose == 'Y' || choose == 'y') {
				System.out.println("All right. Just try again!");
				continue;
			} else if(choose == 'N' || choose == 'n') {
				System.out.println("Welcome next time!");
				scan.close();
				break;
			} else {
				System.out.println("No kidding. Seriouly.");
				break;
			}
		}
	}

}
