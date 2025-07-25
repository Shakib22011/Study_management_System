package main.utils;

public class OTPUtil {
    public static String generateOtp() {
        int otp = (int)(Math.random() * 9000) + 1000; // 4-digit random OTP
        return String.valueOf(otp);
    }
}
