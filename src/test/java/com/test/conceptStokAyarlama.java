package com.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class conceptStokAyarlama {
    public static void main(String[] args) {
        try {
            FileInputStream dosyaGirdisi = new FileInputStream(new File("C:\\Users\\haluk\\Desktop\\Concept Stock.xlsx"));
            Workbook workbook = new XSSFWorkbook(dosyaGirdisi);
            Sheet sheet = workbook.getSheetAt(0); // İlk sayfayı alın

            Map<String, List<Urun>> gruplar = new HashMap<>();

            for (Row row : sheet) {
                Cell cell1 = row.getCell(0); // A sütunu
                Cell cell2 = row.getCell(1); // B sütunu

                if (cell1 != null && cell2 != null) {
                    String urunAdi = cell1.getStringCellValue();
                    int urunMiktari = (int) cell2.getNumericCellValue();
                    String anahtar="";
                    if (urunAdi.startsWith("Pi")){
                    anahtar = urunAdi.substring(5,10);} else anahtar=urunAdi.substring(3,8);
                    System.out.println(anahtar);

                    if (!gruplar.containsKey(anahtar)) {
                        gruplar.put(anahtar, new ArrayList<>());
                    }

                    gruplar.get(anahtar).add(new Urun(urunAdi, urunMiktari));
                }
            }

            for (String anahtar : gruplar.keySet()) {
                List<Urun> grup = gruplar.get(anahtar);
                int enKucukMiktar = Integer.MAX_VALUE;

                for (Urun urun : grup) {
                    if (urun.miktar < enKucukMiktar) {
                        enKucukMiktar = urun.miktar;
                    }
                }

                for (Urun urun : grup) {
                    urun.miktar = enKucukMiktar;
                }
            }

            // Elde edilen sonuçları kullanın veya görüntüleyin
            // Sonuçları yeni bir Excel dosyasına yazdır
            Workbook yeniWorkbook = new XSSFWorkbook();
            Sheet yeniSheet = yeniWorkbook.createSheet("Sonuclar");
            int rowNum = 0;

            for (List<Urun> grup : gruplar.values()) {
                for (Urun urun : grup) {
                    Row row = yeniSheet.createRow(rowNum++);
                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue(urun.ad);
                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(urun.miktar);
                }
            }

            // Yeni Excel dosyasını kaydedin
            FileOutputStream yeniDosyaCikti = new FileOutputStream(new File("C:\\Users\\haluk\\Desktop\\Sonuclar.xlsx"));
            yeniWorkbook.write(yeniDosyaCikti);
            yeniDosyaCikti.close();
            yeniWorkbook.close();
            System.out.println("Sonuçlar başarıyla kaydedildi.");

            dosyaGirdisi.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class Urun {
        String ad;
        int miktar;

        public Urun(String ad, int miktar) {
            this.ad = ad;
            this.miktar = miktar;
        }
    }
}