
package Server;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import com.sun.mail.smtp.SMTPAddressFailedException;

/**
 * The SendEmail class provides a method to send an email using Gmail's SMTP server.
 */
public class SendEmail {

    /**
     * Sends an email with the specified recipient, subject, and message.
     *
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param message the body of the email
     * @return true if the email was sent successfully, false otherwise
     */
    public static boolean sendEmail(String toEmail, String subject, String message) {
        // Gmail SMTP server credentials
        String host = "smtp.gmail.com";
        String port = "465"; // SSL port
        String user = "blibgroup77@gmail.com";  // Replace with your email address
        String password = "mrwd feyg rimj wlhv";  // Use your App Password here

        // Set the SMTP properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", port);

        // Log the properties for debugging
        System.out.println("SMTP Host: " + properties.getProperty("mail.smtp.host"));
        System.out.println("SMTP Port: " + properties.getProperty("mail.smtp.port"));

        // Get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // Create the MimeMessage
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);
            msg.setText(message);

            // Send the email
            Transport.send(msg);
            System.out.println("Email sent successfully!");
            return true;

        } catch (SendFailedException e) {
            // Check if the error is due to an invalid address
            if (e.getNextException() instanceof SMTPAddressFailedException) {
                System.out.println("Invalid email address: " + toEmail);
                // Return false without propagating the exception
                return false;
            } else {
                System.out.println("Failed to send email: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (MessagingException e) {
            // Handle other messaging exceptions
            System.out.println("An error occurred while sending the email.");
            e.printStackTrace();
            return false;
        }
    }
}
