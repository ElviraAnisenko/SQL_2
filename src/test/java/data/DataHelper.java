package data;


import lombok.Value;

import java.util.Random;


public class DataHelper {
    private DataHelper() {
    }


    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getUser() {
        return new AuthInfo("vasya", "qwerty123");
    }


    @Value
    public static class VerificationCode {
        String login;
        String code;
    }

    public static VerificationCode getVerification(String code) {
        return new VerificationCode("vasya", code);
    }

    @Value
    public static class Transfer {
        String from;
        String to;
        Integer amount;
    }

    public static Transfer getTransfer(String from, String to, int amount) {
        return new Transfer(from, to, amount);
    }

    public static int generateValidAmount(int balance) {
        return new Random().nextInt(Math.abs(balance)) + 1;
    }

    public static int generateInValidAmount(int balance) {
        return Math.abs(balance) + new Random().nextInt(10000);
    }

    public static Card getfirstcard() {
        return new Card("92df3f1c-a033-48e6-8390-206f6b1f56c0", "5559 0000 0000 0001");
    }

    public static Card getsecondcard() {
        return new Card("0f3f5c2a-249e-4c3d-8287-09f7a039391d", "5559 0000 0000 0002");
    }

    @Value
    public static class Card {
        String id;
        String cardNumber;

    }

    public static StatusAndUrl getLogin() {
        return new StatusAndUrl("/api/auth", 200);
    }

    public static StatusAndUrl getVerify() {
        return new StatusAndUrl("/api/auth/verification", 200);
    }

    public static StatusAndUrl getViewCard() {
        return new StatusAndUrl("/api/cards", 200);
    }


    @Value
    public static class StatusAndUrl {
        String endPoint;
        int status;
    }

}
