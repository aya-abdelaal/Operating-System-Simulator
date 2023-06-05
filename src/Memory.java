import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Memory {

	Object[] Memory;
	private static Memory instance;

	public Memory() {
		this.Memory = new Object[40];

	}

	public Object load(int x) {
		return Memory[x];

	}

	public void store(int x, Object O) {
		Memory[x] = O;

	}

	private int getProcLocation() {
		if (Memory[MemoryBoundaries.pcb1start] == null) {
			return 1;
		} else {

			if (Memory[MemoryBoundaries.pcb2start] == null) {
				return 2;
			} else {
				if (Memory[2] == ProcessState.TERMINATED || Memory[2] == ProcessState.BLOCKED) {
					unloadProc(1);
					return 1;
				} else {
					if (Memory[7] == ProcessState.TERMINATED || Memory[7] == ProcessState.BLOCKED) {
						unloadProc(2);
						return 2;
					} else {
						if (Memory[2] != ProcessState.RUNNING) {
							unloadProc(1);
							return 1;

						} else {
							unloadProc(2);
							return 2;
						}
					}

				}
			}
		}

	}

	public void unloadProc(int i) {
		int index = i == 1 ? 0 : 5;
		System.out.println("unloading process " + Memory[index]);
		String x = "process" + Memory[index];
		StringBuilder sb = new StringBuilder();
		
		for (int j = 0; j < 5; j++) {//pcb
			sb.append(Memory[index + j]);
			sb.append("\n");
		}
		index = i == 1 ? 22 : 37;
		for (int j = 0; j < 3; j++) {
			sb.append(Memory[index + j]);
			sb.append("\n");
		}

		OS.getInstance().systemCallWriteFile(x, sb.toString());
		index = i == 1 ? 0 : 5;
		for (int j = 0; j < 5; j++) {
			Memory[index + j] = null;
		}

		index = i == 1 ? 10 : 25;
		for (int j = 0; j < 15; j++) {
			Memory[index + j] = null;
		}
	}


	public void unloadProc(Process p){
		if((int)Memory[1] == p.getId()){
			unloadProc(1);
	}else if((int)Memory[6] == p.getId()){
			unloadProc(2);
		}
	}

	public void loadProc(Process p) {
		int i = getProcLocation();
		int index = i == 1 ? 0 : 5;
		System.out.println("loading process " + p.getId());
		String x = "process" + p.getId();
		String atti = OS.getInstance().systemFileReader(x);
		Object[] sebaei = atti.split("\n");
		//id
		Memory[index] = Integer.parseInt((String) sebaei[0]);
		//pc
		Memory[index + 1] = Integer.parseInt((String) sebaei[1]);
		//state
		Memory[index + 2] = sebaei[2];
		//twoCycle?
		Memory[index + 3] = Boolean.parseBoolean((String) sebaei[3]);
		
		index = i == 1 ? 22 : 37;
		for (int aya = 0; aya < 3; aya++) {
			Memory[index + aya] = (sebaei[5 + aya].equals("null")? null:sebaei[5 + aya]);
		}
		p.setMemLoc(i);
		index = i == 1 ? 10 : 25;
		p.setInstructionCounter(loadInstructions(index, p.getId()));

	}

	private int loadInstructions(int index, int i) {
		int count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("src/resources/Program_" + i + ".txt"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				store(index, line);
				index++;
				count++;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return count;
	}
	
	
	public boolean isProcLoaded(int id) {
		if(Memory[0] != null && id == (int)Memory[0])
			return true;
		
		if(Memory[5] != null && id == (int)Memory[5])
			return true;
		return false;
	}
	
	public void toStrings() {
		for(int i = 0; i < Memory.length; i++)
			System.out.println(Memory[i]);
	}

}
