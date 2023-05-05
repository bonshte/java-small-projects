package bg.sofia.uni.fmi.mjt.mail.decoder;


import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;

import java.time.LocalDateTime;
import java.util.Set;

public class MailDefinitionDecoder {
    private static final String SENDER_FORMAT = "sender:";
    private static final String SUBJECT_FORMAT = "subject:";
    private static final String RECIPIENTS_FORMAT = "recipients:";
    private static final String RECEIVED_FORMAT = "received:";


    public static MailMetadata extractMetaData(String metaData) {
        String[] lines = metaData.split(System.lineSeparator());
        LocalDateTime time = null;
        Set<String> recipients = null;
        String subject = null;
        String sender = null;
        for (var line : lines) {
            if (line.startsWith(SUBJECT_FORMAT)) {
                int indexAfterSubject = line.indexOf(SUBJECT_FORMAT) + SUBJECT_FORMAT.length();
                subject = line.substring(indexAfterSubject).trim();
            } else if (line.startsWith(RECIPIENTS_FORMAT)) {
                int indexAfterRecipientsFormat = line.indexOf(RECIPIENTS_FORMAT) + RECIPIENTS_FORMAT.length();
                recipients = WordExtractor.extractWords(line.substring(indexAfterRecipientsFormat));
            } else if ( line.startsWith(RECEIVED_FORMAT)) {
                int indexAfterTimeFormat = line.indexOf(RECEIVED_FORMAT) + RECEIVED_FORMAT.length();
                time = WordExtractor.extractLocalDateTime(line.substring(indexAfterTimeFormat));
            } else if (line.startsWith(SENDER_FORMAT)) {
                int indexAfterFormat = line.indexOf(SENDER_FORMAT) + SENDER_FORMAT.length();
                sender = line.substring(indexAfterFormat).trim();
            }
        }
        return new MailMetadata(time, recipients, subject, sender);


    }
}
