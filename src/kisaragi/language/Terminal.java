package kisaragi.language;

import java.util.HashMap;

/**
 * 词法单元
 * 语言中的基本源字符由该Token类定义，比如：()[]
 * @author KisaragiAoshiro
 *
 */
public class Terminal extends Symbol {
	/**
	 * ε
	 */
	public static final Terminal e = new Terminal(1);
	/**
	 * $(end)
	 */
	public static final Terminal end = new Terminal(0);
	/**
	 * Unknown
	 */
	public static final Terminal unknow = new Terminal(-1);
	
	private static HashMap<String, Terminal> dictionary = new HashMap<String, Terminal>();
	private static int index = 256;
	
	public final int tag;
	
	private Terminal(int t) {
		super(true);
		tag = t;
	}
	
	public static Terminal getTerminal(char c) {
		return new Terminal(c);
	}
	
	public static Terminal getTerminal(String lex) {
		if(lex == null || lex.length() == 0) {
			return null;
		}
		if(dictionary.containsKey(lex)) {
			return dictionary.get(lex);
		}
		if(lex.length() == 1) {
			return new Terminal(lex.charAt(0));
		}
		Terminal token = new Terminal(index++);
		dictionary.put(lex, token);
		return token;
	}
	
	/**
	 * 重写Object类的toString方法，用于调试时显示Token实例
	 */
	public String toString() {
		if(tag == -1) {
			return "unknow";
		}
		if(tag == 0) {
			return "$";
		}
		if(tag == 1) {
			return "ε"; 
		}
		if(tag < 256) {
			return "" + (char)tag;
		}
		else {
			for(String lex : dictionary.keySet()) {
				if(dictionary.get(lex).equals(this)) {
					return lex;
				}
			}
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + tag;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Terminal other = (Terminal) obj;
		if (tag != other.tag)
			return false;
		return true;
	}
}
