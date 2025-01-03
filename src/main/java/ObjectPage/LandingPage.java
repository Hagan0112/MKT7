package ObjectPage;

import ObjectPage.components.ComponentsFactory;

public class LandingPage extends BaseCase{
    protected String _type;
    public HomePage homePage;
    public LandingPage(String types) {
        _type = types;
        homePage = ComponentsFactory.homePageObject(_type);
    }
}
