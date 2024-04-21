package com.example.csit228_f1_v2;

import java.util.Scanner;

public class Main {
    public static void main() {
        System.out.println("Enter windspeed: ");
        Scanner sc = new Scanner(System.in);
        int ws = sc.nextInt();
        if (ws < 62) {
            System.out.println("Tropical Depression");
        } else if (ws <= 88) {
            System.out.println("Tropical Storm");
        }
    }
}
