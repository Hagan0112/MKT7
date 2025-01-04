package ObjectPage.nohu;

import ObjectPage.HomePage;
import common.Contants;
import element.common.Button;
import element.common.Image;
import element.common.TextBox;
import org.testng.Assert;

import java.util.Random;

public class NohuPage extends HomePage {
    Image imgDK = Image.xpath("//div[contains(@class,'btndk')]//img");
    TextBox txtUserName = TextBox.name("username");
    TextBox txtPassWord = TextBox.id("pwd");
    TextBox txtRePassWord = TextBox.name("repeat_pwd");
    Image imgChoiNgay = Image.xpath("//button[@type='submit']//img");
    @Override
    public void loginMainSite() {
        imgDK.click();
        Random random = new Random();
        String loginID = String.format("nohuwin%s",random.nextInt(5000));
        String passWord = "1234qwerA";
        txtUserName.sendKeys(loginID);
        txtPassWord.sendKeys(passWord);
        txtRePassWord.sendKeys(passWord);
        imgChoiNgay.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyRedirected() {
        Assert.assertTrue(getPageUrl().contains(Contants.URL_REDIRECT.get("NOHU")));
    }
}
