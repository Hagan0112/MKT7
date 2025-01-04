package ObjectPage.vic;

import ObjectPage.HomePage;
import common.Contants;
import element.common.Button;
import element.common.Image;
import element.common.TextBox;
import org.testng.Assert;

import java.util.Random;


public class VicClub2Page extends HomePage {
    TextBox txtLoginID = TextBox.id("usrname");
    TextBox txtPassword = TextBox.name("password");
    Button btnDangKy = Button.xpath("//button[contains(@class,'btnsubmit')]");
    Image btnCNBW = Image.xpath("//div[contains(@class,'btn1111')]//img");
    @Override
    public void loginMainSite() {
        Random random = new Random();
        String loginID = String.format("vic2club%s",random.nextInt(5000));
        String passWord = "1234qwerA";
        txtLoginID.sendKeys(loginID);
        txtPassword.sendKeys(passWord);
        btnDangKy.click();
        System.out.println(String.format("Dang ky voi username: %s va password: %s",loginID,passWord));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyRedirected() {
        Assert.assertTrue(getPageUrl().contains(Contants.URL_REDIRECT.get("VIC")));
    }
}
