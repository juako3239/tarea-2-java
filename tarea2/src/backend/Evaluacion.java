package backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Evaluacion {

    private ArrayList<Pregunta> preguntas;

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    public Evaluacion(File archivoCSV) {
        this.preguntas = new ArrayList<>();
        this.preguntasCsv(archivoCSV);
    }

    private void preguntasCsv(File archivoCSV) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;
            boolean primeraLinea = true;
            StringBuilder lineaCompleta = new StringBuilder();

            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                lineaCompleta.append(linea).append("\n");
                long cantidadComillas = lineaCompleta.chars().filter(c -> c == '"').count();

                if (cantidadComillas % 2 == 0) {
                    String lineaFinal = lineaCompleta.toString().trim();
                    List<String> datos = parsearLineaCSV(lineaFinal);

                    if (datos.size() >= 6) {
                        String tipo = datos.get(0).trim();
                        String enunciado = datos.get(1).trim();
                        String bloom = datos.get(2).trim();
                        List<String> alternativas = parsearLineaCSV(datos.get(3).trim());
                        int tiempo = Integer.parseInt(datos.get(5).trim());

                        if (tipo.equals("ALT")) {
                            int respuestaCorrecta = Integer.parseInt(datos.get(4).trim());
                            preguntas.add(new PreguntaAlt(enunciado, bloom, tiempo, respuestaCorrecta,alternativas));
                        } else if (tipo.equals("VOF")) {
                            String respuestaCorrecta = datos.get(4).trim();
                            preguntas.add(new PreguntaVoF(enunciado, bloom, tiempo, respuestaCorrecta));
                        }
                    }

                    lineaCompleta.setLength(0);
                }
            }
            System.out.println("Preguntas cargadas: " + preguntas.size());
        } catch (Exception e) {
            System.out.println("Error al leer las preguntas: " + e.getMessage());
        }
    }

    private List<String> parsearLineaCSV(String linea) {
        List<String> columnas = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroDeComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                dentroDeComillas = !dentroDeComillas;
            } else if (c == ';' && !dentroDeComillas) {
                columnas.add(campo.toString().trim());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }

        columnas.add(campo.toString().trim());
        return columnas;
    }

    public int getTotalPreguntas() {
        return preguntas.size();
    }

    public String obtenerEstadisticasPorBloom() {
        String[] niveles = {"Conocimiento", "Comprender", "Aplicar", "Analizar", "Sintesis", "Evaluacion", "Crear"};
        int[] totalPorNivel = new int[niveles.length];
        int[] correctasPorNivel = new int[niveles.length];

        for (Pregunta p : preguntas) {
            int indice = -1;
            for (int i = 0; i < niveles.length; i++) {
                if (p.getBloom().equalsIgnoreCase(niveles[i])) {
                    indice = i;
                    break;
                }
            }

            if (indice != -1) {
                totalPorNivel[indice]++;
                if (p instanceof PreguntaAlt) {
                    PreguntaAlt alt = (PreguntaAlt) p;
                    if (alt.getRespuestaAlt() != 0 && alt.getRespuestaAlt() == alt.getRespuestaCorrecta()){
                        correctasPorNivel[indice]++;
                    }
                } else if (p instanceof PreguntaVoF) {
                    PreguntaVoF vof = (PreguntaVoF) p;
                    if (vof.getRespuestaVF() != null && vof.getRespuestaVF().equalsIgnoreCase(vof.getRespuestaCorrecta())) {
                        correctasPorNivel[indice]++;
                    }
                }
            }
        }

        String resultado = "";
        for (int i = 0; i < niveles.length; i++) {
            if (totalPorNivel[i] > 0) {
                // Corrección: usar división de punto flotante
                double porcentaje = (correctasPorNivel[i] * 100.0) / totalPorNivel[i];
                resultado += niveles[i] + ": " + String.format("%.1f", porcentaje) + "% correctas\n";
            }
        }
        return resultado;
    }

    public String obtenerEstadisticasPorTipo() {
        int totalALT = 0, correctasALT = 0;
        int totalVOF = 0, correctasVOF = 0;

        for (Pregunta p : preguntas) {
            if (p instanceof PreguntaAlt) {
                totalALT++;
                PreguntaAlt alt = (PreguntaAlt) p;
                if (alt.getRespuestaAlt() != 0 && alt.getRespuestaAlt() == alt.getRespuestaCorrecta()) {
                    correctasALT++;
                }
            } else if (p instanceof PreguntaVoF) {
                totalVOF++;
                PreguntaVoF vof = (PreguntaVoF) p;
                if (vof.getRespuestaVF() != null && vof.getRespuestaVF().equalsIgnoreCase(vof.getRespuestaCorrecta())) {
                    correctasVOF++;
                }
            }
        }

        String resultado = "";
        if (totalALT > 0) {
            // Cambio clave: usar double para la división
            double porcentaje = (correctasALT * 100.0) / totalALT;
            resultado += "ALT: " + String.format("%.1f", porcentaje) + "% correctas\n";
        }
        if (totalVOF > 0) {
            // Cambio clave: usar double para la división
            double porcentaje = (correctasVOF * 100.0) / totalVOF;
            resultado += "VOF: " + String.format("%.1f", porcentaje) + "% correctas\n";
        }

        return resultado;
    }


}