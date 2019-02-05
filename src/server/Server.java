package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


import clefs.*;
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
       GenerationClesRSA.generer(2048, "private.bin","public.bin", true); //param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
       BigInteger[] t=GestionClesRSA.lectureCle("private.bin", true); //t=[moduloCléPrivée (n), exposantCléPrivé (u)];
       t=GestionClesRSA.lectureCle("public.bin", true); //       t=[moduloCléPublique (n), exposantCléPublique (u)];
       serveurSocket = new ServerSocket(5000);
       clientSocket = serveurSocket.accept();
       out = new PrintWriter(clientSocket.getOutputStream());
       in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));


       Thread envoi= new Thread(new Runnable() {
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
       envoi.start();

       Thread recevoir= new Thread(new Runnable() {
          //byte[] msg ;
          String msg;
          public void run() {
             try {
          	   System.out.println("Alice est en ligne");
               msg = in.readLine();
               //Dechiffrement.dechiffrer(msg);
              // msg = in.readLine().getBytes();
            //   messageclair = Dechiffrement.dechiffrer(msg);
             while(msg!=null){
                System.out.println("Alice dit : "+msg);
                msg = in.readLine();
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