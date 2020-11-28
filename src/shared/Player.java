package shared;

public class Player implements Comparable<Player> {

	private Integer score;
	private String name;

	public Player() {
		this.score = 0;
		this.name = null;
	}
	// -----------------------------------------------------
	public Player(String name, Integer score) {
		
		this.name = name;
		this.score = score;
	}
	// -----------------------------------------------------
	public Integer getScore() {
		
		return score;
	}
	// -----------------------------------------------------
	public void setScore(Integer score) {
		
		this.score = score;
	}
	// -----------------------------------------------------
	public String getName() {
		
		return name;
	}
	// -----------------------------------------------------
	public void setName(String name) {
		
		this.name = name;
	}
	// -----------------------------------------------------
	public void addScore(Integer bonus) {
		
		this.score += bonus;
	}
	// -----------------------------------------------------
	@Override
	public int compareTo(Player anotherPlayer) {
		
		return this.score - anotherPlayer.getScore();
	}	
}
