package com.test;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {
    public static void main(String[] args) throws IOException, InterruptedException {


        String mainFilePath = "C:\\Users\\haluk\\Downloads\\siparisler yeni fiyatli(AutoRecovered) Son.xlsx"; // Ana Excel dosyasının yolu
        String inventoryFilePath = "C:\\Users\\haluk\\Downloads\\inventory_data (6).xlsx"; // Stok bilgileri Excel dosyasının yolu
        String outputFilePath = "updated_inventory.xlsx"; // Çıktı Excel dosyasının yolu

        try (FileInputStream mainInputStream = new FileInputStream(mainFilePath);
             FileInputStream inventoryInputStream = new FileInputStream(inventoryFilePath)) {

            Workbook mainWorkbook = WorkbookFactory.create(mainInputStream);
            Workbook inventoryWorkbook = WorkbookFactory.create(inventoryInputStream);

            Sheet mainSheet = mainWorkbook.getSheetAt(0); // Ana Excel dosyasının sayfası
            Sheet inventorySheet = inventoryWorkbook.getSheetAt(0); // Stok bilgileri Excel dosyasının sayfası

            Workbook outputWorkbook = new XSSFWorkbook();
            Sheet outputSheet = outputWorkbook.createSheet("Güncellenmiş Stoklar");

            int outputRowIndex = 0;

            for (Row mainRow : mainSheet) {
                Cell skuCell = mainRow.getCell(0); // Ana Excel'de SKU numarası sütunu
                Cell mainStock= mainRow.getCell(3);

                if (skuCell != null && skuCell.getCellType() == CellType.STRING) {
                    String sku = skuCell.getStringCellValue();




                    for (Row inventoryRow : inventorySheet) {
                        Cell inventorySkuCell = inventoryRow.getCell(0); // SKU numarası sütunu
                        Cell inventoryStockCell = inventoryRow.getCell(1); // Stok sayısı sütunu

                        if (inventorySkuCell != null && inventorySkuCell.getCellType() == CellType.STRING) {
                            String inventorySku = inventorySkuCell.getStringCellValue();


                            if (!inventorySku.startsWith("FBA")&&!(inventorySku.contains("E2"+sku))&&inventorySku.contains(sku)) {

                                Row outputRow = outputSheet.createRow(outputRowIndex++);
                                Cell outputSkuCell = outputRow.createCell(0);
                                Cell outputStockCell = outputRow.createCell(1);
                                outputSkuCell.setCellValue(inventorySku);
                                if (inventoryStockCell != null && inventoryStockCell.getCellType() == CellType.NUMERIC) {
                                    double stock = mainStock.getNumericCellValue();
                                    outputStockCell.setCellValue(stock);
                                    } else {
                                    outputStockCell.setCellValue("Stok bilgisi bulunamadı");
                                }
                              // SKU eşleşmesi bulundu, döngüyü sonlandır
                            }

                        }
                    }


                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                outputWorkbook.write(outputStream);
            }

            System.out.println("İşlem tamamlandı.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}