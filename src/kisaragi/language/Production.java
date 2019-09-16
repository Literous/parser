package kisaragi.language;

import java.util.List;


public class Production {
	private Nonterminal left = null;
	private List<Symbol> right = null;

	public Nonterminal getLeft() {
		return left;
	}
	
	public List<Symbol> getRight(){
		return right;
	}

	public Production(Nonterminal left, List<Symbol> right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Production other = (Production) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(left.toString());
		sb.append(" -> ");
		if (right.size() == 0) {
			sb.append("ε");
		} 
		else {
			for (Symbol symbol : right) {
				sb.append(symbol.toString());
				sb.append(" ");
			}
		}
		return new String(sb);
	}
	
}
