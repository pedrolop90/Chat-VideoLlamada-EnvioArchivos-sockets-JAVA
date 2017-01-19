

package Presentacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inicio extends JPanel{

    private JTextField txtNombre;
    private JLabel label1;
    private JButton cmdEntrar;
    private Entrada entrada;
    
    public Inicio(Entrada e){
      entrada=e;
      init();
    }
    public void init(){
     setBounds(0,0,300, 140);
     setLayout(null);
     label1=new JLabel("ingresa tu nombre");
     label1.setBounds(10, 10, 120, 30);
     add(label1);
     txtNombre=new JTextField();
     txtNombre.setBounds(130,10,120,30);
     add(txtNombre);
     cmdEntrar=new JButton("entrar");
     cmdEntrar.setBounds(100,50,70,30);
     add(cmdEntrar);
     cmdEntrar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if(txtNombre.getText().isEmpty()){
                  entrada.getNegocio().getYo().setNombre("sin nombre");
            }else{
                 entrada.getNegocio().getYo().setNombre(txtNombre.getText());
            }
           
            entrada.getNegocio().asignarmiIp();
            entrada.panelPrincipal();
         }
     });
    }
    
}
