package clientServer;

import models.Nutzer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

public class mailer {

    public static void send2faMail(String recipient, Integer code) {
        String text =
                "Hallo du Filmliebhaber!\n\n" +
                        "Willkommen, willkommen, bei unserer neuen Filmdatenbank.\n\n" +
                        "Damit dein Account sicher ist, kannst du dich nur mit der zwei-Faktor-Authentifizierung anmelden.\n" +
                        "Dein Authentifizierungscode lautet: " + code.toString() + "\n\n" +
                        "Viel spaß in unserer Welt der Filme!";
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(recipient);
        sendMail(text, recipients, "Your 2FA Code");
    }

    public static void sendNewReportNotification(ArrayList<String> recipients) {
        String text = "Lieber Filmdatenbankadministrator,\n\n" +
                "leider wurde ein neuer Fehlerbericht zu einem Film erstellt.\n" +
                "Bitte prüfe diesen Zeitnah.\n\n" +
                "Vielen Dank\n" +
                "Deine lieblings Filmdatenbank";
        sendMail(text, recipients, "Neuer Fehler Bericht");
    }

    public static void sendNewInvitationNotification(Nutzer recipient, Nutzer nutzer, String filmName,
                                                     String datum, String uhrzeit, String nachricht) throws Exception {
        String text =  "Hallo du Filmliebhaber:in! \n \n" +
                "Du wurdest von " + nutzer.getName() + " " + nutzer.getSurname() +" zu einem Film eingeladen \n" +
                "Filmname: " + filmName + "\n" +
                "Datum: " + datum + "\n" +
                "Uhrzeit: " + uhrzeit + "\n" +
                "Nachricht: " + nachricht + "\n" +
                "Bitte melde dich im System an, um mit der Einladung zu interagieren!";


        String email = recipient.getMail();
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(email);
        System.out.println(text);
        System.out.println(email);
        DB.addMessage(recipient.getId(), text);
        sendMail(text, recipients, "Neue Filmeinladung");
    }

    public static void sendNewInvitationDeclinedNotification(Nutzer recipient, Nutzer nutzer) throws Exception {
        String text = "Hallo du Filmliebhaber:in \n \n" +
                "Wir müssen dir leider mitteilen, dass deine Einladung an " +  nutzer.getName() + " " + nutzer.getSurname() + " leider abgelehnt wurde \n" +
                "Vielleicht klappt es beim nächsten mal!";

        String email = recipient.getMail();
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(email);
        System.out.println(text);
        System.out.println(email);
        DB.addMessage(recipient.getId(), text);

        sendMail(text, recipients, "Einladung abgelehnt!");
    }

    public static void sendNewInvitationAcceptedNotification(Nutzer recipient, Nutzer nutzer) throws Exception{
        String text = "Hallo du Filmliebhaber:in \n \n" +
                "Wir freuen uns dir mitteilen zu können, dass deine Einladung an " +  nutzer.getName() + " " + nutzer.getSurname() + " angenommen wurde \n" ;

        String email = recipient.getMail();
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(email);
        System.out.println(text);
        System.out.println(email);
        DB.addMessage(recipient.getId(), text);
        sendMail(text, recipients, "Einladung angenommen!");
    }

    private static void sendMail(String text, ArrayList<String> recipients, String subject) {
        String mail = "info-filmdatenbank@gmx.de";
        String password = "info-filmdatenbank@gmx.de";
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.gmx.net");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("info-filmdatenbank@gmx.de", "info-filmdatenbank@gmx.de");
                    }
                });
        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(mail));
            for (String recipient : recipients)
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            message.setSubject(subject);

            message.setText(text);

            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
