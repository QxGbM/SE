package AI;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;

public class AI {
	
	static int maxNodes = 100;

	public static void main (String[] args) {

		Display display = new Display();
		int[] deck = {101, 101, 102, 102, 103, 103, 104, 105, 201, 201, 202, 202, 203, 204, 205};
		Match.startMatch(deck, display);
		
		while (!Match.shell.isDisposed())
			if (!display.readAndDispatch()) display.sleep();
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
		
	}
}

