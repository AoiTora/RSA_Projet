# RSA_Projet
---
Projet d'un client et d'un serveur qui génère des paire de clés privée et publique, s'échange les clés publique puis communique de manière chiffré.

__Requiert JAVA 1.8 !__

Les programme sont des archives JAVA auto-exécutable qui devraient fonctionner sans appel explicite à JAVA. Cependant, si votre système d'exploitation n'a pas lié le format `.jar` à JAVA, il se peut que vous deviez précéder les commande de lancement par `java -jar`.

# Serveur

Lancement: `./Serveur.jar [Numéro de port] [nombre de bit] [nom du fihier privée] [nom du fihier publique] [niveau de verbosité]`

__Liste des arguments optionnelles:__
  - numéro de port du serveur qui doit être supérieur à 1024. 5000 par défaut.
  - nombre de bit pour générer les nombres premiers des clés qui doit être supérieur à 0. 2048 par défaut.
  - nom du fichier qui contiendra la clé privé. private.bin par défaut.
  - nom du fichier qui contiendra la clé publique. public.bin par défaut.
  - niveau de verbosité: 0=aucune, 1=léger, 2=lourd. 0 par défaut.

# Client

Lancement: `./Client.jar [Adresse] [Numéro de port] [nombre de bit] [nom du fihier privée] [nom du fihier publique] [niveau de verbosité]`

__Liste des arguments optionnelles:__
  - adresse du serveur. 127.0.0.1 par défaut.
  - numéro de port du serveur qui doit être supérieur à 1024. 5000 par défaut.
  - nombre de bit pour générer les nombres premiers des clés . 2048 par défaut.
  - nom du fichier qui contiendra la clé privé. private.bin par défaut.
  - nom du fichier qui contiendra la clé publique. public.bin par défaut.
  - niveau de verbosité: 0=aucune, 1=léger, 2=lourd. 0 par défaut.
