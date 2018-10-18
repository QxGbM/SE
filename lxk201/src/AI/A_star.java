package AI;

import java.util.ArrayList;

public class A_star{
	
	static int maxNodes = 100;

	public static void main (String[] args) {
		VirtualCard[][] board = new VirtualCard[4][4];
		int[] hand = new int[5];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				board[i][j] = new VirtualCard();
			}
		}

		board[0][0] = new VirtualCard(202, 0, 2, false, false);
		board[0][1] = new VirtualCard(105, 3, 5, false, false);
		board[2][0] = new VirtualCard(102);
		board[2][1] = new VirtualCard(103);
		board[3][0] = new VirtualCard(104);
		
		hand[0] = 202;
		hand[1] = 204;
		System.out.println(search(board, hand, 5));
	}
	
	public static ArrayList<String> search (VirtualCard[][] b, int[] h, int sp) {
		ArrayList<State> states = new ArrayList<State>();
		states.add(new State(b, h, sp, new ArrayList<String>()));
		State minS = states.get(0);
		for (int i = 0, j = 1; i < maxNodes && i < j; i++) {
			if (states.get(i).getH() < minS.getH()) {
				minS = states.get(i);
			}
			ArrayList<State> expanded = states.get(i).expand();
			while (!expanded.isEmpty()) {
				int k;
				State s = expanded.remove(0);
				for (k = 0; k < j; k++) {
					if (s.equals(states.get(k))) {
						if(s.getMoves().size() < states.get(k).getMoves().size()) 
						{states.remove(k); if(k < i) i--; j--;}
						else break;
					}
				}
				if (k == j) {
					double f = s.getH() + s.getMoves().size();
					for (k = i + 1; k < j && states.get(k).getH() + states.get(k).getMoves().size() <= f; k++) {}
					states.add(k, s); j++;
				}
			}
		}
		return minS.getMoves();
	}
}

