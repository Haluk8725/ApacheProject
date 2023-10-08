package com.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.*;

public class deneme {

    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.ebay.com");


        try {
            FileInputStream excelFile = new FileInputStream("C:\\Users\\haluk\\Downloads\\OEM_Title_Samples.xlsx");
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0); // İlgili sayfayı seçin

            // 2. sütundaki OEM numaralarını alın
            List<String> oemNumbers = readOEMNumbersFromExcel(sheet, 1); // 2. sütun (sıfır tabanlı dizin 1)
            List<String>minPrice=new ArrayList<>();
            List<String>freeShipMinPrice=new ArrayList<>();
            List<String>url=new ArrayList<>();
            List<Double> ortalama= new ArrayList<>();

            // OEM numaralarını yazdırın veya başka bir işlem yapın
            for (String oemNumber : oemNumbers) {
                System.out.println("OEM Numarası: " + oemNumber);
                WebElement searchBox = driver.findElement(By.id("gh-ac"));
                searchBox.clear();
                searchBox.sendKeys("\""+oemNumber+"\"");
                searchBox.submit();
                Thread.sleep(4000);

                WebElement button = driver.findElement(By.xpath("//button[@aria-label='Sort selector. Best Match selected.']"));
                button.click();
                WebElement lowestButton = driver.findElement(By.xpath("//span[normalize-space()='Price + Shipping: lowest first']"));
                lowestButton.click();
                Thread.sleep(1500);
                WebElement newItem=driver.findElement(By.xpath("//input[@aria-label='New']"));
                newItem.click();
                Thread.sleep(1500);

                int index1 = 2;
                while (true) {

                    try {

                       String fShipProductPrice = driver.findElement(By.xpath("//ul[@class='srp-results srp-list clearfix']/li[" + index1 + "]//span[@class=\"s-item__price\"]")).getText();
                       freeShipMinPrice.add(fShipProductPrice);
                       break;}
                       catch (Exception a) {
                        index1++;
                    }
                    }


                WebElement checkBox= driver.findElement(By.xpath("//input[@aria-label='Free Shipping']"));
                checkBox.click();
                Thread.sleep(1500);


                int index = 2;
                while (true) {

                    try {
                        int i = 0;
                        // WebElement lowestPriceProduct = driver.findElement(By.cssSelector(".s-item"));
                        List<WebElement> prices = driver.findElements(By.xpath("//ul[@class='srp-results srp-list clearfix']/li//span[@class=\"s-item__price\"]"));
                        // String productTitle = lowestPriceProduct.findElement(By.cssSelector(".s-item__title")).getText();
                        Thread.sleep(500);
                        String productPrice = driver.findElement(By.xpath("//ul[@class='srp-results srp-list clearfix']/li[" + index + "]//span[@class=\"s-item__price\"]")).getText();
                        String productUrl = driver.findElement(By.xpath("//ul[@class='srp-results srp-list clearfix']/li[" + index + "]//a")).getAttribute("href");
                        if (i == 1) break;
                        System.out.println("Ürün Fiyatı: " + productPrice);
                        minPrice.add(productPrice);
                        i++;
                        String productShipping="Free shipping";

                        System.out.println("Ortalama: " + ortalama(createList(prices)));
                        ortalama.add(ortalama(createList(prices)));
                        System.out.println("Ürün URL'si: " + productUrl);
                        url.add(productUrl);
                        break;
                    } catch (Exception e) {
                        index++;
                    }

                }
                // Sonuçları yazdırın
                //  System.out.println("Ürün Adı: " + productTitle);

                WebElement ebay = driver.findElement(By.xpath("//a[@id='gh-la']"));
                ebay.click();
            }
            writeToExcel(oemNumbers,freeShipMinPrice,url,minPrice,ortalama);

            // Workbook ve FileInputStream'i kapatın
            workbook.close();
            excelFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Excel dosyasından sütundaki OEM numaralarını alın
    private static List<String> readOEMNumbersFromExcel(Sheet sheet, int columnIndex) {
        List<String> oemNumbers = new ArrayList<>();
        int rowIndex = 0;

        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            rowIndex++;
            if (rowIndex >= 2) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    oemNumbers.add(cell.toString());
                }
            }
        }

        return oemNumbers;
    }

    private static double ortalama(List<Double> list) {
        double ort = 0;

        Collections.sort(list);
        if (list.size() >= 4) {
            for (int i = 0; i < 4; i++) {
                ort += list.get(i);

            }
            return ort / 4;
        } else {
            for (Double aDouble : list) {
                ort += aDouble;
            }
            return ort / list.size();
        }

    }

    private static List<Double> createList(List<WebElement> prices) {
        List<Double> priceList = new ArrayList<>();
        for (WebElement price : prices) {

            priceList.add(Double.parseDouble(price.getText().substring(1).replaceAll(",", "")));
        }
        return priceList;
    }

    public static void writeToExcel(List<String> oemIds,List<String>freeShipP, List<String> urls, List<String> lowestPrices, List<Double> averagePrices) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ebay Veriler");

        // Başlık satırını oluşturun
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("OEM ID");
        headerRow.createCell(4).setCellValue("URL");
        headerRow.createCell(2).setCellValue("En Düşük Fiyat");
        headerRow.createCell(3).setCellValue("Ortalama Fiyat");
        headerRow.createCell(1).setCellValue("Kargo Ücreti Dahil Olmayan");

        // Verileri hücrelere yazın
        for (int i = 0; i < oemIds.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(oemIds.get(i));
            row.createCell(4).setCellValue(urls.get(i));
            row.createCell(2).setCellValue(lowestPrices.get(i));
            row.createCell(3).setCellValue(averagePrices.get(i));
            row.createCell(1).setCellValue(freeShipP.get(i));
        }

        try (FileOutputStream outputStream = new FileOutputStream("Ebay Veriler.xlsx")) {
            workbook.write(outputStream);
            System.out.println("Excel dosyası oluşturuldu.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

