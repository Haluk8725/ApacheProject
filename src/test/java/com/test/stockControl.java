package com.test;

import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class stockControl {
    private JFrame frame;
    private JButton startButton;
    private JTextField inputFilePathField;
    private JTextField mainFilePathField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            stockControl app = new stockControl();
            app.initializeUI();
        });
    }

    private void initializeUI() {
        frame = new JFrame("Stok Kontrol Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 200));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

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
                                inputRow.createCell(1).setCellValue(0);
                            }
                        } else {
                            String value = cell.getStringCellValue();
                            if (value.equals("0")) {
                                System.err.println("Dikkat");
                                inputRow.createCell(1).setCellValue(0);}
                            if (value.equals("< 10")) {
                                System.out.println("Az Dikkat");
                                inputRow.createCell(1).setCellValue(2);
                            }
                            if (value.equals(">= 10")) {
                                inputRow.createCell(1).setCellValue(4);
                            }
                        }
                    }
                }
            }

            FileOutputStream outputStream = new FileOutputStream(inputFilePath);
            inputWorkbookObj.write(outputStream);
            outputStream.close();

            System.out.println("Program tamamlandı.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   private static String itemNo(String sku){
     String result="";
       if (sku.startsWith("TR-") ) {
           result=sku.substring(3); // İlk iki karakteri atla
           System.err.println(result);
       } else if(sku.startsWith("R-")){
          result= sku.substring(2); System.err.println(result);
       }else if(sku.startsWith("R")){
           result= sku.substring(1); System.err.println(result);
       }

     return result;
   }
}
