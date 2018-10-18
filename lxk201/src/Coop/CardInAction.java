package Coop;

public class CardInAction {
	public int x;
	public int y;
	public CardInAction nextCard;
	public String location;
	
	public CardInAction() {}
	
	public CardInAction(int x, String location) {
		this.x = x;
		this.y = 0;
		this.location = location;
		nextCard = null;
	}
	
	public CardInAction(int x, int y, String location) {
		this.x = x;
		this.y = y;
		this.location = location;
		nextCard = null;
	}
	
	public CardInAction getLast() {
		if (nextCard == null) return this;
		else return nextCard.getLast();
	}
	
	public String toString() {
		return "x: " + x + " y: " + y + " " + location;
	}
	
	public boolean equals(CardInAction c) {
		return (x == c.x) && (y == c.y) && (location.equals(c.location));
	}
}
