package net.henbit.yatagarasu;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main
{
    // ポート番号
    public static final int PORT = 9090;
    private final Map<Integer, Boolean> keys = new HashMap<>();
    // 動作のモード
    private Mode mode = Mode.None;
    // 接続先
    private InetSocketAddress inetSocketAddress;
    // シャットダウンフックが登録済みか
    private boolean isHooked = false;
    // クライアントソケット
    private AsynchronousSocketChannel clientSocket;
    // サーバソケット
    private AsynchronousServerSocketChannel serverSocket;
    // ロボット
    private Robot robot;
    // 有効か
    private boolean enable = false;
    // 受信用
    private ReceiverThread receiverThread;
    // キーフック解除
    private final Thread shutdownThread = new Thread(this::run);

    public static void main(String[] args)
    {
        // 設定をリセット
        LogManager.getLogManager().reset();

        // jnativehookのloggerをオフにする
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        Main instance = new Main();
        instance.init();

        SwingUtilities.invokeLater(() -> new Window(instance));
    }

    private void init()
    {
        this.receiverThread = new ReceiverThread(this);

        try
        {
            this.robot = new Robot();
        }
        catch (AWTException awtException)
        {
            awtException.printStackTrace();
        }
    }

    public Mode getMode()
    {
        return mode;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public void addShutdownHook()
    {
        if (!isHooked)
        {
            // VMのシャットダウン時のHook
            Runtime.getRuntime().addShutdownHook(shutdownThread);
            isHooked = true;
        }
    }

    public void removeShutdownHook()
    {
        if (isHooked)
        {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            isHooked = false;
        }
    }

    public AsynchronousServerSocketChannel getServerSocket()
    {
        return serverSocket;
    }

    public void setServerSocket(AsynchronousServerSocketChannel serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public AsynchronousSocketChannel getClientSocket()
    {
        return clientSocket;
    }

    public void setClientSocket(AsynchronousSocketChannel clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    private void run()
    {
        try
        {
            GlobalScreen.unregisterNativeHook();
            if (clientSocket != null && clientSocket.isOpen())
            {
                clientSocket.close();
            }
            if (serverSocket != null && serverSocket.isOpen())
            {
                serverSocket.close();
            }
            if (receiverThread.isRunning())
            {
                receiverThread.shutdown();
            }
        }
        catch (NativeHookException | IOException nativeHookException)
        {
            nativeHookException.printStackTrace();
        }
    }

    public InetSocketAddress getInetSocketAddress()
    {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress address)
    {
        this.inetSocketAddress = address;
    }

    public Robot getRobot()
    {
        return robot;
    }

    public boolean isEnable()
    {
        return enable;
    }

    public void setEnable()
    {
        this.enable = true;
    }

    public void setDisable()
    {
        this.enable = false;
    }

    public Map<Integer, Boolean> getKeys()
    {
        return keys;
    }

    public ReceiverThread getReceiverThread()
    {
        return receiverThread;
    }

    public enum Mode
    {
        Sender,
        Receiver,
        None
    }

}
