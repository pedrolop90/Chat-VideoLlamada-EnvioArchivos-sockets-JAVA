

package Servidor;

import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;

public class recibirArchivo extends Thread{
    
    DataOutputStream cliente;
    String direccionArchivo;
    Socket sk;
    String nombreArchivo;
    
    public recibirArchivo(DataOutputStream flujo, String nomArch,Socket sk,InputStream input,String nombre) {
        cliente = flujo;
        direccionArchivo = nomArch;
        this.sk=sk;
        nombreArchivo=nombre;
        start();
    }
    public void run(){
         try {
            BufferedWriter outReader = new BufferedWriter(new OutputStreamWriter(sk.getOutputStream()));
            FileOutputStream wr = new FileOutputStream(new File(direccionArchivo+"\\"+nombreArchivo));
            InputStream input = sk.getInputStream();  
            byte[] buffer = new byte[sk.getReceiveBufferSize()];
            int bytesReceived = 0;
            JOptionPane.showMessageDialog(null,"Inicia la descarga del archivo");
            while ((bytesReceived = input.read(buffer)) > 0){
                wr.write(buffer, 0, bytesReceived);
            }
            wr.flush();
            outReader.flush();
            JOptionPane.showMessageDialog(null, "Termino la descarga del archivo...");
            outReader.close();
            input.close();
            wr.close();
            cliente.close();
            sk.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
