package com.pie.tlatoani.Probability;

import java.util.ArrayList;
import java.util.List;

import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

import com.pie.tlatoani.Core.Skript.CustomScope;

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
	public boolean go(Event e) {
	    scope.setNext(null);
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
		TriggerItem.walk(start.getTriggerItem(), e);
        scope.setNext(scope.getNext());
		return false;
	}
	
	@Override
	public void setScope() {
		Boolean within = true;
		TriggerItem going = first;
		TriggerItem end = scope.getNext();
		Integer i = 0;
		while (going != null && going != end) {
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
					Logging.reportException(this, e);
				}
			} else {
				triggeritems.add(going);
				i++;
			}
			going = going.getNext();
		}
		last.setNext(null);
	}

}
