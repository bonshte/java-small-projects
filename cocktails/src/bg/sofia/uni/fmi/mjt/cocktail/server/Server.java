package bg.sofia.uni.fmi.mjt.cocktail.server;

import bg.sofia.uni.fmi.mjt.cocktail.command.Command;
import bg.sofia.uni.fmi.mjt.cocktail.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.cocktail.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cocktail.server.response.Response;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.DefaultCocktailStorage;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Server {
    private static final int BYTE_BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private ByteBuffer buffer;
    private Selector selector;
    private boolean isServerWorking;
    private int port;

    public Server(int port) {
        this.port = port;
    }


    public void start() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            selector = Selector.open();
            //server must be first set to a HOST and PORT, then must be made non blocking and registered to a selector
            configureServer(server, selector);
            buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
            DefaultCocktailStorage storage = new DefaultCocktailStorage();
            CommandExecutor commandExecutor = new CommandExecutor(storage);
            this.isServerWorking = true;

            while (isServerWorking) {
                //try catch to make sure exception while managing one client won't quit the server
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        if (selectionKey.isAcceptable()) {
                            // someone wants to connect to us
                            ServerSocketChannel theServerChannel = (ServerSocketChannel) selectionKey.channel();
                            accept(theServerChannel, selector);
                        } else if ( selectionKey.isReadable()) {
                            SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
                            String clientInput = readClientInput(clientSocket);
                            if (clientInput != null) {
                                Command command = CommandCreator.createCommand(clientInput);
                                Response commandResponse = commandExecutor.handleCommand(command);
                                Gson gson = new Gson();
                                String jsonResponse = gson.toJson(commandResponse);
                                writeToClientOutput(jsonResponse, clientSocket);
                            }
                        }
                        selectionKeyIterator.remove();
                    }

                } catch (IOException e) {
                    System.out.println("error processing client request");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }

    }

    private void configureServer(ServerSocketChannel server, Selector selector) throws IOException {
        server.bind(new InetSocketAddress(HOST, port));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void accept(ServerSocketChannel server, Selector selector) throws IOException {
        SocketChannel clientSocket = server.accept();
        clientSocket.configureBlocking(false);
        clientSocket.register(selector, SelectionKey.OP_READ);
    }

    void writeToClientOutput(String output, SocketChannel clientSocket) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();
        clientSocket.write(buffer);
    }
    String readClientInput(SocketChannel clientSocket) throws IOException {
        buffer.clear();

        int readBytes = clientSocket.read(buffer);
        if (readBytes < 0) {
            clientSocket.close();
            return null;
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
