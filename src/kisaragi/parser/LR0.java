package kisaragi.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import kisaragi.language.Grammar;
import kisaragi.language.Nonterminal;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Terminal;
import kisaragi.util.IntegerBiMap;

public abstract class LR0 {
	protected boolean legal = false;
	protected Grammar grammar = null;
	
	protected IntegerBiMap<Set<Item>> Is = new IntegerBiMap<>();
	
	protected Map<Integer, Map<Terminal, Integer>> ACTIONForm = new HashMap<>();
	protected Map<Integer, Map<Symbol, Integer>> GOTOForm = new HashMap<>();
	
	protected abstract void CLOSURE(Set<Item> I);
	
	protected void createGOTOForm(Set<Item> firstI) {
		int count = 0;
		Is.update(count++, firstI);

		boolean flag = false;
		Map<Set<Item>, Integer> temp = new HashMap<>();
		do {
			flag = false;
			for (Set<Item> I : Is.anotherKeySet()) {
				int I_index = Is.get(I); 
				Map<Symbol, Integer> row = null;
				if(GOTOForm.containsKey(I_index)) {
					row = GOTOForm.get(I_index);
				}
				else {
					row = new HashMap<>();
					GOTOForm.put(I_index, row);
				}
				for (Symbol X : grammar.getSymbols()) {
					if(!row.containsKey(X)) {
						Set<Item> J = new HashSet<>();
						for (Item item : I) {
							if (item.getNextSymbol() != null && item.getNextSymbol().equals(X)) {
								Item i = item.getForwardItem();
								J.add(i);
							}
						}
						CLOSURE(J);
						if(J.isEmpty()) {
							row.put(X, 0);
						}
						else {
							if(Is.contains(J)) {
								row.put(X, Is.get(J));
							}
							else if(temp.containsKey(J)){
								row.put(X, temp.get(J));
							}
							else {
								temp.put(J, count);
								row.put(X, count);
								flag = true;
								count++;
							}
						}
					}
				}
			}
			for(Set<Item> I : temp.keySet()) {
				Is.update(temp.get(I), I);
			}
			temp.clear();
		} while (flag);
	}
	
	/**
	 *	正数代表移入，负数代表规约，0代表accept，空值代表报错（注意有别与ACTION函数，因为ACTION函数必须返回一个int值，故以Integer.MIN_VALUE代表报错）
	 */
	protected abstract void createACTIONForm();
	
	/**
	 * 	不检测I_index和A的合法性，因为调用该过程的函数是可靠的内部函数
	 *	返回值应该返回正整数，若返回0则代表在该I_index上的A转换不存在
	 */
	protected int GOTO(int I_index, Symbol A) {
		Map<Symbol, Integer> row = GOTOForm.get(I_index);
		if(row.get(A) <= 0) {
			throw new Error("程序设计错误");
		}
		return row.get(A);
	}
	
	/**
	 *	动作函数
	 * @return 正数：移入；负数：规约；0：accept；Integer.MIN_VALUE:报错
	 */
	protected int ACTION(int I_index, Terminal a) {
		Map<Terminal, Integer> row = ACTIONForm.get(I_index);
		
		if(row.containsKey(a)) {
			return row.get(a);
		}
		else {
			return Integer.MIN_VALUE;
		}
	}
	
	public void program(List<Terminal> terminals) {
		Stack<Integer> stack = new Stack<>();
		stack.push(0);
		int count = 0;
		Terminal a = terminals.get(count++);
		int s = 0;
		while(true) {
			s = stack.peek();
			int action = ACTION(s, a);
			if(action == Integer.MIN_VALUE) {
				System.out.println("Unanticipated Input, there is a syntax error");
				return;
			}
			if(action > 0) {
				stack.push(action);
				a = terminals.get(count++);
			}
			else if(action < 0){
				action *= -1;
				Production production = grammar.getProduction(action);
				int length = production.getRight().size();
				for(int k = 0; k < length; k++) {
					stack.pop();
				}
				int t = stack.peek();
				stack.push(GOTO(t, production.getLeft()));
				System.out.println(production);
			}
			else {
				System.out.println("accept");
				break;
			}
		}
	}
	
	public String displayIs() {
		StringBuffer sb = new StringBuffer();
		for(Set<Item> I : Is.anotherKeySet()) {
			sb.append(Is.get(I));
			sb.append("\n");
			for (Item item : I) {
				sb.append(item.toString());
				sb.append("\n");
			}
			sb.append("\n");
		}
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
	
	public String displayGOTOForm() {
		List<Symbol> symbolList = new ArrayList<>();
		for (Symbol symbol : grammar.getSymbols()) {
			if(symbol.isTerminal()) {
				symbolList.add((Terminal) symbol);
			}
		}
		for(Symbol symbol : grammar.getSymbols()) {
			if(!symbol.isTerminal()) {
				symbolList.add((Nonterminal)symbol);
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("state|");
		for (Symbol symbol : symbolList) {
			sb.append(symbol.toString());
			sb.append("|");
		}
		sb.append("\n");
		for(int j = 0; j <= symbolList.size(); j++) {
			sb.append("-|");
		}
		sb.append("\n");
		for(Integer i : GOTOForm.keySet()) {
			Map<Symbol, Integer> row = GOTOForm.get(i);
			sb.append(i + "|");
			for(Symbol symbol : symbolList) {
				int j = row.get(symbol);
				if(j != 0) {
					sb.append(j);
				}
				sb.append("|");
			}
			sb.append("\n");
		}
		
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
	
	public String displayACTIONForm() {
		List<Terminal> terminalEX = new ArrayList<>();
		for (Symbol symbol : grammar.getSymbols()) {
			if(symbol.isTerminal()) {
				terminalEX.add((Terminal) symbol);
			}
		}
		terminalEX.add(Terminal.end);
		
		StringBuffer sb = new StringBuffer();
		sb.append("state|");
		for (Terminal terminal : terminalEX) {
			sb.append(terminal.toString());
			sb.append("|");
		}
		sb.append("\n");
		for(int j = 0; j <= terminalEX.size(); j++) {
			sb.append("-|");
		}
		sb.append("\n");
		for(Integer i : ACTIONForm.keySet()) {
			Map<Terminal, Integer> row = ACTIONForm.get(i);
			sb.append(i + "|");
			for(Terminal terminal : terminalEX) {
				if(row.containsKey(terminal)) {
					int action = row.get(terminal);
					if(action > 0) {
						sb.append("s" + action);
					}
					else if(action < 0) {
						sb.append("r" + -action);
					}
					else {
						sb.append("accept");
					}
				}
				sb.append("|");
			}
			sb.append("\n");
		}
		
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
	
	public boolean isLegal() {
		return legal;
	}
	
	public LR0(Grammar grammar) {
		this.grammar = grammar;
	}
	
}
