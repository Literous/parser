package kisaragi.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import kisaragi.language.Nonterminal;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Token;


public abstract class LR extends L{

	protected boolean legal = false;
	protected Map<Integer, Set<Production>> itemsAsVALUE = new HashMap<>();
	protected Map<Set<Production>, Integer> itemsAsKEY = new HashMap<>();
	protected Map<Integer, Map<Symbol, String>> analysisForm = new HashMap<>();

	protected abstract void CLOSURE(Set<Production> i);
	protected abstract void createAnalysisForm();
	
	protected Set<Production> GOTO(Set<Production> I, Symbol X) {
		Set<Production> J = new HashSet<>();
		for (Production production : I) {
			if (production.nextSymbol() != null && production.nextSymbol().equals(X)) {
				Production p = production.pointForward();
				J.add(p);
			}
		}
		CLOSURE(J);
		return J;
	}
	
	protected void createItems(boolean isLR1) {
		Set<Production> I0 = new HashSet<>();
		if(isLR1) {
			I0.add(new Production(firstProduction, Token.end));
		}
		else {
			I0.add(firstProduction);
		}
		CLOSURE(I0);
		itemsAsKEY.put(I0, 0);
		int count = 1;
		boolean flag = false;
		Map<Set<Production>, Integer> temp = new HashMap<>();
		do {
			flag = false;
			for (Set<Production> item : itemsAsKEY.keySet()) {
				for (Symbol symbol : symbols) {
					Set<Production> i_new = GOTO(item, symbol);
					if (i_new.size() != 0 && !itemsAsKEY.containsKey(i_new)) {
						temp.put(i_new, count++);
						flag = true;
					}
				}
			}
			itemsAsKEY.putAll(temp);
			temp.clear();
		} while (flag);
		
		for (Set<Production> item : itemsAsKEY.keySet()) {
			int i = itemsAsKEY.get(item);
			itemsAsVALUE.put(i, item);
		}
		
		if(itemsAsVALUE.size() != itemsAsKEY.size()) {
			try {
				throw new RuntimeException("项目集的标号存在重复，表面程序设计存在缺陷");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String ACTION(int sm, Token ai) {
		Map<Symbol, String> row = analysisForm.get(sm);
		
		if(row.containsKey(ai)) {
			return row.get(ai);
		}
		else {
			return null;
		}
	}
	
	private int GOTO(int sm, Nonterminal A) {
		Map<Symbol, String> row = analysisForm.get(sm);
		if(row.containsKey(A)) {
			return Integer.parseInt(row.get(A));
		}
		else {
			return -1;
		}
	}
	
	public LR(String path) {
		super(path);
	}
	
	public void program(List<Token> tokens) {
		Stack<Integer> stack = new Stack<>();
		stack.push(0);
		int count = 0;
		Token a = tokens.get(count++);
		int s = 0;
		while(true) {
			s = stack.peek();
			String operate = ACTION(s, a);
			if(operate == null) {
				System.out.println("Unanticipated Input, there is a syntax error");
				return;
			}
			else if(operate.charAt(0) == 's'){
				stack.push(Integer.parseInt(operate.substring(1, operate.length())));
				a = tokens.get(count++);
			}
			else {
				int i = Integer.parseInt(operate.substring(1, operate.length()));
				if(i == 0) {
					System.out.println("accept");
					break;
				}
				Production production = basicProductionsAsVALUE.get(i);
				int length = production.getRight().size();
				for(int k = 0; k < length; k++) {
					stack.pop();
				}
				int t = stack.peek();
				stack.push(GOTO(t, production.getLeft()));
				System.out.println(production);
			}
		}
	}
	
	public String displayItems() {
		StringBuffer sb = new StringBuffer();
		for(Set<Production> item : itemsAsKEY.keySet()) {
			sb.append(itemsAsKEY.get(item));
			sb.append("\n");
			for (Production production : item) {
				sb.append(production.toString());
				sb.append("\n");
			}
			sb.append("\n");
		}
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
	
	public String displayAnalysisForm() {
		List<Symbol> symbolEX = new ArrayList<>();
		for (Symbol symbol : symbols) {
			if(symbol.isToken()) {
				symbolEX.add(symbol);
			}
		}
		symbolEX.add(Token.end);
		for (Symbol symbol : symbols) {
			if(!symbol.isToken()) {
				symbolEX.add(symbol);
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("state|");
		for (Symbol symbol : symbolEX) {
			sb.append(symbol.toString());
			sb.append("|");
		}
		sb.append("\n");
		for(int j = 0; j <= symbolEX.size(); j++) {
			sb.append("-|");
		}
		sb.append("\n");
		for(Integer i : analysisForm.keySet()) {
			Map<Symbol, String> row = analysisForm.get(i);
			
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
	
	public boolean islegal() {
		return legal;
	}
}
