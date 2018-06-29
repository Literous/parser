package kisaragi.language;

public class Symbol {
	
	private boolean type;
	public Symbol(boolean type) {
		this.type = type;
	}
	
	/**
	 * �жϸ��ķ��������ս���Ż��Ƿ��ս����
	 * @return trueΪ�ս����, falseΪ���ս����
	 */
	public boolean isToken() {
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
