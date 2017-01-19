
package Servidor;
import Cliente.Cliente;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable{
    
    private ServerSocket server;
    private Socket peticion;
    private Socket peticionEnviar;
    private LinkedList<String> colaMensajes;
    private Thread hiloEnviarMensajes; 
    private final static int puerto=8000;
    private final static int puertoCamara=9000;
    private String directorioArchivos;
    private Socket peticionArchivo;
    private BufferedImage imagen;
    private Socket estado;
    
    public Servidor(){
        hiloEnviarMensajes=new Thread(this);
        hiloEnviarMensajes.start();
    }

    @Override
    public void run() {
        try {
            colaMensajes=new LinkedList<String>();
            server=new ServerSocket(puerto);
            while(true){
                peticion=server.accept();
                DataInputStream dato=new DataInputStream(peticion.getInputStream());
                String ms=dato.readUTF();
                if(ms.contains(">>>")&&ms.split(">>>")[0].equalsIgnoreCase("archivo")){
                    InputStream input = peticion.getInputStream();
                    DataOutputStream flujo = new DataOutputStream(peticion.getOutputStream());
                    new recibirArchivo(flujo,directorioArchivos, peticion, input,ms.split(">>>")[1]);
                }else if(!ms.equalsIgnoreCase("conectado")){
                    colaMensajes.addLast(ms);
                    dato.close();
                    peticion.close();
                }
                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
           ex.printStackTrace();
        }
    }
    
    public void enviarArchivo(String ip,String direccion){
         try {
            peticionArchivo=new Socket(ip,puerto);
            File f=new File(direccion);
            DataOutputStream datoEnviado=new DataOutputStream(peticionArchivo.getOutputStream());
                datoEnviado.writeUTF("archivo>>>"+f.getName());
                datoEnviado.flush();
            new hiloEnviarArchivo(peticionArchivo,f);
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
    
    public void enviarMensaje(String ip,String mensaje){
        try {
            peticionEnviar=new Socket(ip,puerto);
            DataOutputStream datoEnviado=new DataOutputStream(peticionEnviar.getOutputStream());
            datoEnviado.writeUTF(mensaje);
            datoEnviado.flush();
            datoEnviado.close();
            peticionEnviar.close();
        } catch (IOException ex) {
            if(peticionEnviar!=null){
                try {
                    peticionEnviar.close();
                } catch (IOException ex1) {
                    ex.printStackTrace();
                }
            }
           ex.printStackTrace();
        }
    }
    
    public boolean estaConectado(String ip){
        try {
            estado=new Socket(ip, puerto);
            DataOutputStream datoEnviado=new DataOutputStream(estado.getOutputStream());
            datoEnviado.writeUTF("conectado");
            estado.close();
            return true;
        } catch (Exception ex) {
           if(estado!=null){
               try {
                   estado.close();
               } catch (IOException ex1) {
                  ex.printStackTrace();
               }
           }
        }
        return false;
    }
    
    public String ip(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
          ex.printStackTrace();
        }
        return "";
    }
    
    public void cambiarArchivoRaiz(String archivoRaiz){
        this.directorioArchivos=archivoRaiz;
    }
    
    public LinkedList<String> getcolaMensajes(){
        return colaMensajes;
    }

    public BufferedImage getImagen() {
        return imagen;
    }

    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
    }
    
}
