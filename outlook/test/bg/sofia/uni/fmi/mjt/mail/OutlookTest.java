package bg.sofia.uni.fmi.mjt.mail;


import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;


import static org.junit.jupiter.api.Assertions.*;

class OutlookTest {

    private Outlook outlook;
    @BeforeEach
    void setupOutlook() {
        outlook = new Outlook();
    }

    @Test
    void testAddAccountIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(null, "me@abv.bg"),
                "should throw exception when null passed");
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("me", null),
                "should throw exception when null passed");
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("  ","me@abv.bg"),
                "should throw illegal argument when empty or blank string passed");
    }

    @Test
    void testAddAccountAccountAlreadyRegistered() {
        Account newUser1 = outlook.addNewAccount("bonshte", "bonshte@abv.bg");
        assertThrows(AccountAlreadyExistsException.class, () -> outlook.addNewAccount("bonshte", "az@abv.bg"),
                "username was already taken");
        assertThrows(AccountAlreadyExistsException.class, () -> outlook.addNewAccount("shtebon", "bonshte@abv.bg"),
                "email was already taken");
    }

    @Test
    void testAddAccountWorks() {
        Account newUser1 = outlook.addNewAccount("bonshte", "bonshte@abv.bg");
        assertTrue(outlook.accountNameTaken("bonshte"));
        assertTrue(outlook.emailTaken("bonshte@abv.bg"));
    }
    @Test
    void testCreateFolderIllegalArgument() {
        Account newUser1 = outlook.addNewAccount("bonshte", "bonshte@abv.bg");
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(null, "/inbox/folder1"),
                "null passed, should throw");
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("bonshte", null),
                "null passed, should throw");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.createFolder("bonshte", " "), "blank path passed");
    }

    @Test
    void testCreateFolderFolderAlreadyExists() {
        Account newUser1 = outlook.addNewAccount("bonshte", "bonshte@abv.bg");
        assertThrows(FolderAlreadyExistsException.class, () -> outlook.createFolder("bonshte", "/inbox/"),
                 "folder was created when user registered, already exists");
        outlook.createFolder("bonshte", "/inbox/gaming");
        assertThrows(FolderAlreadyExistsException.class ,
                () -> outlook.createFolder("bonshte", "/inbox/gaming/"),
                "folder was already created");
    }

    @Test
    void testCreateFolderAccountDoesNotExist() {
        assertThrows(AccountNotFoundException.class, () -> outlook.createFolder("unknown", "/inbox"),
                "account should not be in the database");
    }

    @Test
    void testCreateFolderDoesNotStartFromInbox() {
        Account newUser1 = outlook.addNewAccount("bonshte", "bonshte@abv.bg");
        assertThrows(InvalidPathException.class, () -> outlook.createFolder("bonshte", "sent/ivan"), "" +
                "should not be able to create folders outside of /inbox");
        assertThrows(InvalidPathException.class, () -> outlook.createFolder("bonshte", "inbox2/ivan"), "" +
                "should not be able to create folders outside of /inbox");
        assertThrows(FolderAlreadyExistsException.class,
                () -> outlook.createFolder("bonshte", "/inbox"),"inbox folder is automatically created");
        assertThrows(InvalidPathException.class,
                () ->outlook.createFolder("bonshte", "/inbox2"),"folder does not start from inbox");
        assertThrows(FolderAlreadyExistsException.class,
                () ->outlook.createFolder("bonshte" , "/inbox/"), "inbox folder is auto created");
        assertThrows(InvalidPathException.class,
                () ->outlook.createFolder("bonshte", "inbox/gaming"),
                "does not start from root for mails");
    }


    @Test
    void testCreateInboxDir() {
        outlook.addNewAccount("me", "me@abv.bg");
        assertThrows(FolderAlreadyExistsException.class,
                ()->outlook.createFolder("me", "/inbox"),
                "inbox is created automatically for every user");
        assertThrows(FolderAlreadyExistsException.class,
                ()->outlook.createFolder("me", "/inbox/"),
                "inbox is created automatically for every user");
    }

    @Test
    void testCreateFolderIntermediateMissing() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        assertThrows(InvalidPathException.class, () -> outlook.createFolder("shtiliyan", "/inbox/gaming/league"),
                "intermediate folder does not exist");
    }

    @Test
    void testCreateFolderWorksWithNestedCalls() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        assertDoesNotThrow(() -> outlook.createFolder("shtiliyan", "/inbox/hobbies/"),
                "folder had parent, but could not be created");
        assertThrows(FolderAlreadyExistsException.class,
                () ->outlook.createFolder("shtiliyan", "/inbox/hobbies/"),
                "folder was created");
        assertDoesNotThrow(() -> outlook.createFolder("shtiliyan", "/inbox/hobbies/gaming/"),
                "folder had parent and was not already created");
        assertDoesNotThrow(() -> outlook.createFolder("shtiliyan", "/inbox/hobbies/gaming/league"),
                "folder had parent and was not already created");

        assertDoesNotThrow(() -> outlook.createFolder("shtiliyan", "/inbox/hobbies/gaming/league/2018/"),
                "folder had parent and was not already created");
    }

    @Test
    void testAddRuleIllegalArguments() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        //i am leaving the option for rule to keep the mail in the inbox in case another rule comes which is with less priority
        String folderPath = "/inbox";
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, folderPath, definition, 9),
                "null account name passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("shtiliyan", null , definition, 9),
                "null folder name passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("shtiliyan", folderPath, null, 9),
                "null definition name passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("shtiliyan", folderPath, definition, 11),
                "out of range priority passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("", folderPath, definition, 9),
                "empty account name passed");

    }

    @Test
    void testAddRuleAccountDoesNotExist() {
        String folderPath = "/inbox";
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(AccountNotFoundException.class,
                () -> outlook.addRule("me", folderPath, definition,8),
                "account was not registered");
    }

    @Test
    void testAddRuleDoesNotMoveIncorrectly() {
        outlook.addNewAccount("me","me@abv.bg");
        outlook.createFolder("me", "/inbox/gaming");
        String ruleDefinition = "subject-includes: Witcher3," + System.lineSeparator() +
                "subject-or-body-includes: Witcher3 " + System.lineSeparator() +
                "from: me@abv.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, me@abv.bg";
        String mailDefinition = "sender: me@abv.bg" + System.lineSeparator() +
                "subject: Hello gamer , Witcher3 is out" + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("me", mailDefinition, "Witcher3 is here!");
        outlook.addRule("me","/inbox/gaming/", ruleDefinition , 5);
        Collection<Mail> mailsInGaming = outlook.getMailsFromFolder("me", "/inbox/gaming");
        assertEquals(0, mailsInGaming.size(), "no mail does not match requirements to be here");
        Collection<Mail> mailsInInbox = outlook.getMailsFromFolder("me", "/inbox");
        assertEquals(1,mailsInInbox.size(), "mail was added ");
    }

    @Test
    void testAddRuleForNotExistingFolder() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        String folderPath = "/inbox/gaming";
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(FolderNotFoundException.class,
                () -> outlook.addRule("shtiliyan", folderPath, definition, 8),
                "folder rule moves to does not exist");
    }

    @Test
    void testAddRuleRuleContradictoryToOtherRule() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        outlook.createFolder("shtiliyan", "/inbox/gaming/");
        outlook.createFolder("shtiliyan", "/inbox/streaming");
        String folderPath1 = "/inbox/gaming";
        String folderPath2 = "/inbox/streaming";

        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        outlook.addRule("shtiliyan", folderPath1 , definition , 8);
        assertThrows(RuleAlreadyDefinedException.class,
                () ->outlook.addRule("shtiliyan", folderPath2, definition, 8 ),
                "contradictory rule should not be added");
    }

    @Test
    void testAddRuleIsNotContradictory() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        outlook.createFolder("shtiliyan", "/inbox/gaming/");
        outlook.createFolder("shtiliyan", "/inbox/streaming");
        String folderPath1 = "/inbox/gaming";
        String folderPath2 = "/inbox/streaming";

        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        String definition2 = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: boiko@abv.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        outlook.addRule("shtiliyan", folderPath1 , definition , 8);
        assertDoesNotThrow(() ->outlook.addRule("shtiliyan",folderPath2,definition2, 8 ),
                "rule differs in sender, should not throw");
    }

    @Test
    void testAddRuleInvalidRuleFormat() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        outlook.createFolder("shtiliyan", "/inbox/gaming/");
        String folderPath1 = "/inbox/gaming";
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(RuleAlreadyDefinedException.class,
                () -> outlook.addRule("shtiliyan", folderPath1 , definition , 8),
                "rule has multiple definitions for sender");

    }

    @Test
    void testAddRuleIntermediateFoldersMissing() {
        Account me = outlook.addNewAccount("shtiliyan", "me@abv.bg");
        outlook.createFolder("shtiliyan", "/inbox/gaming/");
        String folderPath1 = "/inbox/gaming/league/2022";

        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, hristo@hotmail.com,";
        assertThrows(FolderNotFoundException.class ,
                () -> outlook.addRule("shtiliyan", folderPath1 , definition ,6),
                "parent path for rule folder does not exist");
    }

    @Test
    void testAddRuleNewRuleIsAppliedOnMail() {
        outlook.addNewAccount("me","me@abv.bg");
        outlook.createFolder("me", "/inbox/gaming");
        String ruleDefinition = "subject-includes: Witcher3," + System.lineSeparator() +
                "subject-or-body-includes: Witcher3 " + System.lineSeparator() +
                "from: me@abv.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, me@abv.bg";
        String mailDefinition = "sender: me@abv.bg" + System.lineSeparator() +
                "subject: Hello gamer , Witcher3 is out" + System.lineSeparator() +
                "recipients: pesho@gmail.com, me@abv.bg," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("me", mailDefinition, "Witcher3 is here!");
        outlook.addRule("me","/inbox/gaming/", ruleDefinition , 5);
        Collection<Mail> mails = outlook.getMailsFromFolder("me", "/inbox/gaming");
        assertEquals(1, mails.size(), "only one mail has to be in gaming");
    }

    @Test
    void testReceiveMailIllegalArguments() {
        outlook.addNewAccount("bonshte", "me@abv.bg");
        String definition = "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("bonshte", definition, null),
                "null content passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail(" ", definition, "hello world!"),
                "empty name  passed");
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("bonshte", null, "hello world!"),
                "null definition passed");
    }


    @Test
    void testReceiveMailAccountNotRegistered() {
        String definition = "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        assertThrows(AccountNotFoundException.class,
                () -> outlook.receiveMail("bonshte", definition, "hello world!"),
                "account with that name does not exist passed");
    }

    @Test
    void testReceiveMailRuleApplied() {
        outlook.addNewAccount("me","me@abv.bg");
        outlook.createFolder("me", "/inbox/gaming");
        String ruleDefinition = "subject-includes: Witcher3," + System.lineSeparator() +
                "subject-or-body-includes: Witcher3 " + System.lineSeparator() +
                "from: me@abv.bg" + System.lineSeparator() +
                "recipients-includes: hello@abv.bg, ivan@gmail.com, me@abv.bg";
        outlook.addRule("me","/inbox/gaming/", ruleDefinition , 5);
        String mailDefinition = "sender: me@abv.bg" + System.lineSeparator() +
                "subject: Hello gamer , Witcher3 is out" + System.lineSeparator() +
                "recipients: pesho@gmail.com, me@abv.bg," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("me", mailDefinition, "Witcher3 is here!");
        Collection<Mail> mails = outlook.getMailsFromFolder("me", "/inbox/gaming");
        assertEquals(1, mails.size(), "only one mail has to be in gaming");
    }



    @Test
    void testReceiveMailCorrectRuleApplied() {
        outlook.addNewAccount("me","me@abv.bg");
        outlook.createFolder("me", "/inbox/gaming");
        outlook.createFolder("me", "/inbox/streaming/");
        String ruleDefinition =
                "subject-or-body-includes: Witcher3";
        outlook.addRule("me","/inbox/gaming/", ruleDefinition , 5);
        outlook.addRule("me", "/inbox/streaming", ruleDefinition , 2);
        String mailDefinition = "sender: me@abv.bg" + System.lineSeparator() +
                "subject: Hello, gamer!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("me", mailDefinition, "Witcher3 is here!");
        Collection<Mail> mailsInGaming = outlook.getMailsFromFolder("me","/inbox/gaming");
        Collection<Mail> mailsInStreaming = outlook.getMailsFromFolder("me" , "/inbox/streaming/");
        assertEquals(1, mailsInStreaming.size(), "mail should be moved there");
        assertEquals(0, mailsInGaming.size(), "no mails should be in gaming");
        Collection<Mail> mailsInInbox = outlook.getMailsFromFolder("me", "/inbox");
        assertEquals(0, mailsInInbox.size(), "mail should be removed from index");
    }

    @Test
    void testGetMailsFromFolderSenderIsNotRegistered() {
        outlook.addNewAccount("me","me@abv.bg");
        outlook.createFolder("me", "/inbox/gaming");
        String ruleDefinition =
                "subject-or-body-includes: Witcher3";
        outlook.addRule("me","/inbox/gaming/",ruleDefinition , 5);
        String mailDefinition = "sender: tasty@abv.bg" + System.lineSeparator() +
                "subject: Hello, gamer!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("me", mailDefinition, "Witcher3 is here!");
        assertThrows(AccountNotFoundException.class,
                () -> outlook.getMailsFromFolder("me", "/inbox/gaming"),
                "sender of the mails is not registered");
    }

    @Test
    void testGetMailsIllegalArguments() {
        outlook.addNewAccount("me", "me@abv.bg");
        assertThrows(IllegalArgumentException.class,
                () ->outlook.getMailsFromFolder(null, "/inbox"),
                "null account string passed,should throw");
        assertThrows(IllegalArgumentException.class,
                () ->outlook.getMailsFromFolder("me", null),
                "null path passed should throw");
        assertThrows(IllegalArgumentException.class,
                () ->outlook.getMailsFromFolder("  ", "/inbox"),
                "empty name passed");
    }

    @Test
    void testGetMailsNotExistingAccount() {
        assertThrows(AccountNotFoundException.class,
                ()->outlook.getMailsFromFolder("not registered", "/inbox"),
                "account is not in the system");
    }

    @Test
    void testGetMailsNotExistingFolder() {
        outlook.addNewAccount("me", "me@abv.bg");
        assertThrows(FolderNotFoundException.class,
                ()->outlook.getMailsFromFolder("me", "/inbox/gaming"),
                "folder does not exist");
    }

    @Test
    void testGetMailsNotExistingIntermediateFolders() {
        outlook.addNewAccount("me", "me@abv.bg");
        assertThrows(FolderNotFoundException.class,
                ()->outlook.getMailsFromFolder("me", "/inbox/gaming/warcraft"),
                "folder does not exist");
    }

    @Test
    void testGetMailsNoMailsSaved() {
        outlook.addNewAccount("me", "me@abv.bg");
        Collection<Mail> mails = outlook.getMailsFromFolder("me", "/inbox");
        assertEquals(0, mails.size(), "no mails were were received by user");
    }


    @Test
    void testSendMailIllegalArguments() {
        String mailDefinition = "sender: tasty@abv.bg" + System.lineSeparator() +
                "subject: Hello, gamer!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        assertThrows(IllegalArgumentException.class,

                () ->outlook.sendMail(null, mailDefinition, "hello" ),
                "null account name passed");
        assertThrows(IllegalArgumentException.class,

                () ->outlook.sendMail("  ", mailDefinition, "hello" ),
                "blank account name passed");
        assertThrows(IllegalArgumentException.class,

                () ->outlook.sendMail("me@abv.bg", null, "hello" ),
                "null mail description name passed");
        assertThrows(IllegalArgumentException.class,

                () ->outlook.sendMail("me@abv.bg", mailDefinition, null ),
                "null account name passed");
    }



    @Test
    void testSendEmailSavesToSentFolder() {
        String mailDefinition = "sender: tasty@abv.bg" + System.lineSeparator() +
                "subject: Hello, gamer!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, tasty@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.addNewAccount("me", "tasty@abv.bg");
        String mailContent = "hello";
        Collection<Mail> sentMails = outlook.getMailsFromFolder("me", "/sent");
        assertEquals(0, sentMails.size(), "mo mails were sent");
        outlook.sendMail("me",mailDefinition , mailContent);
        Collection<Mail> sentMailsAfter = outlook.getMailsFromFolder("me" ,"/sent");
        assertEquals(1, sentMailsAfter.size(), "user had sent a file");
    }

    @Test
    void testSendEmailWrongSenderMetadata() {
        String mailDefinition = "sender: tasty@abv.bg" + System.lineSeparator() +
                "subject: Hello, gamer!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, tasty@gmail.com," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.addNewAccount("me", "me@abv.bg");
        String mailContent = "hello";
        assertThrows(AccountNotFoundException.class ,
                () ->outlook.sendMail("me", mailDefinition, mailContent),
                "sender in metadata differs from real sender");
    }

    @Test
    void testSendEmailSavesInRecipientsFolders() {
        outlook.addNewAccount("gosho","gosho@abv.bg");
        outlook.addNewAccount("ivan" , "ivan@abv.bg");
        outlook.addNewAccount("martin","martin@abv.bg");
        String ruleDefinition = "subject-includes: Witcher3," + System.lineSeparator() +
                "subject-or-body-includes: Witcher3 " + System.lineSeparator() +
                "from: gosho@abv.bg" + System.lineSeparator() +
                "recipients-includes: gosho@abv.bg, ivan@abv.bg,";
        String mailDefinition = "sender: gosho@abv.bg" + System.lineSeparator() +
                "subject: Hello gamer , Witcher3 is out" + System.lineSeparator() +
                "recipients: martin@abv.bg, ivan@abv.bg," + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        String content = "Wither3 is here!";
        outlook.createFolder("ivan", "/inbox/gaming");
        outlook.createFolder("ivan","/inbox/gaming/witcher");
        outlook.createFolder("martin", "/inbox/streaming");
        outlook.createFolder("martin","/inbox/streaming/witcher");
        outlook.addRule("ivan","/inbox/gaming/witcher",ruleDefinition,4);
        outlook.addRule("martin","/inbox/streaming/witcher", ruleDefinition, 3);
        outlook.sendMail("gosho",mailDefinition,content);
        Collection<Mail> ivanGamingWitcherMails = outlook.getMailsFromFolder("ivan","/inbox/gaming/witcher");
        Collection<Mail> martinStreamingWitcherMails = outlook.getMailsFromFolder("martin",
                "/inbox/streaming/witcher");
        assertEquals(1, ivanGamingWitcherMails.size(), "mail must have been moved");
        assertEquals(1, martinStreamingWitcherMails.size(), "mail must have been moved");

    }


}