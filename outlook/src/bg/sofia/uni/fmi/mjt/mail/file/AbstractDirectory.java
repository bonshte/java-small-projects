package bg.sofia.uni.fmi.mjt.mail.file;

import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDirectory implements FileSystemObject {
    private Set<NestedFileObject> nestedFiles;

    public AbstractDirectory() {
        nestedFiles = new HashSet<>();
    }

    public Set<MailFile> getMailFilesInside() {
        Set<MailFile> files = new HashSet<>();
        for (var fileObject : nestedFiles) {
            if (fileObject.isMailFile()) {
                files.add((MailFile) fileObject);
            }
        }
        return files;
    }
    private boolean objectExists(String name) {
        for (var fileObject : nestedFiles) {
            if (fileObject.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public Set<RegularDirectory> getRegularDirectoriesInside() {
        Set<RegularDirectory> directories = new HashSet<>();
        for (var fileObject : nestedFiles) {
            if (fileObject.isRegularDirectory()) {
                directories.add((RegularDirectory) fileObject);
            }
        }
        return directories;
    }
    public RegularDirectory addSubDirectory(String name) {
        if (objectExists(name)) {
            throw new FolderAlreadyExistsException("file object already exists");
        }
        //in unix folder/ == folder
        String realName = name;
        if (name.endsWith(File.separator)) {
            realName = name.substring(0, name.length() - 1);
        }
        RegularDirectory subDir = new RegularDirectory(realName, this);
        nestedFiles.add(subDir);
        return subDir;
    }

    public void removeMailFile(MailFile mailFile) {
        if (!nestedFiles.contains(mailFile)) {
            throw new IllegalArgumentException("file is not inside this directory");
        }
        nestedFiles.remove(mailFile);
    }

    public Set<NestedFileObject> getNestedFiles() {
        return nestedFiles;
    }

    public MailFile addFile(String content, MailMetadata metadata, String name) {
        if (objectExists(name)) {
            throw new FolderAlreadyExistsException("file object already exists");
        }
        MailFile mailFile = new MailFile(name, this, content, metadata);
        nestedFiles.add(mailFile);
        return mailFile;
    }
}
