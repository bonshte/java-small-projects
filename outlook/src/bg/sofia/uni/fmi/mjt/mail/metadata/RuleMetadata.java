package bg.sofia.uni.fmi.mjt.mail.metadata;

import java.util.Set;

public record RuleMetadata(String senderEmail, Set<String> recipientEmails,
                           Set<String> bodyWords, Set<String> subjectWords) {

}
