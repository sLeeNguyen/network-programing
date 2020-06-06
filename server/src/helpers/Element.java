package helpers;

import org.json.simple.JSONArray;

public class Element {
	public static final int SHOOT = 1<<0;
	public static final int SHIELD = 1<<1;
	
	private int id;
	private double layoutX;
	private double layoutY;
	private int speed;
	private int color;
	private int kind;
	private int worth;
	private int skill;
	private int blood;
	private boolean isDead;
	
	public Element(int id, double layoutX, double layoutY, int speed, int color, int kind, int worth, int skill, int blood) {
		this.id = id;
		this.layoutX = layoutX;
		this.layoutY = layoutY;
		this.speed = speed;
		this.color = color;
		this.kind = kind;
		this.worth = worth;
		this.skill = skill;
		this.blood = blood;
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
		obj.add(skill);
		obj.add(blood);
		
		return obj;
	}
}
