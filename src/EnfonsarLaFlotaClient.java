import java.io.IOException;
import java.net.*;
import java.util.Scanner;

//Client
public class EnfonsarLaFlotaClient {

    char[][] tablero = new char[10][10];
    int port;
    DatagramSocket socket;
    InetAddress ip;
    Scanner sc = new Scanner(System.in);
    boolean jugant = true;
    boolean coordsCorrectas = false;

    public void creaTauler() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tablero[i][j] = '·';
            }
        }
    }

    public void mostraTauler() { //WIP
        System.out.println("    0   1   2   3   4   5   6   7   8   9");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + "   ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tablero[i][j] + "   ");
            }
            System.out.print("\n");
        }
    }

    public void init () throws UnknownHostException, SocketException {
        ip = InetAddress.getLocalHost();
        System.out.println("Aplica el port al que vols connectar-te: ");
        port = sc.nextInt();
        socket = new DatagramSocket();
    }

    public void runClient () throws IOException {
        Scanner scanner = new Scanner(System.in);
        byte[] reciveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String sendString;
        String receivedString;
        String auxiliar;

        String s = "Mensaje a servidor";
        sendData = s.getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ip, port);
        socket.send(packet);

        packet = new DatagramPacket(reciveData, reciveData.length);

        socket.receive(packet);

        receivedString = new String(packet.getData(),0,packet.getLength());
        if (receivedString.equals("1")) {
            System.out.println("Benvingut Jugador 1");
            System.out.println("Pressiona 1 per bucar un altre jugador.\n1. Buscar un altre jugador");
            auxiliar = scanner.next();
            if (auxiliar.equals("1")) {

                sendString = "esperar";
                sendData = sendString.getBytes();
                packet = new DatagramPacket(sendData, sendData.length, ip, port);
                socket.send(packet);
            }
        } else if (receivedString.equals("2")) {
            System.out.println("Benvingut Jugador 2");
        }


        //INICIO PARTIDA

        packet = new DatagramPacket(reciveData, reciveData.length);
        socket.receive(packet);
        receivedString = new String(packet.getData(), 0, packet.getLength());

        if (receivedString.equals("start")) {
            System.out.println("Comença la partida");
        }

        //JUEGO
        int[] coordenadas = {-1,-1};

        while (jugant) {
            packet = new DatagramPacket(reciveData, reciveData.length);
            socket.receive(packet);
            receivedString = new String(packet.getData(), 0, packet.getLength());

            if (receivedString.equals("turn")) {
                coordsCorrectas = false;
                while (!coordsCorrectas) {
                    mostraTauler();
                    System.out.println("Indica donde quieres atacar: ");
                    coordenadas = procesarCoords(scanner.next());

                    if (coordenadas[0] != -1) {
                        coordsCorrectas = true;

                        //Enviar jugada

                        sendString = Integer.toString(coordenadas[0]) + Integer.toString(coordenadas[1]);
                        sendData = sendString.getBytes();
                        packet = new DatagramPacket(sendData, sendData.length, ip, port);
                        socket.send(packet); //Envia jugada

                        packet = new DatagramPacket(reciveData, reciveData.length);
                        socket.receive(packet); //Recibe resultado de jugada
                        receivedString = new String(packet.getData(), 0, packet.getLength());

                        if (receivedString.equals("0")) {
                            tablero[coordenadas[1]][coordenadas[0]] = '~';
                            System.out.println("Aigua");
                        } else if (receivedString.equals("1")) {
                            tablero[coordenadas[1]][coordenadas[0]] = 'X';
                            System.out.println("Tocat!\n+1 Punt");
                        } else if (receivedString.equals("2")) {
                            tablero[coordenadas[1]][coordenadas[0]] = 'X';
                            System.out.println("Enfonsat!\n+5 Punts");
                        }

                    } else {
                        System.out.println("Error de sintaxis!");
                        coordsCorrectas = false;
                    }

                }
            } else if (receivedString.equals("noturn")) {
                System.out.println("Espera el teu torn turn!");

            } else {

                System.out.println(receivedString);
                jugant = false;
                break;

            }


        }

    }

    private int[] procesarCoords(String coordsString) {
        int[] coordenadas = new int[2];

        if (coordsString.length() == 2) {
            if (coordsString.charAt(0) == '0' || coordsString.charAt(0) == '1' || coordsString.charAt(0) == '2' || coordsString.charAt(0) == '3' || coordsString.charAt(0) == '4' || coordsString.charAt(0) == '5' || coordsString.charAt(0) == '6' || coordsString.charAt(0) == '7' || coordsString.charAt(0) == '8' || coordsString.charAt(0) == '9') {
                if (coordsString.charAt(1) == '0' || coordsString.charAt(1) == '1' || coordsString.charAt(1) == '2' || coordsString.charAt(1) == '3' || coordsString.charAt(1) == '4' || coordsString.charAt(1) == '5' || coordsString.charAt(1) == '6' || coordsString.charAt(1) == '7' || coordsString.charAt(1) == '8' || coordsString.charAt(1) == '9') {

                    if (coordsString.charAt(0) == '0') {
                        coordenadas[0] = 0;
                    } else if (coordsString.charAt(0) == '1') {
                        coordenadas[0] = 1;
                    } else if (coordsString.charAt(0) == '2') {
                        coordenadas[0] = 2;
                    } else if (coordsString.charAt(0) == '3') {
                        coordenadas[0] = 3;
                    } else if (coordsString.charAt(0) == '4') {
                        coordenadas[0] = 4;
                    } else if (coordsString.charAt(0) == '5') {
                        coordenadas[0] = 5;
                    } else if (coordsString.charAt(0) == '6') {
                        coordenadas[0] = 6;
                    } else if (coordsString.charAt(0) == '7') {
                        coordenadas[0] = 7;
                    } else if (coordsString.charAt(0) == '8') {
                        coordenadas[0] = 8;
                    } else if (coordsString.charAt(0) == '9') {
                        coordenadas[0] = 9;
                    }

                    coordenadas[1] = Character.getNumericValue(coordsString.charAt(1));
                    return coordenadas;

                }
            }
        }
        coordenadas[0] = -1;
        coordenadas[1] = -1;
        return coordenadas;
    }

    public static void main(String[] args) throws IOException {
        EnfonsarLaFlotaClient c = new EnfonsarLaFlotaClient();

        c.creaTauler();
        c.init();
        c.runClient();
    }
}
