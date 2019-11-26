package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Parser {

	static String token;
	static String tempNext;
	static File tokenFile = new File("tokens");
	static Scanner sc = null;
	static String[] tokens;
	static int numTok = -1; 
	static String lexString = "";
	static int o = 0;
	static int opline = 0;
	static Node root;
	static ArrayList<OpCode> codegen = new ArrayList<OpCode>();
	static Stack<BackPatch> backpatch = new Stack<BackPatch>();
	static String id = "";
	static String type = "";
	static String relop = "";
	static int temp = 0;
	static int params = 0;
	static boolean needbp = false;

	public static void main(String args[]) {
		/*
		OpCode one = new OpCode(opline++);
		OpCode two = new OpCode(opline++);
		one.setCmd("alloc");
		two.setCmd("alloc");
		one.setOp1("4");
		two.setOp1("30");
		one.setResult("x");
		two.setResult("lol");
		codegen.add(one);
		codegen.add(two);
		*/
		//printCode();
		
		File file = new File(args[0]);

		try {
			Lexer lexer = new Lexer(file);
			sc = new Scanner(tokenFile);
			while(!lexString.equals("$")) {
				numTok++;
				lexString = sc.nextLine();
			}
			tokens = new String[numTok + 1];
			sc = new Scanner(tokenFile);
			for(int i = 0; i < tokens.length; i++) {
				tokens[i] = sc.nextLine();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		token = tokens[o];
		
		root = new Node("program");
		root.addChild(declarationList());
		if (token.equals("$"))
			System.out.println("ACCEPT");
		else {
			rej();
		}
		printCode();
		
		//System.out.println("HERE@@@@@@@@@@@@@@");
		//root.printChildren(root);
		//System.out.println("END OF PARSE TREE");
		

	}
	
	private static void nextToken() {
		o++;
		token = tokens[o];
	}

	private static Node declarationList() {
		//System.out.println("DECLIST " + token);
		Node node = new Node("declarationList");
		node.addChild(declaration());
		node.addChild(declarationListP());
		
		return node;
	}

	private static Node declarationListP() {
		Node node = new Node("declarationListP");
		//System.out.println("DECLISTP " + token);
		if(token.equals("K: int") || token.equals("K: void")) {
		node.addChild(declaration());
		node.addChild(declarationListP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node declaration() {
		Node node = new Node("declaration");
		//System.out.println("DEC " + token);
		node.addChild(typeSpecifier());
		if (token.contains("ID: ")) {
			//System.out.println("DEC " + token);
			id = token.substring(4);
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}

		node.addChild(declarationP());
		
		return node;
	}

	private static Node declarationP() {
		Node node = new Node("declarationP");
		//System.out.println("DECP " + token);
		if (token.equals(";") || token.equals("[")) {
			OpCode temp = new OpCode(opline++);
			temp.setCmd("alloc");
			temp.setOp1("4");
			temp.setResult(id);
			codegen.add(temp);
			//System.out.println("HEHEHEHEHEHE");
			
			node.addChild(varDeclarationP());
		} else {
			if (token.equals("(")) {
				OpCode temp = new OpCode(opline++);
				temp.setCmd("func");
				temp.setOp1(id);
				temp.setOp2(type);
				id = "";
				type = "";
				codegen.add(temp);
				node.addChild(functionDeclarationP());
			} else {
				rej();
			}
		}
		
		return node;
	}

	private static Node varDeclaration() {
		OpCode temp = new OpCode(opline++);
		temp.setCmd("alloc");
		temp.setOp1("4");
		Node node = new Node("varDeclaration");
		//System.out.println("VARDEC " + token);
		node.addChild(typeSpecifier());
		//System.out.println("VARDEC AFTER TYPE " + token);
		if (token.contains("ID: ")) {
			temp.setResult(token.substring(4));
			node.addChild(token());
			nextToken();
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			} else if (token.equals("[")) {
				node.addChild(token());
				nextToken();
				if (token.contains("NUM: ")) {
					temp.setOp1(Integer.toString(4 * Integer.parseInt(token.substring(5))));
					node.addChild(token());
					nextToken();

					if (token.equals("]")) {
						node.addChild(token());
						nextToken();
						if (token.equals(";")) {
							node.addChild(token());
							nextToken();
						} else {
							rej();
						}
					} else {
						rej();
					}
				} else {
					rej();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		codegen.add(temp);
		return node;

	}
		 

	private static Node varDeclarationP() {
		Node node = new Node("varDeclarationP");
		//System.out.println("VARDECP " + token);
		if (token.equals(";")) {
			node.addChild(token());
			nextToken();
		}
		else if (token.equals("[")) {
			node.addChild(token());
			nextToken();
			if (token.contains("NUM: ")) {
				node.addChild(token());
				nextToken();
				if (token.equals("]")) {
					node.addChild(token());
					nextToken();
					if (token.equals(";")) {
						node.addChild(token());
						nextToken();
					} else {
						rej();
					}
				} else {
					rej();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node typeSpecifier() {
		Node node = new Node("typeSpecifier");
		//System.out.println(node.getData() + "HELLO");
		//System.out.println("TYPESPEC " + token);
		if (token.equals("K: int") || token.equals("K: void")) {
			type = token.substring(3);
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}
		
		return node;
	}

	private static Node functionDeclarationP() {
		Node node = new Node("functionDeclarationP");
		if (token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(params());
			if (token.equals(")")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
			node.addChild(compoundStatement());
		}
		else {
			rej();
		}
		
		
		return node;
	}

	private static Node params() {
		Node node = new Node("params");
		//System.out.println("PARAMS " + token);
		if (token.equals("K: void")) {
			codegen.get(opline - 1).setOp2("Void");
			codegen.get(opline - 1).setResult("0");
			node.addChild(token());
			nextToken();
		} else
			node.addChild(paramsList());
		
		return node;
	}

	private static Node paramsList() {
		Node node = new Node("paramList");
		//System.out.println("PARAMSLIST " + token);
		params++;
		node.addChild(param());
		node.addChild(paramsListP());
		
		return node;
	}

	private static Node paramsListP() {
		Node node = new Node("paramListP");
		//System.out.println("PARAMSLISTP " + token);
		if (token.equals(",")) {
			params++;
			node.addChild(token());
			nextToken();
			node.addChild(param());
			node.addChild(paramsListP());
		}
		else {
			codegen.get(opline - 1).setResult(Integer.toString(params));
			params = 0;
			node.addChild(epsilon());
		}
		return node;
	}

	private static Node param() {
		Node node = new Node("param");
		//System.out.println("PARAM " + token);
		node.addChild(typeSpecifier());
		if(token.contains("ID: ")) {
			node.addChild(token());
			nextToken();
			if(token.equals("[")) {
				node.addChild(token());
				nextToken();
				if(token.equals("]")) {
					node.addChild(token());
					nextToken();
				}
				else {
					rej();
				}
			}
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node compoundStatement() {
		Node node = new Node("compoundStatement");
		//System.out.println("COMPSTMT " + token);
		if (token.equals("{")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		node.addChild(localDeclaration());
		node.addChild(statementList());
		//System.out.println("COMPSTMT " + token);
		if (token.equals("}")) {
			node.addChild(token());
			nextToken();
		}
		else {
			//System.out.println("THIS ONE");
			rej();
		}
		
		return node;
	}

	private static Node localDeclaration() {
		Node node = new Node("localDelcaration");
		//System.out.println("LOCDEC " + token);
		if (token.contains("K: int")) {
			id = "int";
			node.addChild(varDeclaration());
			node.addChild(localDeclaration());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node statementList() {
		Node node = new Node("statementList");
		//System.out.println("STMTLISTP " + token);
		if (token.contains("K: ") || token.equals(";") || token.contains("NUM: ") || token.equals("(") || token.contains("ID: ") || token.equals("{"))  {
			node.addChild(statement());
			node.addChild(statementList());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}


	private static Node statement() {
		Node node = new Node("statement");
		//System.out.println("STMT " + token);
		if(token.equals("{"))
			node.addChild(compoundStatement());
		else if(token.equals("K: if"))
			node.addChild(selectionStatement());
		else if(token.equals("K: while"))
			node.addChild(iterationStatement());
		else if(token.equals("K: return"))
			node.addChild(returnStatement());
		else
			node.addChild(expressionStatement());
		
		return node;

	}

	private static Node expressionStatement() {
		Node node = new Node("expressionStatement");
		//System.out.println("EXPSTMT " + token);
		if (token.equals(";")) {
			node.addChild(token());
			nextToken();
		} else {
			node.addChild(expression());
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
		}
		
		return node;
	}

	private static Node selectionStatement() {
		Node node = new Node("selectionStatement");
		//System.out.println("SELSTMT " + token);
		if (token.equals("K: if")) {
			BackPatch temp = new BackPatch(opline);
			needbp = true;
			node.addChild(token());
			nextToken();
			if (token.equals("(")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				if (token.equals(")")) {
					node.addChild(token());
					nextToken();
					node.addChild(statement());
					if (token.equals("K: else")) {
						node.addChild(token());
						nextToken();
						node.addChild(statement());
					}
				} else {
					
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node iterationStatement() {
		Node node = new Node("iterationStatement");
		//System.out.println("ITERSTMT " + token);
		if (token.equals("K: while")) {
			node.addChild(token());
			nextToken();
			if (token.equals("(")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				if (token.equals(")")) {
					node.addChild(token());
					nextToken();
					node.addChild(statement());
				} else {
					rej();
				}

			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node returnStatement() {
		Node node = new Node("returnStatement");
		//System.out.println("RETSTMT " + token);
		if (token.equals("K: return")) {
			node.addChild(token());
			nextToken();
			if(token.equals("(") || token.contains("NUM: ") || token.contains("ID: ")) {
				node.addChild(expression());
			}
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			}
			else
				rej();
		}
		else
			rej();
		
		return node;
	}

	private static Node expression() {
		Node node = new Node("expression");
		//System.out.println("EXP " + token);
		node.addChild(additiveExpression());
		if(token.equals("<=") || token.equals("<") || token.equals(">") || token.equals(">=") || 
				token.equals("==") || token.equals("!=")) {
			node.addChild(relop());
			node.addChild(additiveExpression());
		}
		
		return node;
	}

	private static Node var() {
		Node node = new Node("var");
		//System.out.println("VAR " + token);
		if(token.equals("[")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			if(token.equals("]")) {
				node.addChild(token());
				nextToken();
			}
			else {
				rej();
			}
		}
		if(token.equals("=")) {
			node.addChild(token());
			nextToken();
			boolean done = false;
			if(token.equals("[")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				done = true;
				if(token.equals("]")) {
					node.addChild(token());
					nextToken();
				}
				else {
					rej();
				}
			}
			if(!done) {
				node.addChild(expression());
			}
			
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}


	private static Node relop() {
		Node node = new Node("relop");
		//System.out.println("RELOP " + token);
		if (token.equals("<=") || token.equals("<") || token.equals(">") || token.equals(">=") || token.equals("==")
				|| token.equals("!=")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	

	private static Node additiveExpression() {
		Node node = new Node("additiveExpression");
		//System.out.println("ADDEXP " + token);
		node.addChild(term());
		node.addChild(additiveExpressionP());
		
		return node;
	}

	private static Node additiveExpressionP() {
		Node node = new Node("additiveExpressionP");
		//System.out.println("ADDEXPP " + token);
		if(token.equals("+") || token.equals("-")) {
			node.addChild(addop());
			node.addChild(term());
			node.addChild(additiveExpressionP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node addop() {
		Node node = new Node("addop");
		//System.out.println("ADDOP " + token);
		if(token.equals("+") || token.equals("-")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node term() {
		Node node = new Node("term");
		//System.out.println("TERM " + token);
		node.addChild(factor());
		node.addChild(termP());
		
		return node;
	}

	private static Node termP() {
		Node node = new Node("termP");
		//System.out.println("TERMP " + token);
		if(token.equals("*") || token.equals("/")) {
			node.addChild(mulop());
			node.addChild(factor());
			node.addChild(termP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node mulop() {
		Node node = new Node("mulop");
		//System.out.println("MULOP " + token);
		if(token.equals("*") || token.equals("/")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node factor() {
		Node node = new Node("factor");
		//System.out.println("FACTOR " + token);
		if (token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			if (token.equals(")")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
		}
		else if (token.contains("ID: ")) {
			node.addChild(token());
			nextToken();
			node.addChild(factorP());
		}

		else if (token.contains("NUM: ")) {
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}
		
		return node;
	}

	private static Node factorP() {
		Node node = new Node("factorP");
		//System.out.println("FACTORP " + token);
		if(token.equals("(") || token.equals(",")) {
			node.addChild(callP());
		}
		else
			node.addChild(var());
		
		return node;
		}
	
	


	private static Node callP() {
		Node node = new Node("callP");
		//System.out.println("CALLP " + token);
		if(token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(args());
			if(token.equals(")")) {
				node.addChild(token());
				nextToken();
			}
			else {
				rej();
			}
		} else if (token.equals(",")) {
			node.addChild(args());
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node args() {
		Node node = new Node("args");
		//System.out.println("ARGS " + token);
		if(token.equals("(") || token.contains("ID: ") || token.contains("NUM: ")) {
			node.addChild(argList());
		}
		else if(token.equals(",")) {
			node.addChild(argListP());
		}
		else {
			node.addChild(epsilon());
		}
		return node;
	}

	private static Node argList() {
		Node node = new Node("argList");
		//System.out.println("ARGLIST " + token);
		node.addChild(expression());
		node.addChild(argListP());
		
		return node;
	}

	private static Node argListP() {
		Node node = new Node("argListP");
		//System.out.println("ARGLISTP " + token);
		if(token.equals(",")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			node.addChild(argListP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static void rej() {
		System.out.println("REJECT");
		System.exit(0);
	}
	
	private static Node token() {
		Node node = new Node(token);
		return node;
	}
	
	private static Node epsilon() {
		Node node = new Node("Epsilon");
		return node;
	}
	
	private static void printCode() {
		System.out.printf("%s       %-12s %-12s %-12s %-12s", "#", "OpCode", "Op1", "Op2", "Result");
		System.out.println();
		for(OpCode op : codegen) {
			System.out.printf("%d       %-12s %-12s %-12s %-12s", op.getLine(), op.getCmd(), op.getOp1(), op.getOp2(), op.getResult());
			System.out.println();
		}
	}

}
