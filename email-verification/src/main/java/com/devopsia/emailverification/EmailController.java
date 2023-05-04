package com.devopsia.emailverification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/")
    public String showEmailForm() {
        return "emailForm";
    }
    
    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam("email") String email, Model model) {
        boolean result = emailService.sendVerificationEmail(email);
        if(result) {
            model.addAttribute("email", email);
            return "otpForm";
        } else {
            return "emailForm";
        }
    }
    
    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("otp") String otp, @RequestParam("email") String email, Model model) {
        boolean result = emailService.verifyEmail(otp, email);
        if(result) {
            return "verificationSuccess";
        } else {
            model.addAttribute("email", email);
            return "verificationError";
        }
    }

}