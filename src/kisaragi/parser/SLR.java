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
		// ��־ĳһ���Ƿ����µ������i��
		boolean flag = false;
		// ����i�ڵ����������޷����ģ����µ����ȼ���û��漯����
		Set<Production> temp = new HashSet<>();
		do {
			flag = false;
			// ��i��ÿһ��A -> ����B�µ���
			for (Production production : i) {
				// ��ȡ��һ���ķ����ţ���B
				Symbol next = production.nextSymbol();
				// ��û����һ���ķ����Ż����ս������continue
				if (next == null || next.isToken()) {
					continue;
				} else {
					// ���ķ�G����
					for (Production basicProduction : basicProductionsAsKEY.keySet()) {
						// G�е�ÿ��B -> �� �Ҳ���i������뻺�漯�ϣ�������־flag��Ϊtrue
						if (next.equals(basicProduction.getLeft()) && !i.contains(basicProduction)) {
							temp.add(basicProduction);
							flag = true;
						}
					}
				}
			}
			// �����漯���е���Ƶ�i�У������
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
					// A -> ����a��
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
				// A -> ����
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
