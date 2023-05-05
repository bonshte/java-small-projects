package bg.sofia.uni.fmi.mjt.mail.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegularDirectoryTest {
    @Test
    void testIsDirectory() {
        RootDirectory root = new RootDirectory();
        RegularDirectory dir = new RegularDirectory("inbox", root);
        assertTrue(dir.isRegularDirectory());
        assertFalse(dir.isMailFile());
    }

    @Test
    void testDirectoryName() {
        RootDirectory root = new RootDirectory();
        RegularDirectory dir = new RegularDirectory("inbox", root);
        assertEquals("inbox", dir.getName(), "name does not match");
        assertEquals("/inbox", dir.getAbsolutePath(), "absolute path does not match");
    }

    @Test
    void testDirectoryWithNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegularDirectory("name", null) );
    }

    @Test
    void testDirectoryWithBlankString() {
        RootDirectory root = new RootDirectory();
        assertThrows(IllegalArgumentException.class, () -> root.addSubDirectory(""));
    }
}