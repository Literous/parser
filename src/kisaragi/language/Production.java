package kisaragi.language;

import java.util.List;


public class Production {
	private Nonterminal left = null;
	private List<Symbol> right = null;
	private int pointIndex = 0;
	private Token forward = null;

	public int getPointIndex() {
		return pointIndex;
	}

	public Nonterminal getLeft() {
		return left;
	}
	
	public List<Symbol> getRight(){
		return right;
	}
	
	public Token getForward() {
		return forward;
	}

	/**
	 * 获取下一个文法符号，若产生式为 A -> ε 或  A -> α· 则返回null 
	 * @return next symbol
	 */
	public Symbol nextSymbol() {
		if (right.size() == 0 || pointIndex >= right.size()) {
			return null;
		} else {
			return right.get(pointIndex);
		}
	}
	
	public Production pointForward() {
		Production p = new Production(left, right);
		p.forward = this.forward;
		p.pointIndex = this.pointIndex;
		p.pointIndex++;
		if(p.pointIndex > p.right.size()) {
			return null;
		}
		return p;
	}

	public Production(Nonterminal left, List<Symbol> right) {
		this.left = left;
		this.right = right;
	}
	
	public Production(Production basic, Token forward) {
		this.left = basic.left;
		this.right = basic.right;
		//this.pointIndex = basic.pointIndex;//使用这一构造方法的场合，basic的pointIndex必为0
		this.forward = forward;
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
		if (forward == null) {
			if (other.forward != null)
				return false;
		} else if (!forward.equals(other.forward))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (pointIndex != other.pointIndex)
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
		result = prime * result + ((forward == null) ? 0 : forward.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + pointIndex;
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(left.toString());
		sb.append(" -> ");
		if (right.size() == 0) {
			sb.append("· ε");
		} else {
			int count = 0;
			for (Object object : right) {
				if (count == pointIndex) {
					sb.append("· ");
				}
				sb.append(object.toString());
				sb.append(" ");
				count++;
			}
			if(pointIndex == count) {
				sb.append("·");
			}
		}
		if(forward != null) {
			sb.append(", ");
			sb.append(forward);
		}
		return new String(sb);
	}
}
