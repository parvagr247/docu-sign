package com.docu_sign.service.serviceimpl;


import com.docu_sign.component.EmailTemplateBuilder;
import com.docu_sign.dto.SignatureRequestEmail;
import com.docu_sign.exception.EmailDeliveryException;
import com.docu_sign.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateBuilder emailTemplateBuilder;

    @Override
    public void sendSignatureRequestEmail(SignatureRequestEmail email){

        System.out.println("EMAIL SERVICE STARTED");

        try {

            System.out.println("EMAIL STEP 1");

            String htmlContent =
                    emailTemplateBuilder.buildSignatureRequestEmail(
                            email.recipientName(),
                            email.documentName(),
                            email.signingUrl()
                    );

            System.out.println("EMAIL STEP 2");

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper( message, true );

            System.out.println("EMAIL STEP 3");

            helper.setTo( email.recipientEmail() );
            helper.setSubject( "Signature Requested: " + email.documentName() );
            helper.setText( htmlContent, true);

            System.out.println("EMAIL STEP 4");

            mailSender.send(message);

            System.out.println("EMAIL STEP 5");

        } catch (MessagingException | MailException ex) {

            System.out.println("EMAIL STEP 6");

            throw new EmailDeliveryException("Failed to send email to "  + email.recipientEmail(), ex );
        }
    }
}
