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
import java.util.Arrays;
import java.util.HashMap;
    
/**
 * Jugador ChapulinMinimax
 * 
 * @author Antony Bano (antony.joel.bano@estudiantat.upc.edu)
 * @author Michael Zerpa (michael.alberto.zerpa@estudiantat.upc.edu)
 */
public class ChapulinMinimax  implements IPlayer, IAuto {
    HashMap<Point, Boolean> top;
    HashMap<Point, Boolean> frame;
    HashMap<Point, Boolean> bad;
    HashMap<Point, Boolean> middle;
    HashMap<Point, Boolean> baddest;
    HashMap<Point, Boolean> never;
    
    private String name;
    private GameStatus s;
    private int depth;
    private static CellType jugador;
    private int nodesExplorats; 
    
    /**
     * Constructor del jugador IA ChapulinMinimax.
     * @param name - nom del jugador
     * @param depth -  profunditat actual de la taula
     */
    public ChapulinMinimax(String name, int depth) {
        this.name = name;
        this.depth = depth;
        
        //Hashmaps amb els conjunts de caselles amb un mateix valor
        this.top = new HashMap<Point, Boolean>() {
            {
                //Cantonades
                put(new Point(0, 0), true);
                put(new Point(0, 7), true);
                put(new Point(7, 0), true);
                put(new Point(7, 7), true);
            }
        };
       
        this.frame = new HashMap<Point, Boolean>() {
            {
                //Marc exterior excepte perpendicularment adjacents a les cantonades
                put(new Point(0, 2), true);
                put(new Point(0, 3), true);
                put(new Point(0, 4), true);
                put(new Point(0, 5), true);
                put(new Point(2, 0), true);
                put(new Point(2, 7), true);
                put(new Point(3, 0), true);
                put(new Point(3, 7), true);
                put(new Point(4, 0), true);
                put(new Point(4, 7), true);
                put(new Point(5, 0), true);
                put(new Point(5, 7), true);
                put(new Point(7, 2), true);
                put(new Point(7, 3), true);
                put(new Point(7, 4), true);
                put(new Point(7, 5), true);
            }
        };
        
                this.never = new HashMap<Point, Boolean>() {
            {
                //Diagonalment adjacents a les cantonades
                put(new Point(1, 1), true);
                put(new Point(1, 6), true);
                put(new Point(6, 1), true);
                put(new Point(6, 6), true);
            }
        };
        this.baddest = new HashMap<Point, Boolean>() {
            {
                //Perpendicularment adjacents a les cantonades
                put(new Point(0, 1), true);
                put(new Point(0, 6), true);
                put(new Point(1, 0), true);
                put(new Point(1, 7), true);
                put(new Point(6, 0), true);
                put(new Point(6, 7), true);
                put(new Point(7, 1), true);
                put(new Point(7, 6), true);
            }
        };
        
        this.bad = new HashMap<Point, Boolean>() {
            {
                //Marc interioir excepte els cantons d'aquest
                put(new Point(1, 2), true);
                put(new Point(1, 3), true);
                put(new Point(1, 4), true);
                put(new Point(1, 5), true);
                put(new Point(2, 1), true);
                put(new Point(2, 6), true);
                put(new Point(3, 1), true);
                put(new Point(3, 6), true);
                put(new Point(4, 1), true);
                put(new Point(4, 6), true);
                put(new Point(5, 1), true);
                put(new Point(5, 6), true);
                put(new Point(6, 2), true);
                put(new Point(6, 3), true);
                put(new Point(6, 4), true);
                put(new Point(6, 5), true);
            }
        };

        this.middle = new HashMap<Point, Boolean>() {
            {
                //Resta caselles centrals
                put(new Point(2, 2), true);
                put(new Point(2, 3), true);
                put(new Point(2, 4), true);
                put(new Point(2, 5), true);
                put(new Point(3, 2), true);
                put(new Point(3, 3), true);
                put(new Point(3, 4), true);
                put(new Point(3, 5), true);
                put(new Point(4, 2), true);
                put(new Point(4, 3), true);
                put(new Point(4, 4), true);
                put(new Point(4, 5), true);
                put(new Point(5, 2), true);
                put(new Point(5, 3), true);
                put(new Point(5, 4), true);
                put(new Point(5, 5), true);

            }
        };
        
    }

