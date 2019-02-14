package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

import clefs.Chiffrement;
import clefs.Dechiffrement;

class ClientHandler implements Runnable {
	private Socket s;
	private int id;
	private final DataInputStream dis;
	private final DataOutputStream dos;
	private boolean isLoggedIn;
	private int verbose;
	private BigInteger[] clientKeys;

	public ClientHandler(Socket so, int i, DataInputStream is, DataOutputStream os, int v) {
		s=so;
		id=i;
		dis=is;
		dos=os;
		isLoggedIn=true;
		verbose=v;
		clientKeys=new BigInteger[2];
	}

	@Override
	public void run() {
		String received;
		String[] splitted;
		ArrayList <BigInteger> msg=new ArrayList<BigInteger>();
		try {
			while(true) {
				// Receive the string
				received=dis.readUTF();
				splitted=received.split("=");
				if(verbose>=1) System.out.println("Message from client "+id+" : "+received);

				// If we receive the client public key
				if(splitted[0].equals("/keys")) {
					clientKeys[0]=new BigInteger(splitted[1]);
					clientKeys[1]=new BigInteger(splitted[2]);

					// We send our public key
					String s="/keys=";
					for(BigInteger b: Server.getPublicKeys()) s+=(b+"=");
					encryptAndSend(s);
				}
				else {
					try {
						// Decrypting message
						for(String s: splitted) msg.add(new BigInteger(s));
						String messageClair=Dechiffrement.dechiffrer(Server.getPrivateKeys(), msg, verbose==2);
						msg.clear();
						System.out.println("Decrypted Message from client "+id+" is : "+messageClair);
	
						if(messageClair.equals("/ping")) encryptAndSend("pong !");
						else if(messageClair.equals("/up")) encryptAndSend("And down !");
						else if(messageClair.startsWith("/send")) {
							splitted=messageClair.split("=");
							try {
								if(splitted.length>=3) {
									for(int i=3; i<splitted.length; i++) splitted[2]+=splitted[i];
									if(Server.sendTo(id, Integer.parseInt(splitted[1]), splitted[2])!=null) encryptAndSend("Client unreachable !");
								}
								else encryptAndSend("Ex: /send=0=hello");
							} catch(NumberFormatException e) {
								encryptAndSend("Client number must be an integer !"+System.lineSeparator()+"Ex: /send=0=hello");
							}
						}
						else if(messageClair.equals("/logout")) disconnect();
						else if(messageClair.equals("/list")) encryptAndSend(Server.listExclude(id));
						else if(messageClair.equals("/id")) encryptAndSend("You are client "+id);
						else encryptAndSend(messageClair); // Reencrypting and sending the message
					} catch(NumberFormatException ex) {}
				}
			}
	    } catch(IOException e) {
	    	if(verbose>=1) System.out.println("Client "+id+" disconnected");
	    	isLoggedIn=false;
	    	Server.removeClient(id);
		}
	}

	public void encryptAndSend(String msg) {
		if(isLoggedIn) {
			try {
				if(verbose>=1) System.out.println("Message to send to client "+id+" : "+msg);
				String text="";
				for(BigInteger b: Chiffrement.chiffrer(clientKeys, msg, verbose==2)) text+=b+"=";
				text=text.replaceAll("=$", "");
				dos.writeUTF(text);
				if(verbose>=1) System.out.println("Sent to client "+id+" : "+text);
				else System.err.println("Client "+id+" isn't logged  !");
			} catch (IOException|NullPointerException e) {
				System.err.println("Error while sending "+msg+" to client "+id);
				e.printStackTrace();
			}
		}
	}

	public String getInetAdress() {
		return s.getInetAddress().getHostAddress();
	}

	public void disconnect() throws IOException {
		encryptAndSend("bye !");
		encryptAndSend("/logout");

		// Closing resources
		s.close();
		dis.close();
		dos.close();
	}
}