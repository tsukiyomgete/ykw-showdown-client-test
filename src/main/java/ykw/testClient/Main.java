
import java.io.*;
import java.net.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    try(Socket socketClient = new Socket("localhost", 5001)){
        System.out.println("Mon pc est connectée aux réseau à l'adresse " + socketClient.getInetAddress().getHostAddress()
        + " et au port " + socketClient.getPort());
        BufferedReader lecteur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        PrintWriter writter = new PrintWriter(socketClient.getOutputStream(), true);

        System.out.println("Veuillez entrez votre message");
        String mess = scan.nextLine();
        writter.println(mess);
        System.out.println(mess + " envoyé !");
        String reponse = lecteur.readLine();
        if(reponse == null)
        {
            System.out.println("Le serveur a fermé la connexion");
        } else {
            System.out.println("Reçu : " + reponse);
        }
    }catch(IOException e) {
        System.out.println("Erreur réseau : " + e.getMessage());
    }

}

