package shared;

import java.util.Random;

public class Question {

	private int number01, number02, operatorIndex;
	private Random random;
	
	public Question() {
		this.random = new Random();
		this.number01 = 0;
		this.number02 = 0;
		this.operatorIndex = 0;
	}
	// --------------------------------------------------------
	public void generateRandom() {
//		number01 = random.nextInt(20000) - 10000;
//		number02 = random.nextInt(20000) - 10000;
		
		number01 = random.nextInt(10);
		number02 = random.nextInt(10);
		operatorIndex = random.nextInt(5);
		if (operatorIndex == 3 && number02 == 0) {
			generateRandom();
		}
	}
	// --------------------------------------------------------
	public int getNumber01() {
		return number01;
	}

	public int getNumber02() {
		return number02;
	}


	public int getOperatorIndex() {
		return operatorIndex;
	}	
}
