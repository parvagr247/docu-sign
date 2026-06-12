package com.docu_sign.component;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {
    public String buildSignatureRequestEmail( String recipientName,  String documentName, String signingUrl ) {

        return """
                <html>
                    <body>
                        <h2>Document Signature Request</h2>

                        <p>Hello %s,</p>

                        <p>
                            A document requires your signature.
                        </p>

                        <p>
                            <strong>Document:</strong> %s
                        </p>

                        <p>
                            Click the link below to review and sign:
                        </p>

                        <p>
                            <a href="%s"
                               style="
                               background-color:#2563eb;
                               color:white;
                               padding:12px 20px;
                               text-decoration:none;
                               border-radius:6px;
                               display:inline-block;">
                                Review & Sign Document
                            </a>
                        </p>

                        <br>

                        <p>
                             If the button doesn't work, copy and paste the following URL:
                         </p>
    
                         <p>%s</p>
    
                         <br>
    
                         <p>
                             Thank you.
                         </p>
                    </body>
                </html>
                """
                .formatted(
                        recipientName,
                        documentName,
                        signingUrl,
                        signingUrl
                );
    }
}