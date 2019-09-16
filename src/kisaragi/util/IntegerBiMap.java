package kisaragi.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 以int值作为索引的双向字典
 * @author Literous
 *
 * @param <Key> 另一键的类型
 */
public class IntegerBiMap<Key> {
	
	private HashMap<Integer, Key> map1 = new HashMap<>();
	private HashMap<Key, Integer> map2 = new HashMap<>();
	
	/**
	 * 更新双向字典
	 * @param index
	 * @param anotherKey
	 */
	public void update(int index, Key anotherKey) {
		if(map1.containsKey(index)) {
			if(map1.get(index) != anotherKey) {
				if(map2.containsKey(anotherKey)) {
					this.remove(index);
					this.remove(anotherKey);
				}
				else {
					this.remove(index);
				}
			}
			else {
				return;
			}
		}
		else {
			if(map2.containsKey(anotherKey)) {
				this.remove(anotherKey);
			}
		}
		map1.put(index, anotherKey);
		map2.put(anotherKey, index);
	}
	
	public void remove(int index) {
		if(map1.containsKey(index)) {
			map2.remove(map1.get(index));
			map1.remove(index);
		}
	}
	
	public void remove(Key anotherKey) {
		if(map2.containsKey(anotherKey)) {
			map1.remove(map2.get(anotherKey));
			map2.remove(anotherKey);
		}
	}
	
	public void clear() {
		map1.clear();
		map2.clear();
	}
	
	/**
	 * 
	 * @param anotherKey
	 * @return 若该键-键对不存在，则返回Integer.MIN_VALUE
	 */
	public int get(Key anotherKey) {
		if(map2.containsKey(anotherKey)) {
			return map2.get(anotherKey);
		}
		return Integer.MIN_VALUE;
	}
	
	public Key get(int index) {
		if(map1.containsKey(index)) {
			return map1.get(index);
		}
		return null;
	}
	
	public boolean isEmpty() {
		return map1.isEmpty();
	}
	
	public int size() {
		return map1.size();
	}
	
	public boolean contains(int index) {
		return map1.containsKey(index);
	}
	
	public boolean contains(Key anotherKey) {
		return map2.containsKey(anotherKey);
	}
	
	/**
	 * 为了保证该双向字典的完整性，防止外部带来的破坏，对返回的键集合任何操作不会反馈至该双向字典
	 * @return
	 */
	public HashSet<Key> anotherKeySet(){
		HashSet<Key> keySet = new HashSet<>();
		keySet.addAll(map2.keySet());
		return keySet;
	}
	
	public int[] indexSet() {
		int[] indexSet = new int[map1.size()];
		int i = 0;
		for(Integer index : map1.keySet()) {
			indexSet[i] = index;
			i++;
		}
		return indexSet;
	}
	
	/**
	 * Integer, Key形式的字符串
	 */
	@Override
	public String toString() {
		return map1.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map1 == null) ? 0 : map1.hashCode());
		result = prime * result + ((map2 == null) ? 0 : map2.hashCode());
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
		IntegerBiMap<?> other = (IntegerBiMap<?>) obj;
		if (map1 == null) {
			if (other.map1 != null)
				return false;
		} else if (!map1.equals(other.map1))
			return false;
		if (map2 == null) {
			if (other.map2 != null)
				return false;
		} else if (!map2.equals(other.map2))
			return false;
		return true;
	}
	
	
}
