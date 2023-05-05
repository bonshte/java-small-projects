package bg.sofia.uni.fmi.mjt.mail.file;

public interface NestedFileObject extends FileSystemObject {
    boolean isRegularDirectory();
    boolean isMailFile();


}
