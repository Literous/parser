package kisaragi.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kisaragi.language.Production;
import kisaragi.language.Symbol;
import kisaragi.language.Token;


public class LR1 extends LR {

	@Override
	protected void CLOSURE(Set<Production> I) {
		// 标志某一轮是否有新的项加入I中
		boolean flag = false;
		// 由于I在迭代过程中无法更改，故新的项先加入该缓存集合中
		Set<Production> temp = new HashSet<>();
		do {
			flag = false;
			// 对I中每一个A -> α·Bβ, a迭代
			for (Production production : I) {
				// 获取下一个文法符号，即B
				Symbol next = production.nextSymbol();
				// 若没有下一个文法符号或是终结符号则continue
				if (next == null || next.isToken()) {
					continue;
				} else {
					// 对文法G迭代 
					for (Production basicProduction : basicProductionsAsKEY.keySet()) {
						// G中的每个B -> γ
						if (next.equals(basicProduction.getLeft())) {
							List<Symbol> right = production.getRight();
							List<Symbol> beta = new ArrayList<>(right.subList(production.getPointIndex() + 1, right.size()));
							if(production.getForward() != null) {
								beta.add(production.getForward());
							}
							// FIRST(βa)中的每个终结符号b
							Set<Token> firstb = FIRST(beta);
							for (Token b : firstb) {
								Production p = new Production(basicProduction, b);
								if(!I.contains(p)) {
									temp.add(p);
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
	protected void createAnalysisForm() {
		for (Integer I : itemsAsVALUE.keySet()) {
			Map<Symbol, String> row = new HashMap<>();
			Set<Production> item = itemsAsVALUE.get(I);
			for (Production production : item) {
				Symbol next = production.nextSymbol();
				// A -> α·aβ, b
				if(next != null) {
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
				// A -> α·, a
				else {
					Production production_basic = new Production(production.getLeft(), production.getRight());

					String action = new String("r" + basicProductionsAsKEY.get(production_basic));
					if(row.containsKey(production.getForward()) && !row.get(production.getForward()).equals(action)) {
						return;
					}
					row.put(production.getForward(), action);
				}
			}
			analysisForm.put(I, row);
		}
		legal = true;
	}

	public LR1(String path) {
		super(path);
		createItems(true);
		createAnalysisForm();
	}
}
