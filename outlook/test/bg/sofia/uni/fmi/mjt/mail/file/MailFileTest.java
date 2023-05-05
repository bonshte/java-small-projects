package bg.sofia.uni.fmi.mjt.mail.file;

import bg.sofia.uni.fmi.mjt.mail.decoder.MailDefinitionDecoder;
import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailFileTest {

    @Test
    void testConstructWithNullString() {
        String definition = "subject: Hello, MJT!" + System.lineSeparator() +
                "sender: testy@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com,";

        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(definition);
        RootDirectory root = new RootDirectory();
        assertThrows(IllegalArgumentException.class, () -> new MailFile(null, root, "content" , metadata),
                "should throw with null string passed");
    }

    @Test
    void testConstructWithNullParent() {
        String definition = "subject: Hello, MJT!" + System.lineSeparator() +
                "sender: testy@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com,";

        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(definition);
        RootDirectory root = new RootDirectory();
        assertThrows(IllegalArgumentException.class, () -> new MailFile("root",  null, "content" , metadata),
                "should throw with null parent passed");
    }

    @Test
    void testConstructWithNullContent() {
        String definition = "subject: Hello, MJT!" + System.lineSeparator() +
                "sender: testy@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com,";

        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(definition);
        RootDirectory root = new RootDirectory();
        assertThrows(IllegalArgumentException.class, () -> new MailFile("root",  root, null , metadata),
                "should throw with null content passed");
    }

    @Test
    void testConstructWithNullMetadata() {
        RootDirectory root = new RootDirectory();
        assertThrows(IllegalArgumentException.class, () -> new MailFile("root",  root, "content" , null),
                "should throw with null metadata passed");
    }

}