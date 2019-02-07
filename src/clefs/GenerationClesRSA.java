package clefs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe permettant de générer une paire de clés privée/publique et de les sauvegarder dans des fichiers.
 * Les noms des fichiers de sortie doivent être spécifiés en paramètre.
 */
public class GenerationClesRSA {
	/**
	 * Méthode principale.
	 * @param args[0] nombre de bit pour générer les nombres premiers
	 * @param args[1] nom du fichier dans lequel sauvegarder la clé privée
	 * @param args[2] nom du fichier dans lequel sauvegarder la clé publique
	 * @param args[3] booleen d'activation du verbose
	 */
	public static void generer(int bitLength, String privateFile, String publicFile, boolean verbose) {
		// index 0=n, index 1=publicKey e, index 2=privateKey u
		final ArrayList<BigInteger> keys=new ArrayList<BigInteger>();

		// Création d'une clé publique
		if(verbose) System.out.println(System.lineSeparator()+"Calcul de la clé pubique sur "+bitLength+" bits.");
		BigInteger p=BigInteger.probablePrime(bitLength, new Random());
		if(verbose) System.out.println("Valeur de p: "+p);
		BigInteger q;
		do q=BigInteger.probablePrime(bitLength, new Random());
		while(p.equals(q));
		if(verbose) System.out.println("Valeur de q: "+q);

		keys.add(p.multiply(q)); // n=p*q
		if(verbose) System.out.println("Valeur de n=p*q: "+keys.get(0));

		// Indicatrice d'Euler
		if(verbose) System.out.println(System.lineSeparator()+"Calcul de l'indicatrice d'Euler.");
		BigInteger m=(p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		if(verbose) System.out.println("Valeur du modulo m=(p-1)*(q-1): "+m);
		BigInteger e=BigInteger.valueOf(3);
		while(!(m.gcd(e)).equals(BigInteger.ONE)) e=e.add(BigInteger.valueOf(2));
		keys.add(e); // Exposant public
		if(verbose) System.out.println("Valeur de l'exposant publique e: "+e);

		// Création d'une clé privé avec l'algorithme d'Euclide étendu
		if(verbose) System.out.println(System.lineSeparator()+"Caclul de la clé privée sur "+bitLength+" bits.");
		BigInteger r0=e, r1=m, u0=BigInteger.ONE, u1=BigInteger.ZERO;
		if(verbose) {
			System.out.println("Caclul de l'algorithme d'Euclide étendu.");
			System.out.println("Avec r0=e: "+r0);
			System.out.println("r1=m: "+r1);
			System.out.println("u0="+u0);
			System.out.println("et u1= "+u1);
		}

		int i=1;
		while(r1.compareTo(BigInteger.ZERO)!=0) {
			if(verbose) {
				System.out.println(System.lineSeparator()+"Itération "+i);
				System.out.print("Valeur de r+"+(i+1)+"=");
			}
			BigInteger tempR=computePattern(r0, r0, r1, r1, verbose);
			if(verbose) {
				System.out.println(tempR);
				System.out.print("Valeur de u+"+(i+1)+"=");
			}
			BigInteger tempU=computePattern(u0, r0, r1, u1, verbose);
			if(verbose) System.out.println(tempU);

			r0=r1;
			r1=tempR;
			u0=u1;
			u1=tempU;
			i++;
		}
		if(verbose) System.out.println("Résultats: u="+u0);

		if(u0.compareTo(BigInteger.ZERO)==-1) {
			if(verbose) System.out.println(System.lineSeparator()+"u<2 donc calcul de u-k*m en faisant varier k de -1 à - l'infini");
			int k=-1;
			do {
				if(verbose) {
					System.out.println("Itération de k="+k);
					System.out.print("u="+u0+"-"+k+"*"+m+"=");
				}
				u0=u0.subtract((BigInteger.valueOf(k)).multiply(m));
				if(verbose) System.out.println(u0);
				k--;
			} while(u0.compareTo(BigInteger.valueOf(2))==-1);
			if(verbose) System.out.println("Résultat: u="+u0);
		}
		keys.add(u0);

		// Sauvegarde de la clé privée
		if(verbose) System.out.println(System.lineSeparator()+"Écriture de la clé privée dans le fichier "+privateFile);
		GestionClesRSA.sauvegardeCle(keys.get(0), keys.get(2), privateFile, verbose);

		// Sauvegarde de la clé publique
		if(verbose) System.out.println("Écriture de la clé publique dans le fichier "+publicFile);
		GestionClesRSA.sauvegardeCle(keys.get(0), keys.get(1), publicFile, verbose);

		if(verbose) System.out.println("Clés sauvegardées."+System.lineSeparator());
    }

	// Compute pattern w-(x/y)*z. ex: ui+1=ui-1(ri-1/ri)*ui
	private static BigInteger computePattern(BigInteger w, BigInteger x, BigInteger y, BigInteger z, boolean verbose) {
		if(verbose) System.out.print(w+"-("+x+"/"+y+")*"+z+": ");
		return w.subtract((x.divide(y)).multiply(z));
	}
}