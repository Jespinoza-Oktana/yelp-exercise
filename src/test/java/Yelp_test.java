import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.openqa.selenium.By;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import static org.testng.Assert.assertEquals;

public class Yelp_test extends Base {
    @BeforeTest
    public void initialize(){
        driver= initializeDriver();
        driver.get("https://www.yelp.com/");
    }

    @Test
    @Description("Open the yelp page")
    public void A_yelp_page_appears() throws InterruptedException{
        String title = driver.getTitle();
        assertEquals(title, "Restaurants, Dentists, Bars, Beauty Salons, Doctors - Yelp");
    }

    @Test
    @Description("Verify that the restaurant option exists")
    public void B_Click_on_Restaurant() throws InterruptedException{
        driver.findElement(By.xpath("//*[@id='header_find_form']/div/div[1]/div/label/div")).click();
        String title = driver.findElement(By.xpath("//*[@id=\"header_find_form\"]/div/div[1]/div/div/ul/li[1]")).getText();
        assertEquals(title, "Restaurants");
    }

    @Test
    @Description("Verify the list of restaurants")
    public void C_First_List_restaurant() throws InterruptedException{
        driver.findElement(By.xpath("//*[@id='header_find_form']/div/div[1]/div/label/div")).click();
        driver.findElement(By.xpath("//*[@id=\"header_find_form\"]/div/div[1]/div/div/ul/li[1]")).click();
        String List_title = driver.getTitle();
        assertEquals(List_title, "Top 10 Best Restaurants in San Francisco, CA - Last Updated April 2021 - Yelp");
    }

    @Test
    @Description("Verify the search button refresh the page")
    public void D_Click_on_Search() throws InterruptedException{
        driver.findElement(By.cssSelector("button[value='submit']")).click();
    }

    @Test
    @Description("Search for pizza restaurants")
    public void E_Search_Restaurant_pizza() throws InterruptedException{
        driver.findElement(By.cssSelector("input[value='Restaurants']")).clear();
        driver.findElement(By.cssSelector("input[value='Restaurants']")).sendKeys(" Pizza");
        this.D_Click_on_Search();
        Thread.sleep(3000);
        String Pizzamessage;
        Pizzamessage=driver.findElement(By.xpath("/html/body/yelp-react-root/div[1]/div[4]/div/div[1]/div[1]/div[2]/div/ul/li[1]/div/div/div[1]/h1/span")).getText();
        assertEquals(Pizzamessage, "Best Restaurants Pizza in San Francisco, CA");
        this.obtain_pizza_results();
    }

    public Integer obtain_N_results(int a, int b) throws InterruptedException{

        List<WebElement> pizzaResult = new ArrayList<WebElement>();
        List<WebElement> pizzaFinal = new ArrayList<WebElement>();
        Integer totalnum;

        for (Integer i = a; i < b; i++) {

            pizzaResult=(driver.findElements(By.xpath("//h4/span[contains(text(),"+ i+")]")));
            if (pizzaResult.size() != 0) {
                pizzaFinal.add(pizzaResult.get(0));

            }
        }
        totalnum=pizzaFinal.size();
        return totalnum;
    }

    public void obtain_pizza_results() throws InterruptedException{

        String num_page;
        Integer totalnum=0;
        Integer rsult=0;
        Integer Lastpage=0;
        Integer total_Results;
        rsult=this.obtain_N_results(1, 11);

        if (rsult == 10) {
            System.out.println("The number of results for the first page are : " + rsult);
            Allure.addAttachment("The number of results for the first page are :", String.valueOf(rsult));
            num_page =driver.findElement(By.xpath("//span[contains(text(), '1 of')]")).getText();
            totalnum=(Integer.parseInt(num_page.substring(5)) -1)*10;
            String url = driver.getCurrentUrl();
            String page = totalnum.toString();
            driver.navigate().to(url +"&start="+ page);
            Lastpage=this.obtain_N_results(totalnum+1, totalnum+11);
            total_Results= totalnum+Lastpage;
            System.out.println("The total number of results are : " + total_Results);
            Allure.addAttachment("The total number of results are :", String.valueOf(total_Results));

        } else {
            System.out.println("The total number of results are : " + rsult);
            Allure.addAttachment("The total number of results are :", String.valueOf(rsult));
        }

        driver.navigate().back();
        Thread.sleep(5000);

    }
    public String fill_price(int price)
    {
        String price_selector;
        if(price ==1) {
            price_selector="'$'";
        }else if(price ==2){
            price_selector="'$$'";
        }else if(price ==3){
            price_selector="'$$$'";
        }else if(price ==4){
            price_selector="'$$$$'";
        }else {
            price_selector="None";
        }
        return price_selector;
    }

