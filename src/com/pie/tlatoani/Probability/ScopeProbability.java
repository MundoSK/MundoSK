package com.pie.tlatoani.Probability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;

public class ScopeProbability extends CustomScope {
	private List<CondProbability> probs = new ArrayList<CondProbability>();

	@Override
	public String toString(Event e, boolean debug) {
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
		TriggerItem going = probs.get(j).getTriggerItem();
		Boolean within = true;

		while (within) {
			try {
				going = (TriggerItem) walkmethod.invoke(going, e);
				if (going == null || going.getIndentation().length() <= indent) within = false;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void afterSetNext() {
		Boolean within = true;
		TriggerItem going = first;
		while (within) {
			if (going instanceof CondProbability) probs.add((CondProbability) going);
			else if (going instanceof Conditional) {
				try {
					Object goingcond = condition.get((TriggerSection) going);
					if (goingcond instanceof CondProbability) {
						probs.add((CondProbability) goingcond);
						((CondProbability) goingcond).setTriggerSection((TriggerSection) going); 
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			going = going.getNext();
			if (going == null || going.getIndentation().length() <= indent) within = false;
		}
	}

}
