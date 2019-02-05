package clefs;

import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Cipher;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Classe permettant de chiffrer un message à l'aide d'une clé publique.
 * Le message chiffré est placé dans un fichier.
 */
public class Chiffrement {

    /**
     * Méthode principale.
     * @param args[0] nom du fichier dans lequel se trouve la clé publique
     * @param args[1] message à chiffrer
     * @param args[2] nom du fichier dans lequel sauvegarder le message chiffré
     */
    @SuppressWarnings("finally")
	public static int[] chiffrer(String str) {

        int[] msg = null;


        System.out.println("Message à chiffrer : " + str);
        msg = new int[str.length()];

        // Recuperation de la cle publique
       // PublicKey clePublique = GestionClesRSA.lectureClePublique("publique.bin");

        for ( int i = 0; i < str.length(); ++i ){
            char c = str.charAt(i);
            int j = (int) c;
            msg[i]=j;
            System.out.println("ASCII value of "+c +" is " + j + ".");
            }


            return msg;

    }


}