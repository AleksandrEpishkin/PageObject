package ru.netology.web.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {

    @BeforeEach
    void shouldStart() {
        open("http://localhost:9999/");
    }

    @AfterEach
    void shouldBalanceOut() {
        var authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = new DashboardPage();
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        int difference;
        if (beforeBalanceFirstCard == beforeBalanceSecondCard) {
            return;
        }
        if (beforeBalanceFirstCard > beforeBalanceSecondCard) {
            difference = beforeBalanceFirstCard - beforeBalanceSecondCard;
            difference = difference / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getFirstCardInfo(authInfo).getCardNumber(),
                    difference);
        } else {
            difference = beforeBalanceSecondCard - beforeBalanceFirstCard;
            difference = difference / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getSecondCardInfo(authInfo).getCardNumber(),
                    difference);
        }
    }

    @Test
    void shouldTransferMoneyToFirstCardFromSecondCardTest() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);

        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getSecondCardInfo(authInfo).getCardNumber(),
                1000);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));

        Assertions.assertEquals(beforeBalanceFirstCard + 1000, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard - 1000, afterBalanceSecondCard);
    }

    @Test
    void shouldTransferMoneyToSecondCardFromFirstCardTest() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);

        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(),
                1000);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));

        Assertions.assertEquals(beforeBalanceFirstCard - 1000, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard + 1000, afterBalanceSecondCard);
    }

    @Test
    void shouldTransferMoneyToFirstCardFromSecondCardHugeSumTest() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));

        transferPage.transferMoney(
                DataHelper.getSecondCardInfo(authInfo).getCardNumber(),
                20000);

        transferPage.transferPageErrorMassage();
    }

}