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

import static java.awt.event.KeyEvent.*;

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

        instance.receiverThread = new ReceiverThread(instance);

        try
        {
            instance.robot = new Robot();
        }
        catch (AWTException awtException)
        {
            awtException.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Window(instance));
    }

    @SuppressWarnings("DuplicatedCode")
    private static void init(final Main instance)
    {
        instance.getKeys().put(VK_ESCAPE, false);
        instance.getKeys().put(VK_F1, false);
        instance.getKeys().put(VK_F2, false);
        instance.getKeys().put(VK_F3, false);
        instance.getKeys().put(VK_F4, false);
        instance.getKeys().put(VK_F5, false);
        instance.getKeys().put(VK_F6, false);
        instance.getKeys().put(VK_F7, false);
        instance.getKeys().put(VK_F8, false);
        instance.getKeys().put(VK_F9, false);
        instance.getKeys().put(VK_F10, false);
        instance.getKeys().put(VK_F11, false);
        instance.getKeys().put(VK_F12, false);
        instance.getKeys().put(VK_BACK_QUOTE, false);
        instance.getKeys().put(VK_1, false);
        instance.getKeys().put(VK_2, false);
        instance.getKeys().put(VK_3, false);
        instance.getKeys().put(VK_4, false);
        instance.getKeys().put(VK_5, false);
        instance.getKeys().put(VK_6, false);
        instance.getKeys().put(VK_7, false);
        instance.getKeys().put(VK_8, false);
        instance.getKeys().put(VK_9, false);
        instance.getKeys().put(VK_0, false);
        instance.getKeys().put(VK_MINUS, false);
        instance.getKeys().put(VK_EQUALS, false);
        instance.getKeys().put(VK_BACK_SPACE, false);
        instance.getKeys().put(VK_TAB, false);
        instance.getKeys().put(VK_CAPS_LOCK, false);
        instance.getKeys().put(VK_A, false);
        instance.getKeys().put(VK_B, false);
        instance.getKeys().put(VK_C, false);
        instance.getKeys().put(VK_D, false);
        instance.getKeys().put(VK_E, false);
        instance.getKeys().put(VK_F, false);
        instance.getKeys().put(VK_G, false);
        instance.getKeys().put(VK_H, false);
        instance.getKeys().put(VK_I, false);
        instance.getKeys().put(VK_J, false);
        instance.getKeys().put(VK_K, false);
        instance.getKeys().put(VK_L, false);
        instance.getKeys().put(VK_M, false);
        instance.getKeys().put(VK_N, false);
        instance.getKeys().put(VK_O, false);
        instance.getKeys().put(VK_P, false);
        instance.getKeys().put(VK_Q, false);
        instance.getKeys().put(VK_R, false);
        instance.getKeys().put(VK_S, false);
        instance.getKeys().put(VK_T, false);
        instance.getKeys().put(VK_U, false);
        instance.getKeys().put(VK_V, false);
        instance.getKeys().put(VK_W, false);
        instance.getKeys().put(VK_X, false);
        instance.getKeys().put(VK_Y, false);
        instance.getKeys().put(VK_Z, false);
        instance.getKeys().put(VK_OPEN_BRACKET, false);
        instance.getKeys().put(VK_CLOSE_BRACKET, false);
        instance.getKeys().put(VK_BACK_SLASH, false);
        instance.getKeys().put(VK_SEMICOLON, false);
        instance.getKeys().put(VK_QUOTE, false);
        instance.getKeys().put(VK_ENTER, false);
        instance.getKeys().put(VK_PERIOD, false);
        instance.getKeys().put(VK_SLASH, false);
        instance.getKeys().put(VK_SPACE, false);
        instance.getKeys().put(VK_PRINTSCREEN, false);
        instance.getKeys().put(VK_SCROLL_LOCK, false);
        instance.getKeys().put(VK_PAUSE, false);
        instance.getKeys().put(VK_INSERT, false);
        instance.getKeys().put(VK_DELETE, false);
        instance.getKeys().put(VK_HOME, false);
        instance.getKeys().put(VK_END, false);
        instance.getKeys().put(VK_PAGE_UP, false);
        instance.getKeys().put(VK_PAGE_DOWN, false);
        instance.getKeys().put(VK_UP, false);
        instance.getKeys().put(VK_LEFT, false);
        instance.getKeys().put(VK_CLEAR, false);
        instance.getKeys().put(VK_RIGHT, false);
        instance.getKeys().put(VK_DOWN, false);
        instance.getKeys().put(VK_NUM_LOCK, false);
        instance.getKeys().put(VK_SEPARATER, false);
        instance.getKeys().put(VK_SHIFT, false);
        instance.getKeys().put(VK_CONTROL, false);
        instance.getKeys().put(VK_ALT, false);
        instance.getKeys().put(VK_META, false);
        instance.getKeys().put(VK_CONTEXT_MENU, false);
        instance.getKeys().put(VK_KATAKANA, false);
        instance.getKeys().put(VK_UNDERSCORE, false);
        instance.getKeys().put(VK_KANJI, false);
        instance.getKeys().put(VK_HIRAGANA, false);
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
