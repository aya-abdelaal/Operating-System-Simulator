
public class Process {

	private int memlocation; // 1 ya 2 
	private boolean done;
	private int id;
	private ProcessState state;
	private int instructionCounter;

	public Process(int memlocation, int id) {
		this.memlocation = memlocation;
		this.state = ProcessState.NEW;
		this.id = id;
		this.instructionCounter = 14-3;
		
		String x = "process" + id;
		StringBuilder sb = new StringBuilder();
			sb.append(id);
			sb.append("\n");
			sb.append(0);
			sb.append("\n");
			sb.append(state);
			sb.append("\n");
			sb.append(false);
			sb.append("\n");


		for (int j = 0; j < 4; j++) {
			sb.append("null");
			sb.append("\n");
		}

		OS.getInstance().systemCallWriteFile(x, sb.toString());
	}

	public void setInstructionCounter(int i) {
		instructionCounter = i;
	}

	public int getMemLoc() {
		return memlocation;
	}

	public void setMemLoc(int i) {
		memlocation = i;
	}

	public void procDone() {
		done = true;
	}

	public int getId() {
		return id;
	}

	public boolean isDone() {
		int pcLoc = memlocation == 1 ? 1 : 6;
		int pc = (int) OS.getInstance().systemCallMemRead(pcLoc);
		if(pc >= instructionCounter) {
			done = true;
			state = ProcessState.TERMINATED;
		}
		return done;
	}

	public ProcessState getState() {
		return state;
	}

	public void setState(ProcessState state) {
		this.state = state;
		System.out.println("Process " + id + " state set to " + state);
	}

			

}
