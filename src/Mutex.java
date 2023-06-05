import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Mutex {

	boolean available;
	Queue blocked;
	Process curr;

	public Mutex() {
		blocked = new ArrayBlockingQueue(4);
		available = true;

	}

	public boolean semWait(Process p) {
		if (available) {
			available = false;
			curr = p;
			return true;
		}
		blocked.add(p);
		return false;

	}

	public Process semSignal(Process p) {
		if (!available && curr == p) {
			available = true;
			curr = null;
			if (!blocked.isEmpty()) {
				curr = (Process) blocked.remove();
				available = false;
				return curr;
			}
		}
		return null;

	}

}
