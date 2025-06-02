package backend;
import java.util.ArrayList;
import java.util.List;

public class PreguntaAlt extends Pregunta{

    //de private a public
    private int respuestaAlt; //aca se guarda respuesta del usuario
    private List<String> alternativas;

    private int respuestaCorrectaALT;

    public PreguntaAlt(String enunciado, String bloom, int tiempoEstimado,int respuestaCorrectaALT, List<String> alternativas) {
        super(enunciado, bloom, tiempoEstimado);

        this.respuestaCorrectaALT = respuestaCorrectaALT;
        this.alternativas = alternativas;
    }

    //no esta en uso porque aun no se leen las alternativas desde el .csv


    public List<String> getAlternativas() {
        return alternativas;
    }

    public int getRespuestaCorrecta() {
        return respuestaCorrectaALT;
    }

    public int getRespuestaAlt() {
        return respuestaAlt;
    }

    public void setRespuestaAlt(int respuestaAlt) {
        this.respuestaAlt = respuestaAlt;
    }
}
