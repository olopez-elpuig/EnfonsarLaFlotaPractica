import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class EnfonsarLaFlotaServidor {

    char[][] tauler = new char[10][10];
    char[][] taulerJ1 = new char[10][10];
    char[][] taulerJ2 = new char[10][10];

    //Info NET
    int port = 5557;
    DatagramSocket socket;
    InetAddress inetAddress;
    boolean jugant = true;
    String srvIp = "localhost";
    InetAddress inetAddressJ1;
    int portJ1;
    InetAddress inetAddressJ2;
    int portJ2;
    int jugadores = 0;
    int turn = 1;
    int puntsJ1 = 0;
    int puntsJ2 = 0;

    public void lobby() {
        try {

            socket = new DatagramSocket(5557);
            byte[] msgReceive = new byte[1024];
            byte[] msgSend = new byte[1024];
            String auxiliar;

            DatagramPacket packet = new DatagramPacket(msgReceive, msgReceive.length);

            while (jugadores < 2) {
                socket.receive(packet);
                //Bienvenido tal cual sout

                //guarda IP jugador
                jugadores++;
                if (jugadores == 1) {
                    inetAddressJ1 = packet.getAddress();
                    portJ1 = packet.getPort();
                    String num = "1";
                    msgSend = num.getBytes();
                    socket.send(new DatagramPacket(msgSend, msgSend.length, inetAddressJ1, portJ1));

                    packet = new DatagramPacket(msgReceive, msgReceive.length);
                    socket.receive(packet);

                    auxiliar = new String(packet.getData(), 0, packet.getLength());
                    if (auxiliar.equals("esperar")) {
                        System.out.println("Buscant jugador 2"); //WIP

                    }

                } else if (jugadores == 2) {
                    inetAddressJ2 = packet.getAddress();
                    portJ2 = packet.getPort();

                    String num = "2";
                    msgSend = num.getBytes();
                    socket.send(new DatagramPacket(msgSend, msgSend.length, inetAddressJ2, portJ2));
                    break;
                }
            }

        } catch (IOException e) {
        }

    }

    public void jocCoop() {

        try {

            DatagramPacket packet;

            inetAddress = InetAddress.getByName(srvIp);

            byte[] msgRecibir = new byte[1024];
            byte[] msgEnviar = new byte[1024];
            String stringRecibir;
            String resultado;

            msgEnviar = new String("Comença").getBytes();
            packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
            socket.send(packet);
            packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ2, portJ2);
            socket.send(packet);
            System.out.println("Conemça el joc");

            while (jugant) {
                if (turn == 1) {
                    msgEnviar = new String("turn").getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                    socket.send(packet);
                    msgEnviar = new String("noturn").getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ2, portJ2);
                    socket.send(packet);
                } else if (turn == 2) {
                    msgEnviar = new String("turn").getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ2, portJ2);
                    socket.send(packet);
                    msgEnviar = new String("noturn").getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                    socket.send(packet);
                }

                packet = new DatagramPacket(msgRecibir, msgRecibir.length);
                socket.receive(packet);

                stringRecibir = new String(packet.getData(), 0, packet.getLength());
                resultado = comprobarCoords(stringRecibir);

                msgEnviar = new String(resultado).getBytes();
                if (turn == 1) {
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                } else if (turn == 2) {
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ2, portJ2);
                }
                socket.send(packet);

                jugant = false;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (tauler[i][j] != '·') {
                            jugant = true;
                        }
                    }
                }

                if (!jugant) {
                    String enviarMensaje = "Fi del joc! Jugador 1: " + puntsJ1 + " puntos ; Jugador 2: " + puntsJ2 + " puntos.";
                    msgEnviar = new String(enviarMensaje).getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                    socket.send(packet);
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ2, portJ2);
                    socket.send(packet);
                    break;
                }

                if (turn == 1) {
                    turn = 2;
                } else if (turn == 2) {
                    turn = 1;
                }






            }


        } catch (IOException e) {
        }



    }

    public void juegoSingle() {

        try {

            DatagramPacket packet;

            inetAddress = InetAddress.getByName(srvIp);

            byte[] msgRecibir = new byte[1024];
            byte[] msgEnviar = new byte[1024];
            String stringRecibir;
            String resultado;

            msgEnviar = new String("start").getBytes();
            packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
            socket.send(packet);
            System.out.println("Se empieza el juego");

            while (jugant) {
                msgEnviar = new String("turn").getBytes();
                packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                socket.send(packet);

                packet = new DatagramPacket(msgRecibir, msgRecibir.length);
                socket.receive(packet);

                stringRecibir = new String(packet.getData(), 0, packet.getLength());
                resultado = comprobarCoords(stringRecibir);

                msgEnviar = new String(resultado).getBytes();
                packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                socket.send(packet);

                jugant = false;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (tauler[i][j] != '·') {
                            jugant = true;
                        }
                    }
                }

                if (!jugant) {
                    String enviarMensaje = "Fin del joc! Jugador 1: " + puntsJ1 + " punts ; Jugador 2: " + puntsJ2 + " punts.";
                    msgEnviar = new String(enviarMensaje).getBytes();
                    packet = new DatagramPacket(msgEnviar, msgEnviar.length, inetAddressJ1, portJ1);
                    socket.send(packet);
                    break;
                }

                if (turn == 1) {
                    turn = 2;
                } else if (turn == 2) {
                    turn = 1;
                }






            }


        } catch (IOException e) {
        }



    }

    private String comprobarCoords(String coordenadas) {

        int n2 = Character.getNumericValue(coordenadas.charAt(0));
        int n1 = Character.getNumericValue(coordenadas.charAt(1));
        int barco;
        boolean destruido = true;

        if (tauler[n1][n2] == '·') {
            return "0";
        } else {
            barco = Character.getNumericValue(tauler[n1][n2]);
            tauler[n1][n2] = '·';

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (Character.getNumericValue(tauler[i][j]) == barco) {
                        destruido = false;
                    }
                }
            }

            if (destruido) {
                if (turn == 1) {
                    puntsJ1 += 5;
                } else {
                    puntsJ2 += 5;
                }
                return "2"; //HUNDIDO
            } else {
                if (turn == 1) {
                    puntsJ1++;
                } else {
                    puntsJ2++;
                }
                return "1";
            }

        }


    }

    public void nouTauler() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tauler[i][j] = '·';
            }
        }

        //J1 TAULER
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                taulerJ1[i][j] = '·';
            }
        }

        //J2 TAULER
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                taulerJ2[i][j] = '·';
            }
        }



        //tauler vaixells
        tauler[0][0] = '0';
        tauler[0][1] = '0';
        tauler[0][2] = '0';
        tauler[0][3] = '0';

        tauler[0][9] = '1';
        tauler[1][9] = '1';
        tauler[2][9] = '1';

        tauler[9][0] = '2';
        tauler[9][1] = '2';
        tauler[9][2] = '2';

        tauler[4][2] = '3';
        tauler[3][2] = '3';

        tauler[5][7] = '4';
        tauler[5][8] = '4';

        tauler[9][7] = '5';
        tauler[9][8] = '5';

        tauler[7][4] = '6';

        tauler[3][5] = '7';

        tauler[1][7] = '8';

        tauler[5][4] = '9';

    }

    public void mostrarTauler() { //WIP
        System.out.println("    0   1   2   3   4   5   6   7   8   9");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + "   ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tauler[i][j] + "   ");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {

        EnfonsarLaFlotaServidor enfonsarLaFlotaServidor = new EnfonsarLaFlotaServidor();
        enfonsarLaFlotaServidor.nouTauler();

        enfonsarLaFlotaServidor.lobby();

        if (enfonsarLaFlotaServidor.jugadores == 1) {
            enfonsarLaFlotaServidor.juegoSingle();
        } else if (enfonsarLaFlotaServidor.jugadores == 2) {
            enfonsarLaFlotaServidor.jocCoop();
        }

    }

}
