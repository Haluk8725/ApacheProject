package com.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;
import java.util.Scanner;

public class deneme {

    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver=new ChromeDriver();
        driver.get("https://www.ebay.com");
        WebElement searchBox = driver.findElement(By.id("gh-ac"));
        searchBox.sendKeys("51974247");
        searchBox.submit();
        Thread.sleep(500);
        WebElement button = driver.findElement(By.xpath("//button[@aria-label='Sort selector. Best Match selected.']"));
        button.click();


        WebElement lowestPriceProduct = driver.findElement(By.cssSelector(".s-item"));
        String productTitle = lowestPriceProduct.findElement(By.cssSelector(".s-item__title")).getText();
        String productPrice = lowestPriceProduct.findElement(By.cssSelector(".s-item__price")).getText();
        String productUrl = lowestPriceProduct.findElement(By.cssSelector(".s-item__link")).getAttribute("href");

        // Sonuçları yazdırın
        System.out.println("Ürün Adı: " + productTitle);
        System.out.println("Ürün Fiyatı: " + productPrice);
        System.out.println("Ürün URL'si: " + productUrl);

//        Thread.sleep(2000);
//        driver.findElement(By.xpath("(//a[normalize-space()='Buyers Guide Detail'])[1]")).click();
//        WebElement table = driver.findElement(By.xpath("(//table[@class='table table-bordered table-sm'])[1]"));  // Tabloyu ID ile bulun
//        List<WebElement> rows = table.findElements(By.tagName("tr"));
//        for(WebElement row :rows){
//            List<WebElement> columns = row.findElements(By.tagName("td"));
//            for (int i = 0; i < columns.size(); i++) {
//                if(i==1){
//                    WebElement column =columns.get(i);
//                    System.out.print(column.getText()+ " ");
//                }
//                if(i==2){
//                    WebElement column =columns.get(i);
//                    List<WebElement> features=column.findElements(By.tagName("li"));
//                    for (int i1 = 1; i1 < features.size(); i1++) {
//
//                        System.out.print(features.get(i1).getText()+ " ");}
//                }
//                if(i==3){
//                    WebElement column =columns.get(i);
//                    System.out.print(column.getText()+ " ");
//                }
//                if(i==4){
//                    WebElement column =columns.get(i);
//                    System.out.print(column.getText()+ " ");
//                }
//
//
//            }System.out.println();
//        }


    }
}
