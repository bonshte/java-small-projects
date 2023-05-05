package bg.sofia.uni.fmi.mjt.mail.file;

import bg.sofia.uni.fmi.mjt.mail.validators.StringValidator;

import java.io.File;

public class RegularDirectory extends AbstractDirectory implements NestedFileObject {
    private AbstractDirectory parent;
    private String name;

    public AbstractDirectory getParent() {
        return parent;
    }
    public RegularDirectory(String name, AbstractDirectory parent) {
        super();
        if (!StringValidator.isValidString(name) || parent == null) {
            throw new IllegalArgumentException("null,empty or blank argument passed to constructor");
        }
        this.name = name;
        this.parent = parent;
    }
    @Override
    public boolean isMailFile() {
        return false;
    }

    @Override
    public boolean isRegularDirectory() {
        return true;
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
