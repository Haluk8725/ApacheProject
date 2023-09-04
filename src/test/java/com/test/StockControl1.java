package com.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StockControl1 {
    private JFrame frame;
    private JButton startButton;
    private JTextField inputFilePathField;
    private JTextField mainFilePathField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StockControl1 app = new StockControl1();
            app.initializeUI();
        });
    }

    private void initializeUI() {
        frame = new JFrame("Stok Kontrol Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 200)); // Boyutu daha büyük ayarlayın

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1));

        inputPanel.add(new JLabel("Girdi Dosyası:"));
        inputFilePathField = new JTextField();
        inputPanel.add(inputFilePathField);

        JButton inputFileChooserButton = new JButton("Dosya Seç");
        inputFileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    inputFilePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(inputFileChooserButton);

        inputPanel.add(new JLabel("Ana Dosya:"));
        mainFilePathField = new JTextField();
        inputPanel.add(mainFilePathField);

        JButton mainFileChooserButton = new JButton("Dosya Seç");
        mainFileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    mainFilePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(mainFileChooserButton);

        startButton = new JButton("Başlat");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processStockControl();
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(inputPanel, BorderLayout.CENTER);
        frame.getContentPane().add(startButton, BorderLayout.SOUTH);


        frame.pack();
        frame.setVisible(true);
    }

    private void processStockControl() {
        String inputFilePath = inputFilePathField.getText(); // Girdi dosyasının yolu
        String mainFilePath = mainFilePathField.getText(); // Ana dosyanın yolu

        try {

            FileInputStream inputWorkbook = new FileInputStream(inputFilePath);
            FileInputStream mainWorkbook = new FileInputStream(mainFilePath);

            Workbook inputWorkbookObj = WorkbookFactory.create(inputWorkbook);
            Workbook mainWorkbookObj = WorkbookFactory.create(mainWorkbook);

            Sheet inputSheet = inputWorkbookObj.getSheetAt(0);
            Sheet mainSheet = mainWorkbookObj.getSheetAt(0);

            int inputRowCount = inputSheet.getLastRowNum();
            int mainRowCount = mainSheet.getLastRowNum();
            Map<String, String> matchedValuesMap = new HashMap<>();
            for (int i = 0; i <= inputRowCount; i++) {
                Row inputRow = inputSheet.getRow(i);
                String itemNumber = String.valueOf(inputRow.getCell(0));
                String sku = itemNo(itemNumber);

                for (int j = 0; j <= mainRowCount; j++) {
                    Row mainRow = mainSheet.getRow(j);
                    String mainItemNumber = String.valueOf(mainRow.getCell(0));
                    Cell cell = mainRow.getCell(3);
                    String mainItemNumber1=mainItemNumber.substring(0,mainItemNumber.length()-2);
                    if (mainItemNumber1.equalsIgnoreCase(sku)) {
                        System.out.println(mainItemNumber1);
                        if (cell.getCellType() == CellType.NUMERIC) {
                            double numericValue = cell.getNumericCellValue();
                            if (numericValue == 0) {
                                System.err.println("Dikkat");
                                matchedValuesMap.put(itemNumber, "0");
                            }
                        } else {
                            String value = cell.getStringCellValue();
                            if (value.equals("0")) {
                                System.err.println("Dikkat");
                                matchedValuesMap.put(itemNumber, "0");}
                            if (value.equals("< 10")) {
                                System.out.println("Az Dikkat");
                                matchedValuesMap.put(itemNumber, "1");
                            }
                            if (value.equals(">= 10")) {
                                matchedValuesMap.put(itemNumber, "2");
                            }
                        }
                    }
                }
            }

            Workbook newWorkbook = new XSSFWorkbook();
            Sheet newSheet = newWorkbook.createSheet("csv_inventory_template");

            // Map'teki eşleşen değerleri yeni Excel dosyasına yaz
            int rowIndex = 0;
            for (Map.Entry<String, String> entry : matchedValuesMap.entrySet()) {
                Row newRow = newSheet.createRow(rowIndex++);
                Cell cell1 = newRow.createCell(0);
                Cell cell2 = newRow.createCell(1);
                cell1.setCellValue(entry.getKey()); // Eşleşen item numarası
                int intValue = Integer.parseInt(entry.getValue());
                cell2.setCellValue(intValue); // Şarta göre değer
            }

            // Yeni Excel dosyasını kaydet
            try {
                FileOutputStream newOutputStream = new FileOutputStream("matched_values.xlsx");
                newWorkbook.write(newOutputStream);
                newOutputStream.close();
                System.out.println("Eşleşen değerler yeni dosyaya kaydedildi: matched_values.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        }

    } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static String itemNo(String sku){
        String result="";
        if(sku.startsWith("TR-") && ((sku.endsWith("-1")||((sku.endsWith("-2")))||((sku.endsWith("-3")))||((sku.endsWith("-4")))||
                ((sku.endsWith("-5")))))) {
            result = sku.substring(3, sku.length() - 2); // İlk iki karakteri atla
        } else if (sku.startsWith("TR-") ) {
            result=sku.substring(3); // İlk iki karakteri atla
            System.err.println(result);
        }
        else if(sku.startsWith("R-") &&((sku.endsWith("-1")||((sku.endsWith("-2")))||((sku.endsWith("-3")))||((sku.endsWith("-4")))||
                ((sku.endsWith("-5")))))) {
            result = sku.substring(2,sku.length()-2); // İlk iki karakteri atla
                    }
        else if(sku.startsWith("R-")){
            result= sku.substring(2); System.err.println(result);
        }
        else if(sku.startsWith("R") &&((sku.endsWith("-1")||((sku.endsWith("-2")))||((sku.endsWith("-3")))||((sku.endsWith("-4")))||
                ((sku.endsWith("-5")))))) {
            result = sku.substring(1, sku.length() - 2); // İlk iki karakteri atla
        }
        else if(sku.startsWith("R")){
            result= sku.substring(1); System.err.println(result);
        }
        return result;
    }
}


