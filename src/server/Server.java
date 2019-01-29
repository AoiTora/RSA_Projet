package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import clefs.Chiffrement;
import clefs.Dechiffrement;
import clefs.GenerationClesRSA;
/*
 * www.codeurjava.com
 */
public class Server {

   public static void main(String[] test) {

     final ServerSocket serveurSocket  ;
     final Socket clientSocket ;
     final BufferedReader in;
     final PrintWriter out;
     final Scanner sc=new Scanner(System.in);

     try {
    	 GenerationClesRSA.generer();
       serveurSocket = new ServerSocket(5000);
       clientSocket = serveurSocket.accept();
       out = new PrintWriter(clientSocket.getOutputStream());
       in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
       Thread envoi= new Thread(new Runnable() {
          String msg;
          public void run() {
             while(true){
                 msg = sc.nextLine();
                // String chiffre=Chiffrement.chiffrer(msg).toString();
                // out.println(chiffre);
                 out.flush();
             }
          }
       });
       envoi.start();

       Thread recevoir= new Thread(new Runnable() {
          byte[] msg ;
          String messageclair;
          public void run() {
             try {
          	   System.out.println("Alice est en ligne");
               msg = in.readLine().getBytes();
            //   messageclair = Dechiffrement.dechiffrer(msg);
             while(msg!=null){
                System.out.println("Alice dit : "+msg);
                msg = in.readLine().getBytes();
             }
                System.out.println("Déconnexion de Bob");
                out.close();
                clientSocket.close();
                serveurSocket.close();
             } catch (IOException e) {
                  e.printStackTrace();
             }
         }
      });
      recevoir.start();
      }catch (IOException e) {
         e.printStackTrace();
      }
   }
}