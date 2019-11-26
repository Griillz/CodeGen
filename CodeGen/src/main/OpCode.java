package main;

public class OpCode {
	
	public int line = 0;
	public String cmd = "";
	public String op1 = "";
	public String op2 = "";
	public String result = "";
	
	public OpCode() {
		
	}
	
	public OpCode(int curline) {
		this.line = curline;
	}

	
	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getOp1() {
		return op1;
	}

	public void setOp1(String op1) {
		this.op1 = op1;
	}

	public String getOp2() {
		return op2;
	}

	public void setOp2(String op2) {
		this.op2 = op2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
}
