package AI;

public class VirtualCard {
	public int id = 0;
	public int[] hp = {0, 0};
	public boolean shielded = false;
	public int[] mp = {0, 0};
	public int mpCost = 0;
	public boolean skillUsed = false;
	public boolean skillActivatable = false;
	public int targetTypeIndication = 0;
	/* 1: Single target damage
 	 * 2: All targets damage
 	 * 3: Column damage
 	 * 4: All friendly recovery
 	 * 5: Shielding damage
 	 * 6: Summon
 	 */
	public int damage = 0;
	
	public int VpGen = 0;
	public int SpGen = 0;
	public int SpCost = 0;
	
	public double hpValue = 0;
	public double damageValue = 0;
	public double economicValue = 0;
	public double totalValue = 0;
	
	public VirtualCard() {}
	
	public VirtualCard (int ID) {
		id = ID;
		this.shielded = false;
		switch (ID) {
		case 101: {hp[0] = hp[1] = 4; mp[0] = mp[1] = 0; skillUsed = true; setCard(0, 0, 0, 4, 2, 0); break;}
		case 102: {hp[0] = hp[1] = 3; mp[0] = mp[1] = 0; skillUsed = true; setCard(0, 0, 0, 10, 1, 0); break;}
		case 103: {hp[0] = hp[1] = 4; mp[0] = mp[1] = 5; skillUsed = true; setCard(1, 4, 2, 2, 2, 0); break;}
		case 104: {hp[0] = hp[1] = 6; mp[0] = mp[1] = 0; skillUsed = true; setCard(0, 1, 2, 5, 3, 0); break;}
		case 105: {hp[0] = hp[1] = 5; mp[0] = mp[1] = 3; skillUsed = true; setCard(3, 6, 301, 8, 1, 0); break;}
		case 201: {hp[0] = hp[1] = 3; mp[0] = mp[1] = 3; skillUsed = false; setCard(1, 5, 0, 0, 0, 1); break;}
		case 202: {hp[0] = hp[1] = 2; mp[0] = mp[1] = 0; skillUsed = false; setCard(0, 1, 1, 0, 0, 1); break;}
		case 203: {hp[0] = hp[1] = 4; mp[0] = mp[1] = 4; skillUsed = false; setCard(2, 4, 2, 0, 0, 2); break;}
		case 204: {hp[0] = hp[1] = 3; mp[0] = mp[1] = 4; skillUsed = false; setCard(2, 3, 4, 0, 0, 3); break;}
		case 205: {hp[0] = hp[1] = 6; mp[0] = mp[1] = 6; skillUsed = false; setCard(3, 2, 3, 0, 0, 4); break;}
		case 301: {hp[0] = hp[1] = 6; mp[0] = mp[1] = 6; skillUsed = false; setCard(3, 2, 3, 0, 0, 0); break;}
		}
	}
	
	public VirtualCard (int ID, int hp, int mp, boolean skillUsed, boolean shielded) {
		id = ID;
		this.hp[0] = hp; 
		this.mp[0] = mp;
		this.skillUsed = skillUsed;
		this.shielded = shielded;
		switch (ID) {
		case 101: {this.hp[1] = 4; this.mp[1] = 0; setCard(0, 0, 0, 4, 2, 0); break;}
		case 102: {this.hp[1] = 3; this.mp[1] = 0; setCard(0, 0, 0, 10, 1, 0); break;}
		case 103: {this.hp[1] = 4; this.mp[1] = 5; setCard(1, 4, 1, 2, 2, 0); break;}
		case 104: {this.hp[1] = 6; this.mp[1] = 0; setCard(0, 1, 2, 5, 3, 0); break;}
		case 105: {this.hp[1] = 5; this.mp[1] = 3; setCard(3, 6, 301, 8, 1, 0); break;}
		case 201: {this.hp[1] = 3; this.mp[1] = 3; setCard(1, 5, 0, 0, 0, 1); break;}
		case 202: {this.hp[1] = 2; this.mp[1] = 0; setCard(0, 1, 1, 0, 0, 1); break;}
		case 203: {this.hp[1] = 4; this.mp[1] = 4; setCard(2, 4, 2, 0, 0, 2); break;}
		case 204: {this.hp[1] = 3; this.mp[1] = 4; setCard(2, 3, 4, 0, 0, 3); break;}
		case 205: {this.hp[1] = 6; this.mp[1] = 6; setCard(3, 2, 3, 0, 0, 4); break;}
		case 301: {this.hp[1] = 6; this.mp[1] = 6; setCard(3, 2, 3, 0, 0, 0); break;}
		}
	}
	
	public void setCard (int mpCost, int targetTypeIndication, int damage, int VpGen, int SpGen, int SpCost) {
		this.mpCost = mpCost;
		this.targetTypeIndication = targetTypeIndication;
		this.damage = damage;
		this.VpGen = VpGen;
		this.SpGen = SpGen;
		this.SpCost = SpCost;
		updateValue();
	}
	
	public void updateValue() {
		if (isEmpty()) {totalValue = 0; return;}
		if (mp[0] > mp[1]) mp[0] = mp[1];
		if (hp[0] > hp[1]) hp[0] = hp[1];
		
		if (mp[0] >= mpCost && targetTypeIndication != 0 && !skillUsed) skillActivatable = true; 
		else skillActivatable = false;
		
		if (shielded) hpValue = hp[0] * 1.5;
		else hpValue = hp[0];
		
		switch (targetTypeIndication) {
		case 1: {damageValue = damage; break;}
		case 2: {damageValue = damage * 2; break;}
		case 3: {damageValue = damage * 1.5; break;}
		case 4: {damageValue = damage * 0.75; break;}
		case 5: {damageValue = 0; break;}
		case 6: {damageValue = 0; break;}
		default: {damageValue = 0; break;}
		}
		
		economicValue = VpGen + SpGen - SpCost;
		totalValue = hpValue + damageValue + economicValue;
		
		//System.out.println("id: " + id + " " + hpValue + " " + damageValue + " " + economicValue);
	}
	
	public boolean isEmpty() {
		return id == 0;
	}
	
	public void setEmpty() {
		id = 0;
	}
	
	public boolean equals(VirtualCard c) {
		return (this.id == c.id) && 
				(this.hp[0] == c.hp[0]) && 
				(this.mp[0] == c.mp[0]) &&
				(this.skillUsed == c.skillUsed) &&
				(this.shielded == c.shielded);
	}
	
	public void hpDeduct(int amount) {
		if (shielded) hp[0] -= 1;
		else hp[0] -= amount;
		if (hp[0] <= 0) setEmpty();
		updateValue();
	}
	
	public VirtualCard clone() {
		return new VirtualCard(id, hp[0], mp[0], skillUsed, shielded);
	}

	public String toString() {
		if(isEmpty()) 
			return "Empty";
		else
			return hp[0] + " " + mp[0] + " " + skillActivatable + " " + totalValue;
	}

}
