package kisaragi.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import kisaragi.language.Grammar;
import kisaragi.language.Nonterminal;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Terminal;


public class LL1 {

	private Map<Nonterminal, Map<Terminal, Production>> analysisForm = new HashMap<>();
	private Grammar grammar = null;
	private boolean legal = false;
	
	private void createAnalysisForm() {
		for(Symbol symbol : grammar.getSymbols()) {
			if(!symbol.isTerminal()) {
				Map<Terminal, Production> row = new HashMap<>();
				analysisForm.put((Nonterminal)symbol, row);
			}
		}
		
		for (Production production : grammar.getProductions()) {
			Set<Terminal> firstalpha = grammar.FIRST(production.getRight(), 0);
			for (Terminal token : firstalpha) {
				Map<Terminal, Production> rowtemp = analysisForm.get(production.getLeft());
				if(rowtemp.containsKey(token)) {
					if(!rowtemp.get(token).equals(production)) {
						return;
					}
				}
				else {
					rowtemp.put(token, production);
				}
			}
			if(firstalpha.contains(Terminal.e)) {
				Set<Terminal> followalpha = grammar.FOLLOW(production.getLeft());
				for (Terminal token : followalpha) {
					Map<Terminal, Production> rowtemp = analysisForm.get(production.getLeft());
					if(rowtemp.containsKey(token)) {
						if(!rowtemp.get(token).equals(production)) {
							return;
						}
					}
					else {
						rowtemp.put(token, production);
					}
				}
			}
		}
		legal = true;
	}
	
	public LL1(Grammar grammar) {
		this.grammar = grammar;
		createAnalysisForm();
	}
	
	public boolean isLegal() {
		return legal;
	}
	
	public void program(List<Terminal> tokens) {
		int count = 0;
		Terminal ip = tokens.get(count++);
		Stack<Symbol> stack = new Stack<>();
		stack.push(Terminal.end);
		stack.push(grammar.getFirstNonTerminal());
		Symbol X = stack.peek();
		while(!X.equals(Terminal.end)) {
			if(X.equals(ip)) {
				stack.pop();
				ip = tokens.get(count++);
			}
			else if(X.isTerminal()){
				System.out.println("error");
				return;
			}
			else if(analysisForm.get(X).get(ip) == null) {
				System.out.println("error");
				return;
			}
			else {
				System.out.println(analysisForm.get(X).get(ip));
				stack.pop();
				List<Symbol> right = new ArrayList<>(analysisForm.get(X).get(ip).getRight());
				Collections.reverse(right);
				for (Symbol symbol : right) {
					stack.push(symbol);
				}
			}
			X = stack.peek();
		}
	}
	
	
	public String displayAnalysisForm() {
		
		List<Symbol> symbolEX = new ArrayList<>();
		for (Symbol symbol : grammar.getSymbols()) {
			if(symbol.isTerminal()) {
				symbolEX.add(symbol);
			}
		}
		symbolEX.add(Terminal.end);
		
		StringBuffer sb = new StringBuffer();
		sb.append("Nonterminal|");
		for (Symbol symbol : symbolEX) {
			sb.append(symbol.toString());
			sb.append("|");
		}
		sb.append("\n");
		for(int j = 0; j <= symbolEX.size(); j++) {
			sb.append("-|");
		}
		sb.append("\n");
		for(Nonterminal i : analysisForm.keySet()) {
			Map<Terminal, Production> row = analysisForm.get(i);
			
			sb.append(i + "|");
			for(Symbol symbol : symbolEX) {
				if(row.get(symbol)!=null) {
					sb.append(row.get(symbol) + "|");
				}
				else {
					sb.append("|");
				}
			}
			sb.append("\n");
		}
		
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
}
