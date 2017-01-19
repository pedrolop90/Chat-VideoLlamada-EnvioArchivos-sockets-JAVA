
package Presentacion;

import Negocio.Negocio;
import javax.swing.*;

public class Entrada extends JFrame{

    
    private Negocio negocio;
    private JPanel panel1;
    
    
    public Entrada(){
     negocio=new Negocio();
     panelInicio();
    }
    
     public void panelInicio(){
     pack();
     panel1=new Inicio(this);
     this.setSize(panel1.getSize());
     add(panel1);
    }
    
     public void panelPrincipal(){
     pack();
     panel1=new panelPrincipal(this);
     this.setSize(panel1.getSize());
     add(panel1);
     }
     
    public Negocio getNegocio() {
        return negocio;
    }

    
    public static void main(String[] args) {
        Entrada entrada=new Entrada();
        entrada.setVisible(true);
        entrada.setDefaultCloseOperation(3);
        
    }
}
