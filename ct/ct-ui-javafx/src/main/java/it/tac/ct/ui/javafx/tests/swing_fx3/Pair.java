/*
 * UnB - Universidade de Brasília
 * CIC - Departamento de Ciência da Computação
 * IA - Introdução a Inteligência Artificial
 * 
 * @author zidenis
 * @version 0.1 (24/11/2014)
*/
package it.tac.ct.ui.javafx.tests.swing_fx3;

/**
 * Um par imutável de objetos de tipos genéricos parametrizados.
 * @author Denis Albuquerque (11/0114388)
 * @version 0.1 (24/11/2014)
 */
public class Pair<F, S> {
    private final F first; 
    private final S second;

    /**
     * Cria um Pair de elementos de tipos parametrizados <F, S>
     * @param first primeiro elemtndo do par
     * @param second segundo elemento do par
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Obtém o primeiro elemento
     * @return primeiro elemento de tipo F
     */
    public F getFirst() {
        return first;
    }

    /**
     * Obtém o segundo elemento
     * @return segundo elemento de tipo S
     */
    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return first.toString() + " " + second.toString();
    }
}