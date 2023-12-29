package test;

import data.DataHelper;
import data.SQLHelper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {

    @AfterAll
    static void cleanDB () {
        SQLHelper.cleanDatabase();
    }

    static String token;
    static Integer firstCardBalance;
    static Integer secondCardBalance;

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    @Order(1)
    @Test
    public void requestForAuth() {
        var user = DataHelper.getUser();
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    @Order(2)
    @Test
    public void requestForVerification() {
        var verificationCode = SQLHelper.getVerificationCode();
        var verification = DataHelper.getVerification(verificationCode);
        token =
                given()
                        .spec(requestSpec)
                        .body(verification)
                        .when()
                        .post("/api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");
        assertThat(token, equalTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY"));


    }

    @Test
    public void requestForViewingCard() {
        requestForVerification();
        Response response =
                given()
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .body("[0].id", equalTo("0f3f5c2a-249e-4c3d-8287-09f7a039391d"))
                        .body("[1].id", equalTo("92df3f1c-a033-48e6-8390-206f6b1f56c0"))
                        .statusCode(200)
                        .extract().response();
        firstCardBalance = response.path("[1].balance");
        secondCardBalance = response.path("[0].balance");
    }


    @Test
    public void requestForValidTransferMoney() {
        requestForViewingCard();
        var firstCard = DataHelper.getfirstcard().getCardNumber();
        var secondCard = DataHelper.getsecondcard().getCardNumber();
        int amount = DataHelper.generateValidAmount(firstCardBalance);
        var transfer = DataHelper.getTransfer(firstCard, secondCard, amount);

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transfer)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
        int expectedBalanceFirstCard = firstCardBalance - amount;
        int expectedBalanceSecondCard = secondCardBalance + amount;
        requestForViewingCard();
        assertThat(firstCardBalance, equalTo(expectedBalanceFirstCard));
        assertThat(secondCardBalance, equalTo(expectedBalanceSecondCard));

    }


    @Test
    public void requestForInvalidTransferMoney() {
        requestForViewingCard();
        var firstCard = DataHelper.getfirstcard().getCardNumber();
        var secondCard = DataHelper.getsecondcard().getCardNumber();
        int amount = DataHelper.generateInValidAmount(firstCardBalance);
        var transfer = DataHelper.getTransfer(firstCard, secondCard, amount);

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transfer)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(400);
        int expectedBalanceFirstCard = firstCardBalance;
        int expectedBalanceSecondCard = secondCardBalance;
        requestForViewingCard();
        assertAll(() -> assertThat(firstCardBalance, equalTo(expectedBalanceFirstCard)),
                () -> assertThat(secondCardBalance, equalTo(expectedBalanceSecondCard)));

    }


}
