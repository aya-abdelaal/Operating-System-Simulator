import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;

public class OS {
	final static OS instance = new OS();
	Mutex userInput = new Mutex();
	Mutex userOutput = new Mutex();
	Mutex file = new Mutex();
	int clock;
	Process current;
	Scheduler sc;
	Memory memory;
	CodeParser cp;
	boolean twoCycle;
	Object var;

	private OS() {
		 userInput = new Mutex();
		userOutput = new Mutex();
		file = new Mutex();
		sc = new Scheduler();
		memory = new Memory();
		cp = new CodeParser();
	}

	public Memory getMemory() {
		return memory;
	}

	public static OS getInstance() {
		return instance;
	}

	public void arrival(Process p) {
		if (current == null && sc.isReadyEmpty())
			current = p;
		sc.addReadyProcess(p);
	}

	public void run() {
		int pcLoc;
		int pc;

		System.out.println("\n\n\nclock cycle " + clock);



			current = sc.getNext(); // process to run

			if (clock == 0) {
				// p1

				Process p1 = new Process(3, 1);
				arrival(p1);

			} else if (clock == 2) {
				Process p2 = new Process(3, 2);
				arrival(p2);

			}

			else if (clock == 4) {
				Process p3 = new Process(3, 3);
				arrival(p3);
			}
			

			if (current != null) {
				
				System.out.println("Process " + current.getId() + " is running");
				if (!memory.isProcLoaded(current.getId())) {
					memory.loadProc(current);
				}
				
				pcLoc = current.getMemLoc() == 1 ? 1 : 6;
				int mempc = (int) (memory.load(pcLoc));
				pc = current.getMemLoc() == 1 ? (10 + mempc) : (25 + mempc);
				String instruction = (String) memory.load(pc);
				System.out.println("instruction : " + instruction);
				
				twoCycle = (boolean) systemCallMemRead(current.getMemLoc() ==1 ? 3:8);
				
				if (twoCycle) {
					String[] instr = instruction.split(" ");
					Object value;
					if(sc.getCount() == 2)
						value = var;
					else 
						value = systemCallMemRead(current.getMemLoc() ==1 ? 24:39);
					
					systemCallMemWrite(instr[1], value);
					twoCycle = false;
					memory.store(current.getMemLoc() ==1 ? 3:8, twoCycle);
					var = null;
					memory.store(current.getMemLoc() ==1 ? 24:39, null);
				}else {
					cp.parse(instruction);
				}

				if (!twoCycle) {

					memory.store(pcLoc, mempc + 1);
				}
				
				memory.toStrings();

			} else {
				return;
			}

		clock++;
		sc.incrementCount();
		run();
	}

	public void execSemWait(String x) {
		boolean flag;
		if (x.equals("userinput")) {
			flag = userInput.semWait(current);
		} else {
			if (x.equals("useroutput")) {
				flag = userOutput.semWait(current);
			} else {
				flag = file.semWait(current);
			}
		}
		if (!flag)

			sc.addBlockedProcess(current);

	}

	public void execSignal(String x) {
		Process p;
		if (x.equals("userinput")) {
			p = userInput.semSignal(current);
		} else {
			if (x.equals("useroutput")) {
				p = userOutput.semSignal(current);
			} else {
				p = file.semSignal(current);
			}
		}
		if (p != null)
			sc.unblockProcess(p);

	}

	public void systemCallMemWrite(String variable, Object value) {

		int index = current.getMemLoc() == 1 ? 22 : 37;
		if (variable.equals("a"))
			memory.store(index, value);
		else if (variable.equals("b"))
			memory.store(index + 1, value);
		else
			memory.store(index + 2, value);
	}

	public Object systemCallMemRead(int index) {
		return memory.load(index);
	}

	public void systemCallInput() {
		twoCycle = true;
		Scanner s = new Scanner(System.in);
		System.out.println("Please enter a value");
		var = s.nextLine();
		memory.store(current.getMemLoc() ==1 ? 3:8, twoCycle);
		memory.store(current.getMemLoc() ==1 ? 24:39, var);

	}

	public void systemCallInputFile(String fileName) {
		twoCycle = true;
		var = systemFileReader(fileName);
		memory.store(current.getMemLoc() ==1 ? 3:8, twoCycle);
		memory.store(current.getMemLoc() ==1 ? 24:39, var);

	}



	public void systemCallPrint(String x) {
		System.out.println(x);
	}

	public void systemCallWriteFile(String x, String data) {
		File file = new File("src/resources/" + x + ".txt");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try{
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String systemFileReader(String x) {
		File file = new File("src/resources/" + x + ".txt");
		BufferedReader fr = null;
		StringBuilder sb = null;
		try {
			fr = new BufferedReader(new FileReader(file));
			String line;
			sb = new StringBuilder();
			while ((line = fr.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			fr.close();
		} catch (Exception e) {
			System.out.println("problem reading file");
			exit(1);
		}

		return sb.toString();
	}

	public static void reset() {
		File file = new File("src/resources/process1.txt");
		file.delete();
		file = new File("src/resources/process2.txt");
		file.delete();
		file = new File("src/resources/process3.txt");
		file.delete();
		file = new File("src/resources/myFile.txt");
		file.delete();
	}
	public String getVariable(String s) {
		int index = current.getMemLoc() == 1 ? 22 : 37;
		if (s.equals("a"))
			return systemCallMemRead(index).toString();
		else if (s.equals("b"))
			return systemCallMemRead(index + 1).toString();
		else
			return systemCallMemRead(index + 2).toString();
	}


	public static void main(String[] args) {

		reset();
		System.out.println("Memory format :  \n5 cells for each PCB (0-9) \n12 cells for each process's instructions (10-21) (25-36) \n3 cells for each process's variables (22-24) (37-39) \n");


		OS os = OS.getInstance();
		os.run();

	}
}
