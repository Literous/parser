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
		// ��־ĳһ���Ƿ����µ������I��
		boolean flag = false;
		// ����I�ڵ����������޷����ģ����µ����ȼ���û��漯����
		Set<Production> temp = new HashSet<>();
		do {
			flag = false;
			// ��I��ÿһ��A -> ����B��, a����
			for (Production production : I) {
				// ��ȡ��һ���ķ����ţ���B
				Symbol next = production.nextSymbol();
				// ��û����һ���ķ����Ż����ս������continue
				if (next == null || next.isToken()) {
					continue;
				} else {
					// ���ķ�G���� 
					for (Production basicProduction : basicProductionsAsKEY.keySet()) {
						// G�е�ÿ��B -> ��
						if (next.equals(basicProduction.getLeft())) {
							List<Symbol> right = production.getRight();
							List<Symbol> beta = new ArrayList<>(right.subList(production.getPointIndex() + 1, right.size()));
							if(production.getForward() != null) {
								beta.add(production.getForward());
							}
							// FIRST(��a)�е�ÿ���ս����b
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
			// �����漯���е���Ƶ�i�У������
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
				// A -> ����a��, b
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
				// A -> ����, a
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
