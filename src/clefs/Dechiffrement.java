package clefs;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Classe permettant de déchiffrer un message à l'aide d'une clé privée.
 */
public class Dechiffrement {
	/**
	 * Methode principale.
	 * @param args[0] paire de clé privée où index 0=m et index 1=u
	 * @param args[1] collection de caractères à déchiffrer
	 * @param args[2] booleen d'activation du verbose
	 */
	public static String dechiffrer(BigInteger[] privateKeys, ArrayList<BigInteger> msg, boolean verbose) throws NullPointerException {
		if(privateKeys[0]==null || privateKeys[1]==null) throw new NullPointerException("Empty private keys !");
		String str=new String();
		if(verbose) System.out.println("To Decrypt: "+msg);
		for(BigInteger i: msg) {
			int x=i.modPow(privateKeys[1], privateKeys[0]).intValueExact();
			char c=(char)x;
			str+=c;
			if(verbose) {
				System.out.println("Decrypted ASCII value of "+i+" is "+x);
				System.out.println("Char value of "+x+" is "+c);
			}
		}
		if(verbose) System.out.println("Decrypted message: "+str);
		return str;
	}
}