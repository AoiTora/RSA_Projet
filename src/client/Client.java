package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

import clefs.Chiffrement;
import clefs.Dechiffrement;
import clefs.GenerationClesRSA;
import clefs.GestionClesRSA;

public class Client {

   public static void main(String[] args) {

      final Socket clientSocket;
      final BufferedReader in;
      final PrintWriter out;
      final Scanner sc = new Scanner(System.in);


      try {
    	  GenerationClesRSA.generer(2048, "private.bin","public.bin", true); //param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
          BigInteger[] t=GestionClesRSA.lectureCle("private.bin", true); //t=[moduloCléPrivée (n), exposantCléPrivé (u)];
          t=GestionClesRSA.lectureCle("public.bin", true); //       t=[moduloCléPublique (n), exposantCléPublique (u)];

         clientSocket = new Socket("127.0.0.1",5000);

         out = new PrintWriter(clientSocket.getOutputStream());

         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         Thread envoyer = new Thread(new Runnable() {
             String msg;
              public void run() {
                while(true){
                	msg = sc.nextLine();
                    final int[] text = Chiffrement.chiffrer(msg);
                    out.println(text);
                    out.flush();
                }
             }
         });
         envoyer.start();

        Thread recevoir = new Thread(new Runnable() {
            int[] msg;
            String messageclair;
            int i=0;
            public void run() {
               try {
            	   System.out.println("Bob est en ligne");
            	   messageclair = in.readLine();
                   // msg = in.readLine().getBytes();
                 //   messageclair = Dechiffrement.dechiffrer(msg);
                 while(messageclair!=null){
                	 msg[i]=Integer.parseInt(messageclair);
                    messageclair = in.readLine();
                    i++;

                 }
                 String messageDechiffre = Dechiffrement.dechiffrer(msg);
                 System.out.println("Bob dit : "+messageDechiffre);
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