package kisaragi.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kisaragi.language.Grammar;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Terminal;

public class SLR extends LR0{
	
	@Override
	protected void CLOSURE(Set<Item> I) {
		// 标志某一轮是否有新的项加入i中
		boolean flag = false;
		// 由于i在迭代过程中无法更改，故新的项先加入该缓存集合中
		Set<Item> temp = new HashSet<>();
		if(I.isEmpty()) {
			return;
		}
		do {
			flag = false;
			// 对i中每一个A -> α·Bβ迭代
			for (Item item : I) {
				// 获取下一个文法符号，即B
				Symbol next = item.getNextSymbol();
				// 若没有下一个文法符号或是终结符号则continue
				if (next == null || next.isTerminal()) {
					continue;
				} 
				else {
					// 对文法G迭代
					for (Production production : grammar.getProductions()) {
						// G中的每个B -> γ 且不在i中则加入缓存集合，并将标志flag置为true
						if (next.equals(production.getLeft())) {
							Item newItem = new Item(production);
							if(!I.contains(newItem)) {
								temp.add(newItem);
								flag = true;
							}
						}
					}
				}
			}
			// 将缓存集合中的项复制到i中，并清空
			I.addAll(temp);
			temp.clear();
		} while (flag);
	}
	
	@Override
	protected void createACTIONForm() {
		for (int i = 0; i < Is.size(); i++) {
			Map<Terminal, Integer> row = new HashMap<>();
			Set<Item> I = Is.get(i);
			for (Item item : I) {
				Symbol next = item.getNextSymbol();
				// A -> α·β
				if(next != null) {
					if(next.isTerminal()) {
						int s = GOTO(i, next);
						if(row.containsKey(next)) {
							if(!row.get(next).equals(s)) {
								return;
							}
						}
						else {
							row.put((Terminal) next, s);
						}
					}
				}
				// A -> α·
				else {
					Production production = item.getProduction();
					for (Terminal terminal : grammar.FOLLOW(production.getLeft())) {
						int r = - grammar.getProductionIndex(production);
						if(row.containsKey(terminal)) {
							if(!row.get(terminal).equals(r)) {
								return;
							}
						}
						else {
							row.put(terminal, r);	
						}
					}
				}
			}
			ACTIONForm.put(i, row);
		}
		legal = true;
	}
	
	public SLR(Grammar grammar) {
		super(grammar);
		
		Set<Item> I0 = new HashSet<>();
		Item firstItem = new Item(grammar.getFirstProduction());
		I0.add(firstItem);
		CLOSURE(I0);
		createGOTOForm(I0);
		
		createACTIONForm();
	}
}
