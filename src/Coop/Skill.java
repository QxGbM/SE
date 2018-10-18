package Coop;

import java.util.ArrayList;

public class Skill {
	
	public boolean requiresTarget;
	public String targetType;
	public ArrayList<String> effects;
	
	public Skill(String[] effects) {
		this.requiresTarget = false;
		targetType = "";
		this.effects = new ArrayList<String>();
		for(int i = 0; i < effects.length; i++) {
			this.effects.add(effects[i]);		
		}
	}
	
	public Skill(String targetType, String[] effects) {
		this.requiresTarget = true;
		this.targetType = targetType;
		this.effects = new ArrayList<String>();
		for(int i = 0; i < effects.length; i++) {
			this.effects.add(effects[i]);		
		}
	}
	
	public void reflect(CardInAction actionHead) {
		int x = actionHead.x, y = actionHead.y;
		String location = actionHead.location;
		for(int i = 0; i < effects.size(); i++) {
			String effect = effects.get(i);
			String[] s = effect.split(" ");
			if (s[0].equals("damage")) {
				if (s[1].equals("self")) {
					if(location.equals("Board")) {
						if (Coop.myTurn) {
							if (!Coop.myBoard[x][y].mpCost(Integer.valueOf(s[2]))) return;
							Coop.myBoard[x][y].hpDeduct(Integer.valueOf(s[3]));
						}
						else {
							Coop.myBoard[x][y].mpDeduct(Integer.valueOf(s[2]));
							Coop.myBoard[x][y].hpDeduct(Integer.valueOf(s[3]));
						}
					}
					else if (location.equals("Friend")) {
						Coop.friendBoard[x][y].mpDeduct(Integer.valueOf(s[2]));
						Coop.friendBoard[x][y].hpDeduct(Integer.valueOf(s[3]));
					}
				}
				else if (s[1].equals("single")) {
					int p = actionHead.nextCard.y;
					String targetLocation = actionHead.nextCard.location;
					if (targetLocation.equals("Boss")) {
						if (p >= 0 && p <= 1) {
							Coop.minions[p].hpDeduct(Integer.valueOf(s[3]));
						}
						else if (p == 2) {
							Coop.boss.mpDeduct(Integer.valueOf(s[2]));
							Coop.boss.hpDeduct(Integer.valueOf(s[3]));
						}
					}
				}
				else if (s[1].equals("column")) {
					int p = actionHead.nextCard.y;
					String targetLocation = actionHead.nextCard.location;
					if (targetLocation.equals("Boss")) {
						if (p >= 0 && p <= 1) {
							Coop.minions[p].hpDeduct(Integer.valueOf(s[3]));
							Coop.boss.mpDeduct(Integer.valueOf(s[2]));
							Coop.boss.hpDeduct(Integer.valueOf(s[3]));
						}
						else if (p == 2) {
							Coop.boss.mpDeduct(Integer.valueOf(s[2]));
							Coop.boss.hpDeduct(Integer.valueOf(s[3]));
						}
					}
				}
				else if (s[1].equals("all")) {
					if (!Coop.minions[0].isEmpty()) {
						Coop.minions[0].hpDeduct(Integer.valueOf(s[3]));
					}
					if (!Coop.minions[1].isEmpty()) {
						Coop.minions[1].hpDeduct(Integer.valueOf(s[3]));
					}
					if (Coop.minions[0].isEmpty() && Coop.minions[1].isEmpty()) {
						Coop.boss.mpDeduct(Integer.valueOf(s[2]));
						Coop.boss.hpDeduct(Integer.valueOf(s[3]));
					}
				}
			}
			else if (s[0].equals("heal")) {
				if (s[1].equals("all")) {
					for (int j = 0; j < 2; j++) {
						for (int k = 0; k < 4; k++) {
							if (!Coop.myBoard[j][k].isEmpty() && Coop.myBoard[j][k].isMonster()) {
								Coop.myBoard[j][k].mpRecover(Integer.valueOf(s[2]));
								Coop.myBoard[j][k].hpRecover(Integer.valueOf(s[3]));
							}
							if (!Coop.friendBoard[j][k].isEmpty() && Coop.friendBoard[j][k].isMonster()) {
								Coop.friendBoard[j][k].mpRecover(Integer.valueOf(s[2]));
								Coop.friendBoard[j][k].hpRecover(Integer.valueOf(s[3]));
							}
						}
					}
				}
				else if (s[1].equals("self")) {
					if(location.equals("Board")) {
						Coop.myBoard[x][y].mpRecover(Integer.valueOf(s[2]));
						Coop.myBoard[x][y].hpRecover(Integer.valueOf(s[3]));
					}
					else if (location.equals("Friend")) {
						Coop.friendBoard[x][y].mpRecover(Integer.valueOf(s[2]));
						Coop.friendBoard[x][y].hpRecover(Integer.valueOf(s[3]));
					}
				}
			}
			else if (s[0].equals("summon")) {
				CoopCard card = Coop.lookupCard(Integer.valueOf(s[1]));
				String slotLocation = actionHead.nextCard.location;
				int x2 = actionHead.nextCard.x, y2 = actionHead.nextCard.y;
				if (slotLocation.equals("Board"))
					Coop.summon(card, Coop.myBoard[x2][y2], x2, y2, slotLocation);
				else if (slotLocation.equals("Friend"))
					Coop.summon(card, Coop.friendBoard[x2][y2], x2, y2, slotLocation);
			}
			else if (s[0].equals("spGen")) {
				if(location.equals("Board")) {
					Coop.spgen += Integer.valueOf(s[1]);
					Coop.mySPGen.setText("SPGen: " + Coop.spgen);
				}
				else if (location.equals("Friend")) {
					Coop.fspgen += Integer.valueOf(s[1]);
					Coop.friendSPGen.setText("friend SPGen: " + Coop.fspgen);
				}
			}
			else if (s[0].equals("vpGen")) {
				if(location.equals("Board")) {
					Coop.vpgen += Integer.valueOf(s[1]);
					Coop.myVPGen.setText("VPGen: " + Coop.vpgen);
				}
				else if (location.equals("Friend")) {
					Coop.fvpgen += Integer.valueOf(s[1]);
					Coop.friendVPGen.setText("friend VPGen: " + Coop.fvpgen);
				}
			}
			else if (s[0].equals("shield")) {
				if(location.equals("Board")) {
					Coop.myBoard[x][y].shielded = true;
					Coop.myBoard[x][y].shieldedTurns = Integer.valueOf(s[1]);
				}
				else if (location.equals("Friend")) {
					Coop.friendBoard[x][y].shielded = true;
					Coop.friendBoard[x][y].shieldedTurns = Integer.valueOf(s[1]);
				}
			}
		}
		if (location.equals("Board")) {
			Coop.myBoard[x][y].skillUsed = true;
		}
		else if (location.equals("Friend")) {
			Coop.friendBoard[x][y].skillUsed = true;
			
		}
	}
	
	public String toString() {
		return requiresTarget + " " + targetType + " " + effects.toString();
	}

}
