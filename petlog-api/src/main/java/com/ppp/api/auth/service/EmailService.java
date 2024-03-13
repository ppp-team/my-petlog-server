package com.ppp.api.auth.service;

import com.ppp.api.auth.exception.AuthException;
import com.ppp.api.auth.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "mypetlog.auth@gmail.com";

    public int createNumber() {
        return (int)(Math.random() * (90000)) + 100000;
    }

    public MimeMessage createEmailForm(String mail, int number){
        MimeMessage message = javaMailSender.createMimeMessage();
        String title = "[마이펫로그] 회원가입 인증 코드를 확인해 주세요";
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject(title);
            String body =
                    "<div style=\"letter-spacing: -0.025em; padding: 28px; max-width: 374px; margin: 16px auto; border: 1px solid rgba(0,0,0,.06); border-radius: 8px;\">\n" +
                    "\n" +
                    "    <div style=\"letter-spacing: -0.025em; padding: 13px 0; height: 38px;\">\n" +
                    "    </div>\n" +
                    "<div style=\"letter-spacing: -0.025em; box-sizing: border-box; display: block; Margin: 0 auto;\">\n" +
                    "    <h1 style=\"letter-spacing: -0.025em; margin-bottom: 30px; width: 100%; font-size: 28px; line-height: 38px; color: #1D1D1D; font-weight: bold; margin: 16px 20px 0 0; white-space: pre-line; word-break: break-word; padding-bottom: 24px;\">이메일 인증 코드를\n" +
                    "확인해 주세요</h1>\n" +
                    "    <div style=\"letter-spacing: -0.025em; color: #545454; font-size: 15px; line-height: 24px;\">\n" +
                    "        <p style=\"letter-spacing: -0.025em; font-size: 15px; font-weight: 400; font-style: normal; margin: 0; padding-bottom: 8px; color: #555555; white-space: pre-line;\">아래 인증 코드를 회원가입 페이지에 입력해 주세요</p>\n" +
                    "        <div style=\"letter-spacing: -0.025em; padding: 40px 0 20px 0;\">\n" +
                    "            <section style=\"letter-spacing: -0.025em; background: rgba(153, 153, 153, 0.15); border-radius: 8px; font-weight: 500; font-size: 24px; line-height: 30px; text-align: center; padding: 16px 32px;\">"+number+"</section>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "    <div style=\"letter-spacing: -0.025em; margin: 0; padding: 0; word-break: break-word;\">\n" +
                    "        <ul style=\"letter-spacing: -0.025em; font-size: 15px; font-weight: 400; font-style: normal; padding: 8px 0 4px 20px; margin: 0;\"><li style=\"letter-spacing: -0.025em; margin-left: 5px; line-height: 20px; margin: 0 0 4px 0; list-style-position: outside; color: #999999; font-weight: 400; font-size: 13px;\">인증 코드는 10분 동안 유효합니다</li><li style=\"letter-spacing: -0.025em; margin-left: 5px; line-height: 20px; margin: 0 0 4px 0; list-style-position: outside; color: #999999; font-weight: 400; font-size: 13px;\">본 메일은 발신 전용입니다</li></ul>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "</div>";

            message.setText(body,"UTF-8", "html");
        } catch (Exception e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}", mail);
            throw new AuthException(ErrorCode.SEND_EMAIL_FAILURE);
        }

        return message;
    }

    public int sendEmailCode(String email) {
        int code = createNumber();
        MimeMessage emailForm = createEmailForm(email, code);
        javaMailSender.send(emailForm);
        return code;
    }


    public void sendEmail(MimeMessage message) {
        javaMailSender.send(message);
    }
}
