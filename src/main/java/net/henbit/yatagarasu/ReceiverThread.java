package net.henbit.yatagarasu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReceiverThread extends Thread
{

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final Main instance;

    public ReceiverThread(final Main instance)
    {
        this.instance = instance;
    }

    @Override
    public void start()
    {
        isRunning.set(true);
        new Thread(this).start();
    }

    @Override
    public void run()
    {
        try
        {
            while (isRunning.get())
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                instance.setServerSocket(AsynchronousServerSocketChannel.open());
                instance.getServerSocket().bind(new InetSocketAddress(Main.PORT));
                Future<AsynchronousSocketChannel> acceptFuture = instance.getServerSocket().accept();
                AsynchronousSocketChannel worker = acceptFuture.get();
                worker.read(byteBuffer).get();
                byteBuffer.clear();

                int keyEventKeyCode = Utils.getKeyEventKeyCode(byteBuffer.getInt());
                if (keyEventKeyCode != Utils.UNDEFINED && instance.isEnable())
                {
                    if (instance.getKeys().get(keyEventKeyCode) == null || !instance.getKeys().get(keyEventKeyCode))
                    {
                        instance.getKeys().put(keyEventKeyCode, true);
                        instance.getRobot().keyPress(keyEventKeyCode);
//                        System.out.println("Pressed: " + KeyEvent.getKeyText(keyEventKeyCode));
                    }
                    else
                    {
                        instance.getKeys().put(keyEventKeyCode, false);
                        instance.getRobot().keyRelease(keyEventKeyCode);
//                        System.out.println("Released: " + KeyEvent.getKeyText(keyEventKeyCode));
                    }
                }

                worker.close();
                instance.getServerSocket().close();
            }
        }
        catch (IOException | InterruptedException | ExecutionException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * @return is running
     */
    public boolean isRunning()
    {
        return isRunning.get();
    }

    /**
     * shutdown thread
     */
    public void shutdown()
    {
        isRunning.set(false);
    }
}
