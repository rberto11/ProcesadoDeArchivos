import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;

public class Procesamiento {
    private JButton salirButton;
    private JTextArea textArea1;
    private JButton abrirButton;
    private JLabel nombreDelArchivoLabel;
    private JButton guardarButton;
    private JLabel nombreArchivo;
    private JPanel panelPrincipal;
    private JToggleButton mostrarHistogramaToggleButton;
    private JProgressBar progressBar1;
    private JFileChooser fc;
    private String archivoCompleto = "";

    public Procesamiento() {
        fc = new JFileChooser();

        FileFilter filtro = new FileNameExtensionFilter("TXT Files", "txt");
        HashMap<String,Integer> histograma = new HashMap<>();


        abrirButton.addActionListener((ActionEvent e) -> {
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(filtro);
            if (JFileChooser.APPROVE_OPTION==fc.showOpenDialog(panelPrincipal)) {
                nombreArchivo.setText(fc.getSelectedFile().getName());
                File archivo = fc.getSelectedFile();
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))){
                    String l;
                    while ((l= br.readLine())!=null) {
                        archivoCompleto += l+"\n";
                    }
                    String archivoFormat = archivoCompleto.replaceAll("[^a-zA-Z0-9á-úÁ-ÚñÑ ]", "");    // Expresión regular para cambiar todos los carácteres que no sean
                    String [] palabras = archivoFormat.toLowerCase(Locale.ROOT).split(" ");                      // letras números o letras con tildes junto con la eñe
                    for (String palabra :
                            palabras) {
                        if (palabra.length()>2) {
                            histograma.merge(palabra,1,Integer::sum);   // manera de pablo
                            /*if (!histograma.containsKey(palabra)) {
                                histograma.put(palabra, 1);
                            } else {
                                histograma.replace(palabra, histograma.get(palabra), histograma.get(palabra)+1);

                            }*/
                        }
                    }
                    textArea1.setText(archivoCompleto);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                textArea1.setText("Selecciona un archivo para mostrar.");
                nombreArchivo.setText("");

            }
            // HashMap<String,Integer> hola = new HashMap<>(); Almacenar las palabras y el número de veces que aparece.
        });

        salirButton.addActionListener(e -> System.exit(0));


        mostrarHistogramaToggleButton.addItemListener(e -> {
            int estado = e.getStateChange();
            if (estado==ItemEvent.SELECTED) {
                mostrarHistogramaToggleButton.setText("Mostrar Archivo");
                String histogramaFormateado = "";
                for (String clave :
                        histograma.keySet()) {
                    int valor = histograma.get(clave);
                    histogramaFormateado += "La palabra: "+clave+ " se repite: "+valor+" veces.\n";
                }
                textArea1.setText(histogramaFormateado);
            } else {
                mostrarHistogramaToggleButton.setText("Mostrar Histograma");
                textArea1.setText(archivoCompleto);
            }
        });
        guardarButton.addActionListener(e -> {
            fc = new JFileChooser();
            String extensionArchivo = "_histograma.csv";
            progressBar1.setMaximum(histograma.size());
            progressBar1.setMinimum(0);
            if (JFileChooser.APPROVE_OPTION==fc.showSaveDialog(null)) {
                FileWriter fw=null;
                try {
                    fw = new FileWriter(fc.getSelectedFile()+extensionArchivo);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try (CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT.withHeader("Palabras","Valores"))) {
                    histograma.forEach((palabra,valor) ->{
                        progressBar1.setValue(progressBar1.getValue()+1);
                        try {
                            printer.printRecord(palabra,valor);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if (progressBar1.getValue()== progressBar1.getMaximum()) {
                            JOptionPane.showMessageDialog(null,"Su archivo ha sido creado correctamente.");
                            progressBar1.setValue(0);
                        }
                    });
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Procesamiento");
        frame.setSize(500,500);
        frame.setContentPane(new Procesamiento().panelPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