    public void number_stars () throws InterruptedException {
        String[][] Stars = new String[10][2];

        List<WebElement> Result = new ArrayList<WebElement>();
        List<WebElement> Restaurant = new ArrayList<WebElement>();
        List<WebElement> Rating =driver.findElements(By.xpath("//div[contains(@aria-label, 'star rating')]"));

        for (Integer i = 1; i < 11; i++) {


            Result=(driver.findElements(By.xpath("//h4/span[contains(text(),"+ i+")]")));
            if (Result.size() != 0) {
                Restaurant.add(Result.get(0));
            }
            Stars[i-1][0] =Restaurant.get(i-1).getText();
            Stars[i-1][1] =Rating.get(i+1).getAttribute("aria-label");
        }
        System.out.printf("\n");
        System.out.printf("%-50s %s\n", "Restaurant", "Star rating");
        for (Integer i = 0; i < 10; i++) {
            System.out.printf("%-50s %s\n", Stars[i][0], Stars[i][1]);
            Allure.addAttachment("The number " +(i+1)+ " restaurant and rating", Stars[i][0] + " and the star rating: " + Stars[i][1]);
        }




    }
    public void filter_reload()
    {
        String filtermessage;
        filtermessage=driver.findElement(By.xpath("/html/body/yelp-react-root/div[1]/div[4]/div/div[1]/div[1]/div[1]/div/p")).getText();
        assertEquals(filtermessage, "2 filters");

    }
    @Test(dataProvider = "filter2 options", dataProviderClass = DataProviderFilter.class)
    @Description("Select 2 filter options, the parameters are price and category")
    public void F_filter_options(int price,String suggest) throws InterruptedException{
        String money = this.fill_price(price);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[contains(text(), "+ money+")]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//a[contains(text(), 'See all')]")).click();
        driver.findElement(By.cssSelector("input[value= "+ suggest+"]")).click();
        driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();
        Thread.sleep(2000);
        this.obtain_pizza_results();
        this.number_stars();
        this.filter_reload();
        driver.findElement(By.xpath("//span[contains(text(), 'Clear all')]")).click();
        Thread.sleep(2000);

    }
    @Test(priority = 5)
    @Description("Open the restaurant that appears")
    public void G_First_Result() throws InterruptedException{
        driver.get("https://www.yelp.com/search?find_desc=Restaurants%20Pizza&find_loc=San%20Francisco%2C%20CA&attrs=RestaurantsPriceRange2.2&cflt=pizza");
        List<WebElement> Result = new ArrayList<WebElement>();
        String first_rest;
        Result=driver.findElements(By.xpath("//h4/span[contains(text(),'1')]"));
        first_rest= Result.get(0).getText().substring(3);
        driver.findElement(By.xpath("//h4/span[contains(text(), '1')]")).click();
        String filtermessage;
        filtermessage=driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[3]/yelp-react-root/div/div[2]/div[1]/div[1]/div/div/div[1]/h1")).getText();
        assertEquals(filtermessage, first_rest);
        String address = driver.findElement(By.xpath("//span[contains(text(), '3611 18th St')]")).getText();
        String p_number = driver.findElement(By.xpath("//p[contains(text(), '(415)')]")).getText();
        String page_url = driver.findElement(By.linkText("pizzeriadelfina.com")).getText();

        //print and add restaurant name
        System.out.println("The name of the restaurant is : " + first_rest);
        Allure.addAttachment("The name of the restaurant is", first_rest);

        //print and add restaurant address
        System.out.println("The address of the restaurant is : " + address);
        Allure.addAttachment("The address of the restaurant is", address);

        //print and add restaurant phone number
        System.out.println("The phone number of the restaurant is : " + p_number);
        Allure.addAttachment("The phone number of the restaurant is", p_number);

        //print and add restaurant main webpage name
        System.out.println("The webpage of the restaurant is : " + page_url);
        Allure.addAttachment("The webpage of the restaurant is", page_url);
    }

    @AfterTest
    public void closeDriver(){
        driver.close();
    }
}
