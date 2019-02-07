package clefs;

import java.math.BigInteger;

/**
 * Classe permettant de chiffrer un message à l'aide d'une clé publique.
 */
public class Chiffrement {
	/**
	 * Méthode principale.
	 * @param args[0] paire de clé publique où index 0=m et index 1=e
	 * @param args[1] message à chiffrer
	 * @param args[2] booleen d'activation du verbose
	 */
	public static BigInteger[] chiffrer(BigInteger[] publicKeys, String str, boolean verbose) throws NullPointerException {
		if(publicKeys[0]==null || publicKeys[1]==null) throw new NullPointerException("Empty public keys !");
		BigInteger[] msg = null;
		if(verbose) System.out.println("To encrypt: "+str);
		msg = new BigInteger[str.length()];
		for(int i=0; i<str.length(); ++i) {
			char c = str.charAt(i);
			int j = (int) c;
			msg[i]=BigInteger.valueOf(j).modPow(publicKeys[1], publicKeys[0]);
			if(verbose) {
				System.out.println("ASCII value of "+c+" is "+j);
				System.out.println("Encrypted value of "+j+" is "+msg[i]);
			}
		}
		if(verbose) {
			System.out.print("Encrypted message: ");
			String s="";
			for(BigInteger b: msg) s+=b+" ";
			s=s.replaceAll(" $", "");
			System.out.println(s);
		}
		return msg;
	}
}