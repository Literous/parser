package kisaragi.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kisaragi.util.IO;
import kisaragi.util.IntegerBiMap;


public class Grammar {
	private Production firstProduction = null;
	private IntegerBiMap<Production> productions = new IntegerBiMap<>();
	private Map<Nonterminal, Set<Terminal>> firsts = new HashMap<>();
	private Map<Nonterminal, Set<Terminal>> follows = new HashMap<>();
	private Set<Symbol> symbols = new HashSet<>();
	
	/**
	 * 从指定文件读取文法，该文法的第一条被视作增广产生式
	 * @param path 文件路径
	 * @return 增广产生式
	 */
	private void createProductions(String path) {
		String str = IO.readText(path);
		String[] languages = str.split("\n");
		Pattern p = Pattern.compile("<(\\w++)>|\"([^\"]+)\"");

		for (int i = 0, count = 0; i < languages.length; i++) {
			String language = languages[i];
			if (language.length() != 0) {
				Matcher m = p.matcher(language);
				if (m.find()) {
					Nonterminal left = new Nonterminal(m.group(1));
					symbols.add(left);
					List<Symbol> right = new ArrayList<>();
					while (m.find()) {
						if (m.group(1) != null) {
							right.add(new Nonterminal(m.group(1)));
						} 
						else {
							Terminal token = Terminal.getTerminal(m.group(2));
							symbols.add(token);
							right.add(token);
						}
					}
					Production production = new Production(left, right);
					productions.update(count++, production);
				}
			}
		}
		
		firstProduction = productions.get(0);
	}
	
	/**
	 * 粗略地计算指定非终结符号的FIRST集，若递归调用该方法时，firstState表明该非终结符号正在计算，则会跳过
	 * @param nonterminal 指定的非终结符号
	 * @param firstState 记录非终结符号的状态(true正在计算, false不在计算)
	 * @return 本轮计算是否有新的终结符号加入任何非终结符号的FIRST集
	 */
	private boolean FIRST(Nonterminal nonterminal, Map<Nonterminal, Boolean> firstState) {
		boolean flag = false;
		firstState.put(nonterminal, true);
		Set<Terminal> first = firsts.get(nonterminal);
		
		for (Production production : productions.anotherKeySet()) {
			if (nonterminal.equals(production.getLeft())) {
				List<Symbol> right = production.getRight();
				// X -> ε
				if (right.size() == 0) {
					if(!first.contains(Terminal.e)) {
						first.add(Terminal.e);
						flag = true;
					}
				} 
				// X -> Y1Y2Yk
				else {
					for (int i = 0; i < right.size(); i++) {
						Symbol Y = right.get(i);
						if(Y.isTerminal()) {
							if(!first.contains(Y)) {
								first.add((Terminal)Y);
								flag = true;
							}
							break;
						}
						else {
							if(firstState.get(Y)) {
								if(firsts.get(Y).contains(Terminal.e)) {
									continue;
								}
								else {
									break;
								}
							}
							else {
								flag |= FIRST((Nonterminal)Y, firstState);
								Set<Terminal> firstY = firsts.get(Y);
								
								for (Terminal token : firstY) {		
									if(!token.equals(Terminal.e) && !first.contains(token)) {
										first.add(token);
										flag = true;
									}
								}
						
								if(firstY.contains(Terminal.e)) {
									if(i == right.size() - 1 && !first.contains(Terminal.e)) {
										first.add(Terminal.e);
										flag = true;
									}
								}
								else {
									break;
								}
							}				
						}
					}
				}
			}
		}
		firstState.put(nonterminal, false);
		return flag;
	}
	
	private void createFTRST() {
		Map<Nonterminal, Boolean> firstState = new HashMap<>();
		for(Symbol symbol: symbols) {
			if(!symbol.isTerminal()) {
				firsts.put((Nonterminal)symbol, new HashSet<Terminal>());
				firstState.put((Nonterminal)symbol, false);
			}
		}
		boolean flag = false;
		do {
			flag = false;
			for (Nonterminal nonterminal : firsts.keySet()) {
				flag |= FIRST(nonterminal, firstState);
			}
		} while(flag);
	}
	
