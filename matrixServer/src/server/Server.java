package server;

import matrix.InvalidMatrixDimensionsException;
import matrix.MatrixFiller;
import point.InvalidPointException;
import matrix.Matrix;
import point.Point;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable{
    private static final int BUFFER_SIZE = 2048;

    private static final String HOST = "localhost";

    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        //creating the server
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        //the server was stopped from admin
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            Object matrix = key.attachment();
                            String clientInput = getClientInput(clientChannel);
                            if (clientInput == null) {
                                //problem with the connection (client connection lost)
                                continue;
                            }
                            if ( matrix == null) {
                                try {
                                    key.attach(setUpMatrix(clientInput));
                                } catch (InvalidMatrixDimensionsException e) {
                                    writeClientOutput(clientChannel, "invalid dimensions format passed to server");
                                }
                            } else {
                                try {
                                    List<Point> pointsPassed = getPoints(clientInput);
                                    Matrix theMatrix = (Matrix) matrix;
                                    //removes all points that go out of bounds
                                    //any cell that is passed twice will be added only as its last input
                                    //since parallel execution is undetermined in order
                                    Set<Point> validPoints = getValidPoints(pointsPassed, theMatrix);
                                    List<Thread> matrixFillerThreads = getListOfFillerThreads(validPoints, theMatrix);
                                    if (matrixFillerThreads.size() == 1) {
                                        //if it is only one we don't want to create a new thread, let it run on this one
                                        matrixFillerThreads.iterator().next().run();
                                    } else {
                                        try (ExecutorService fillerServiceExecutor = Executors.newFixedThreadPool(matrixFillerThreads.size())) {
                                            for (var matrixFiller : matrixFillerThreads) {
                                                fillerServiceExecutor.execute(matrixFiller);
                                            }
                                            fillerServiceExecutor.shutdown();
                                            try {
                                                fillerServiceExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException("jvm interrupted the thread before all tasks were finished", e);
                                            }
                                        }
                                    }
                                    writeClientOutput(clientChannel, theMatrix.getMatrixAsString());
                                } catch (InvalidPointException e) {
                                    writeClientOutput(clientChannel, "invalid point passed");
                                }
                            }


                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            //if client socket side has closed
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private Matrix setUpMatrix(String request) throws InvalidMatrixDimensionsException {
        String[] dimensions = request.split(" ");
        if (dimensions.length != 2) {
            throw new InvalidMatrixDimensionsException("dimensions passed are not 2");
        }
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        return new Matrix(rows, cols);
    }
    private List<Point> getPoints(String request) throws InvalidPointException {
        List<Point> pointsPassed = new LinkedList<>();
        String[] points = request.split(" ");
        if (points.length % 3 != 0 || points.length == 0) {
            throw new InvalidPointException("invalid point passed");
        }
        for (int i = 0; i < points.length; i+=3 ){
            int x = Integer.parseInt(points[i]) - 1;
            int y = Integer.parseInt(points[i + 1]) - 1;
            int value = Integer.parseInt(points[i + 2]);
            pointsPassed.add(new Point(x, y, value));
        }
        return pointsPassed;

    }
    private boolean validPoint(Point point, Matrix matrix) {
        return !(point.getX() < 0 || point.getX() >= matrix.getRows() ||
            point.getY() < 0 || point.getY() >= matrix.getCols());
    }
    private Set<Point> getValidPoints(List<Point> points, Matrix matrix) {
        Set<Point> validPoints = new HashSet<>();
        for (var point : points) {
            if (validPoint(point, matrix)) {
                //points are equal if the represent the same cell
                //we want the latest instance of a cell in the list to be added to the set
                if (validPoints.contains(point)) {
                    validPoints.remove(point);
                }
                validPoints.add(point);
            }
        }
        return validPoints;
    }
    private List<Thread> getListOfFillerThreads (Set<Point> validPoints, Matrix matrix) {
        List<Thread> fillerThreads = new LinkedList<>();
        for (var point : validPoints) {
            Thread fillerThread = new Thread(new MatrixFiller(matrix, point));
            fillerThreads.add(fillerThread);
        }
        return fillerThreads;
    }
}