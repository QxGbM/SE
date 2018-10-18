package AI;

import java.util.ArrayList;

public class State {
	private VirtualCard[][] board;
	private int[] hand;
	private ArrayList<String> moves;
	private int sp;
	private double h;
	
	public State(VirtualCard[][] board, int[] hand, int sp, ArrayList<String> moves) {
		h = 0;
		this.board = board;
		this.hand = hand;
		this.sp = sp;
		for (int i = 0; i < 4; i++) {
			if (board[1][i].isEmpty()) h -= board[0][i].totalValue;
			else h -= board[0][i].totalValue * 1.2;
		}
		for (int i = 0; i < 4; i++) {
			h -= board[1][i].totalValue;
		}
		for (int i = 0; i < 4; i++) {
			h += board[2][i].totalValue;
		}
		for (int i = 0; i < 4; i++) {
			if (board[2][i].isEmpty()) h += board[3][i].totalValue;
			else h += board[3][i].totalValue * 1.2;
		}
		
		this.moves = new ArrayList<String>();
		this.moves.addAll(moves);
	}
	
	public ArrayList<String> getMoves() {return moves;}
	public double getH() {return h;}
	
	public ArrayList<State> expand() {
		ArrayList<State> result = new ArrayList<State>();
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				if (board[i][j].skillActivatable) {
					result.addAll(activateSkill(i, j));
				}
			}
		}
		
		for (int i = 0; i < 5; i++) {
			if (hand[i] != 0) {
				result.addAll(summon(i));
			}
		}
		return result;
	}
	
	public VirtualCard[][] cloneBoard() {
		VirtualCard[][] board = new VirtualCard[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				board[i][j] = this.board[i][j].clone();
		return board;
	}
	
	public int[] cloneHand() {
		int[] hand = new int[5];
		for (int i = 0; i < 5; i++)
			hand[i] = this.hand[i];
		return hand;
	}
	
	public ArrayList<State> activateSkill(int i, int j) {
		ArrayList<State> result = new ArrayList<State>();
		switch (board[i][j].targetTypeIndication) {
		case 1: {
			for (int k = 0; k < 4; k++) {
				if(board[2][k].isEmpty()) {
					if(!board[3][k].isEmpty()) {
						ArrayList<String> list = new ArrayList<String>();
						list.addAll(moves);
						list.add("activate " + (i * 4 + j) + " " + (12 + k));
						VirtualCard[][] board2 = cloneBoard();
						board[3][k].hpDeduct(board[i][j].damage);
						board[i][j].mp[0] -= board[i][j].mpCost;
						board[i][j].skillUsed = true;						
						board[3][k].updateValue();
						board[i][j].updateValue();
						State s = new State(board, cloneHand(), sp, list);
						result.add(s);
						board = board2;
					}
				}
				else {
					ArrayList<String> list = new ArrayList<String>();
					list.addAll(moves);
					list.add("activate " + (i * 4 + j) + " " + (8 + k));
					VirtualCard[][] board2 = cloneBoard();
					board[2][k].hpDeduct(board[i][j].damage);
					board[i][j].mp[0] -= board[i][j].mpCost;
					board[i][j].skillUsed = true;
					board[2][k].updateValue();
					board[i][j].updateValue();
					State s = new State(board, cloneHand(), sp, list);
					result.add(s);
					board = board2;
				}
			}
			break;
		}
		case 2: {
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(moves);
			list.add("activate " + (i * 4 + j));
			VirtualCard[][] board2 = cloneBoard();
			for (int m = 0; m < 4; m++) {
				if (!board[2][m].isEmpty()) {
					board[2][m].hpDeduct(board[i][j].damage);
					board[2][m].updateValue();
				}
				else {
					board[3][m].hpDeduct(board[i][j].damage);
					board[3][m].updateValue();
				}
			}
			board[i][j].mp[0] -= board[i][j].mpCost;
			board[i][j].skillUsed = true;
			board[i][j].updateValue();
			State s = new State(board, cloneHand(), sp, list);
			result.add(s);
			board = board2;
			break;
		}
		case 3: {
			for (int k = 0; k < 4; k++) {
				if(board[2][k].isEmpty()) {
					if(!board[3][k].isEmpty()) {
						ArrayList<String> list = new ArrayList<String>();
						list.addAll(moves);
						list.add("activate " + (i * 4 + j) + " " + (12 + k));
						VirtualCard[][] board2 = cloneBoard();
						board[3][k].hpDeduct(board[i][j].damage);
						board[i][j].mp[0] -= board[i][j].mpCost;
						board[i][j].skillUsed = true;
						board[3][k].updateValue();
						board[i][j].updateValue();
						State s = new State(board, cloneHand(), sp, list);
						result.add(s);
						board = board2;
					}
				}
				else {
					ArrayList<String> list = new ArrayList<String>();
					list.addAll(moves);
					list.add("activate " + (i * 4 + j) + " " + (8 + k));
					VirtualCard[][] board2 = cloneBoard();
					board[2][k].hpDeduct(board[i][j].damage);
					board[i][j].mp[0] -= board[i][j].mpCost;
					if (!board[3][k].isEmpty()) {
						board[3][k].hpDeduct(board[i][j].damage);
						board[3][k].updateValue();
					}
					board[i][j].skillUsed = true;
					board[2][k].updateValue();
					board[i][j].updateValue();
					State s = new State(board, cloneHand(), sp, list);
					result.add(s);
					board = board2;
				}
			}
			break;
		}
		case 4: {
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(moves);
			list.add("activate " + (i * 4 + j));
			VirtualCard[][] board2 = cloneBoard();
			for (int m = 0; m < 2; m++) {
				for (int n = 0; n < 4; n++) {
					if (!board[m][n].isEmpty()) {
						board[m][n].hp[0] += board[i][j].damage;
						board[m][n].updateValue();
					}
				}
			}
			board[i][j].mp[0] -= board[i][j].mpCost;
			board[i][j].skillUsed = true;
			board[i][j].updateValue();
			State s = new State(board, cloneHand(), sp, list);
			result.add(s);
			board = board2;
			break;
		}
		case 5: {
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(moves);
			list.add("activate " + (i * 4 + j));
			VirtualCard[][] board2 = cloneBoard();
			board[i][j].mp[0] -= board[i][j].mpCost;
			board[i][j].shielded = true;
			board[i][j].skillUsed = true;
			board[i][j].updateValue();
			State s = new State(board, cloneHand(), sp, list);
			result.add(s);
			board = board2;
			break;
		}
		case 6: {
			for (int m = 0; m < 2; m++) 
				for (int n = 0; n < 4; n++) {
					VirtualCard c = new VirtualCard(board[i][j].damage);
					if (board[m][n].isEmpty() && c.SpCost <= sp) {
						ArrayList<String> list = new ArrayList<String>();
						list.addAll(moves);
						list.add("activate " + (i * 4 + j) + " " + (m * 4 + n));
						VirtualCard[][] board2 = cloneBoard();
						board[m][n] = c;
						board[i][j].mp[0] -= board[i][j].mpCost;
						board[i][j].skillUsed = true;
						board[i][j].updateValue();
						State s = new State(board, cloneHand(), sp - c.SpCost, list);
						result.add(s);
						board = board2;
					}
				}
		}
		default: break;
		}
		return result;
	}
	
	public ArrayList<State> summon(int i) {
		ArrayList<State> result = new ArrayList<State>();
		VirtualCard c = new VirtualCard(hand[i]);
		if (c.SpCost <= sp) {
			for (int j = 0; j < 2; j++) 
				for (int k = 0; k < 4; k++) {
					if (board[j][k].isEmpty()) {
						ArrayList<String> list = new ArrayList<String>();
						list.addAll(moves);
						list.add("summon " + i + " " + (j * 4 + k));
						VirtualCard[][] board2 = cloneBoard();
						board[j][k] = c;
						int[] hand2 = cloneHand();
						hand[i] = 0;
						State s = new State(board, hand, sp - c.SpCost, list);
						result.add(s);
						board = board2;
						hand = hand2;
					}
				}
		}
		return result;
	}
	
	
	public boolean equals(State s) {
		for (int i = 0; i < 4 ; i++) 
			for (int j = 0; j < 4; j++)
				if (!this.board[i][j].equals(s.board[i][j])) return false;
		return true;
	}
	
	public void printState() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(board[i][j].toString() + "\t");
			}
			System.out.println();
		}
		System.out.println(moves);
		System.out.println("h = " + h);
	}
}
