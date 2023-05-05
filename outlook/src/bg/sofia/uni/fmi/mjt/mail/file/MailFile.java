package bg.sofia.uni.fmi.mjt.mail.file;

import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;
import bg.sofia.uni.fmi.mjt.mail.validators.StringValidator;

import java.io.File;

public class MailFile implements NestedFileObject {
    private String content;
    private MailMetadata metadata;
    private AbstractDirectory parent;
    private String name;

    public MailFile(String name, AbstractDirectory parent, String content, MailMetadata metadata) {
        if (!StringValidator.isValidString(name) || content == null || parent == null || metadata == null) {
            throw new IllegalArgumentException("null, empty or blank argument passed to constructor");
        }
        if (name.endsWith(File.separator)) {
            throw new InvalidPathException("Mail file name cannot end with /, it is not a directory");
        }
        this.name = name;
        this.parent = parent;
        this.metadata = metadata;
        this.content = content;
    }

    public AbstractDirectory getParent() {
        return parent;
    }

    public MailMetadata getMetadata() {
        return metadata;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean isMailFile() {
        return true;
    }

    @Override
    public boolean isRegularDirectory() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbsolutePath() {
        return parent.getAbsolutePath() + File.separator + name;
    }
}
