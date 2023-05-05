package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.decoder.MailDefinitionDecoder;
import bg.sofia.uni.fmi.mjt.mail.decoder.RuleDefinitionDecoder;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.file.AbstractDirectory;
import bg.sofia.uni.fmi.mjt.mail.file.NestedFileObject;
import bg.sofia.uni.fmi.mjt.mail.file.RegularDirectory;
import bg.sofia.uni.fmi.mjt.mail.file.MailFile;
import bg.sofia.uni.fmi.mjt.mail.file.RootDirectory;
import bg.sofia.uni.fmi.mjt.mail.metadata.MailMetadata;
import bg.sofia.uni.fmi.mjt.mail.metadata.RuleMetadata;
import bg.sofia.uni.fmi.mjt.mail.rule.Rule;
import bg.sofia.uni.fmi.mjt.mail.rule.RulePriorityComparator;
import bg.sofia.uni.fmi.mjt.mail.validators.StringValidator;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Outlook implements MailClient {
    private Map<Account, RootDirectory> accountToRootDirectory;
    private Map<Account, Set<Rule>> accountToRules;

    private long uniqueMailNumber = 1;

    public Outlook() {
        this.accountToRules = new HashMap<>();
        this.accountToRootDirectory = new HashMap<>();
    }

    public boolean accountNameTaken(String name) {
        Set<Account> accounts = accountToRootDirectory.keySet();
        for (var account : accounts) {
            if (account.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public boolean emailTaken(String email) {
        Set<Account> accounts = accountToRootDirectory.keySet();
        for (var account : accounts) {
            if (account.emailAddress().equals(email)) {
                return true;
            }
        }
        return false;
    }
    private Account getAccountByName(String accountName) {
        Set<Account> accountsRegistered = accountToRootDirectory.keySet();
        for (var account : accountsRegistered) {
            if (account.name().equals(accountName)) {
                return account;
            }
        }
        throw new AccountNotFoundException("accountName not found for accountName, logical problem with the code");
    }
    private Account getAccountByEmail(String email) {
        Set<Account> accountsRegistered = accountToRootDirectory.keySet();
        for (var account : accountsRegistered) {
            if (account.emailAddress().equals(email)) {
                return account;
            }
        }
        throw new AccountNotFoundException("accountName not found for email, logical problem with the code");
    }
    private RootDirectory createStartingDirectory() {
        RootDirectory root = new RootDirectory();
        root.addSubDirectory("inbox");
        root.addSubDirectory("sent");
        return root;
    }

    private String getUnixPath(String path) {
        String realPath = path.trim();
        if (realPath.endsWith(File.separator)) {
            realPath = realPath.substring(0, realPath.length() - 1);
        }
        return realPath;
    }
    private MailFile addMailFileToInbox(Account account, String content, MailMetadata metadata) {
        RegularDirectory inboxDirectory = getAccountInboxDirectory(account);
        MailFile mailFile = inboxDirectory.addFile(content, metadata , "mail" + uniqueMailNumber);
        uniqueMailNumber++;
        return mailFile;
    }

    private MailFile addMailFileToSent(Account account, String content, MailMetadata metadata) {
        RegularDirectory sentDirectory = getAccountSentDirectory(account);
        MailFile mailFile = sentDirectory.addFile(content, metadata, "mail" + uniqueMailNumber);
        uniqueMailNumber++;
        return mailFile;
    }
    private boolean ruleIsContradictory(Rule rule, Account account) {
        Set<Rule> alreadyDefinedRules = accountToRules.get(account);
        for (var alreadyDefinedRule : alreadyDefinedRules) {
            if (rule.equals(alreadyDefinedRule) && rule.getFolder() != alreadyDefinedRule.getFolder()) {
                return true;
            }
        }
        return false;
    }

    private Rule getRuleWithPriorityOnMailFile(MailFile mailFile, Account account) {
        Set<Rule> accountRules = accountToRules.get(account);
        for ( var rule : accountRules) {
            if (ruleIsApplicableOnMailFile(rule, mailFile)) {
                return rule;
            }
        }
        return null;
    }


    private AbstractDirectory getParentDirectory(Account account, String path) {
        String[] names = path.split(File.separator);
        AbstractDirectory currentDir = accountToRootDirectory.get(account);
        int layersCount = names.length;
        for (int layerIndex = 0; layerIndex < layersCount - 2; ++layerIndex) {
            if (!names[layerIndex].equals(currentDir.getName())) {
                throw new InvalidPathException("missing intermediate folders");
            }
            boolean intermediateFound = false;
            Set<RegularDirectory> directories = currentDir.getRegularDirectoriesInside();
            for (var directory : directories) {
                if (directory.getName().equals(names[layerIndex + 1])) {
                    intermediateFound = true;
                    currentDir = directory;
                    break;
                }
            }
            if (!intermediateFound) {
                throw new InvalidPathException("missing intermediate folders");
            }

        }
        return currentDir;

    }

    private RegularDirectory getDirectory(Account accountName, String path) {
        AbstractDirectory parentDir = getParentDirectory(accountName, path);

        Set<RegularDirectory> directories = parentDir.getRegularDirectoriesInside();
        for ( var directory : directories) {
            if (directory.getAbsolutePath().equals(path)) {
                return directory;
            }
        }
        return null;

    }



    private boolean ruleIsApplicableOnMailFile(Rule rule, MailFile file) {
        Set<String> ruleRecipients = rule.getMetadata().recipientEmails();
        Set<String> mailRecipients = file.getMetadata().recipientEmails();
        String ruleSenderEmail = rule.getMetadata().senderEmail();
        String mailSenderEmail = file.getMetadata().sender();
        Set<String> ruleSubjectWords = rule.getMetadata().subjectWords();
        Set<String> ruleBodyOrSubjectWords = rule.getMetadata().bodyWords();
        String mailContent = file.getContent();
        String mailSubject = file.getMetadata().subject();
        if (ruleBodyOrSubjectWords != null && !ruleBodyOrSubjectWords.isEmpty()) {
            for (var word : ruleBodyOrSubjectWords) {
                if (!mailContent.contains(word) && (mailSubject == null || !mailSubject.contains(word))) {
                    return false;
                }
            }
        }
        if (ruleSubjectWords != null && !ruleSubjectWords.isEmpty()) {
            if (mailSubject == null) {
                return false;
            }
            for (var word : ruleSubjectWords) {
                if (!mailSubject.contains(word)) {
                    return false;
                }
            }
        }
        if (ruleSenderEmail != null) {
            if (mailSenderEmail == null || !mailSenderEmail.equals(ruleSenderEmail)) {
                return false;
            }
        }
        if (ruleRecipients != null && !ruleRecipients.isEmpty()) {
            if (mailRecipients == null) {
                return false;
            }
            for (var mailRecipient : mailRecipients) {
                if (ruleRecipients.contains(mailRecipient)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private RegularDirectory getAccountInboxDirectory(Account account) {
        RootDirectory userRoot = accountToRootDirectory.get(account);
        Set<RegularDirectory> regularDirectories = userRoot.getRegularDirectoriesInside();
        for (var dir : regularDirectories) {
            if (dir.getName().equals("inbox")) {
                return dir;
            }
        }

        throw new IllegalStateException("inbox folder not found");
    }

    private RegularDirectory getAccountSentDirectory(Account account) {
        RootDirectory userRoot = accountToRootDirectory.get(account);
        Set<RegularDirectory> regularDirectories = userRoot.getRegularDirectoriesInside();
        for (var dir : regularDirectories) {
            if (dir.getName().equals("sent")) {
                return dir;
            }
        }

        throw new IllegalStateException("sent folder not found");
    }

    private Set<MailFile> getMailFilesRuleIsApplicableOn(Rule rule, Account account) {
        Set<MailFile> files = new HashSet<>();
        RegularDirectory inboxDirectory = getAccountInboxDirectory(account);
        Set<MailFile> mailFiles = inboxDirectory.getMailFilesInside();
        for (var mailFile : mailFiles) {
            if (ruleIsApplicableOnMailFile(rule, mailFile)) {
                files.add(mailFile);
            }
        }
        return files;

    }

    private void moveAccordingToRule(MailFile mailFile, Rule rule) {
        AbstractDirectory directoryToMoveTo = rule.getFolder();
        AbstractDirectory directoryToRemoveFrom = mailFile.getParent();
        directoryToRemoveFrom.removeMailFile(mailFile);
        //In Unix moving an object creates the old one and makes a copy of the new one (hard links also die)
        directoryToMoveTo.addFile(mailFile.getContent(), mailFile.getMetadata(), mailFile.getName());
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(email)) {
            throw new IllegalArgumentException("null, empty or blank string passed");
        }
        if (emailTaken(email)) {
            throw new AccountAlreadyExistsException("account is already taken");
        }
        if (accountNameTaken(accountName)) {
            throw new AccountAlreadyExistsException("email already taken");
        }
        Account user = new Account(email, accountName);
        RootDirectory root = createStartingDirectory();
        accountToRootDirectory.put(user, root);
        Set<Rule> userRules = new TreeSet<>(new RulePriorityComparator());
        accountToRules.put(user, userRules);
        return user;
    }

    @Override
    public void createFolder(String accountName, String path) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(path)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }
        if (!accountNameTaken(accountName)) {
            throw new AccountNotFoundException("account with this name was not found");
        }
        String realPath = getUnixPath(path);
        if (realPath.equals("/inbox")) {
            throw new FolderAlreadyExistsException("inbox folder already exists");
        }
        if (!realPath.startsWith("/inbox/")) {
            throw new InvalidPathException("path for folder creation must start from root '/");
        }

        //in unix systems .../x/ == ...x still points to the folder x, also white trailing whitespaces are ignored

        Account account = getAccountByName(accountName);
        AbstractDirectory parentDirectory = getParentDirectory(account, realPath);
        Set<NestedFileObject> fileObjects = parentDirectory.getNestedFiles();
        for (var fileObject : fileObjects) {
            if (fileObject.getAbsolutePath().equals(realPath)) {
                if (fileObject.isMailFile()) {
                    throw new FolderAlreadyExistsException("a mail file exists on the given path");
                } else if (fileObject.isRegularDirectory()) {
                    throw new FolderAlreadyExistsException("directory already exists");
                }
            }
        }
        String name = realPath.substring(realPath.lastIndexOf(File.separator) + 1);
        parentDirectory.addSubDirectory(name);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(folderPath) ||
                !StringValidator.isValidString(ruleDefinition)) {
            throw new IllegalArgumentException("null, empty or blank string passed");
        }

        if (!accountNameTaken(accountName)) {
            throw new AccountNotFoundException("account not found");
        }
        Account account = getAccountByName(accountName);
        AbstractDirectory directory;
        String realFolderPath = getUnixPath(folderPath);
        try {
            //throws InvalidPath if intermediate are missing
            directory = getDirectory(account, realFolderPath);
        } catch (InvalidPathException e) {
            throw new FolderNotFoundException("intermediate folders do not exist", e);
        }
        if (directory == null) {
            throw new FolderNotFoundException("folder not found");
        }

        RuleMetadata metadata = RuleDefinitionDecoder.decodeDefinition(ruleDefinition);
        Rule rule = new Rule(metadata, priority, directory);
        if (ruleIsContradictory(rule, account)) {
            throw new RuleAlreadyDefinedException("rule is contradictory");
        }
        Set<MailFile> mailFilesToMove = getMailFilesRuleIsApplicableOn(rule, account);
        for (var mailFile : mailFilesToMove) {
            moveAccordingToRule(mailFile, rule);
        }
        accountToRules.get(account).add(rule);
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(mailMetadata) ||
            !StringValidator.isValidString(mailContent)) {
            throw new IllegalArgumentException("null,empty or blank string passed");
        }

        if (!accountNameTaken(accountName)) {
            throw new AccountNotFoundException("account with such name was not found");
        }
        Account account = getAccountByName(accountName);
        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(mailMetadata);
        MailFile mailFile = addMailFileToInbox(account, mailContent, metadata);
        Rule ruleWithPriority = getRuleWithPriorityOnMailFile(mailFile, account);

        if (ruleWithPriority != null) {
            moveAccordingToRule(mailFile, ruleWithPriority);
        }

    }

    @Override
    public Collection<Mail> getMailsFromFolder(String accountName, String folderPath) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(folderPath)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }

        if (!accountNameTaken(accountName)) {
            throw new AccountNotFoundException("account was not found");
        }
        String realPath = getUnixPath(folderPath);
        Account account = getAccountByName(accountName);
        Collection<Mail> mails = new LinkedList<>();
        AbstractDirectory directory;
        try {
            directory = getDirectory(account, realPath);
        } catch (InvalidPathException e) {
            throw new FolderNotFoundException("intermediate folders to given path not found", e);
        }
        if (directory == null) {
            throw new FolderNotFoundException("folder not found");
        }
        Set<MailFile> mailFilesInDirectory = directory.getMailFilesInside();

        for (var mailFile : mailFilesInDirectory) {
            if (!emailTaken(mailFile.getMetadata().sender())) {
                throw new AccountNotFoundException("sender of the email was never registered");
            }
            Account sender = getAccountByEmail(mailFile.getMetadata().sender());
            Mail mail = new Mail(mailFile, sender);
            mails.add(mail);
        }

        return mails;

    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if (!StringValidator.isValidString(accountName) || !StringValidator.isValidString(mailMetadata) ||
            !StringValidator.isValidString(mailContent)) {
            throw new IllegalArgumentException("null, empty or blank string passed");
        }

        if (!accountNameTaken(accountName)) {
            throw new AccountNotFoundException("account with such username not found");
        }
        Account account = getAccountByName(accountName);
        MailMetadata metadata = MailDefinitionDecoder.extractMetaData(mailMetadata);
        if (!metadata.sender().equals(account.emailAddress())) {
            throw new AccountNotFoundException("real sender differs from metadata sender");
        }
        addMailFileToSent(account, mailContent, metadata);
        Set<String> recipientEmails = metadata.recipientEmails();
        for (var email :recipientEmails) {
            if (emailTaken(email)) {
                String recipientAccountName = getAccountByEmail(email).name();
                this.receiveMail(recipientAccountName, mailMetadata, mailContent);
            }
        }
    }
}
