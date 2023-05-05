package bg.sofia.uni.fmi.mjt.mail.rule;

import java.util.Comparator;

public class RulePriorityComparator implements Comparator<Rule> {
    @Override
    public int compare(Rule o1, Rule o2) {
        return o1.getPriority() - o2.getPriority();
    }
}
