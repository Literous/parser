package kisaragi.language;

import java.util.HashMap;

/**
 * 词法单元
 * 语言中的基本源字符由该Token类定义，比如：()[]
 * @author KisaragiAoshiro
 *
 */
public class Token extends Symbol {
	/**
	 * ε
	 */
	public static final Token e = new Token(1);
	/**
	 * $(end)
	 */
	public static final Token end = new Token(0);
	/**
	 * Unknown
	 */
	public static final Token unknow = new Token(-1);
	
	private static HashMap<String, Token> dictionary = new HashMap<String, Token>();
	
	public final int tag;
	
	private Token(int t) {
		super(true);
		tag = t;
	}
	
	public static Token getToken(String lex) {
		if(lex == null || lex.length() == 0) {
			return null;
		}
		if(dictionary.containsKey(lex)) {
			return dictionary.get(lex);
		}
		if(lex.length() == 1) {
			Token token = new Token((int)lex.charAt(0));
			dictionary.put(lex, token);
			return token;
		}
		Token token = new Token(lex.hashCode());
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
			String str = null;
			for(String lex : dictionary.keySet()) {
				if(lex.hashCode() == tag) {
					str = lex;
					break;
				}
			}
			return str;
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
		Token other = (Token) obj;
		if (tag != other.tag)
			return false;
		return true;
	}
}
