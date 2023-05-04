package com.devopsia.emailverification;

import java.util.Random;
import java.util.Timer;
import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY = 15 * 60 * 1000; // 15 minutes
    
    private Map<String, String> otpMap = new HashMap<>();
    private Map<String, Integer> attemptsMap = new HashMap<>();
    
    @Autowired
    private JavaMailSender emailSender;
    
    public boolean sendVerificationEmail(String email) {
        // Generate OTP
        String otp = generateOTP(email);
        
        // Send email
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Email Verification");
            helper.setText("Your OTP is " + otp + ". Thanks for registering. OTP will expire in 15 minutes.");
            emailSender.send(message);
            
            // Store OTP for verification
            otpMap.put(email, otp);
            attemptsMap.put(email, 0);
            
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean verifyEmail(String otp, String email) {
        if (otpMap.containsKey(email) && otpMap.get(email).equals(otp)) {
            // Remove OTP from map
            otpMap.remove(email);
            attemptsMap.remove(email);
            return true;
        }
        otpMap.remove(email);
        attemptsMap.remove(email);
        return false;
    }
    
    private String generateOTP(String email) {
        Random random = new Random();
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        String otpString = String.format("%06d", otp);
        
        otpMap.put(otpString, email);
        // Create task to remove OTP from map
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	
                otpMap.remove(otpString);
            }
        };
      // Run task after time expires
        new Timer().schedule(task, OTP_EXPIRY);
        
        return otpString;
    }
}