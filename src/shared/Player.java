package shared;

import java.util.ArrayList;
import java.util.List;


public class Player implements Comparable<Player> {

	private Integer score;
	private String name;
	private List<Integer> historyRace;

	// -----------------------------------------------------
	public Player() {
		
		this.score = 0;
		this.name = null;
		this.historyRace = new ArrayList<>(); 
	}
	// -----------------------------------------------------
	public Player(String name, Integer score) {
		
		this.name = name;
		this.score = score;
		this.historyRace = new ArrayList<>();
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
		this.historyRace.add(bonus);
	}
	// -----------------------------------------------------
	public boolean isLose() {
		
		int len = this.historyRace.size();
		if (len > 2 && historyRace.get(len - 1) == -1 && historyRace.get(len - 2) == -1 && historyRace.get(len - 3) == -1) {
			return true;
		}
		else {
			return false;
		}
	}
	// -----------------------------------------------------
	public void display() {
		
		int len = this.historyRace.size();
		System.out.print(this.name + ": [ ");
		for (int i = 0; i < len; i++) {
			
			System.out.print(this.historyRace.get(i) + " ");
		}
		System.out.println("];");
	}
	// -----------------------------------------------------
	@Override
	public int compareTo(Player anotherPlayer) {
		
		return this.score - anotherPlayer.getScore();
	}	
}
