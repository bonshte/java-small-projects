package bg.sofia.uni.fmi.mjt.mail.rule;

import bg.sofia.uni.fmi.mjt.mail.file.AbstractDirectory;
import bg.sofia.uni.fmi.mjt.mail.metadata.RuleMetadata;
import java.util.Objects;


public class Rule {
    private RuleMetadata metadata;
    private int priority;

    private final static int MAX_PRIORITY = 10;

    AbstractDirectory folder;

    public Rule(RuleMetadata metadata, int priority, AbstractDirectory path) {
        if (path == null || metadata == null) {
            throw new IllegalArgumentException("null passed to constructor");
        }
        if (priority < 1 || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("priority out of the allowed range");
        }
        this.metadata = metadata;
        this.priority = priority;
        this.folder = path;
    }

    public int getPriority() {
        return priority;
    }
    public RuleMetadata getMetadata() {
        return metadata;
    }

    public AbstractDirectory getFolder() {
        return folder;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule rule)) return false;
        return priority == rule.priority && Objects.equals(metadata, rule.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, priority);
    }
}
