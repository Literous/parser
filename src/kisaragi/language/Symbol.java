package kisaragi.language;

public abstract class Symbol {
	
	private boolean type;
	
	public Symbol(boolean type) {
		this.type = type;
	}
	
	/**
	 * 判断该文法符号是终结符号还是非终结符号
	 * @return true为终结符号, false为非终结符号
	 */
	public boolean isTerminal() {
		return type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (type ? 1231 : 1237);
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
		Symbol other = (Symbol) obj;
		if (type != other.type)
			return false;
		return true;
	}
	
}
