package bg.sofia.uni.fmi.mjt.mail.decoder;

import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MailDefinitionDecoderTest {

    @Test
    void testExtractMetadataWithFullData() {
        String definition = "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(definition);
        assertEquals("testy@gmail.com", metadata.sender(), "email not matching");
        assertEquals( "Hello, MJT!", metadata.subject(), "subject not matching");
        assertEquals( LocalDateTime.of(2022,12,8,14,14), metadata.time(), "wrong time");
        assertEquals(Set.of("pesho@gmail.com" , "gosho@gmail.com"), metadata.recipientEmails(), "emails do not match");
    }

    @Test
    void testExtractMetadataWithPermutationOfData() {
        String definition = "subject: Hello, MJT!" + System.lineSeparator() +
                "sender: testy@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(definition);
        assertEquals("testy@gmail.com", metadata.sender(), "email not matching");
        assertEquals( "Hello, MJT!", metadata.subject(), "subject not matching");
        assertEquals( LocalDateTime.of(2022,12,8,14,14), metadata.time(), "wrong time");
        assertNull(metadata.recipientEmails(), "no recipients were passed");

    }
}