package socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * describe : NIO selector
 * Created by jiadu on 2017/10/26 0026.
 */
public class SelectSockets {
    private static final int PORT = 8082;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        SelectSockets ss = new SelectSockets();
        ss.go();
    }

    public void go() throws IOException {

        System.out.println("listening on port:" + PORT);

        ServerSocketChannel ssc = ServerSocketChannel.open();//打开channel

        ServerSocket ss = ssc.socket();//打开socket

        Selector selector = Selector.open();//多路复用器

        ss.bind(new InetSocketAddress(PORT));

        ssc.configureBlocking(false);

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int n = selector.select();

            if (n == 0) {
                continue;
            }

            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key
                            .channel();
                    SocketChannel client = server.accept();
                    register(selector, client, SelectionKey.OP_READ);
                    System.out.println("Accept client:" + client);
                    acceptClient(client);
                }
                if (key.isReadable()) {
                    readData(key);
                }
                iter.remove();
            }
        }
    }

    protected void register(Selector selector, SelectableChannel channel,
                            int ops) throws IOException {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);

        channel.register(selector, ops);

    }

    protected void readData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count;
        while ((count = socketChannel.read(buffer)) > 0) {
            buffer.flip();

            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            buffer.clear();

            if (count < 0) {
                socketChannel.close();
            }
        }
    }

    private void acceptClient(SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put("you have already connected server!".getBytes());
        buffer.flip();

        channel.write(buffer);
    }
}
