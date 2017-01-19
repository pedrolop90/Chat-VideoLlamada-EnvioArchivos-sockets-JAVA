
package Negocio;

import Cliente.Cliente;
import Servidor.Servidor;
import java.awt.image.BufferedImage;
import java.util.LinkedList;


public class Negocio implements Runnable{
    
    private Cliente yo;
    private Servidor server;
    private LinkedList<Cliente> contactos;
    private Thread hiloEnviarMensaje;
    
    
    public Negocio(){
       contactos=new  LinkedList<>();
        server=new Servidor();
        yo=new Cliente("","");
        hiloEnviarMensaje=new Thread(this);
        hiloEnviarMensaje.start();
    }

    public void enviarMensaje(String ip,String mensaje){
        server.enviarMensaje(ip,yo.getIp()+"___"+mensaje);
        Cliente cliente=buscarContacto(ip);
        cliente.setHistorial(cliente.getHistorial()+"\nyo: "+mensaje);
    }
    
    public Cliente buscarContacto(String ip) {
        for (Cliente cliente : contactos) {
            if (cliente.getIp().equalsIgnoreCase(ip)) {
                return cliente;
            }
        }
        return null;
    }
    
    public String mostrarHistorialContacto(String ip){
      return buscarContacto(ip).getHistorial();
    }
    
    public void contactosConectados(){
         for (Cliente cliente : contactos) {
           cliente.setConectado(server.estaConectado(cliente.getIp()));
        }
    }
    
    public boolean agregarContacto(String nombre,String ip){
        if(buscarContacto(ip)==null){
            contactos.add(new Cliente(nombre,ip));
            return true;
        }
           return false;
    }
    
    public void enviarArchivo(String ip,String direccion){
        server.enviarArchivo(ip,direccion);
    }
    
    public void asignarmiIp(){
        yo.setIp(server.ip());
    }
    
    @Override
    public void run() {  
       while(true){
           if(server.getcolaMensajes().size()>0){
               String datoRecibido=server.getcolaMensajes().removeFirst();
               String ip=datoRecibido.split("___")[0];
               String mensaje=datoRecibido.split("___")[1];
               Cliente cliente=buscarContacto(ip);
               if(cliente==null){
                   cliente=new Cliente("sin nombre",ip);
                   contactos.add(cliente);
               }
               cliente.setHistorial(cliente.getHistorial()+"\n"+cliente.getNombre()+": "+mensaje);
           }
           try {
               Thread.sleep(100);
           } catch (InterruptedException ex) {
              ex.printStackTrace();
           }
       }
    }

    public LinkedList<Cliente> getContactos() {
        return contactos;
    }

    public Cliente getYo() {
        return yo;
    }

    public void setYo(Cliente yo) {
        this.yo = yo;
    }

    public void cambiarRaiz(String raiz){
        server.cambiarArchivoRaiz(raiz);
    }

    public Servidor getServer() {
        return server;
    }

    public void setServer(Servidor server) {
        this.server = server;
    }
    
    
    
}
