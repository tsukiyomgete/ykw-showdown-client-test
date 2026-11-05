import java.io.*;
import java.net.*;

public static String userClient;
public static String passClient;
private static volatile boolean jeQuitte = false;
static Scanner clavier = new Scanner(System.in);

public static void Connection()
{
    System.out.println("user : ");
    userClient = clavier.nextLine();
    System.out.println("mot de passe : ");
    passClient = clavier.nextLine();
}

public static void sendRequest(PrintWriter wrt, String request)
{
    String regexRequest = "/";
    String[] arrayRequest = request.substring(1).split(regexRequest);
    if (arrayRequest[0].equals("login"))
    {
        wrt.println(request);
    } else if (arrayRequest[0].equals("msg"))
    {
        wrt.println(request);
    }
}

public static void main(String[] args) {
    Connection();
    try (Socket socketClient = new Socket("localhost", 5001)) {
        System.out.println("Connectée au serveur " + socketClient.getInetAddress().getHostAddress()
                + " port " + socketClient.getPort()
                + " (mon port : " + socketClient.getLocalPort() + ")");
        BufferedReader lecteur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        PrintWriter writter = new PrintWriter(socketClient.getOutputStream(), true);

        System.out.println("Vérification...");
        // /login/user/xxxx/passwd/yyyy
        sendRequest(writter, "/login/user/" + userClient + "/passwd/" + passClient);

        String reponse = lecteur.readLine();
        if (reponse == null)
        {
            System.out.println("Le serveur a fermé la connexion");
            return;
        } else {
            System.out.println("Reçu : " + reponse);
        }

        Thread ecouteur = Thread.startVirtualThread(() -> {
            try {
                String ligne;
                while ((ligne = lecteur.readLine()) != null) {
                    System.out.println(ligne);
                }
                System.out.println("Le serveur a fermé la connexion");
            } catch (IOException e) {
                if (jeQuitte) {
                    System.out.println("Au revoir !");
                } else {
                    System.out.println("Connexion perdue : " + e.getMessage());
                }
            }
        });

        while (clavier.hasNextLine()) {
            String ligne = clavier.nextLine();
            if (ligne.equals("quitter")) {
                jeQuitte = true;
                break;
            }
            // /msg/user/xxxx/content/yyyy
            sendRequest(writter, "/msg/user/" + userClient + "/content/" + ligne);
        }

        socketClient.close();
        try {
            ecouteur.join(); // laisser l'ecouteur afficher son dernier message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    } catch (IOException e) {
        System.out.println("Erreur réseau : " + e.getMessage());
    }
}