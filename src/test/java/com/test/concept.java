package com.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class concept {
    public static void main(String[] args) {
        try {
            // Dosya okuma
            Scanner scanner = new Scanner(System.in);
            System.out.println("Güncel Liste :  ");
            String gl= scanner.nextLine();
            System.out.println("Hedef Liste : ");
            String hl= scanner.nextLine();;
            FileInputStream tycl = new FileInputStream(new File(gl));
           FileInputStream listing = new FileInputStream(hl);
            Workbook workbook1 = new XSSFWorkbook(tycl);
            Sheet sheet1 = workbook1.getSheetAt(0);

            // Veri okuma
            for (Row row : sheet1) {
                for (Cell cell : row) {
                    System.out.print(cell.toString() + "\t");
                }
                System.out.println();
            }

//            // Dosya yazma
//            Row newRow = sheet1.createRow(sheet1.getLastRowNum() + 1);
//            Cell newCell = newRow.createCell(0);
//            newCell.setCellValue("New Data");
//
//            FileOutputStream fos = new FileOutputStream("path/to/your/excel.xlsx");
//            workbook1.write(fos);

            // Kapatma
            //fos.close();
            tycl.close();
            workbook1.close();

            System.out.println("Excel dosya işlemleri tamamlandı.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
public static void main(String[] args) {
        try {
            // Excel dosyasının yolunu belirtin
            String excelFilePath = "path/to/your/excel.xlsx";

            FileInputStream fis = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fis);

            // İşlem yapılacak sayfa ve hedef sütunun numarasını belirtin
            Sheet sheet = workbook.getSheetAt(0);
            int itemNumberCol = 1; // 2. sütun (0'dan başlayarak)
            int targetValueCol = 4; // 5. sütun (0'dan başlayarak)

            // Aranacak item numarasını belirtin
            String targetItemNumber = "ARANAN_ITEM_NUMARASI";

            // Sayfadaki tüm satırları kontrol et
            for (Row row : sheet) {
                Cell itemNumberCell = row.getCell(itemNumberCol);
                if (itemNumberCell != null && itemNumberCell.toString().equals(targetItemNumber)) {
                    // Item numarasını bulduk, hedef hücreyi güncelle
                    Cell targetValueCell = row.getCell(targetValueCol);
                    if (targetValueCell != null) {
                        // Yeni değeri belirtin
                        String newValue = "YENI_DEGER";
                        targetValueCell.setCellValue(newValue);
                        System.out.println("Hedef hücre değeri güncellendi.");
                    }
                    break; // İşlemi tamamla
                }
            }

            // Dosyayı güncelleyin
            FileOutputStream fos = new FileOutputStream(excelFilePath);
            workbook.write(fos);

            fis.close();
            fos.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



*/