package clefs;

import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Classe permettant de déchiffrer un message à l'aide une clé privée.
 */
public class Dechiffrement {

    /**
     * Methode principale.
     * @param args[0] nom du fichier dans lequel se trouve la clé privée
     * @param args[1] message à déchiffrer
     */
    public static String dechiffrer(int[] messageCode) {

        // Récupération de la clé privée
        //PrivateKey clePrivee = GestionClesRSA.lectureClePrivee("prive.bin");
        String str = new String();



        // Recuperation de la cle publique
       // PublicKey clePublique = GestionClesRSA.lectureClePublique("publique.bin");

        for ( int i : messageCode ){
            str+=(char)i;
            //int j = (int) c;
            //msg[i]=j;
            System.out.println("Message = "+str);
            }


            return str;
    }
}