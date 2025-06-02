package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter; // libreria que filtra segun tipo de archivo
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class tableroVisual extends JFrame implements ActionListener{

    private JPanel contenedor;
    private JPanel cargaArchivos;
    private JPanel preguntas;
    private JPanel revisionPrueba;
    private JLabel tituloCargaArchivos;
    private JButton cargarArchivo;
    private JButton inicioPrueba;
    private JLabel rutaArchivo;
    private JLabel tituloRevision;
    private JLabel pCorrectasBloom;
    private JButton finPrueba;
    private JLabel tituloPreguntas;
    private JLabel pCorrectasItem;
    private int preguntaActual = 0;
    private JLabel cantidadItem;
    private JLabel tiempoTotal;
    private JPanel muestraPregunta;
    private JButton anterior;
    private JButton siguiente;
    private Evaluacion backend;
    private List<List<JRadioButton>> todosLosBotonesPorPregunta = new ArrayList<>();

    //creo variable archivoSeleccionado para que sea accesible dentro del programa
    private File archivoSeleccionado = null;

    public tableroVisual() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(this.contenedor);

        //Defino contenedor como cardlayout para su uso dentro de los botones (para cambiar de jpanel a otro)
        contenedor.setLayout(new CardLayout());
        contenedor.add(cargaArchivos, "cargaArchivos");
        contenedor.add(preguntas, "preguntas");
        contenedor.add(revisionPrueba, "revisionPrueba");

        this.pack();
        this.setListeners(); //clase que contiene los listeners
        this.setTitle("Tarea n°2");
        this.setSize(1000,720);

        cantidadItem.setVisible(false);
        tiempoTotal.setVisible(false);

        muestraPregunta.setLayout(new CardLayout());

        //-------bordes aca-----------------------------------------------------------

        //Bordes cargaArchivos
        tituloCargaArchivos.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
        rutaArchivo.setBorder(BorderFactory.createEtchedBorder());

        //Bordes preguntas
        tituloPreguntas.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));

        //Bordes revisionPrueba
        tituloRevision.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
        pCorrectasBloom.setBorder(BorderFactory.createEtchedBorder());
        pCorrectasItem.setBorder(BorderFactory.createEtchedBorder());
    }


    private void cargarPreguntasEnPanel(){
        muestraPregunta.removeAll();
        todosLosBotonesPorPregunta.clear(); // Limpiar la lista anterior

        int i = 1;
        for (Pregunta p : backend.getPreguntas()) {
            JPanel registroPanel = new JPanel();
            registroPanel.setLayout(new BoxLayout(registroPanel, BoxLayout.Y_AXIS));
            String tipo = " ";
            registroPanel.add(new JLabel(i + ") " + p.getBloom() + ".- " + p.getEnunciado()));

            if (p instanceof PreguntaAlt) {
                tipo = "Pregunta de tipo Alternativa";
                PreguntaAlt alt = (PreguntaAlt) p;
                registroPanel.add(new JLabel("              "));
                registroPanel.add(new JLabel(tipo));
                registroPanel.add(new JLabel("              "));
                ButtonGroup grupoOpciones = new ButtonGroup();

                List<String> alternativas = alt.getAlternativas();
                List<JRadioButton> botonesDeEstaPregunta = new ArrayList<>();

                for (int idx = 0; idx < alternativas.size(); idx++) {
                    String opcion = alternativas.get(idx);
                    JRadioButton botonOpcion = new JRadioButton(opcion);

                    // CREAR UNA VARIABLE FINAL
                    final int opcionIndex = idx + 1;
                    final int preguntaIndex = i - 1; // índice de la pregunta (0-based)

                    botonOpcion.addActionListener(e -> {
                        alt.setRespuestaAlt(opcionIndex);
                        System.out.println("Pregunta " + (preguntaIndex + 1) + " - Respuesta seleccionada: " + opcionIndex);
                    });

                    grupoOpciones.add(botonOpcion);
                    botonesDeEstaPregunta.add(botonOpcion);
                    registroPanel.add(botonOpcion);

                    // MARCAR SI YA ESTABA SELECCIONADA
                    if (alt.getRespuestaAlt() == opcionIndex) {
                        botonOpcion.setSelected(true);
                    }
                }
                // GUARDAMOS LOS BOTONES DE ESTA PREGUNTA
                todosLosBotonesPorPregunta.add(botonesDeEstaPregunta);

            } else if (p instanceof PreguntaVoF) {
                tipo = "Pregunta de tipo VoF";
                PreguntaVoF vof = (PreguntaVoF) p;
                registroPanel.add(new JLabel("              "));
                registroPanel.add(new JLabel(tipo));
                registroPanel.add(new JLabel("              "));
                ButtonGroup grupoVoF = new ButtonGroup();

                JRadioButton verdadero = new JRadioButton("Verdadero");
                JRadioButton falso = new JRadioButton("Falso");

                // Listeners para guardar la respuesta V/F
                verdadero.addActionListener(e -> {
                    vof.setRespuestaVF("Verdadero");
                    System.out.println("Respuesta V/F guardada: Verdadero");
                });
                falso.addActionListener(e -> {
                    vof.setRespuestaVF("Falso");
                    System.out.println("Respuesta V/F guardada: Falso");
                });

                grupoVoF.add(verdadero);
                grupoVoF.add(falso);

                registroPanel.add(verdadero);
                registroPanel.add(falso);

                // Marcar seleccion previa
                if ("Verdadero".equals(vof.getRespuestaVF())) {
                    verdadero.setSelected(true);
                } else if ("Falso".equals(vof.getRespuestaVF())) {
                    falso.setSelected(true);
                }

                // PARA VoF, AGREGAMOS LISTA VACIA PARA MANTENER LA CONSISTENCIA DE LOS INDICES
                todosLosBotonesPorPregunta.add(new ArrayList<>());
            }

            muestraPregunta.add(registroPanel, "Card" + i);
            i++;
        }
        preguntaActual = 0;
        mostrarPreguntaActual();
        muestraPregunta.revalidate();
        muestraPregunta.repaint();
    }

    private void mostrarPreguntasConRespuestasCorrectas() {
        revisionPrueba.removeAll();

        // Crear nuevo panel para el listado
        JPanel panelListado = new JPanel();
        panelListado.setLayout(new BoxLayout(panelListado, BoxLayout.Y_AXIS));

        int i = 1;
        for (Pregunta p : backend.getPreguntas()) {
            String tipo = "";
            String respuesta = "";

            if (p instanceof PreguntaAlt) {
                tipo = "Alternativa";
                PreguntaAlt alt = (PreguntaAlt) p;
                respuesta = Integer.toString(alt.getRespuestaCorrecta());
            } else if (p instanceof PreguntaVoF) {
                tipo = "Verdadero o Falso";
                PreguntaVoF vof = (PreguntaVoF) p;
                respuesta = vof.getRespuestaCorrecta();
            }

            JPanel preguntaPanel = new JPanel();
            preguntaPanel.setLayout(new BoxLayout(preguntaPanel, BoxLayout.Y_AXIS));
            preguntaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            preguntaPanel.add(new JLabel(i + ") [" + tipo + "] " + p.getBloom() + ".- " + p.getEnunciado()));
            preguntaPanel.add(new JLabel("Respuesta correcta: " + respuesta));

            panelListado.add(preguntaPanel);
            panelListado.add(Box.createVerticalStrut(10)); // Espacio entre preguntas
            i++;

        }
        // añadimos estadisticas || Se usa html para activar este modo,<b> es para mostrar en negrita, <br> para hacer un salto de linea y replaceAll() para la conversión de saltos de linea en etiquetas de html
        JLabel bloomStats = new JLabel("<html><b>Porcentaje de respuestas correctas por nivel Bloom:</b><br>"
                + backend.obtenerEstadisticasPorBloom().replaceAll("\n", "<br>") + "</html>");

        JLabel tipoStats = new JLabel("<html><b>Porcentaje de respuestas correctas por tipo de ítem:</b><br>"
                + backend.obtenerEstadisticasPorTipo().replaceAll("\n", "<br>") + "</html>");

        panelListado.add(Box.createVerticalStrut(20)); // Espacio antes de estadísticas
        panelListado.add(bloomStats);
        panelListado.add(Box.createVerticalStrut(10)); // Espacio entre estadísticas
        panelListado.add(tipoStats);

        JScrollPane scroll = new JScrollPane(panelListado);
        scroll.setPreferredSize(new Dimension(600, 400));

        revisionPrueba.setLayout(new BorderLayout()); // Asegura el layout adecuado
        revisionPrueba.add(scroll, BorderLayout.CENTER);

        revisionPrueba.revalidate();
        revisionPrueba.repaint();
    }

    //metodo mostarPregunta actual
    private void mostrarPreguntaActual() {
        if (backend != null && backend.getPreguntas().size() > 0) {
            CardLayout cl = (CardLayout) muestraPregunta.getLayout();
            cl.show(muestraPregunta, "Card" + (preguntaActual + 1));

            // debugger, muestra en consola qué pregunta se está mostrando
            System.out.println("Mostrando pregunta: " + (preguntaActual + 1) + " de " + backend.getPreguntas().size());
        }
    }

    //-----------------------------------------------------------------------------

    //metodo setListeners que contienen todos los listeners de los botones y jLabels
    public void setListeners() {
        //aca van los listeners de los botones

        this.cargarArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Seleccione archivo");

                int resultado = fileChooser.showOpenDialog(tableroVisual.this);

                if (resultado == JFileChooser.APPROVE_OPTION) {
                    archivoSeleccionado = fileChooser.getSelectedFile();

                    if (archivoSeleccionado != null && archivoSeleccionado.getName().toLowerCase().endsWith(".csv")) {
                        System.out.println("Archivo seleccionado: " + archivoSeleccionado.getAbsolutePath());
                        rutaArchivo.setText("Archivo seleccionado: " + archivoSeleccionado.getName());

                        // Creamos el backend
                        backend = new Evaluacion(archivoSeleccionado);

                        // Obtenemos informacion del backend
                        int cantidadPreguntas = backend.getTotalPreguntas();

                        // Calculamos tiempo total
                        int tiempoTotalEstimado = 0;
                        for (Pregunta p : backend.getPreguntas()) {
                            tiempoTotalEstimado += p.getTiempoEstimado();
                        }

                        cantidadItem.setText("Cantidad de Items: " + cantidadPreguntas);
                        tiempoTotal.setText("Tiempo total estimado: " + tiempoTotalEstimado + " minutos");
                        cantidadItem.setVisible(true);
                        tiempoTotal.setVisible(true);

                        cargarPreguntasEnPanel();

                    } else {
                        rutaArchivo.setText("Error: Seleccione un archivo CSV válido.");
                    }
                } else if (resultado == JFileChooser.CANCEL_OPTION) {
                    rutaArchivo.setText("Selección de archivo cancelada.");
                } else {
                    rutaArchivo.setText("Ocurrió un error en la selección del archivo.");
                }
            }
        });


        //boton inicio prueba
        this.inicioPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Boton presionado inicioPrueba"); //compruebo si se ejecuta el boton, para descartar problemas
                CardLayout cl = (CardLayout) contenedor.getLayout();

                //crear if si no existe un archivo, no pueda comenzar la prueba ni cambiar de jPanel
                if (archivoSeleccionado == null) {
                    rutaArchivo.setText("¡Necesitas cargar el archivo para comenzar!");
                } else {
                    cl.show(contenedor, "preguntas");
                }

            }
        });

        //boton finalizar prueba
        this.finPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Boton presionado finPrueba");
                mostrarPreguntasConRespuestasCorrectas();
                CardLayout cl = (CardLayout) contenedor.getLayout();

                cl.show(contenedor, "revisionPrueba");
            }
        });

        //boton anterior pregunta
        this.anterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (backend != null && backend.getPreguntas().size() > 0) {
                    // Ir a la pregunta anterior
                    if (preguntaActual > 0) {
                        preguntaActual--;
                        mostrarPreguntaActual();
                    } else {
                        // Si está en la primera, ir a la última
                        preguntaActual = backend.getPreguntas().size() - 1;
                        mostrarPreguntaActual();
                    }
                }
            }
        });

        //boton siguiente pregunta
        this.siguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (backend != null && backend.getPreguntas().size() > 0) {
                    if (preguntaActual < backend.getPreguntas().size() - 1) {
                        preguntaActual++;
                        mostrarPreguntaActual();
                    } else {
                        preguntaActual = 0;
                        mostrarPreguntaActual();
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}