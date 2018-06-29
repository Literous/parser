package kisaragi.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Token;

public class SLR extends LR{
	
	@Override
	protected void CLOSURE(Set<Production> i) {
		// 标志某一轮是否有新的项加入i中
		boolean flag = false;
		// 由于i在迭代过程中无法更改，故新的项先加入该缓存集合中
		Set<Production> temp = new HashSet<>();
		do {
			flag = false;
			// 对i中每一个A -> α·Bβ迭代
			for (Production production : i) {
				// 获取下一个文法符号，即B
				Symbol next = production.nextSymbol();
				// 若没有下一个文法符号或是终结符号则continue
				if (next == null || next.isToken()) {
					continue;
				} else {
					// 对文法G迭代
					for (Production basicProduction : basicProductionsAsKEY.keySet()) {
						// G中的每个B -> γ 且不在i中则加入缓存集合，并将标志flag置为true
						if (next.equals(basicProduction.getLeft()) && !i.contains(basicProduction)) {
							temp.add(basicProduction);
							flag = true;
						}
					}
				}
			}
			// 将缓存集合中的项复制到i中，并清空
			i.addAll(temp);
			temp.clear();
		} while (flag);
	}

	@Override 
	protected void createAnalysisForm() {
		for (Integer i : itemsAsVALUE.keySet()) {
			Map<Symbol, String> row = new HashMap<>();
			Set<Production> item = itemsAsVALUE.get(i);
			for (Production production : item) {
				Symbol next = production.nextSymbol();
				if(next != null) {
					// A -> α·aβ
					if(next.isToken()) {
						int j = itemsAsKEY.get(GOTO(item, next));
						String action = new String("s" + j);
						if(row.containsKey(next) && !row.get(next).equals(action)) {
							return;
						}
						row.put(next, action);
					}
					else if(!row.containsKey(next)){
						int j = itemsAsKEY.get(GOTO(item, next));
						row.put(next, new String(j+""));
					}
				}
				// A -> α·
				else {
					for (Token token : follows.get(production.getLeft())) {
						Production production_basic = new Production(production.getLeft(), production.getRight());
						String action = new String("r" + basicProductionsAsKEY.get(production_basic));
						if(row.containsKey(token) && !row.get(token).equals(action)) {
							return;
						}
						row.put(token, action);
					}
				}
			}
			analysisForm.put(i, row);
		}
		legal = true;
	}
	
	public SLR(String path) {
		super(path);
		createItems(false);
		createAnalysisForm();
	}
}
