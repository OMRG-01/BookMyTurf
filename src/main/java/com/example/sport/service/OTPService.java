package com.example.sport.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OTPService {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRY = 5 * 60 * 1000; // 5 minutes

    public String generateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp + "|" + (System.currentTimeMillis() + OTP_EXPIRY));
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        String stored = otpStore.get(email);
        if (stored == null) return false;
        
        String[] parts = stored.split("\\|");
        if (parts.length != 2) return false;
        
        if (System.currentTimeMillis() > Long.parseLong(parts[1])) {
            otpStore.remove(email);
            return false;
        }
        
        return parts[0].equals(otp);
    }
}