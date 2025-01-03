package ObjectPage.components;

import ObjectPage.HomePage;
import ObjectPage.nohu.NohuPage;
import ObjectPage.vic.VicClub2Page;

public class ComponentsFactory {

    public static HomePage homePageObject(String appName) {
        switch (appName){
            case "NOHU":
                return new NohuPage();
            default:
                return new VicClub2Page();
        }
    }
}
