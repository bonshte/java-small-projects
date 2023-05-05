package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.file.MailFile;

import java.time.LocalDateTime;
import java.util.Set;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {

    public Mail(MailFile mailFile, Account account) {
        this(account, mailFile.getMetadata().recipientEmails(), mailFile.getMetadata().subject(),
                mailFile.getContent(), mailFile.getMetadata().time());
    }

}