package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import clefs.Chiffrement;
import clefs.Dechiffrement;
import clefs.GenerationClesRSA;

public class Client {

   public static void main(String[] args) {

      final Socket clientSocket;
      final BufferedReader in;
      final PrintWriter out;
      final Scanner sc = new Scanner(System.in);


      try {
     	 GenerationClesRSA.generer();

         clientSocket = new Socket("127.0.0.1",5000);

         out = new PrintWriter(clientSocket.getOutputStream());

         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

         Thread envoyer = new Thread(new Runnable() {
             String msg;
              public void run() {
                while(true){
                  msg = sc.nextLine();
                  //String chiffre=Chiffrement.chiffrer(msg).toString();
                  //out.println(chiffre);
                  out.flush();
                }
             }
         });
         envoyer.start();

        Thread recevoir = new Thread(new Runnable() {
            byte[] msg;
            String messageclair;
            public void run() {
               try {
            	   System.out.println("Bob est en ligne");
                   msg = in.readLine().getBytes();
                   //messageclair = Dechiffrement.dechiffrer(msg);
                 while(msg!=null){
                    System.out.println("Alice dit : "+msg);
                    msg = in.readLine().getBytes();
                 }
                 System.out.println("Alice déconnectée");
                 out.close();
                 clientSocket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
            }
        });
        recevoir.start();

      } catch (IOException e) {
           e.printStackTrace();
      }
  }
}