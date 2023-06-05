import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Scheduler {

	// round robin
	int quantum;
	Queue blocked;
	Queue ready;
	int count;


	public Scheduler() {
		blocked = new ArrayBlockingQueue(4);
		ready = new ArrayBlockingQueue(4);
		this.quantum = 12;
	}

	public void addReadyProcess(Process p) {
		ready.add(p);
		p.setState(ProcessState.READY);
	}

	public Process getNext() {
		if(ready.isEmpty())
			return null;
		Process p = (Process) ready.peek();
		if (p.isDone()) {
			OS.getInstance().getMemory().store(p.getMemLoc() == 1? 2:7, ProcessState.TERMINATED);
			ready.remove();
			System.out.println("Process " + p.getId() + " is finished");
			printQueues();
			if(ready.isEmpty())
				return null;
		} else {
			if (count < quantum) {
				return p;
			}
			ready.add(ready.remove());
		}
		count = 0;
		p = (Process) ready.peek();
		System.out.println("Process " + p.getId() + " is chosen");
		printQueues();
		return p;

	}


	public void incrementCount () {
		count ++;
	}

	public boolean isReadyEmpty() {
		return ready.isEmpty();
	}

	public void addBlockedProcess(Process p) {
		blocked.add(p);
		p.setState(ProcessState.BLOCKED);
		OS.getInstance().getMemory().store(p.getMemLoc() == 1? 2:7, ProcessState.BLOCKED);
		count = -1;
		ready.remove(p);
		printQueues();
	}

	public void unblockProcess(Process p) {
	p.setState(ProcessState.READY);
		OS.getInstance().getMemory().store(p.getMemLoc() == 1? 2:7, ProcessState.READY);
		blocked.remove(p);
		ready.add(p);
		printQueues();
	}

	public int getCount() {
		return count;
	}

	public void printQueues(){
		System.out.println("Ready Queue: ");
		for(Object p : ready){
			System.out.println(((Process)p).getId());
		}
		System.out.println("Blocked Queue: ");
		for(Object p : blocked){
			System.out.println(((Process)p).getId());
		}
	}
}
