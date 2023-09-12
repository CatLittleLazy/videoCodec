package com.youmehe.messagesend;

import android.util.Log;

import java.io.File;
 
public class SendMailUtil {
    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
    private static final String FROM_ADD = "877198316@qq.com";
    private static final String FROM_PSW = "kycwngksgdycbdjg";
 
//    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "25"; //或者465  994
//
    //private static final String HOST = "pop.163.com";
    //private static final String PORT = "995"; //或者465  994
//    private static final String FROM_ADD = "changyiqiang666@163.com";
//    private static final String FROM_PSW = "qwertyuiop123456";//qwertyuiop123456
//    private static final String TO_ADD = "2584770373@qq.com";
 
 
    public static void send(final File file, String toAdd, String content){
        final MailInfo mailInfo = creatMail(toAdd, content);
        final MailSender sms = new MailSender();
        new Thread(() -> sms.sendFileMail(mailInfo,file)).start();
    }
 
 
    public static void send(String toAdd, String content){
        final MailInfo mailInfo = creatMail(toAdd, content);
        final MailSender sms = new MailSender();
        new Thread(() -> {
            boolean send = sms.sendTextMail(mailInfo);
            Log.e("wyt", "is send " + send);
        }).start();
    }
 
    private static MailInfo creatMail(String toAdd, String content) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);// 发送邮件的服务器的IP
        mailInfo.setMailServerPort(PORT);// 发送邮件的服务器的端口
        mailInfo.setValidate(true);// 是否需要身份验证
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址 // 登陆邮件发送服务器的用户名
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码 登陆邮件发送服务器的密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱 // 邮件发送者的地址
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        mailInfo.setSubject("Hello"); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }
}