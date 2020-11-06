package shared;

public class GameInfo {

	private boolean registerStatus;
	private int maxNumPlayers;
	private int maxNumQuestions;
	
	public GameInfo(boolean registerStatus, int maxNumPlayers, int maxNumQuestions) {
		
		this.registerStatus = registerStatus;
		this.maxNumPlayers = maxNumPlayers;
		this.maxNumQuestions = maxNumQuestions;
	}

	public boolean isRegisterStatus() {
		return registerStatus;
	}

	public void setRegisterStatus(boolean registerStatus) {
		this.registerStatus = registerStatus;
	}

	public int getMaxNumPlayers() {
		return maxNumPlayers;
	}

	public void setMaxNumPlayers(int maxNumPlayers) {
		this.maxNumPlayers = maxNumPlayers;
	}

	public int getMaxNumQuestions() {
		return maxNumQuestions;
	}

	public void setMaxNumQuestions(int maxNumQuestions) {
		this.maxNumQuestions = maxNumQuestions;
	}
}
