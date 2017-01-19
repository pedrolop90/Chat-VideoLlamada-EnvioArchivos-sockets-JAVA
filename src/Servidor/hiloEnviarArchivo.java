
package Servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

public class hiloEnviarArchivo extends Thread{
    
    private Socket cliente;
    private File archivo;
    
    public hiloEnviarArchivo(Socket c,File f){
        cliente=c;
        archivo=f;
        start();
    }
    
    public void run(){
        try{
                JOptionPane.showMessageDialog(null,"Inicia la transferencia del archivo...");
                OutputStream output = cliente.getOutputStream();
                FileInputStream file = new FileInputStream(archivo);
                byte[] buffer = new byte[cliente.getSendBufferSize()];
                int bytesRead = 0;
                while ((bytesRead = file.read(buffer)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                file.close();
                cliente.close();
               JOptionPane.showMessageDialog(null, "Termino la transferencia del archivo...");
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
