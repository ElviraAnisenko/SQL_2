package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private APIHelper() {
    }

    @Getter
    private static String token;
    @Getter
    private static int firstCardBalance;
    @Getter
    private static int secondCardBalance;


    @Getter
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    public static void requestForAuth() {
        var user = DataHelper.getUser();
        var endPointAndStatus = DataHelper.getLogin();
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post(endPointAndStatus.getEndPoint())
                .then()
                .statusCode(endPointAndStatus.getStatus());
    }


    public static void requestForVerification() {
        var verificationCode = SQLHelper.getVerificationCode();
        var verification = DataHelper.getVerification(verificationCode);
        var endPointAndStatus = DataHelper.getVerify();
        token =
                given()
                        .spec(requestSpec)
                        .body(verification)
                        .when()
                        .post(endPointAndStatus.getEndPoint())
                        .then()
                        .statusCode(endPointAndStatus.getStatus())
                        .extract()
                        .path("token");
    }


    public static void requestForViewingCard() {
        var endPointAndStatus = DataHelper.getViewCard();
        Response response =
                given()
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get(endPointAndStatus.getEndPoint())
                        .then()
                        .statusCode(endPointAndStatus.getStatus())
                        .extract().response();
        firstCardBalance = response.path("[1].balance");
        secondCardBalance = response.path("[0].balance");
    }


}
