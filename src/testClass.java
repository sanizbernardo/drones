import java.util.Random;

public class testClass {
	public static void main(String[] args){
		byte random = 0;
		Random randomG = new Random();
		byte[] array = new byte[200*200*3];
		randomG.nextBytes(array);
		long start = System.nanoTime();
		for (int i= 0; i<200*200*3;i++){
			random = array[i];
		}
		long end = System.nanoTime();
		System.out.println((end - start)/1000000 + "ms");
	
		System.out.println(random & 0xFF );
	
	}
	
	
}
