package clefs;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.math.BigInteger;

public class GestionClesRSA {

	/**
	 * Sauvegarde d'une clé publique ou privée dans un fichier
	 * @param args[0] BigInteger issue de n=p*q
	 * @param args[1] exposant, qu'il soit publique ou privé
	 * @param args[2] nom du fichier dans lequel sauvegarder la clé
	 * @param args[3] booleen d'activation du verbose
	 */
	public static void sauvegardeCle(BigInteger n, BigInteger x, String nomFichier, boolean verbose) {
		try {
			if(verbose) System.out.println(System.lineSeparator()+"Ouverture du fichier "+nomFichier+" en écriture");
			ObjectOutputStream fichier=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(nomFichier)));
			if(verbose) System.out.println("Écriture du modulo.");
			fichier.writeObject(n);
			if(verbose) System.out.println("Écriture de l'exposant.");
			fichier.writeObject(x);
			if(verbose) System.out.println("Fermeture du fichier "+nomFichier);
			fichier.close();
		} catch(IOException e) {
			System.err.println("Erreur lors de la sauvegarde de la clé !"+System.lineSeparator()+"Vérifier que vous avez le droit d'écriture dans le dossier courant !");
			System.exit(-1);
		}
	}

	/**
	 * Lecture d'une clé publique ou privée depuis un fichier.
	 * @param nomFichier le nom du fichier contenant une clé publique ou privé
	 * @return BigInteger[] où index0=modulo, index1=exposant publique ou privé
	 */
	public static BigInteger[] lectureCle(String nomFichier, boolean verbose) {
		BigInteger modulo=null, exposant=null;
		try {
			if(verbose) System.out.println(System.lineSeparator()+"Ouverture du fichier "+nomFichier+" en lecture");
			ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));
			if(verbose) System.out.print("Lecture du modulo: ");
			modulo=(BigInteger)ois.readObject();
			if(verbose) {
				System.out.println(modulo);
				System.out.print("Lecture de l'exposant: ");
			}
			exposant=(BigInteger)ois.readObject();
			if(verbose) {
				System.out.println(exposant);
				System.out.println("Fermeture du fichier "+nomFichier);
			}
			ois.close();
		} catch(IOException e) {
			System.err.println("Erreur lors de la lecture de la clé !"+System.lineSeparator()+"Vérifier que vous avez le droit de lecture dans le dossier courant et que le fichier "+nomFichier+" y est bien présent !");
			System.exit(-1);
		} catch(ClassNotFoundException e) {
			System.err.println("Fichier de clé incorrect: "+e);
			System.exit(-1);
		}
		BigInteger temp[]={modulo, exposant};
		return temp;
	}
}