    @Override
    public void timeout() {
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
        jugador = s.getCurrentPlayer();//Jugador actual
        System.out.println("_______________________________________");
        System.out.println("########## Jugades possibles ##########");
        
        if(s.currentPlayerCanMove()){ //¿Jugador actual puede moverse?
            for(int i = 0; i < moves.size(); ++i){
                nodesExplorats++;
                GameStatus gs_aux = new GameStatus(s);
                gs_aux.movePiece(moves.get(i));//mueve una pieza
                int candidat = miniMax(gs_aux, depth-1,opposite(jugador),false);
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
    
    private int miniMax(GameStatus s, int depth, CellType jugador, boolean maximitza) {
        //Caso Base: profundidad maxima o juego acabado
        if(depth == 0 || s.checkGameOver())  return heuristica(s, jugador);
        else{
            ArrayList<Point> moves = s.getMoves();     
            int valor;
            if(maximitza) valor = Integer.MIN_VALUE;
            else valor = Integer.MAX_VALUE;

            for(int i = 0; i < moves.size(); ++i){
                nodesExplorats++;
                GameStatus gs_aux = new GameStatus(s);
                gs_aux.movePiece(moves.get(i));
                int candidat = miniMax(gs_aux, depth-1,opposite(jugador),!maximitza);
                if((maximitza && valor > candidat) || (!maximitza && valor < candidat)){                
                    valor = candidat;
                }
            }

            return valor;
        }
    }
    
    private int heuristica(GameStatus s, CellType jugador) {
        int my=0, other=0;
        ArrayList<Point> moves = s.getMoves();     
        for(int i = 0; i < moves.size(); ++i){
            if(s.canMove(moves.get(i),jugador)) my++;
            if(s.canMove(moves.get(i),opposite(jugador))) other++;
        }
        /*
        //No hi han moviments + / - infinit
        if( my==0 && other==0){
            if(s.getScore(jugador) > s.getScore(opposite(jugador))) return Integer.MAX_VALUE;
            else return Integer.MIN_VALUE;
        }
        
        int h = 0;
        boolean [] cols = new boolean[s.getSize()];
        Arrays.fill(cols, Boolean.TRUE);
        for (int i = 0; i < s.getSize(); i++) {
            //Mirar columnes del mateix color fer taula de 8 posicions i anar anotant a cada columna
            boolean row=true;
            for (int j = 0; j < s.getSize(); j++) {
                
                Point x = new Point(i, j);
                CellType xColor = s.getPos(x);
                // row observa files assegurades
                if(row) row = xColor == jugador;
                if(cols[j]) cols[j]= xColor == jugador; 


                if (xColor != CellType.EMPTY) {
                    //Cantonades del tauler
                    if (top.containsKey(x)) {                        
                        h += 10000 * (xColor.ordinal() * jugador.ordinal());
                    //Marc exterior    
                    } else if (frame.containsKey(x)) {
                        //Si hi ha una casella lliure a les vores, devaluem si som al costat del contrari amb un espai al costat
                        //Verticals
                        if (i==0 || i==7) {
                            if ((s.getPos(i, j-1) != xColor) && (s.getPos(i, j-1) != s.getPos(i, j+1))) h -= 200 * (xColor.ordinal() * jugador.ordinal());
                            //Valorem positivament qualsevol jugador ja que despres les intentarem voltejar
                            else h+=200; 
                        }
                        //Horitzontals
                        if (j==0 || j==7) {
                            if ((s.getPos(i-1, j) != xColor) && (s.getPos(i-1, j) != s.getPos(i+1, j))) h -= 200 * (xColor.ordinal() * jugador.ordinal());
                            //Valorem positivament qualsevol jugador ja que despres les intentarem voltejar
                            else h+=200; 
                        }
                        else{
                            h+=200;
                        }
                            
                    //Caselles adjacents a les vores    
                    } else if (bad.containsKey(x)) {
                        
                        //Valorar positivament una d'aquestes caselles si els 3 moviments del marc estan ocupats
                        if (i==1 && (j>1 && j<6)) {
                            if (s.getPos(0,j-1)==jugador || s.getPos(0,j)==jugador || s.getPos(0, j+1)==jugador) h-= 200 * (xColor.ordinal() * jugador.ordinal());
                            else h+=200 * (xColor.ordinal() * jugador.ordinal());
                        } else if (i==6 && (j>1 && j<6)) {
                            if (s.getPos(7,j-1)==jugador || s.getPos(7,j)==jugador || s.getPos(7, j+1)==jugador) h-= 200 * (xColor.ordinal() * jugador.ordinal());
                            else h+=200 * (xColor.ordinal() * jugador.ordinal());
                        } else if (j==1 && (i>1 && i<6)) {
                            if (s.getPos(i,0)==jugador || s.getPos(i-1,0)==jugador || s.getPos(i+1, 0)==jugador) h-= 200 * (xColor.ordinal() * jugador.ordinal());
                            else h+=200 * (xColor.ordinal() * jugador.ordinal());
                        } else if (j==1 && (i>1 && i<6)) {
                            if (s.getPos(i,7)==jugador || s.getPos(i+1,7)==jugador || s.getPos(i-1, 7)==jugador) h-= 200 * (xColor.ordinal() * jugador.ordinal());
                            else h+=200 * (xColor.ordinal() * jugador.ordinal());
                        }
                    //Caselles centrals    
                    } else if (middle.containsKey(x)) {
                         h += 200 * (xColor.ordinal() * jugador.ordinal());
                      //perpendiculars de la vora de les cantonades
                    } else if (baddest.containsKey(x)){
                        if((i==1 && j==0) || (i==0 && j==1)){
                            if(s.getPos(0, 0)!=jugador) h+=2000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=3000 * (xColor.ordinal() * jugador.ordinal());
                        } else if((i==0 && j==6) || (i==1 && j==7)){
                            if(s.getPos(0, 7)!=jugador) h+=2000* (xColor.ordinal() * jugador.ordinal());
                            else h-=3000 * (xColor.ordinal() * jugador.ordinal());
                        } else if((i==6 && j==0) || (i==7 && j==1)){
                            if(s.getPos(7, 0)!=jugador) h+=2000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=3000* (xColor.ordinal() * jugador.ordinal());
                        } else if((i==6 && j==7) || (i==7 && j==6)){
                            if(s.getPos(7, 7)!=jugador) h+=2000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=3000 * (xColor.ordinal() * jugador.ordinal());
                        }
                        
                    } else if (never.containsKey(x)){
                        if(i==1 && j==1){
                            if(s.getPos(0, 0)!=jugador) h+=1000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=5000 * (xColor.ordinal() * jugador.ordinal());
                        } else if(i==1 && j==6){
                            if(s.getPos(0, 7)!=jugador) h+=1000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=5000 * (xColor.ordinal() * jugador.ordinal());
                        } else if(i==6 && j==1){
                            if(s.getPos(7, 0)!=jugador) h+=1000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=5000 * (xColor.ordinal() * jugador.ordinal());
                        } else if(i==6 && j==6){
                            if(s.getPos(7, 7)!=jugador) h+=1000 * (xColor.ordinal() * jugador.ordinal());
                            else h-=5000 * (xColor.ordinal() * jugador.ordinal());
                        }
                        
                    }
                }
            }
            //Tota la fila del mateix color
            if(row){
                if(i==0 || i==7)h+= 10000;
                else h += 500;
            }
        }
        //Incrementem per columna acumulada
        for(int i=0; i<s.getSize(); ++i){
            if(cols[i]) h+=500;
        }
        if(cols[0]) h+= 10000;
        if(cols[7]) h+= 10000;
        //System.out.println("Heuristic:"+h);
            //A partir del torn 57 la diferencia de fitxes amb el contrari puntua
            if(57 < my+other) h = h + 10*(my-other);
            return h;*/
        
               
        /*System.out.println("###### Peces meves: "+pecesMeves+"######");
        System.out.println("###### Peces adversari: "+pecesAdv+"######");*/
        return 0;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     * @return 
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }
 
}
