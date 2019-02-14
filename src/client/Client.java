package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import clefs.Chiffrement;
import clefs.Dechiffrement;
import clefs.GenerationClesRSA;
import clefs.GestionClesRSA;

public class Client {
	static int bitLength=2048, verbose=0;
	static String privateFile="private.bin", publicFile="public.bin";
	static DataInputStream dis;
	static DataOutputStream dos;
	static BigInteger[] serveurKeys;
	static Boolean isLoggedIn;
	static BigInteger[] privateKeys, publicKeys;
	static String message="";

	/**
	 * Méthode principale.
	 * @param args[0] adresse du serveur
	 * @param args[1] numéro de port du serveur
	 * @param args[2] nombre de bit pour générer les nombres premiers des clés
	 * @param args[3] nom du fichier qui contiendra la clé privé
	 * @param args[4] nom du fichier qui contiendra la clé publique
	 * @param args[5] niveau de verbosité: 0=aucune, 1=léger, 2=lourd
	 */
	public static void main(String args[]) throws UnknownHostException, IOException {
		if(args.length>0 && (args[0].equals("--help") || args[0].equals("help") || args[0].equals("?") || args[0].equals("/?"))) {
			System.out.println("first optional argument: server adress. default: 127.0.0.1");
			System.out.println("second optional argument: server port number higher than 1024. default: 5000");
			System.out.println("third optional argument: keys bit length higher than 0. default: 2048");
			System.out.println("fourth optional argument: private key file name. default: private.bin");
			System.out.println("fifth optional argument: public key file name. default: public.bin");
			System.out.println("sixth optional argument: verbose mode, 0=nothing, 1=light verbose, 2=heavy verbose. default: 0");
		}
		else {
			try {
				@SuppressWarnings("resource")
				final Scanner scn=new Scanner(System.in);
		
				if(args.length>2) bitLength=Integer.parseInt(args[2]);
				if(bitLength<1) bitLength=2048;
				if(args.length>3 && !args[3].equals("")) privateFile=args[3];
				if(args.length>4 && !args[4].equals("")) publicFile=args[4];
				generateKeys(bitLength, privateFile, publicFile);
				serveurKeys=new BigInteger[2];
		
				int portNumber=5000;
				if(args.length>1) portNumber=Integer.parseInt(args[1]);
				if(portNumber<1024) portNumber=5000;
				// Establish the connection
				final Socket s=new Socket(args.length>0 ? args[0] : "127.0.0.1", portNumber);
				isLoggedIn=true;
		
				// Obtaining input and output streams
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
		
				try {
					if(args.length>5) verbose=Integer.parseInt(args[5]);
				} catch(NumberFormatException e) {
					System.err.println("Verbose parameter must be an integer !"+System.lineSeparator()+"0=no verbose"+System.lineSeparator()+"1=ligt verbose"+System.lineSeparator()+"2=heavy verbose");
					verbose=0;
				}
		
				// Send Message thread
				Thread sendMessage=new Thread(new Runnable() {
					@Override
					public void run() {
						while(true) {
							message=scn.nextLine();
							if(message.equals("/keys")) {
								generateKeys(bitLength, privateFile, publicFile);
								sendPublicKeys();
							}
							else encryptAndSend(message);
						}
					}
				});
		
				// Read Message thread
				Thread readMessage=new Thread(new Runnable() {
					@Override
					public void run() {
						String received;
						String[] splitted;
						ArrayList <BigInteger> msg=new ArrayList<BigInteger>();
						try {
							while(true) {
								// Read the message sent to this client
								// Receive the string
								received=dis.readUTF();
								splitted=received.split("=");
								if(verbose>=1) System.out.println("Message from server : "+received);
								try {
									// Decrypting message
									for(String s: splitted) msg.add(new BigInteger(s));
									String messageClair=Dechiffrement.dechiffrer(privateKeys, msg, verbose==2);
									msg.clear();
									System.out.println("Decrypted Message from server is : "+messageClair);

									if(messageClair.startsWith("/keys=")) {
										splitted=messageClair.split("=");
										serveurKeys[0]=new BigInteger(splitted[1]);
										serveurKeys[1]=new BigInteger(splitted[2]);
									}
									else {
										if(messageClair.equals(message)) System.out.println("SUCCESS !");
										else System.out.println("FAIL !");
										if(messageClair.equals("/ping")) encryptAndSend("pong !");
										else if(messageClair.equals("/up")) encryptAndSend("And down !");
										else if(messageClair.equals("/logout")) {
											// Closing resources
											s.close();
											dis.close();
											dos.close();
										}
									}
								} catch(NumberFormatException ex) {}
							}
						} catch (IOException e) {
							System.out.println("Disconnected !");
						}
					}
				});
		
				try {
					sendMessage.start();
					readMessage.start();
					sendPublicKeys();
					System.out.println("Client ready");
					readMessage.join();
					System.out.println("Stopping...");
					sendMessage.interrupt();
				} catch (InterruptedException e) {
					System.err.println("Interrupt exception");
					e.printStackTrace();
				} finally {
					System.out.println("END");
					System.exit(0);
				}
			} catch(ConnectException e) {
				System.out.println("Server unreachable !");
			}
		}
	}

	public static void encryptAndSend(String msg) {
		if(isLoggedIn) {
			try {
				if(verbose>=1) System.out.println("Message to send to server : "+msg);
				String text="";
				for(BigInteger b: Chiffrement.chiffrer(serveurKeys, msg, verbose==2)) text+=b+"=";
				text=text.replaceAll("=$", "");
				dos.writeUTF(text);
				if(verbose>=1) System.out.println("Sent to server : "+text);
			} catch (IOException|NullPointerException e) {
				System.err.println("Error while sending "+msg+" to server");
				e.printStackTrace();
			}
		}
	}

	public static void generateKeys(int bitLength, String privateFile, String publicFile) {
		privateFile=privateFile!=null && !privateFile.equals("") ? privateFile : "private.bin";
		publicFile=publicFile!=null && !publicFile.equals("") ? publicFile : "public.bin";
		// Param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
		GenerationClesRSA.generer(bitLength>0 ? bitLength : 2048, privateFile, publicFile, verbose==2);
		privateKeys=GestionClesRSA.lectureCle(privateFile, verbose==2); // t=[moduloCléPrivée (n), exposantCléPrivé (u)];
		publicKeys=GestionClesRSA.lectureCle(publicFile, verbose==2); // t=[moduloCléPublique (n), exposantCléPublique (u)];
	}

	public static void sendPublicKeys() {
		try {
			String text="/keys=";
			for(BigInteger b: publicKeys) text+=b+"=";
			dos.writeUTF(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}