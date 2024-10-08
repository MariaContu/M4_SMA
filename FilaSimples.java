import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FilaSimples {
    static int count = 100000;
    static double previous = 0.5; 
    static final double a = 1664525;
    static final double c = 1013904223;
    static final double m = Math.pow(2, 32); 
    static final int capacidadeMaxima = 5;
    static final int numServidores = 2;
    static double tempoGlobal = 0.0;
    static double[] temposAcumulados = new double[capacidadeMaxima + 1];
    static int clientesPerdidos = 0;
    static int servidoresOcupados = 0;

    enum TipoEvento {
        CHEGADA, SAIDA
    }

    static class Evento implements Comparable<Evento> {
        TipoEvento tipo;
        double tempo;

        Evento(TipoEvento tipo, double tempo) {
            this.tipo = tipo;
            this.tempo = tempo;
        }

        @Override
        public int compareTo(Evento outro) {
            return Double.compare(this.tempo, outro.tempo);
        }
    }

    static PriorityQueue<Evento> filaDeEventos = new PriorityQueue<>();
    static Queue<Evento> filaDeClientes = new LinkedList<>();

    static double nextRandom() {
        previous = (a * previous + c) % m;
        return previous / m;
    }

    static Evento nextEvent() {
        return filaDeEventos.poll();
    }

    static void processaChegada(Evento evento) {
        if (filaDeClientes.size() < capacidadeMaxima) {
            filaDeClientes.add(evento);
            if (servidoresOcupados < numServidores) {
                servidoresOcupados++;
                double tempoAtendimento = 3 + nextRandom() * 2;
                filaDeEventos.add(new Evento(TipoEvento.SAIDA, evento.tempo + tempoAtendimento));
            }
        } else {
            clientesPerdidos++;
        }
    }

    static void processaSaida(Evento evento) {
        servidoresOcupados--;
        filaDeClientes.poll();
        if (!filaDeClientes.isEmpty() && servidoresOcupados < numServidores) {
            servidoresOcupados++;
            double tempoAtendimento = 3 + nextRandom() * 2;
            filaDeEventos.add(new Evento(TipoEvento.SAIDA, evento.tempo + tempoAtendimento));
        }
    }

    public static void main(String[] args) {
        filaDeEventos.add(new Evento(TipoEvento.CHEGADA, 2.0));
        double tempoAnterior = 0.0;

        while (count > 0) {
            Evento evento = nextEvent();
            if (evento != null) {
                double deltaTempo = evento.tempo - tempoAnterior;
                temposAcumulados[filaDeClientes.size()] += deltaTempo;
                tempoGlobal += deltaTempo;
                tempoAnterior = evento.tempo;

                if (evento.tipo == TipoEvento.CHEGADA) {
                    processaChegada(evento);
                    double tempoChegada = 2 + nextRandom() * 3; // Chegada entre 2 e 5
                    filaDeEventos.add(new Evento(TipoEvento.CHEGADA, evento.tempo + tempoChegada));
                } else if (evento.tipo == TipoEvento.SAIDA) {
                    processaSaida(evento);
                }
                count--;
            }
        }

        for (int i = 0; i < capacidadeMaxima + 1; i++) {
            double probabilidade = temposAcumulados[i] / tempoGlobal * 100;
            System.out.println(i + " clientes: " + temposAcumulados[i] + " unidades de tempo (" + probabilidade + "%)");
        }

        System.out.println("Clientes perdidos: " + clientesPerdidos);
        System.out.println("Tempo global da simulação: " + tempoGlobal + " unidades de tempo");
    }
}
