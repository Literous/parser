package kisaragi.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kisaragi.language.Grammar;
import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Terminal;


public class LR1 extends LR0{

	public LR1(Grammar grammar) {
		super(grammar);
		
		Set<Item> I0 = new HashSet<>();
		Item firstItem = new Item(grammar.getFirstProduction(), Terminal.end);
		I0.add(firstItem);
		CLOSURE(I0);
		createGOTOForm(I0);
		
		createACTIONForm();
	}

	@Override
	protected void CLOSURE(Set<Item> I) {
		// 标志某一轮是否有新的项加入I中
		boolean flag = false;
		// 由于I在迭代过程中无法更改，故新的项先加入该缓存集合中
		Set<Item> temp = new HashSet<>();
		do {
			flag = false;
			// 对I中每一个A -> α·Bβ, a迭代
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
						// G中的每个B -> γ
						if (next.equals(production.getLeft())) {
							List<Symbol> BETAa = item.getBETAa();
							// FIRST(βa)中的每个终结符号b
							Set<Terminal> first = grammar.FIRST(BETAa, 0);
							for (Terminal b : first) {
								Item new_item = new Item(production, b);
								if(!I.contains(new_item)) {
									temp.add(new_item);
									flag = true;
								}
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
				// A -> α·aβ, b
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
				// A -> α·, a
				else {
					int r = - grammar.getProductionIndex(item.getProduction());
					if(row.containsKey(item.getLookingForward())) {
						if(!row.get(item.getLookingForward()).equals(r)) {
							return;
						}
					}
					else {
						row.put(item.getLookingForward(), r);						
					}
				}
			}
			ACTIONForm.put(i, row);
		}
		legal = true;
	}

}
