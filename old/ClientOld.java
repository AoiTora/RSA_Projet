package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import clefs.Chiffrement;
import clefs.Dechiffrement;
import clefs.GenerationClesRSA;
import clefs.GestionClesRSA;

public class ClientOld {
	public static void main(String[] args) {
		final Socket clientSocket;
		final BufferedReader in;
		final PrintWriter out;
		final Scanner sc=new Scanner(System.in);
	
		try {
			// param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
			GenerationClesRSA.generer(2048, "private.bin","public.bin", true);
			final BigInteger[] privateKeys=GestionClesRSA.lectureCle("private.bin", true); // t=[moduloCléPrivée (n), exposantCléPrivé (u)];
			final BigInteger[] publicKeys=GestionClesRSA.lectureCle("public.bin", true); // t=[moduloCléPublique (n), exposantCléPublique (u)];
			final BigInteger[] serveurKeys=new BigInteger[2];

			clientSocket = new Socket("127.0.0.1", 5000);
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			out.print("keys=");
			for(BigInteger bigInt: publicKeys) out.print(bigInt.toString()+"=");
			out.println();
			out.flush();

			Thread envoyer = new Thread(new Runnable() {
				String msg;
				public void run() {
					while(true){
						msg = sc.nextLine();
						BigInteger[] encrypted = Chiffrement.chiffrer(serveurKeys, msg, true);
						String text="";
						for(BigInteger bigInt: encrypted) text+=bigInt+"=";
						text=text.replaceAll("=$", "");
						System.out.println("sent: "+text+System.lineSeparator());
						out.print(text);
						out.println();
						out.flush();
					}
				}
			});
			envoyer.start();

			Thread recevoir = new Thread(new Runnable() {
				ArrayList<BigInteger> msg=new ArrayList<BigInteger>();
				String received;
				int i=0;
				public void run() {
					try {
						System.out.println("Bob est en ligne");
						received = in.readLine();
						// msg = in.readLine().getBytes();
						while(received!=null) {
							String[] splitted=received.split("=");
							// réceptions clé publique du serveur
							if(received.startsWith("keys=")) {
								serveurKeys[0]=new BigInteger(splitted[1]);
								serveurKeys[1]=new BigInteger(splitted[2]);
							}
							else {
								for(String s: splitted) {
									System.out.println("from split: "+s+System.lineSeparator());
									msg.add(new BigInteger(s));
								}
								String messageDechiffre = Dechiffrement.dechiffrer(privateKeys, msg, true);
								System.out.println("Bob dit : "+messageDechiffre);
							}
							System.out.println(received);
							received = in.readLine();
							i++;
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