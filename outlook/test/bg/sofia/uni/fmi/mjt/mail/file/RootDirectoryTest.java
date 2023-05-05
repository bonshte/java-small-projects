package bg.sofia.uni.fmi.mjt.mail.file;

import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RootDirectoryTest {
    @Test
    void testGetAbsolutePath() {
        RootDirectory root = new RootDirectory();
        assertEquals("", root.getAbsolutePath(), "root's name is not empty");
    }

    @Test
    void testAddDirectoryToRoot() {
        RootDirectory root = new RootDirectory();
        RegularDirectory subDir1 = root.addSubDirectory("inbox");
        RegularDirectory subDir2 = root.addSubDirectory("sent");
        Set<RegularDirectory> subDirectories = root.getRegularDirectoriesInside();
        assertEquals(2, subDirectories.size(), "directories number differs");
        assertEquals(Set.of(subDir1, subDir2), subDirectories, "directories do not match");
    }

    @Test
    void testAddDirectoryToRootAbsolutePath() {
        RootDirectory root = new RootDirectory();
        RegularDirectory subDir = root.addSubDirectory("inbox");
        assertEquals("/inbox",subDir.getAbsolutePath(), "absolute path wrong");
    }

    @Test
    void testAddDirectoryThrowsAlreadyInsideWithDirectory() {
        RootDirectory root = new RootDirectory();
        RegularDirectory subDir1 = root.addSubDirectory("inbox");
        assertThrows(FolderAlreadyExistsException.class, () -> root.addSubDirectory("inbox"),
                "folder ahs to already exist");
    }

    @Test
    void testAddDirectoryThrowsAlreadyInsideWithMail() {
        RootDirectory root = new RootDirectory();
        RegularDirectory subDir1 = root.addSubDirectory("inbox");
        LocalDateTime testTime = LocalDateTime.of(2000,10,10,10,10);
        MailMetadata metadata = new MailMetadata(testTime,
                Set.of("me@abv.bg", "you@gmail.com"),"testing", "me");
        String testContent = "none";
        assertThrows(FolderAlreadyExistsException.class, () -> root.addFile(testContent, metadata, "inbox"),
                "folder already has to exist");
    }



    @Test
    void testAddMailFile() {
        RootDirectory root = new RootDirectory();
        LocalDateTime testTime = LocalDateTime.of(2000,10,10,10,10);
        MailMetadata metadata = new MailMetadata(testTime,
                Set.of("me@abv.bg", "you@gmail.com"),"testing", "me");
        String testContent = "none";
        MailFile firstFile = root.addFile(testContent,metadata,"file1");
        MailFile secondFile = root.addFile(testContent,metadata,"file2");
        assertEquals(2, root.getNestedFiles().size(), "only 2 file objects were added");
        assertEquals(2, root.getMailFilesInside().size(), "2 mails were added");
        assertEquals(Set.of(firstFile, secondFile), root.getMailFilesInside(), "mails added do not match");
    }

    @Test
    void testAddDirectoryAndMail() {
        RootDirectory root = new RootDirectory();
        LocalDateTime testTime = LocalDateTime.of(2000,10,10,10,10);
        MailMetadata metadata = new MailMetadata(testTime,
                Set.of("me@abv.bg", "you@gmail.com"),"testing", "me");
        String testContent = "none";
        MailFile firstFile = root.addFile(testContent,metadata,"file1");
        RegularDirectory subDir = root.addSubDirectory("subdir");
        assertEquals(2, root.getNestedFiles().size(), "2 file objects were added");
    }


}