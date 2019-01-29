package clefs;

import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Classe permettant de générer une paire de clés privée/publique et de les
 * sauvegarder dans des fichiers.
 * Les noms des fichiers de sortie doivent être spécifiés en ligne de commande.
 * Par exemple : java GenerationClesRSA prive.bin publique.bin
 * La clé privée est sauvée dans 'prive.bin' et la clé publique est sauvée
 * dans 'publique.bin'.
 */
public class GenerationClesRSA {

    /**
     * Méthode principale.
     * @param args[0] nom du fichier dans lequel sauvegarder la clé privée
     * @param args[1] nom du fichier dans lequel sauvegarder la clé publique
     */
    public static void generer() {

        // Création d'un générateur RSA
        KeyPairGenerator generateurCles = null;
        try {
            generateurCles = KeyPairGenerator.getInstance("RSA");
            generateurCles.initialize(2048);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors de l'initialisation du générateur de clés : " + e);
            System.exit(-1);
        }

        // Génération de la paire de clés
        KeyPair paireCles = generateurCles.generateKeyPair();

        // Sauvegarde de la clé privée
        GestionClesRSA.sauvegardeClePrivee(paireCles.getPrivate(), "prive.bin");

        // Sauvegarde de la clé publique
        GestionClesRSA.sauvegardeClePublique(paireCles.getPublic(), "publique.bin");

        System.out.println("Clés sauvegardées.");
    }

}