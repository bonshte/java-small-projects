package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    //change the port manually, it is not telepathy
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 2048;

    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");
            int rows = setUp(scanner, "rows");
            int cols = setUp(scanner, "cols");
            String dimensions = rows + " " + cols;
            buffer.clear();
            buffer.put(dimensions.getBytes());
            buffer.flip();
            socketChannel.write(buffer);

            while (true) {
                System.out.println("enter points coordinates and value or quit to exit");
                String message = scanner.nextLine();
                message = message.trim();

                if ("quit".equals(message)) {
                    break;
                }
                if (!validPoints(message, rows, cols)) {
                    System.out.println("invalid point passed, format is: <x> <y> <value>," +
                            " make sure dimensions are within the created matrix");
                    continue;
                }

                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();
                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, "UTF-8");

                System.out.println(reply);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static int setUp(Scanner scanner, String toSet) {

        System.out.println("enter " + toSet);
        int result = Integer.parseInt(scanner.nextLine().trim());
        while (result < 1 || result > 50) {
            System.out.println(toSet + " must be between 1 and 50, enter again");
            result = Integer.parseInt(scanner.nextLine());
        }
        return result;
    }

    private static boolean validPoints(String pointStr,int rows, int cols) {
        String[] points = pointStr.split(" ");
        if (points.length % 3 != 0) {
            return false;
        }
        for (int i = 0; i < points.length; i += 3) {
            if ((Integer.parseInt(points[i]) < 1 || Integer.parseInt(points[i]) > rows) ||
                    (Integer.parseInt(points[i + 1])  < 1 || Integer.parseInt(points[i + 1]) > cols)) {
                return false;
            }
        }
        return true;
    }


}
