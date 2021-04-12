import org.testng.annotations.DataProvider;

public class DataProviderFilter {

    @DataProvider(name = "filter2 options")
    public Object[][] filter2ops(){
        return  new Object[][]{
                {4, "restaurants"},
                {3, "food"},
                {2, "bars"}
        };
    }
}
