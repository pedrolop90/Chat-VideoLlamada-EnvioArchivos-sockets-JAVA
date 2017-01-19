

package Presentacion;

import Cliente.Cliente;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.text.StyledDocument;

public class Chat extends JFrame implements Runnable{
    
    private panelPrincipal panelPrincipal;
    private Cliente cliente;
    private JTextField mensaje;
    private JTextArea historial;
    private JLabel nombre;
    private JButton enviar;
    private JPanel panel1;
    private Thread hilo;
    private ActionListener accionEnviarMensaje;
    private JComboBox comboAcciones;
    private ActionListener accionCombo;
    private JFileChooser listaArchivos;
    private camara llamada;
    private camara panel2;
    private WebcamPanel panelCamara;
    private Webcam camara;
    private boolean exit=true;
    private JTextPane panelChat;
    private JEditorPane editarChat;
    
    public Chat(panelPrincipal p,Cliente c){
        panelPrincipal=p;
        cliente=c;
        pack();
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        acciones();
        init();
        hilo=new Thread(this);
        hilo.start();
        this.setTitle(cliente.getNombre());
    }

    public void init(){
        this.setLayout(null);
        panel1=new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(0, 0, 300, 400);
        add(panel1);
        panel2 = new camara(this);
        panel2.setLocation(310, 0);
        nombre=new JLabel(cliente.getNombre()+"- "+cliente.getIp());
        nombre.setBounds(0,0,210,40);
        panel1.add(nombre);
        comboAcciones=new JComboBox();
        comboAcciones.setBounds(210,0,90,30);
        comboAcciones.addItem("ninguno");
        comboAcciones.addItem("adjuntar");
        comboAcciones.addItem("Llamar");
        comboAcciones.addItem("Detener llamada");
        comboAcciones.addActionListener(accionCombo);
        panel1.add(comboAcciones);
        historial=new JTextArea();
        historial.setText(cliente.getHistorial());
        historial.setBounds(0,40,300,275);
        historial.setEditable(false);
        mensaje=new JTextField();
        mensaje.setBounds(0,325,210,30);
        panel1.add(mensaje);
        enviar=new JButton("enviar");
        enviar.setBounds(210, 325,90, 30);
        accionEnviarMensaje=new  ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               panelPrincipal.getEntrada().getNegocio().enviarMensaje(cliente.getIp(),mensaje.getText());
               mensaje.setText("");
            }
        };
        enviar.addActionListener(accionEnviarMensaje);
        enviar.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        panel1.add(enviar);
        panel1.add(historial);
        if(!Webcam.getWebcams().isEmpty()){
            camara=Webcam.getDefault();
             panelCamara=new WebcamPanel(camara); 
            panelCamara.setBounds(300, 0, 200, 200);
            add(panelCamara);
        }
        
    }
    
    private void acciones(){
        accionCombo=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aux=(String)comboAcciones.getSelectedItem();
                switch (aux) {
                    case "adjuntar":
                        listaArchivos = new JFileChooser();
                        listaArchivos.showOpenDialog(null);
                        if (listaArchivos.getSelectedFile() != null) {
                            panelPrincipal.getEntrada().getNegocio().enviarArchivo(cliente.getIp(), listaArchivos.getSelectedFile().getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(null, "debes seleccionar un archivo");
                        }
                        listaArchivos = null;
                        break;
                    case "Llamar":  
                            panel2.iniciarTransmision();
                            add(panel2);
                        break;
                    case "Detener llamada":
                        panel2.cerrarTransmision();
                        remove(panel2);
                        break;
                }
            }
        };
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    @Override
    public void run() {
       while(exit){
           try {
               historial.setText(cliente.getHistorial());
               Thread.sleep(100);
           } catch (InterruptedException ex) {
               ex.printStackTrace();
           }
       }
    }

    public panelPrincipal getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(panelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public camara getPanel2() {
        return panel2;
    }

    public void setPanel2(camara panel2) {
        this.panel2 = panel2;
    }

    public void cerrarChat(){
        exit=false;
        panel2.setEnabled(false);
        this.dispose();
        try {
            this.finalize();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    
}
