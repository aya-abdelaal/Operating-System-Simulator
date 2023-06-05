
public class CodeParser {



	public CodeParser() {

	}

	public void parse(String instruction) {
		String[] instr = instruction.toLowerCase().split(" ");
		OS os = OS.getInstance();
		switch (instr[0]) {
		case "semwait":
			os.execSemWait(instr[1]);
			break;
		case "semsignal":
			os.execSignal(instr[1]);
			break;
		case "assign": {
			if (instr[2].equals("input")) {
				// 2 clk cycles
				os.systemCallInput();
			} else if(instr[2].equals("readfile"))
					os.systemCallInputFile(os.getVariable(instr[3]));
				else
				os.systemCallMemWrite(instr[1], instr[2]);

			break;
		}
		case "printfromto": {
			int a;
			int b;
			try {

				a = Integer.parseInt(instr[1]);
				b = Integer.parseInt(instr[2]);
			}
			catch (NumberFormatException e) {
				try{
					 a =	Integer.parseInt( os.getVariable(instr[1]));
					b = Integer.parseInt(os.getVariable(instr[2]));
					if(a >= b){
						os.systemCallPrint("Invalid input");
						break;
					}
				}
				catch (NumberFormatException e1) {
					os.systemCallPrint("Invalid input");
					break;
				}
			}
			for (int i = a; i< b; i++) {
				os.systemCallPrint(((Integer) i).toString());
			}
			break;
		}
		
		case "writefile": {
			os.systemCallWriteFile(os.getVariable(instr[1]), os.getVariable(instr[2]));
		break;
		}
		
		case "print": {
			if(instr[1].equals("a") || instr[1].equals("b"))
				os.systemCallPrint(os.getVariable(instr[1]));
			else
			os.systemCallPrint(instr[1]);
			break;
		}


		
		default: break;
		}

	}

}
