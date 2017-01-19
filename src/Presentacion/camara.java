
package Presentacion;

import javax.swing.*;
import com.github.sarxos.webcam.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.imageio.ImageIO;

public class camara extends JPanel implements Runnable{
    
     private Webcam camara;
     private Thread hilo;
     private server server;
     private Chat chat;
     private boolean iniciarTransmision=false;
     private Socket socket;
     private byte imagen[];
     private BufferedImage img ;
     private static final int puerto=9000;
     private boolean cerrarConexion=true;
     
     public camara(Chat c){
        chat = c;
        this.setSize(200, 400);
        this.setLayout(null);
        this.setVisible(true);
        hilo = new Thread(this);
        hilo.start();
    }

      public void paint(Graphics g){
         if(img!=null){
             g.drawImage(img, 0, 215,img.getWidth(),img.getHeight(), this);
         }else
              g.clearRect(0, 215, 200, 200);
        
     }
     
    @Override
    public void run() {
        while (cerrarConexion) {
            if (iniciarTransmision) {
                BufferedInputStream entradaImagen = null;
                DataInputStream entrada = null;
                ByteArrayInputStream baos = null;
                try {
                    socket = new Socket(chat.getCliente().getIp(), puerto);
                    entradaImagen = new BufferedInputStream(socket.getInputStream());
                    entrada = new DataInputStream(socket.getInputStream());
                    while (true) {
                        int x = entrada.readInt();
                        if (x > 0) {
                            imagen = new byte[x];
                            baos = new ByteArrayInputStream(imagen);
                            for (int i = 0; i < imagen.length; i++) {
                                imagen[i] = (byte) entradaImagen.read();
                            }
                            img = ImageIO.read(baos);
                            repaint();
                            baos.close();
                        }
                        if (!this.isEnabled()) {
                            try {
                                entradaImagen.close();
                                entrada.close();
                                socket.close();
                            } catch (IOException ex1) {
                                ex1.printStackTrace();
                            }
                            return;
                        }
                    }
                } catch (IOException ex) {
                    try {
                        entradaImagen.close();
                        entrada.close();
                        socket.close();
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                        ex.printStackTrace();
                }
            }
            repaint();
        }
    }

    public boolean iniciarTransmision() {
        return iniciarTransmision=true;
    }

    public void cerrarTransmision() {
        this.iniciarTransmision = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void cerrarConexionLlamada(){
        cerrarConexion=false;
    }
    
    
}
