package kisaragi.parser;

import java.util.ArrayList;
import java.util.List;

import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Terminal;

/**
 * 	这里使用扩展的LR1项，即第二个分量（向前看符号）长度为1
 * @author Literous
 *
 */
public class Item {
	
	private Production production;
	private int index;
	private Terminal lookingForward;
	
	/**
	 * 以指定的production创建item，位置被初始化为0即默认为 A → ·α
	 * @param production 指定的production
	 */
	public Item(Production production) {
		this.production = production;
		this.index = 0;
	}
	
	/**
	 * 
	 * @param production
	 * @param lookingForward
	 */
	public Item(Production production, Terminal lookingForward) {
		this.production = production;
		this.lookingForward = lookingForward;
		this.index = 0;
	}
	
	/**
	 * 私有的拷贝构造函数，外部没有使用的需求
	 * @param item
	 */
	private Item(Item item) {
		this.production = item.production;
		this.index = item.index;
		this.lookingForward = item.lookingForward;
	}
	
	/**
	 * 获取下一个文法符号，若产生式为 A -> ε 或  A -> α· 则返回null 
	 * @return next symbol
	 */
	public Symbol getNextSymbol() {
		if (index == production.getRight().size()) {
			return null;
		} 
		else {
			return production.getRight().get(index);
		}
	}
	
	public Production getProduction(){
		return production;
	}

	public Item getForwardItem() {
		if(index == production.getRight().size()) {
			return null;
		}
		Item forwardItem = new Item(this);
		forwardItem.index++;
		return forwardItem;
	}
	
	public Terminal getLookingForward() {
		return lookingForward;
	}
	
	/**
	 * 若该项为 A → α·Bβ,a 则返回βa，若项为A → α·,a 或为A → α·bβ,a 则返回null
	 * @return
	 */
	public List<Symbol> getBETAa(){
		if(index == production.getRight().size() || production.getRight().get(index).isTerminal()) {
			return null;
		}
		
		List<Symbol> beta = new ArrayList<>();
		for(int i = index + 1; i < production.getRight().size(); i++) {
			beta.add(production.getRight().get(i));
		}
		
		if(lookingForward != null) {
			beta.add(lookingForward);
		}
		
		return beta;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((lookingForward == null) ? 0 : lookingForward.hashCode());
		result = prime * result + ((production == null) ? 0 : production.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (index != other.index)
			return false;
		if (lookingForward == null) {
			if (other.lookingForward != null)
				return false;
		} else if (!lookingForward.equals(other.lookingForward))
			return false;
		if (production == null) {
			if (other.production != null)
				return false;
		} else if (!production.equals(other.production))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(production.getLeft().toString());
		sb.append(" -> ");
		if (production.getRight().isEmpty()) {
			sb.append("· ε");
		} 
		else {
			int count = 0;
			for (Symbol symbol : production.getRight()) {
				if (count == index) {
					sb.append("· ");
				}
				sb.append(symbol.toString());
				sb.append(" ");
				count++;
			}
			if(index == count) {
				sb.append("·");
			}
		}
		
		if(lookingForward != null) {
			sb.append(", ");
			sb.append(lookingForward);
		}
		
		return new String(sb);
	}
}
