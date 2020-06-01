package helpers;

import java.util.Random;

import org.json.simple.JSONArray;

public class Game {
	public static final int MAX_LEVEL = 1;
	
	private final int COLOR_SIZE = 4; // black, blue, green, red
	private final int NUM_KIND = 5;
	private final int NUM_BASE = 20;
	private final int MULTI_BASE = 20;
	private final int SPEED_BASE = 3;
	
	private Random random;
	private int level;
	private int batch;
	private Element[] elements;
	private int numOfElements;
	private int numInBatch;
	private int numOfDead;
	
	private int numOfPlayersAlive;
	
	public Game(int players) {
		this.numOfPlayersAlive = players;
		this.random = new Random();
		this.level = 1;
		this.batch = 0;
		this.numInBatch = 10;
		this.numOfElements = NUM_BASE;
		this.numOfDead = 0;
		generateElements();
	}
	
	public void generateElements() {
		elements = new Element[numOfElements];
		int maxSpeed = SPEED_BASE + level;
		int worth = 5 + level*5;
		
		for (int i = 0; i < numOfElements; ++i) {
			// Element(int id, double layoutX, double layoutY, int speed, int color, int kind, int worth)
			elements[i] = new Element(i, genInt(40, 1160), -genInt(100, 3000), genInt(SPEED_BASE, maxSpeed), genInt(0, COLOR_SIZE), genInt(0, NUM_KIND), worth);
		}
	}
	
	public void killEnemy(Element e) {
		e.dead();
		numOfDead++;
	}
	
	public void killPlayer() {
		numOfPlayersAlive--;
	}
	
	public boolean checkBatch() {
		return numOfDead == numInBatch;
	}
	
	public boolean passLevel() {
		return batch == numOfElements/numInBatch && checkBatch();
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray nextBatch() {
		numOfDead = 0;
		JSONArray arr = new JSONArray();
		int start = batch*numInBatch;
		int end = (++batch)*numInBatch;
		for (int i = start; i < end; ++i) {
			arr.add(elements[i].toJSONArray());
		}
		
		return arr;
	}
	
	public void nextLevel() {
		level++;
		batch = 0;
		numOfDead = 0;
		numOfElements = NUM_BASE + level*MULTI_BASE;
		generateElements();
	}
	
	public int getLevel() {
		return level;
	}
	
	public int checkEndGame() {
		if (numOfPlayersAlive == 0) return -1;
		
		if (passLevel() && level == MAX_LEVEL) return 1;
		
		return 0;
	}
	
	public int genInt(int low, int bound) {
		return low + random.nextInt(bound);
	}
	
	public Element getElement(int pos) {
		return elements[pos];
	}
}