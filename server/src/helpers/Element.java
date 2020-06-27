package helpers;

import java.util.Random;

import org.json.simple.JSONArray;

public class Element {
	public static final int SHOOT = 1<<0;
	public static final int SHIELD = 1<<1;
	
	private Random random;
	private int id;
	private double layoutX;
	private double layoutY;
	private int speed;
	private int color;
	private int kind;
	private int worth;
	private int skills;
	private int blood;
	private int bulletDelay;
	private boolean isDead;
	
	public Element(int id, double layoutX, double layoutY, int speed, int color, int kind, int worth, int skill, int blood) {
		random = new Random();
		this.id = id;
		this.layoutX = layoutX;
		this.layoutY = layoutY;
		this.speed = speed;
		this.color = color;
		this.kind = kind;
		this.worth = worth;
		this.skills = skill;
		this.blood = blood;
		this.isDead = false;
		if ((skills & SHOOT) != 0) bulletDelay = 250 + random.nextInt(50);
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
		obj.add(skills);
		obj.add(bulletDelay);
		obj.add(blood);

		return obj;
	}
}
