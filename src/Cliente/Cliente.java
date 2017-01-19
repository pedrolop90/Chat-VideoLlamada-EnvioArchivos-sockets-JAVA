
package Cliente;

public class Cliente {
    
    private String nombre;
    private String ip;
    private String historial;
    private boolean conectado;
    
    public Cliente(String nombre, String ip) {
        this.nombre = nombre;
        this.ip = ip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHistorial() {
        return historial;
    }

    public void setHistorial(String historial) {
        this.historial = historial;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
    
    
    
    public String toString(){
        if(conectado){
            return nombre+"- conectado";
        }else{
            return nombre+"- desconectado";
        }
        
    }
    
}
