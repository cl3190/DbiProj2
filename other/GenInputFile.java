import java.util.*;

public class GenInputFile {
	public static void main(String[] args) {
		int key = 0;
		int payload = 0;
		boolean flag = true;
		int i = 0;
		while (i++ < 40000) {
			Random rd = new Random();
			key = rd.nextInt();
			key = key == 0 ? key+1 : key;
			payload = rd.nextInt();
			System.out.println(key + " " + payload);
		}
	}
}