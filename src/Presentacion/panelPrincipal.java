

package Presentacion;

import Cliente.Cliente;
import com.github.sarxos.webcam.Webcam;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class panelPrincipal extends JPanel implements Runnable{
    
    private Entrada entrada;
    private JList listaContactos;
    private Thread hilo;
    private JButton crearContacto;
    private JLabel label1;
    private JLabel label2;
    private JTextField txtNombre;
    private JTextField txtIp;
    private LinkedList<Chat> listaChats;
    private JButton definirRaiz;
    private server server;
    private Webcam camara;
    private static final int puerto=9000;
    
     public panelPrincipal(Entrada e){
       entrada=e;
       setSize(300, 530);
       setLayout(null);
       init();
       hilo=new Thread(this);
       hilo.start();
       listaChats=new LinkedList<>();
       elegirRaiz();
        if (!Webcam.getWebcams().isEmpty()) {
             camara = Webcam.getDefault();
             camara.open();
             server = new server(puerto, camara, 1000, true);
             server.start();
         }
    }
    
     public void init(){
         this.entrada.setTitle(entrada.getNegocio().getYo().getIp());
       listaContactos=new JList();
       listaContactos.setBounds(10, 10, 260, 350);
       listaContactos.addListSelectionListener(new ListSelectionListener() {
           @Override
          public void valueChanged(ListSelectionEvent e) {
              Cliente cliente=(Cliente)listaContactos.getSelectedValue();
               if (cliente != null) {
                   boolean x = false;
                       for (Chat c : listaChats) {
                           if (cliente.getIp().equalsIgnoreCase(c.getCliente().getIp())) {
                               x = true;
                               break;
                           }
                       }
                       
                   if (!x) {
                       listaChats.addLast(new Chat(panelPrincipal.this, cliente));
                       listaChats.getFirst().setVisible(true);
                   }
               }
           }
       });
       add(listaContactos);
       label1=new JLabel("nombre contacto");
       label1.setBounds(10,370,100,30);
       add(label1);
       txtNombre=new JTextField();
       txtNombre.setBounds(120,370,100,30);
       add(txtNombre);
       label2=new JLabel("ip contacto");
       label2.setBounds(10,410,100,30);
       add(label2);
       txtIp=new JTextField();
       txtIp.setBounds(120,410,100,30);
       add(txtIp);
       crearContacto=new JButton("añadir contacto");
       crearContacto.setBounds(0,450,140,30);
       crearContacto.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               String nombre=txtNombre.getText();
               String ip=txtIp.getText();
              if(nombre.isEmpty()||ip.isEmpty()){
                    JOptionPane.showMessageDialog(null, "debes llenar las casillas de ip y nombre");
                  return;
              }
              if(entrada.getNegocio().agregarContacto(nombre, ip)){
                   JOptionPane.showMessageDialog(null, "contacto agregado");
                   txtNombre.setText("");
                   txtIp.setText("");
                   return;
              }
                  JOptionPane.showMessageDialog(null, "el contacto no se pudo agregar");
              
           }
       });
       add(crearContacto);
       definirRaiz=new JButton("directorio");
       definirRaiz.setBounds(150,450,110,30);
       definirRaiz.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 elegirRaiz();
             }
         });
       add(definirRaiz);
     }

     public void elegirRaiz(){
         JFileChooser aux=new JFileChooser();
         aux.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         aux.showSaveDialog(null);
         if(aux.getSelectedFile()==null){
             elegirRaiz();
         }else{
             entrada.getNegocio().cambiarRaiz(aux.getSelectedFile().getAbsolutePath());
         }
     }
     
    @Override
    public void run() {
        while(true){
             for (Chat chat : listaChats) {
                   if (!chat.isVisible()) {
                       chat.getPanel2().cerrarTransmision();
                       chat.getPanel2().cerrarConexionLlamada();
                       chat.cerrarChat();
                       listaChats.remove(chat);
                   }
               }
            DefaultListModel lista=new DefaultListModel();
            entrada.getNegocio().contactosConectados();
            for (Cliente cliente : entrada.getNegocio().getContactos()) {
                if(cliente.isConectado()){
                    lista.addElement(cliente);
                }else{
                     lista.addElement(cliente);
                }
            }
            listaContactos.setModel(lista);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
        }
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public LinkedList<Chat> getListaChats() {
        return listaChats;
    }

    public void setListaChats(LinkedList<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    public server getServer() {
        return server;
    }

    public void setServer(server server) {
        this.server = server;
    }

    public Webcam getCamara() {
        return camara;
    }

    public void setCamara(Webcam camara) {
        this.camara = camara;
    }
     
    
    
}
