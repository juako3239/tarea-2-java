    package backend;

    public class Pregunta {

        private String enunciado;
        private String bloom;
        private int tiempoEstimado;

        //crear respuesta = -1 (significa q no respondiste) y con el listener se cambia el valor de la variable

        public Pregunta(String enunciado, String bloom, int tiempoEstimado) {
            this.enunciado = enunciado;
            this.bloom = bloom;
            this.tiempoEstimado = tiempoEstimado;
        }


        public String getEnunciado() {
            return enunciado;
        }

        public String getBloom() {
            return bloom;
        }


        public int getTiempoEstimado() {
            return tiempoEstimado;
        }
    }