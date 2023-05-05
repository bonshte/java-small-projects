package bg.sofia.uni.fmi.mjt.mail.metadata;

import java.time.LocalDateTime;
import java.util.Set;

public record MailMetadata(LocalDateTime time, Set<String> recipientEmails, String subject, String sender) {
}
