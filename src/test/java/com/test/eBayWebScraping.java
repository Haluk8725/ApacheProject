package com.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class eBayWebScraping {
    public static void main(String[] args) {
        // WebDriver'ı başlatın
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // eBay web sitesini açın
        driver.get("https://www.ebay.com");

        try {
            FileInputStream excelFile = new FileInputStream("C:\\Users\\haluk\\Downloads\\OEM_Title_Samples.xlsx");
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0); // İlgili sayfayı seçin

            // 2. sütundaki OEM numaralarını alın
            List<String> oemNumbers = readOEMNumbersFromExcel(sheet, 1); // 2. sütun (sıfır tabanlı dizin 1)

            // Yeni bir Excel dosyası oluşturun
            Workbook newWorkbook = new XSSFWorkbook();
            Sheet newSheet = newWorkbook.createSheet("eBayVerileri");

            // Excel dosyasının başlık satırını oluşturun
            Row headerRow = newSheet.createRow(0);
            Cell headerOemCell = headerRow.createCell(0);
            headerOemCell.setCellValue("OEM Numarası");
            Cell headerLowestPriceCell = headerRow.createCell(1);
            headerLowestPriceCell.setCellValue("En Düşük Ücret");
            Cell headerAveragePriceCell = headerRow.createCell(2);
            headerAveragePriceCell.setCellValue("Ortalama Ücret");
            Cell headerUrlCell = headerRow.createCell(3);
            headerUrlCell.setCellValue("URL");

            // OEM numaralarını yazdırın veya başka bir işlem yapın
            for (String oemNumber : oemNumbers) {
                System.out.println("OEM Numarası: " + oemNumber);
                WebElement searchBox = driver.findElement(By.id("gh-ac"));
                searchBox.clear();
                searchBox.sendKeys(oemNumber);
                searchBox.submit();
                Thread.sleep(4000);

                WebElement button = driver.findElement(By.xpath("//button[@aria-label='Sort selector. Best Match selected.']"));
                button.click();
                WebElement lowestButton = driver.findElement(By.xpath("//span[normalize-space()='Price + Shipping: lowest first']"));
                lowestButton.click();
                Thread.sleep(500);

                int index = 2;
                while (true) {
                    try {
                        // Listeyi temizleyin
                        List<WebElement> prices = driver.findElements(By.xpath("//ul[@class='srp-results srp-list clearfix']/li//span[@class=\"s-item__price\"]"));
                        List<Double> priceList = createList(prices);

                        // Ortalama hesaplamak için ortalama fonksiyonunu kullanın
                        double average = ortalama(priceList);

                        // Diğer verileri alın
                        String productPrice = driver.findElement(By.xpath("//ul[@class='srp-results srp-list clearfix']/li[" + index + "]//span[@class=\"s-item__price\"]")).getText();
                        String productUrl = driver.findElement(By.xpath("//ul[@class='srp-results srp-list clearfix']/li[" + index + "]//a")).getAttribute("href");

                        // Orijinal Excel dosyasına verileri yazın
                        Row dataRow = sheet.getRow(index);
                        Cell oemCell = dataRow.createCell(4); // 5. sütun (sıfır tabanlı dizin 4)
                        oemCell.setCellValue(oemNumber);
                        Cell lowestPriceCell = dataRow.createCell(5); // 6. sütun (sıfır tabanlı dizin 5)
                        lowestPriceCell.setCellValue(productPrice);
                        Cell averagePriceCell = dataRow.createCell(6); // 7. sütun (sıfır tabanlı dizin 6)
                        averagePriceCell.setCellValue(average);
                        Cell urlCell = dataRow.createCell(7); // 8. sütun (sıfır tabanlı dizin 7)
                        urlCell.setCellValue(productUrl);

                        // Yeni Excel dosyasına verileri yazın
                        Row newDataRow = newSheet.createRow(index);
                        Cell newOemCell = newDataRow.createCell(0);
                        newOemCell.setCellValue(oemNumber);
                        Cell newLowestPriceCell = newDataRow.createCell(1);
                        newLowestPriceCell.setCellValue(productPrice);
                        Cell newAveragePriceCell = newDataRow.createCell(2);
                        newAveragePriceCell.setCellValue(average);
                        Cell newUrlCell = newDataRow.createCell(3);
                        newUrlCell.setCellValue(productUrl);

                        break;
                    } catch (Exception e) {
                        index++;
                    }
                }

                // eBay ana sayfasına geri dön
                WebElement ebay = driver.findElement(By.xpath("//a[@id='gh-la']"));
                ebay.click();
            }

            // Orijinal Excel dosyasını kaydedin
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\haluk\\Downloads\\OEM_Title_Samples.xlsx");
            workbook.write(outputStream);
            outputStream.close();

            // Yeni Excel dosyasını kaydedin
            FileOutputStream newOutputStream = new FileOutputStream("EbayVerileri.xlsx");
            newWorkbook.write(newOutputStream);
            newOutputStream.close();

            // Workbook ve FileInputStream'leri kapatın
            workbook.close();
            newWorkbook.close();
            excelFile.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver'ı kapatın
            driver.quit();
        }
    }

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
}