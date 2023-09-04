package com.test;

public class deneme {

        public static void main(String[] args) {
            String[] values = {
                    "TR-1234-4",
                    "R-5678-3",
                    "R-9876",
                    "TR-1234",
                    "ABC-123",
                    "S-4567",
                    "R7890-1"
            };

            for (String value : values) {
                if(value.startsWith("TR-") && ((value.endsWith("-1")||((value.endsWith("-2")))||((value.endsWith("-3")))||((value.endsWith("-4")))||
                        ((value.endsWith("-5")))))) {
                    String result = value.substring(3,value.length()-2); // İlk iki karakteri atla
                    System.out.println(result);}
                else if (value.startsWith("TR-") ) {
                    String result = value.substring(3); // İlk iki karakteri atla
                    System.out.println(result);
                }
                else if(value.startsWith("R-") &&((value.endsWith("-1")||((value.endsWith("-2")))||((value.endsWith("-3")))||((value.endsWith("-4")))||
                        ((value.endsWith("-5")))))) {
                    String result = value.substring(2,value.length()-2); // İlk iki karakteri atla
                    System.out.println(result);
                }
                else if(value.startsWith("R-")){
                    String result = value.substring(2); // İlk iki karakteri atla
                    System.out.println(result);
                } else if(value.startsWith("R") &&((value.endsWith("-1")||((value.endsWith("-2")))||((value.endsWith("-3")))||((value.endsWith("-4")))||
                        ((value.endsWith("-5")))))) {
                    String result = value.substring(1,value.length()-2); // İlk iki karakteri atla
                    System.out.println(result);
                }else if(value.startsWith("R")){
                    String result = value.substring(1); // İlk iki karakteri atla
                    System.out.println(result);
                }
            }
        }
    }


