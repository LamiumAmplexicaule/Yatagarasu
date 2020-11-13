package net.henbit.yatagarasu;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class GlobalKeyListener implements NativeKeyListener
{

    // インスタンス
    private final Main instance;

    // バッファー
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(4);

    public GlobalKeyListener(final Main instance)
    {
        this.instance = instance;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e)
    {
//        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
//        System.out.println("Key Pressed: " + e.getKeyCode());
//        System.out.println("Key Pressed(Raw): " + e.getRawCode());

        if (instance.isEnable())
        {
            try
            {
                instance.setClientSocket(AsynchronousSocketChannel.open());
                instance.getClientSocket().connect(instance.getInetSocketAddress()).get();
                byteBuffer.clear();
                byteBuffer.putInt(e.getKeyCode());
                byteBuffer.flip();
                instance.getClientSocket().write(byteBuffer).get();
                instance.getClientSocket().close();
            }
            catch (InterruptedException | ExecutionException | IOException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e)
    {
//        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
//        System.out.println("Key Released: " + e.getKeyCode());
//        System.out.println("Key Released(Raw): " + e.getRawCode());

        if (instance.isEnable())
        {
            try
            {
                instance.setClientSocket(AsynchronousSocketChannel.open());
                instance.getClientSocket().connect(instance.getInetSocketAddress()).get();
                byteBuffer.clear();
                byteBuffer.putInt(e.getKeyCode());
                byteBuffer.flip();
                instance.getClientSocket().write(byteBuffer).get();
                instance.getClientSocket().close();
            }
            catch (InterruptedException | IOException exception)
            {
                exception.printStackTrace();
            }
            catch (ExecutionException executionException)
            {
                try
                {
                    instance.getClientSocket().close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e)
    {
        // ignore
    }
}
