package ru.netology.BDD.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.BDD.page.DashboardPage;
import ru.netology.BDD.page.LoginPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.BDD.data.DataHelper.*;

public class TransferTest {

    DashboardPage dashboardPage;

    @BeforeEach
    void setUp() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(secondCardBalance);
        var expectedBalanceSecondCard = secondCardBalance - amount;
        var expectedBalanceFirstCard = firstCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);

    }

//    @Test
//    void shouldGetErrorMessageIfAmountMoreBalance() {
//        var firstCardInfo = getFirstCardInfo();
//        var secondCardInfo = getSecondCardInfo();
//        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
//        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
//        var amount = generateInvalidAmount(firstCardBalance);
//        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
//        transferPage.makeTransfer(String.valueOf(amount), firstCardInfo);
//        transferPage.findErrorMessage("Сумма пополнения превышает остаток на карте списания");
//        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
//        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
//        assertEquals(firstCardBalance, actualBalanceFirstCard);
//        assertEquals(secondCardBalance, actualBalanceSecondCard);
//
//    }

    @Test
    void shouldGetErrorMessageIfInvalidCard() { // некорректно указана карта-источник

        $("[data-test-id='action-deposit']").click();
        $("[data-test-id='amount'] input").setValue("5000");
        $("[data-test-id='from'] input").setValue("5559000000000003");
        $("[data-test-id='action-transfer']").click();
        $("[data-test-id='error-notification']").shouldHave(Condition.exactText("Ошибка\n" +
                "Ошибка! Произошла ошибка"), Duration.ofSeconds(3));
    }

}
