package test;

import ObjectPage.LandingPage;
import common.BaseCaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MainSiteTest extends BaseCaseTest {
    @Test(groups = {"smoke"})
    public void MainSiteTest_001() {
        log("@Title: Sign in Mainsite");
        log("@Step 1: Fill in Username and Password");
        log("@Step 2: Click to Dang ky");
        landingPage.homePage.loginMainSite();
        log("Verify 1: Validate redirect to game site");
        landingPage.homePage.verifyRedirected();
    }
}
