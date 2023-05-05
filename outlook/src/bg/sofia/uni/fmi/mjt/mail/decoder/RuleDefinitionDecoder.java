package bg.sofia.uni.fmi.mjt.mail.decoder;

import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.metadata.RuleMetadata;

import java.util.Set;

public class RuleDefinitionDecoder {
    private static final String SUBJECT_FORMAT = "subject-includes:";
    private static final String SUBJECT_AND_BODY_FORMAT = "subject-or-body-includes:";
    private static final String FROM_FORMAT = "from:";
    private static final String RECIPIENTS_FORMAT = "recipients-includes:";

    //keep the validations here
    public static RuleMetadata decodeDefinition(String ruleDefinition) {
        Set<String> subjectWords = null;
        Set<String> bodyWords = null;
        Set<String> recipientsEmails = null;
        String sender = null;

        String[] lines = ruleDefinition.split(System.lineSeparator());
        for (var line : lines) {
            int indexAfterFormat;
            if (line.startsWith(SUBJECT_FORMAT)) {
                if (subjectWords == null) {
                    indexAfterFormat = line.indexOf(SUBJECT_FORMAT) + SUBJECT_FORMAT.length();
                    subjectWords = WordExtractor.extractWords(line.substring(indexAfterFormat));
                } else {
                    throw new RuleAlreadyDefinedException("rule has more than one subject formats");
                }
            } else if (line.startsWith(SUBJECT_AND_BODY_FORMAT)) {
                if (bodyWords == null) {
                    indexAfterFormat = line.indexOf(SUBJECT_AND_BODY_FORMAT) + SUBJECT_AND_BODY_FORMAT.length();
                    bodyWords = WordExtractor.extractWords(line.substring(indexAfterFormat));
                } else {
                    throw new RuleAlreadyDefinedException("rule has more than one body formats");
                }

            } else if (line.startsWith(RECIPIENTS_FORMAT)) {
                if (recipientsEmails == null) {
                    indexAfterFormat = line.indexOf(RECIPIENTS_FORMAT) + RECIPIENTS_FORMAT.length();
                    recipientsEmails = WordExtractor.extractWords(line.substring(indexAfterFormat));
                } else {
                    throw new RuleAlreadyDefinedException("rule has more than one recipient formats");
                }
            } else if (line.startsWith(FROM_FORMAT)) {
                if (sender == null) {
                    indexAfterFormat = line.indexOf(FROM_FORMAT) + FROM_FORMAT.length();
                    sender = line.substring(indexAfterFormat).trim();
                } else {
                    throw new RuleAlreadyDefinedException("rule has more than one sender formats");
                }
            }


        }

        return new RuleMetadata(sender, recipientsEmails, bodyWords, subjectWords);

    }
}
