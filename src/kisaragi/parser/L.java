package kisaragi.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kisaragi.language.Nonterminal;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Token;
import kisaragi.util.IO;


public abstract class L {
	protected Production firstProduction = null;
	protected Map<Production, Integer> basicProductionsAsKEY = new HashMap<>();
	protected Map<Integer, Production> basicProductionsAsVALUE = new HashMap<>();
	protected Map<Nonterminal, Set<Token>> firsts = new HashMap<>();
	protected Map<Nonterminal, Set<Token>> follows = new HashMap<>();
	protected Set<Symbol> symbols = new HashSet<>();
	
	/**
	 * 从指定文件读取文法，该文法的第一条被视作增广产生式
	 * @param path 文件路径
	 * @return 增广产生式
	 */
	private Production createBasicProductions(String path) {
		String str = IO.readText(path);
		String[] languages = str.split("\n");
		Pattern p = Pattern.compile("<(\\w++)>|\"([^\"]+)\"");
		// 标志增广产生式是否被赋值
		boolean flag = false;
		int count = 0;
		for (String language : languages) {
			if (language.length() != 0) {
				Matcher m = p.matcher(language);
				if (m.find()) {
					Nonterminal left = new Nonterminal(m.group(1));
					if (flag) {
						symbols.add(left);
					}
					List<Symbol> right = new ArrayList<>();
					while (m.find()) {
						if (m.group(1) != null) {
							right.add(new Nonterminal(m.group(1)));
						} else {
							Token token = Token.getToken(m.group(2));
							symbols.add(token);
							right.add(token);
						}
					}
					Production pro = new Production(left, right);
					if (!flag) {
						firstProduction = pro;
						flag = true;
					}
					basicProductionsAsKEY.put(pro, count++);
				} else {
					System.out.println("error");
					break;
				}
			}
		}
		
		for (Production production : basicProductionsAsKEY.keySet()) {
			Integer i = basicProductionsAsKEY.get(production);
			basicProductionsAsVALUE.put(i, production);
		}
		
		if(basicProductionsAsKEY.size() != basicProductionsAsVALUE.size()) {
			try {
				throw new RuntimeException("产生式的标号存在重复，表面程序设计存在缺陷");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return firstProduction;
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
		Set<Token> first = firsts.get(nonterminal);
		
		for (Production production : basicProductionsAsKEY.keySet()) {
			if (nonterminal.equals(production.getLeft())) {
				List<Symbol> right = production.getRight();
				// X -> ε
				if (right.size() == 0) {
					if(!first.contains(Token.e)) {
						first.add(Token.e);
						flag = true;
					}
				} 
				// X -> Y1Y2Yk
				else {
					for (Symbol Y : right) {
						if(Y.isToken()) {
							if(!first.contains(Y)) {
								first.add((Token)Y);
								flag = true;
							}
							break;
						}
						else {
							if(firstState.get(Y)) {
								if(firsts.get(Y).contains(Token.e)) {
									continue;
								}
								else {
									break;
								}
							}
							else {
								flag |= FIRST((Nonterminal)Y, firstState);
								Set<Token> firstY = firsts.get(Y);
								if(!first.containsAll(firstY)) {
									first.addAll(firstY);
									flag = true;
								}
								if(!firstY.contains(Token.e)) {
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
			if(!symbol.isToken()) {
				firsts.put((Nonterminal)symbol, new HashSet<Token>());
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
	
	protected Set<Token> FIRST(List<Symbol> Xn) {
		Set<Token> first = new HashSet<>();
		int count = 0;
		for (Symbol X : Xn) {
			if(X.isToken()) {
				first.add((Token)X);
				return first;
			}
			else {
				Set<Token> firstX = firsts.get(X);
				for (Token token : firstX) {
					if(!token.equals(Token.e)) {
						first.add(token);
					}
				}
				if(!firstX.contains(Token.e)) {
					return first;
				}
			}
			count++;
		}
		if (count == Xn.size()) {
			first.add(Token.e);
		}
		return first;
	}

	private boolean FOLLOW(Nonterminal B, Map<Nonterminal, Boolean> followState) {
		if(B.equals(firstProduction.getLeft())) {
			return false;
		}
		boolean flag = false;
		followState.put(B, true);
		Set<Token> follow = follows.get(B);
		
		for (Production production : basicProductionsAsKEY.keySet()) {
			List<Symbol> right = production.getRight();
			int firstIndex = right.indexOf(B);
			int lastIndex = right.lastIndexOf(B);
			if (firstIndex == -1) {
				continue;
			}
			else {
				// A -> αBβ
				if (firstIndex != right.size() - 1) {
					// A -> αBβB
					if(firstIndex != lastIndex && lastIndex == right.size() - 1 && !followState.get(production.getLeft())) {
						flag |= FOLLOW(production.getLeft(), followState);

						Set<Token> followA = follows.get(production.getLeft());
						if(!follow.containsAll(followA)) {
							follow.addAll(followA);
							flag = true;
						}
					}
					List<Symbol> beta = right.subList(firstIndex + 1, right.size());
					Set<Token> firstBeta = FIRST(beta);
					// β的first集包含ε
					if(firstBeta.contains(Token.e)) {
						firstBeta.remove(Token.e);
						if(!followState.get(production.getLeft())) {
							flag |= FOLLOW(production.getLeft(), followState);

							Set<Token> followA = follows.get(production.getLeft());
							if(!follow.containsAll(followA)) {
								follow.addAll(followA);
								flag = true;
							}
						}
					}
					if(!follow.containsAll(firstBeta)) {
						follow.addAll(firstBeta);
						flag = true;
					}
				}
				// A -> αB
				else if(!followState.get(production.getLeft())){
					flag |= FOLLOW(production.getLeft(), followState);

					Set<Token> followA = follows.get(production.getLeft());
					if(!follow.containsAll(followA)) {
						follow.addAll(followA);
						flag = true;
					}
				}
			}
		}
		followState.put(B, false);
		return flag;
	}
	
	private void createFOLLOW() {
		Map<Nonterminal, Boolean> followState = new HashMap<>();
		for(Symbol symbol: symbols) {
			if(!symbol.isToken()) {
				follows.put((Nonterminal)symbol, new HashSet<Token>());
				followState.put((Nonterminal)symbol, false);
			}
		}
		//在增广文法中，开始符号永远不会出现在产生式右部，且其FOLLOW集有且仅有$
		Set<Token> followS1 = new HashSet<>();
		followS1.add(Token.end);
		follows.put(firstProduction.getLeft(), followS1);
		followState.put(firstProduction.getLeft(), false);
		
		boolean flag = false;
		do {
			flag = false;
			for (Nonterminal nonterminal : follows.keySet()) {
				flag |= FOLLOW(nonterminal, followState);
			}
		} while(flag);
	}
	
	public L(String path) {
		createBasicProductions(path);
		createFTRST();
		createFOLLOW();
	}
	
	public String displayFIRSTandFOLLOW() {
		StringBuffer sb = new StringBuffer();
		sb.append("FIRST\n");
		for (Nonterminal nonterminal : firsts.keySet()) {
			Set<Token> first = firsts.get(nonterminal);
			sb.append(nonterminal.toString());
			sb.append(":\t{");
			int count = 0;
			for (Token token : first) {
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
			Set<Token> follow = follows.get(nonterminal);
			sb.append(nonterminal.toString());
			sb.append(":\t{");
			int count = 0;
			for (Token token : follow) {
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
