package kisaragi.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import kisaragi.language.Nonterminal;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Token;


public class LL1 extends L {

	private Map<Nonterminal, Map<Token, Production>> analysisForm = new HashMap<>();
	
	
	private void createAnalysisForm() {
		for(Symbol symbol : symbols) {
			if(!symbol.isToken()) {
				Map<Token, Production> row = new HashMap<>();
				analysisForm.put((Nonterminal)symbol, row);
			}
		}
		Map<Token, Production> row = new HashMap<>();
		analysisForm.put(firstProduction.getLeft(), row);
		
		for (Production production : basicProductionsAsKEY.keySet()) {
			Set<Token> firstalpha = FIRST(production.getRight());
			for (Token token : firstalpha) {
				Map<Token, Production> rowtemp = analysisForm.get(production.getLeft());
				rowtemp.put(token, production);
			}
			if(firstalpha.contains(Token.e)) {
				Set<Token> followalpha = follows.get(production.getLeft());
				for (Token token : followalpha) {
					Map<Token, Production> rowtemp = analysisForm.get(production.getLeft());
					rowtemp.put(token, production);
				}
			}
		}
	}
	
	public LL1(String path) {
		super(path);
		createAnalysisForm();
	}
	
	public void program(List<Token> tokens) {
		int count = 0;
		Token ip = tokens.get(count++);
		Stack<Symbol> stack = new Stack<>();
		stack.push(Token.end);
		stack.push(firstProduction.getLeft());
		Symbol X = stack.peek();
		while(!X.equals(Token.end)) {
			if(X.equals(ip)) {
				stack.pop();
				ip = tokens.get(count++);
			}
			else if(X.isToken()){
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
		for (Symbol symbol : symbols) {
			if(symbol.isToken()) {
				symbolEX.add(symbol);
			}
		}
		symbolEX.add(Token.end);
		
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
			Map<Token, Production> row = analysisForm.get(i);
			
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
