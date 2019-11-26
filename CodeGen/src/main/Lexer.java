package main;

import java.io.File;


import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Lexer {
	
	public Lexer() {
		
	}
	
	public Lexer(File input) throws FileNotFoundException {
		// Array of individual chars to be caught
		char language[] = {'+', '-', '*', '/', '>', '<', '=',
		';', ',', '(', ')', '[', ']', '{', '}'};	
		// Array of keywords
		String keywords[] = {"else", "if", "int", "return", "void", "while"};
		boolean comment = false; // Comment mode boolean
		

				PrintWriter printer = new PrintWriter("tokens");
				
				 // Initializes file
				Scanner scanner = new Scanner(input); // Reads in file
				int i = 0;
				
				// reads file line by line
				while (scanner.hasNextLine()) {
					String current = scanner.nextLine();

					i = 0;
					if(current.isEmpty()) {
						
					}
					else
					//System.out.println("INPUT: " + current);
					try {
						while (i < current.length()) {
								
								// reads char by char until at end of line
								switch (current.charAt(i)) { 
								case '*':
									if(current.charAt(i + 1) == '/' && comment == true) {
										comment = false;
										i += 2;
									}
									else if(comment == false)  {
										//System.out.println("*");
										printer.println("*");
										i++;
									}
									else {
										i++;
									}
									break;
								case '/':
									if(current.charAt(i + 1) == '*') {
										comment = true;
										i += 2;
									}
									else if(current.charAt(i + 1) == '/') {
										i += current.length();
									}
									else if(comment == false){
										//System.out.println("/");
										printer.println("/");
										i++;
									}
									else {
										i++;
									}
									break;
								case '+':
									if(comment == false) {
									//System.out.println("+");
									printer.println("+");
									i++;
									}
									else {
										i++;
									}
									break;
								case '-':
									//System.out.println("-");
									printer.println("-");
									i++;
									break;
								case '>':
									if (current.charAt(i + 1) == '=' && comment == false) {
										//System.out.println(">=");
										printer.println(">=");
										i += 2;
									} else if (comment == false){
										//System.out.println(">");
										printer.println(">");
										i++;
									}
									else {
										i++;
									}
									break;
								case '<':
									if (current.charAt(i + 1) == '=' && comment == false) {
										//System.out.println("<=");
										printer.println("<=");
										i += 2;
									} else if (comment == false){
										//System.out.println("<");
										printer.println("<");
										i++;
									}
									else {
										i++;
									}
									break;
								case '=':
									if (current.charAt(i + 1) == '=' && comment == false) {
										//System.out.println("==");
										printer.println("==");
										i += 2;
									} else if (comment == false){
										//System.out.println("=");
										printer.println("=");
										i++;
									}
									else {
										i++;
									}
									break;
								case ';':
									if(comment == false) {
										//System.out.println(";");
									printer.println(";");
									i++;
									}
									else {
										i++;
									}
									break;
								case ',':
									if(comment == false) {
										//System.out.println(",");
									printer.println(",");
									i++;
									}
									else {
										i++;
									}
									break;
								case '(':
									if(comment == false) {
										//System.out.println("(");
									printer.println("(");
									i++;
									}
									else {
										i++;
									}
									break;
								case ')':
									if(comment == false) {
									//System.out.println(")");
									printer.println(")");
									i++;
									}
									else {
										i++;
									}
									break;
								case '[':
									if(comment == false) {
										//System.out.println("[");
									printer.println("[");
									i++;
									}
									else {
										i++;
									}
									break;
								case ']':
									if(comment == false) {
										//System.out.println("]");
									printer.println("]");
									i++;
									}
									else {
										i++;
									}
									break;
								case '{':
									if(comment == false) {
										//System.out.println("{");
									printer.println("{");
									i++;
									}
									else {
										i++;
									}
									break;
								case '}':
									if(comment == false) {
										//System.out.println("}");
									printer.println("}");
									i++;
									}
									else {
										i++;
									}
									break;
								case '!':
									if(comment == false) {
									if (current.charAt(i + 1) == '=') {
										//System.out.println("!=");
										printer.println("!=");
										i += 2;
									}   else {
										//.out.println("EXCLAMATION");
										System.out.println("REJECT");
										System.exit(0);
										i++;
									}
									}
									else {
										i++;
									}
									break;
								default:
									if (Character.isWhitespace(current.charAt(i))) {
										i++;
									} else if(comment == false && Character.isLetter(current.charAt(i))) {
										boolean keyword = false;
										String string = getString(current, i);
										i += string.length();
										for(int j = 0; j < keywords.length; j++) {
											if(string.equals(keywords[j])) {
												//System.out.println("K: " + string);
												printer.println("K: " + string);
												keyword = true;
												break;
											}
										}
										if(keyword == false) {
										//System.out.println("ID: " + string);
										printer.println("ID: " + string);
										}
									}
									else if(comment == true && Character.isLetter(current.charAt(i))) {
										String string = getString(current, i);
										i += string.length();									
									} else if(comment == false && Character.isDigit(current.charAt(i))) {
										String string = getNumber(current, i);
										i += string.length();
										//System.out.println("INT: " + string);;
										printer.println("NUM: " + string);
									}
									else if(comment == true && Character.isDigit(current.charAt(i))) {
										String string = getNumber(current, i);
										i += string.length();									
									}
									else {
										i++;
										System.out.println("REJECT");
										System.exit(0);
									}

								}
							}
						

					}
					// catch out of bounds exception
					catch (StringIndexOutOfBoundsException e) {
						for(int j = 0; j < language.length; j++) {
							if(current.charAt(i) == language[j] && comment == false) {
								//System.out.println(current.charAt(i));
								printer.println(current.charAt(i));
								break;
							}
							else if (j == language.length - 1 && comment == false){
								System.out.println("REJECT");
								System.exit(0);
							}
						}
					}

				}
				scanner.close();
				printer.println("$");
				printer.close();
			 
		
	}
	

	
	// String builder method
	public String getString(String s, int i) {
		int j = i;
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}

		return s.substring(i, j);

	}
	// Digit builder method
	public String getNumber(String s, int i) {
		int j = i;
		for (; j < s.length();) {
			if (Character.isDigit(s.charAt(j))) {
				j++;
			} 
			else {
				return s.substring(i, j);
			}
		}

		return s.substring(i, j);

	}

}
