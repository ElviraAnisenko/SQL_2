package test;

import data.APIHelper;
import data.DataHelper;

import data.SQLHelper;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;


public class ApiTest {


    @AfterAll
    static void cleanDB () {
        SQLHelper.cleanDatabase();
    }


    @BeforeAll
     static void setUp () {
       APIHelper.requestForAuth();
       APIHelper.requestForVerification();
    }


    @Test
    public void requestForValidTransferMoney() {
        APIHelper.requestForViewingCard();
        var firstCard = DataHelper.getfirstcard().getCardNumber();
        var secondCard = DataHelper.getsecondcard().getCardNumber();
        int amount = DataHelper.generateValidAmount(APIHelper.getFirstCardBalance());
        var transfer = DataHelper.getTransfer(firstCard, secondCard, amount);
        var endPointAndUrl=DataHelper.getTransferMoney(200);
        APIHelper.requestForTransferMoney(transfer,endPointAndUrl);
        int expectedBalanceFirstCard = APIHelper.getFirstCardBalance() - amount;
        int expectedBalanceSecondCard = APIHelper.getSecondCardBalance() + amount;
        APIHelper.requestForViewingCard();
        assertThat(APIHelper.getFirstCardBalance(), equalTo(expectedBalanceFirstCard));
        assertThat(APIHelper.getSecondCardBalance(), equalTo(expectedBalanceSecondCard));

    }


    @Test
    public void requestForTransferInvalidMoney() {
        APIHelper.requestForViewingCard();
        var firstCard = DataHelper.getfirstcard().getCardNumber();
        var secondCard = DataHelper.getsecondcard().getCardNumber();
        int amount = DataHelper.generateInValidAmount(APIHelper.getFirstCardBalance());
        var transfer = DataHelper.getTransfer(firstCard, secondCard, amount);
        var endPointAndUrl=DataHelper.getTransferMoney(400);
        APIHelper.requestForTransferMoney(transfer,endPointAndUrl);
        int expectedBalanceFirstCard = APIHelper.getFirstCardBalance();
        int expectedBalanceSecondCard = APIHelper.getSecondCardBalance();
        APIHelper.requestForViewingCard();
        assertAll(() -> assertThat(APIHelper.getFirstCardBalance(), equalTo(expectedBalanceFirstCard)),
                () -> assertThat(APIHelper.getSecondCardBalance(), equalTo(expectedBalanceSecondCard)));

    }

    @Test
    public void requestForInvalidTransferMoneyToUnknownCard() {
        APIHelper.requestForViewingCard();
        var secondCard = DataHelper.getsecondcard().getCardNumber();
        int amount = DataHelper.generateValidAmount(APIHelper.getSecondCardBalance());
        var transfer = DataHelper.getTransfer(secondCard, "", amount);
        var endPointAndUrl=DataHelper.getTransferMoney(400);
        APIHelper.requestForTransferMoney(transfer,endPointAndUrl);
        int expectedBalanceFirstCard = APIHelper.getFirstCardBalance();
        int expectedBalanceSecondCard = APIHelper.getSecondCardBalance();
        APIHelper.requestForViewingCard();
        assertAll(() -> assertThat(APIHelper.getFirstCardBalance(), equalTo(expectedBalanceFirstCard)),
                () -> assertThat(APIHelper.getSecondCardBalance(), equalTo(expectedBalanceSecondCard)));

    }


}
