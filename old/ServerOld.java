package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


import clefs.*;
/*
 * www.codeurjava.com
 */
public class ServerOld {

	public static void main(String[] test) {
		final ServerSocket serveurSocket;
		final Socket clientSocket ;
		final BufferedReader in;
		final PrintWriter out;
		final Scanner sc=new Scanner(System.in);

		try {
			// param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
			GenerationClesRSA.generer(2048, "private.bin", "public.bin", true);
			final BigInteger[] privateKeys=GestionClesRSA.lectureCle("private.bin", true); // t=[moduloCléPrivée (n), exposantCléPrivé (u)];
			final BigInteger[] publicKeys=GestionClesRSA.lectureCle("public.bin", true); // t=[moduloCléPublique (n), exposantCléPublique (e)];
			final BigInteger[] clientKeys=new BigInteger[2];

			serveurSocket = new ServerSocket(5000);
			clientSocket = serveurSocket.accept();
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));

			Thread envoi= new Thread(new Runnable() {
				public void run() {
					while(true) encryptAndSend(out, clientKeys, sc.nextLine(), true);
				}
			});
			envoi.start();

			Thread recevoir= new Thread(new Runnable() {
				ArrayList <BigInteger> msg=new ArrayList<BigInteger>();
				String received;
				public void run() {
					try {
						System.out.println("Alice est en ligne");
						received = in.readLine();
						// msg = in.readLine().getBytes();
						while(received!=null) {
							String[] splitted=received.split("=");
							// si on reçoit la clé publique du client
							if(received.startsWith("keys=")) {
								// on la stocke
								clientKeys[0]=new BigInteger(splitted[1]);
								clientKeys[1]=new BigInteger(splitted[2]);

								// puis on envoie notre clé publique
								out.print("keys=");
								for(BigInteger bigInt: publicKeys) out.print(bigInt.toString()+"=");
								out.println();
								out.flush();
							}
							else {
								for(String s: splitted) {
									System.out.println("from split: "+s+System.lineSeparator());
									msg.add(new BigInteger(s));
								}
								String messageclair = Dechiffrement.dechiffrer(privateKeys, msg, true);
								System.out.println("Alice dit : "+messageclair);
								encryptAndSend(out, clientKeys, messageclair, true);
							}
							System.out.println(received);
							received = in.readLine();
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
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void encryptAndSend(PrintWriter out, BigInteger[] clientKeys, String msg, boolean verbose) {
		final BigInteger[] encrypted = Chiffrement.chiffrer(clientKeys, msg, verbose);
		String text="";
		for(BigInteger bigInt: encrypted) text+=bigInt+"=";
		text=text.replaceAll("=$", "");
		System.out.println("sent: "+text+System.lineSeparator());
		out.print(text);
		out.println();
		out.flush();
	}
}