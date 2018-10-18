package AI;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;

public class AI {
	
	static int maxNodes = 100;

	public static void startAIMatch (int[] deck) {
		Display display = main.Game.display;
		Match.startMatch(deck, display);
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
	
	public static void endGame(boolean win) {
		//TODO 可选：发送数据库更新
		main.Game.inMatch = false;
	}
}

