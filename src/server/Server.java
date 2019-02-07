package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import clefs.GenerationClesRSA;
import clefs.GestionClesRSA;

public class Server {
	private static Scanner scn=new Scanner(System.in);
	private static int verbose=0;
	private static BigInteger[] privateKeys, publicKeys;

	// HashMap to store active clients: id=>ClientHandler
	private static HashMap<Integer, ClientHandler> ac=new HashMap<Integer, ClientHandler>();

	// Counter for clients
	private static int i=0;

	/**
	 * Méthode principale.
	 * @param args[0] numéro de port du serveur
	 * @param args[1] nombre de bit pour générer les nombres premiers des clés
	 * @param args[2] nom du fichier qui contiendra la clé privé
	 * @param args[3] nom du fichier qui contiendra la clé publique
	 * @param args[4] niveau de verbosité: 0=aucune, 1=léger, 2=lourd
	 */
	public static void main(String[] args) throws IOException {
		if(args.length>0 && (args[0].equals("--help") || args[0].equals("help") || args[0].equals("?") || args[0].equals("/?"))) {
			System.out.println("first optional argument: server port number higher than 1024. default: 5000");
			System.out.println("second optional argument: keys bit length higher than 0. default: 2048");
			System.out.println("third optional argument: private key file name. default: private.bin");
			System.out.println("fourth optional argument: public key file name. default: public.bin");
			System.out.println("fifth optional argument: verbose mode, 0=nothing, 1=light verbose, 2=heavy verbose. default: 0");
		}
		else try {
			int portNumber=5000;
			if(args.length>0) portNumber=Integer.parseInt(args[0]);
			if(portNumber<1024) portNumber=5000;
			final ServerSocket ss=new ServerSocket(portNumber, 0, InetAddress.getByName("localhost"));
			System.out.println("Server running");
			Socket s;
			try {
				if(args.length>4) verbose=Integer.parseInt(args[4]);
				if(verbose<0) verbose=0;
			} catch(NumberFormatException e) {
				System.err.println("Verbose parameter must be an integer !"+System.lineSeparator()+"0=no verbose"+System.lineSeparator()+"1=ligt verbose"+System.lineSeparator()+"2=heavy verbose");
				verbose=0;
			}

			int bitLength=0;
			if(args.length>1) bitLength=Integer.parseInt(args[1]);
			if(bitLength<1) bitLength=2048;
			String privateFile="private.bin";
			if(args.length>2 && !args[2].equals("")) privateFile=args[2];
			String publicFile="public.bin";
			if(args.length>3 && !args[3].equals("")) publicFile=args[3];
			// Param=nombre premiers codées sur 2048 bits, chemin où la clé privée sera écrite, chemin où la clé publique sera écrite, booléen qui active le verbose
			GenerationClesRSA.generer(bitLength, privateFile, publicFile, verbose==2);
			privateKeys=GestionClesRSA.lectureCle(privateFile, verbose==2); // t=[moduloCléPrivée (n), exposantCléPrivé (u)];
			publicKeys=GestionClesRSA.lectureCle(publicFile, verbose==2); // t=[moduloCléPublique (n), exposantCléPublique (e)];

			Thread envoi= new Thread(new Runnable() {
				public void run() {
					while(true) {
						String[] splitted=scn.nextLine().split("=");
						if(splitted[0].equals("/list")) {
							if(ac.size()==0) System.out.println("There is no client. You are alone...");
							for(Integer id: ac.keySet()) System.out.println(System.lineSeparator()+"Client number "+id+" at address "+ac.get(id).getInetAdress()+System.lineSeparator());
						}
						else if(splitted[0].equals("/kick")) {
							if(splitted.length==1) System.err.println("You must specify at least one client number with /kick=clientNumber");
							for(int i=1; i<splitted.length; i++) try {
									int id=Integer.parseInt(splitted[i]);
									if(ac.containsKey(id)) ac.get(id).disconnect();
								} catch(IOException e) {
									e.printStackTrace();
								} catch(NumberFormatException e) {
									System.err.println("Client number must be an integer ! Unknow client "+splitted[i]);
								}
						}
						else if(splitted[0].equals("/stop")) {
							System.out.println("Disconnecting every client...");
							try {
								for(ClientHandler c: ac.values()) c.disconnect();
								System.out.println("Stopping...");
								ss.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);
						}
						else if(splitted.length>=2) {
							try {
								for(int i=2; i<splitted.length; i++) splitted[1]+=splitted[i];
								sendTo(Integer.parseInt(splitted[0]), splitted[1]);
							} catch(NumberFormatException e) {
								System.err.println("Client number must be an integer !");
							}
						}
						else {
							System.err.println("To send a message type: clientNumber=YourMessage");
							System.err.println("To get all the clients type /list");
							System.err.println("You can kick clients with /kick=clientNumber1=clientNumber2=...");
						}
					}
				}
			});
			envoi.start();

			try {
				// Running infinite loop for getting client request
				while(true) {
					// Accept the incoming request
					s=ss.accept();
					if(verbose>=1) System.out.println("New client request received: "+s);

					// Create a new handler object for handling this request
					if(verbose>=2) System.out.println("Creating a new handler for this client...");
					ClientHandler mtch=new ClientHandler(s, i, new DataInputStream(s.getInputStream()), new DataOutputStream(s.getOutputStream()), verbose);

					// Create a new Thread with this object
					Thread t=new Thread(mtch);

					// Add this client to active clients list
					if(verbose>=2) System.out.println("Adding this client to active client list at index "+i);
					ac.put(i, mtch);

					// Start the thread.
					t.start();

					// Increment i for new client
					i++;
				}
			} catch(Exception e) {
				System.out.println("Server stopped");
			}
		} catch(NumberFormatException e) {
			System.err.println("Keys bit length and port number must be integers and the port number must be higher than 1024 !");
		}
	}

	public static void removeClient(int id) {
		if(ac.containsKey(id)) ac.remove(id);
	}

	public static void sendTo(int id, String msg) {
		if(ac.containsKey(id)) ac.get(id).encryptAndSend(msg);
		else System.err.println("Client "+id+" unreachable !");
	}

	public static BigInteger[] getPublicKeys() {
		return publicKeys;
	}

	public static BigInteger[] getPrivateKeys() {
		return privateKeys;
	}
}