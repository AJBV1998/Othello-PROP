/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.teamchapulin;

import edu.upc.epsevg.prop.othello.CellType;
import static edu.upc.epsevg.prop.othello.CellType.opposite;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author maic1
 */
public class PlayerMinMax  implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int depth;
    private static CellType jugador;
    private int nodesExplorats; 

    public PlayerMinMax(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    @Override
    public void timeout() {
        //System.out.println("FALLO POR TIEMPO DE ESPERA");
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        ArrayList<Point> moves = s.getMoves();     
        int valor = Integer.MIN_VALUE;
        Point millorMoviment = moves.get(0);
        System.out.println("_______________________________________");
        System.out.println("########## Jugades possibles ##########");
        
        
        if(s.currentPlayerCanMove()){ //¿Jugador actual puede moverse?
            for(int i = 0; i < moves.size(); ++i){
                nodesExplorats++;
                GameStatus gs_aux = new GameStatus(s);
                gs_aux.movePiece(moves.get(i));//mueve una pieza
                if(gs_aux.checkGameOver()) return new Move(moves.get(i), (long) nodesExplorats, depth, SearchType.MINIMAX);                
                int candidat = miniMax(gs_aux, depth-1,Integer.MIN_VALUE,Integer.MAX_VALUE,false);                
                System.out.println(moves.get(i) + " --> Valor heuristic: " + candidat);
                if(candidat == Integer.MAX_VALUE) millorMoviment = moves.get(i);
                if(valor < candidat){                
                    valor = candidat;
                    millorMoviment = moves.get(i);
                } 
            }
        }   else{s.skipTurn();}
        
        System.out.println("_______________________________________");
        System.out.println("###### Jugades explorades: "+nodesExplorats+"######");
        System.out.println("Escollim: " + millorMoviment + " --> heuristica " + valor);
        System.out.println("#######################################");
        return new Move(millorMoviment, (long) nodesExplorats, depth, SearchType.MINIMAX);
    }
    
    private int miniMax(GameStatus s, int depth,int alpha, int beta, boolean maximitza) {
        //Caso Base: profundidad maxima o juego acabado
        if(depth == 0 || s.checkGameOver()){
            return (int) Heuristica.evaluador(s);
        }
        
        if(s.currentPlayerCanMove()){
            ArrayList<Point> moves_minmax = s.getMoves();  
            //Maximiza
            if (maximitza){          
                //Recorrido de los posibles movimientos
                for(int i=0;i<moves_minmax.size();i++){
                    nodesExplorats++;
                    GameStatus gs_max = new GameStatus(s); // copia del estado de juego
                    gs_max.movePiece(moves_minmax.get(i)); //Realizamos el movimiento
                    //Se encuentra una solucion posible, no hace falta seguir explorando
                    if(gs_max.checkGameOver()) return Integer.MAX_VALUE;
                    //Exploramos el arbol
                    int valor =  miniMax(gs_max, depth-1, alpha, beta, false); 
                    //Nos quedamos con el valor maximo de los hijos
                    alpha = Math.max(alpha, valor);
                    //Poda alpha-beta
                    if (beta <= alpha){ break;  }                
                }
                return alpha;
            } 
            //Minimiza
            else{
                //Recorrido de los posibles movimientos            
                for(int i=0;i<moves_minmax.size();i++){
                    nodesExplorats++;
                    GameStatus gs_mix = new GameStatus(s); // copia del estado de juego
                    gs_mix.movePiece(moves_minmax.get(i)); //Realizamos el movimiento
                    //Se encuentra una solucion posible, no hace falta seguir explorando
                    if(gs_mix.checkGameOver()) return Integer.MIN_VALUE;
                    //Exploramos el arbol
                    int valor = miniMax(gs_mix, depth-1, alpha, beta, true); 
                    //Nos quedamos con el valor maximo de los hijos
                    beta = Math.min(beta, valor);
                    //Poda alpha-beta
                    if (beta <= alpha){ break;  }                
                }
                return beta;
            } 
        }else{
            s.skipTurn();
            return miniMax(s, depth-1, alpha, beta, !maximitza); 
        }
    }
   
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return name;
    }
    
}
