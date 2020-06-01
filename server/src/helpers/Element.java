package helpers;

import org.json.simple.JSONArray;

public class Element {
	private int id;
	private double layoutX;
	private double layoutY;
	private int speed;
	private int color;
	private int kind;
	private int worth;
	private boolean isDead;
	
	public Element(int id, double layoutX, double layoutY, int speed, int color, int kind, int worth) {
		this.id = id;
		this.layoutX = layoutX;
		this.layoutY = layoutY;
		this.speed = speed;
		this.color = color;
		this.kind = kind;
		this.worth = worth;
		this.isDead = false;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public void dead() {
		isDead = true;
	}
	
	public int getWorth() {
		return worth;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSONArray() {
		JSONArray obj = new JSONArray();
		obj.add(id);
		obj.add(layoutX);
		obj.add(layoutY);
		obj.add(speed);
		obj.add(color);
		obj.add(kind);
		
		return obj;
	}
}
