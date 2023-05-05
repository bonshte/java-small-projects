package bg.sofia.uni.fmi.mjt.mail.decoder;

import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.metadata.RuleMetadata;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RuleDefinitionDecoderTest {

    @Test
    void testDecodeDefinitionFull() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                    "subject-or-body-includes: izpit" + System.lineSeparator() +
                    "from: stoyo@fmi.bg" + System.lineSeparator() +
                    "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";

        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertEquals(Set.of("mjt", "izpit", "2022"), metadata.subjectWords(), "subject set does not match");
        assertEquals(Set.of("izpit"), metadata.bodyWords(), "subject set does not match");
        assertEquals(Set.of("hello@abv.bg", "ivan@gmail.com", "hristo@hotmail.com"), metadata.recipientEmails(), "emails do not match");
        assertEquals("stoyo@fmi.bg", metadata.senderEmail(), "sender email does not match");

    }

    @Test
    void testDecodeDefinitionEmptyField() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: " + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";

        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertEquals(Set.of("mjt", "izpit", "2022"), metadata.subjectWords(), "subject set does not match");
        assertEquals(Set.of(), metadata.bodyWords(), "subject set does not match");
        assertEquals(Set.of("hello@abv.bg", "ivan@gmail.com", "hristo@hotmail.com"), metadata.recipientEmails(), "emails do not match");
        assertEquals("stoyo@fmi.bg", metadata.senderEmail(), "sender email does not match");
    }

    @Test
    void testDecodeDefinitionFullPermutation() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,"  +
                System.lineSeparator() + "subject-or-body-includes: izpit";


        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertEquals(Set.of("mjt", "izpit", "2022"), metadata.subjectWords(), "subject set does not match");
        assertEquals(Set.of("izpit"), metadata.bodyWords(), "subject set does not match");
        assertEquals(Set.of("hello@abv.bg", "ivan@gmail.com", "hristo@hotmail.com"), metadata.recipientEmails(), "emails do not match");
        assertEquals("stoyo@fmi.bg", metadata.senderEmail(), "sender email does not match");
    }

    @Test
    void testDecodeDefinitionNotFull() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";

        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertEquals(Set.of("mjt", "izpit", "2022"), metadata.subjectWords(), "subject set does not match");
        assertNull(metadata.bodyWords(), "no body passes, should be null");
        assertEquals(Set.of("hello@abv.bg", "ivan@gmail.com", "hristo@hotmail.com"), metadata.recipientEmails(), "emails do not match");
        assertNull(metadata.senderEmail(), "no sender passed, should be null");
    }

    @Test
    void testDecodeDefinitionOnlySenderPassed() {
        String definition = "from: stoyo@fmi.bg";
        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertNull(metadata.recipientEmails(), "no emails passed");
        assertNull(metadata.bodyWords(), "no body passes, should be null");
        assertNull(metadata.subjectWords(), "no subject passed");
        assertEquals("stoyo@fmi.bg", metadata.senderEmail(), "sender does not match");
    }

    @Test
    void testDecodeDefinitionNoData() {
        String definition = "";
        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(definition);
        assertNull(metadata.recipientEmails(), "no emails passed");
        assertNull(metadata.bodyWords(), "no body passes, should be null");
        assertNull(metadata.subjectWords(), "no subject passed");
        assertNull(metadata.senderEmail(), "no sender passed, should be null");
    }

    @Test
    void testDecodeDefinitionDuplicateSender() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,"
                + System.lineSeparator() +  "from: stoyo@fmi.bg";
        assertThrows(RuleAlreadyDefinedException.class, () -> RuleDefinitionDecoder.decodeDefinition(definition));
    }

    @Test
    void testDecodeDefinitionDuplicateRecipients() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,"
                + System.lineSeparator() +  "recipients-includes: misho@abv.bg, hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(RuleAlreadyDefinedException.class, () -> RuleDefinitionDecoder.decodeDefinition(definition));
    }

    @Test
    void testDecodeDefinitionDuplicateBodyWords() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-or-body-includes: izpit, uspeh" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(RuleAlreadyDefinedException.class, () -> RuleDefinitionDecoder.decodeDefinition(definition));
    }

    @Test
    void testDecodeDefinitionDuplicateSubjectWords() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-includes: mjt, izpit, 2022, january" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(RuleAlreadyDefinedException.class, () -> RuleDefinitionDecoder.decodeDefinition(definition));
    }

}