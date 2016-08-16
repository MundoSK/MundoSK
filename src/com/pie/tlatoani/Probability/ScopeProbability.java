package com.pie.tlatoani.Probability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;

public class ScopeProbability extends CustomScope {
	private List<CondProbabilityValue> probs = new ArrayList<CondProbabilityValue>();
	private List<TriggerItem> triggeritems = new ArrayList<TriggerItem>();
	private List<Integer> indeces = new ArrayList<Integer>();

	@Override
	public String getString() {
		return "probability";
	}

	@Override
	public void go(Event e) {
		List<Number> nums = new ArrayList<Number>();
		Number total = 0;
		for (int i = 0; i < probs.size(); i++) {
			total = total.doubleValue() + probs.get(i).get(e).doubleValue();
			nums.add(total);
		}
		Number random = Math.random() * total.doubleValue();
		Boolean searching = true;
		int j = 0;
		while (searching) {
			if (random.doubleValue() <= nums.get(j).doubleValue()) searching = false;
			else j++;
		}
		CondProbabilityValue start = probs.get(j);
		if (!start.ret) {
			TriggerItem uniquegoing = start.first;
			Boolean uniquewithin = true;
			while (uniquewithin) {
				try {
					uniquegoing = (TriggerItem) walkmethod.invoke(uniquegoing, e);
					if (uniquegoing == null || uniquegoing.getIndentation().length() <= start.section.getIndentation().length()) uniquewithin = false;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		Boolean within = true;
		if (triggeritems.size() > 0) {
			int k = indeces.get(j);
			while (within) {
				TriggerItem going = triggeritems.get(k);
				if (going instanceof Condition) {
					try {
						within = (Boolean) runmethod.invoke(going, e);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (going instanceof TriggerSection) {
					TriggerItem uniquegoing = going;
					Boolean uniquewithin = true;
					while (uniquewithin) {
						try {
							uniquegoing = (TriggerItem) walkmethod.invoke(uniquegoing, e);
							if (uniquegoing == null || uniquegoing.getIndentation().length() <= going.getIndentation().length()) uniquewithin = false;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} else {
					try {
						walkmethod.invoke(going, e);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				k++;
				within = within && k < triggeritems.size();
			}
		}
	}
	
	@Override
	public void afterSetScope() {
		Boolean within = true;
		TriggerItem going = first;
		TriggerItem end = scope.getNext();
		Integer i = 0;
		while (within) {
			if (going instanceof CondProbabilityValue) {
				probs.add((CondProbabilityValue) going);
				indeces.add(i);
			} else if (going instanceof Conditional) {
				try {
					Object goingcond = condition.get((TriggerSection) going);
					if (goingcond instanceof CondProbabilityValue) {
						probs.add((CondProbabilityValue) goingcond);
						((CondProbabilityValue) goingcond).setTriggerSection((TriggerSection) going);
						indeces.add(i);
					} else {
						triggeritems.add(going);
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				triggeritems.add(going);
				i++;
			}
			going = going.getNext();
			if (going == null || going == end) within = false;
		}
	}

}
