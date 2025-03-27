package com.dinidu.lk.pmt.utils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class EmailHelper {
    public static MimeMultipart createEmailWithInlineImage(String htmlContent, String imagePath, String contentId) throws MessagingException {
        MimeMultipart multipart = new MimeMultipart("related");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html");
        multipart.addBodyPart(htmlPart);

        MimeBodyPart imagePart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(imagePath);
        imagePart.setDataHandler(new DataHandler(fds));
        imagePart.setHeader("Content-ID", "<" + contentId + ">");
        multipart.addBodyPart(imagePart);

        return multipart;
    }
}