	public Set<Terminal> FIRST(List<Symbol> beta, int index){
		Set<Terminal> first = new HashSet<>();
		for(; index < beta.size(); index++) {
			Symbol X = beta.get(index);
			if(X.isTerminal()) {
				first.add((Terminal)X);
				return first;
			}
			else {
				Set<Terminal> X_first = firsts.get(X);
				first.addAll(X_first);
				if(X_first.contains(Terminal.e)) {
					first.remove(Terminal.e);
					continue;
				}
				else {
					return first;
				}
			}
		}
		first.add(Terminal.e);
		return first;
	}
	
	public Set<Terminal> FIRST(Nonterminal A){
		return firsts.get(A);
	}

	private void createFOLLOW() {
		Set<Terminal> S_follow = new HashSet<>();
		S_follow.add(Terminal.end);
		follows.put(firstProduction.getLeft(), S_follow);
	
		boolean flag = false;
		do {
			flag = false;
			for(Production production : productions.anotherKeySet()) {
				Set<Terminal> A_follow = null;
				if(follows.containsKey(production.getLeft())) {
					A_follow = follows.get(production.getLeft());
				}
				else {
					A_follow = new HashSet<Terminal>();
					follows.put((Nonterminal)production.getLeft(), A_follow);
				}
				List<Symbol> right = production.getRight();
				for(int i = 0; i < right.size(); i++) {
					Symbol symbol = right.get(i);
					if(!symbol.isTerminal()) {
						Set<Terminal> B_follow = null;
						if(follows.containsKey(symbol)) {
							B_follow = follows.get(symbol);
						}
						else {
							B_follow = new HashSet<Terminal>();
							follows.put((Nonterminal)symbol, B_follow);
						}
						if(i == right.size() - 1) {	
							if(!B_follow.containsAll(A_follow)) {
								B_follow.addAll(A_follow);
								flag = true;
							}
						}
						else {
							Set<Terminal> beta_first = FIRST(right, i+1);
							if(beta_first.contains(Terminal.e)) {
								if(!B_follow.containsAll(A_follow)) {
									B_follow.addAll(A_follow);
									flag = true;
								}
								B_follow.add(Terminal.e);
								if(!B_follow.containsAll(beta_first)) {
									B_follow.addAll(beta_first);
									flag = true;
								}
								B_follow.remove(Terminal.e);
							}
							else {
								if(!B_follow.containsAll(beta_first)) {
									B_follow.addAll(beta_first);
									flag = true;
								}
							}
						}
					}
				}
			}
		} while(flag);
	}
	
	public Set<Terminal> FOLLOW(Nonterminal A){
		return follows.get(A);
	}
	
	public Set<Symbol> getSymbols(){
		return symbols;
	}
	
	public Set<Production> getProductions() {
		return productions.anotherKeySet();
	}
	
	public Nonterminal getFirstNonTerminal() {
		return firstProduction.getLeft();
	}
	
	public Production getFirstProduction() {
		return firstProduction;
	}
	
	public Production getProduction(int index) {
		return productions.get(index);
	}
	
	public int getProductionIndex(Production production) {
		return productions.get(production);
	}
	
	public Grammar(String path) {
		createProductions(path);
		createFTRST();
		createFOLLOW();
	}
	
	public String displayFIRSTandFOLLOW() {
		StringBuffer sb = new StringBuffer();
		sb.append("FIRST\n");
		for (Nonterminal nonterminal : firsts.keySet()) {
			Set<Terminal> first = firsts.get(nonterminal);
			sb.append(nonterminal.toString());
			sb.append(":\t{");
			int count = 0;
			for (Terminal token : first) {
				sb.append(token.toString());
				count++;
				if(count == first.size()) {
					sb.append("}");
				}
				else {
					sb.append(", ");
				}
			}
			sb.append("\n");
		}
		sb.append("\nFOLLOW\n");
		for (Nonterminal nonterminal : follows.keySet()) {
			Set<Terminal> follow = follows.get(nonterminal);
			sb.append(nonterminal.toString());
			sb.append(":\t{");
			int count = 0;
			for (Terminal token : follow) {
				sb.append(token.toString());
				count++;
				if(count == follow.size()) {
					sb.append("}");
				}
				else {
					sb.append(", ");
				}
			}
			sb.append("\n");
		}
		String str = new String(sb);
		System.out.println(str);
		return str;
	}
}
