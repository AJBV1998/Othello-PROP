/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.teamchapulin;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Jugador MiniMax amb profunditat limitada.
 * 
 * @author Antony Baño (antony.joel.bano@estudiantat.upc.edu)
 * @author Michael Zerpa (michael.alberto.zerpa@estudiantat.upc.edu)
 */
public class PlayerMiniMax  implements IPlayer, IAuto {
    private final String nom;
    private final InfoNode[] TT;
    private long nodesExplorats = 0L;    
    private final long[][][] zobristKeys;
    private final int depth;    
    
    /**
     * Constructor del jugador IA MiniMax amb profunditat limitada.
     * 
     * Es dona per defecte el nom del jugador i es crea la taula de transposicions i 
     * el vector tridimensional de les claus de Zobrist. Reben la profunditat limitada.
     * 
     * @param depth profunditat limitada
     */
    public PlayerMiniMax(int depth){
        this.nom = "MiniMax";
        this.depth = depth;
        this.TT = new InfoNode[714285714];
        this.zobristKeys = creaZobristRandom();
    }
    
    /**
    * Base de dades dels nodes de la taula de transposicio.
    */
    public class InfoNode {
        private final long hashKey;
        private final int heuristica;
        private final byte profPendent;
        private final byte tipus;   
        
        /**
         * Constructor d'un InfoNode.
         * 
         * Asignem els diferents valors per les variables.
         * 
         * @param key clau Zobrist de l'estat del tauler
         * @param valor valor heuristic del moviment
         * @param depth profunditat assolida
         * @param tipus tipus de node 0-> exacte, 1-> poda alpha, 2-> poda beta 
         */
        public InfoNode(long key, int valor, byte depth, byte tipus) {
            this.hashKey = key;
            this.heuristica = valor;
            this.profPendent = depth;
            this.tipus = tipus;
        }
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
        //Inicialitzem variables
        nodesExplorats = 0; int valor=0;
        
        //Llista de moviments possibles
        ArrayList<Point> moves = s.getMoves();
        int millorMov = 0, millorMov2 = 0;
        
        //Si el jugador pot tirar explora els diferents moviments
        if(s.currentPlayerCanMove()){
            long hashKey;
            valor = Integer.MIN_VALUE;
            //Recorregut dels moviments possibles 
            for(int i = 0; i < moves.size(); ++i){
                //Copia de l'estat del tauler
                GameStatus gs_nou = new GameStatus(s);
                //Fem un moviment
                gs_nou.movePiece(moves.get(i));
                //Si es un moviment guanyador, fem return del moviment
                if(gs_nou.checkGameOver()){
                    return new Move(moves.get(i), nodesExplorats, depth, SearchType.MINIMAX);
                }
                //Calculem el valor hash zobrist de l'estat actual del tauler
                hashKey = calculateHashTable(gs_nou);
                //Explorem el moviment del node candidat a millor moviment
                int candidat = miniMax(gs_nou,depth-1,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,false, hashKey);
                //Si el node candidat te el maxim valor el guardem
                if(candidat == Integer.MAX_VALUE){
                    millorMov = i;
                }
                
                //Si el node candidat te el mateix valor heuristic que l'anterior
                if(valor == candidat){
                        int desempat = Heuristica.stability_board[moves.get(i).x][moves.get(i).y];
                        if(millorMov2 < desempat) millorMov = i;
                    }
                
                //Si el node candidat es millor fins ara el guardem
                if(valor < candidat){                
                    valor = candidat;
                    millorMov = i;
                    millorMov2 = Heuristica.stability_board[moves.get(i).x][moves.get(i).y];
                }                              
            }  
            
        }   else{s.skipTurn();} //El jugador no pot tirar, pasa torn
        
        return new Move(moves.get(millorMov), nodesExplorats, depth, SearchType.MINIMAX);
    }
    
    /**
     * Genera possibles jugades simulant tirades a cada columna, escollint el millor moviment per a tu, suposant que l'adversari escollirà  el pitjor moviment per a tu. 
     * 
     * La poda alpha-beta és una tècnica de cerca que redueix el nombre de nodes avaluats en un arbre de joc per l'algorisme Minimax.
     * Aquesta cerca alfa-beta actualitza el valor dels paràmetres segons es recorre l'arbre. El mètode ha de fer la poda de 
     * les branques restants quan el valor actual que s'està examinant sigui pitjor que el valor actual de alpha o beta per a MAX o MIN.
     * 
     * @param gs Tauler i estat actual de joc.
     * @param depth profunditat actual de la tauler
     * @param depth_a profunditat que es preten assolir
     * @param alpha valor per maximitxar benefici
     * @param beta valor per minimitzar perdudes
     * @param maximitza valor bolea per saber si es maximitza o es minimitza
     * @param key clau hash de l'estat actual del tauler
     * @return Eleccio de la jugada amb maxim valor heuristic.
     */ 
    public int miniMax(GameStatus gs, int depth, int depth_a, int alpha, int beta, boolean maximitza, long key) {  
        //Index de l'estat de la taula despres del moviment
        int estat = (int)(key%TT.length);
        
        if(TT[estat] != null && TT[estat].hashKey == key && TT[estat].profPendent >= depth ){
            switch (TT[estat].tipus) {
                case 0 -> {//EXACT
                    return TT[estat].heuristica;
                }
                case 1 -> {//ALPHA
                    alpha = Math.max(alpha, TT[estat].heuristica);
                }
                case 2 ->{ //BETA
                    beta = Math.min(beta, TT[estat].heuristica);
                }
            }
            //Poda alpha-beta
            if(alpha >= beta)   return TT[estat].heuristica;
            
        }
        
        //Final de partida
        if(gs.isGameOver()){
            return Integer.MAX_VALUE; 
        }
        
        //Profunditat igual a 0
        if(depth == 0){
            nodesExplorats++;
            int valor = (int) Heuristica.evaluador(gs);
            
            //Si l'estat no existeix 
            if(TT[estat] == null){
                TT[estat] = afegir(key, valor, (byte) depth_a, (byte)0);
            }
            //Si l'estat existeix i es el mateix estat o es una col.lisio
            else if(TT[estat] != null){
                if(TT[estat].profPendent <= depth_a){
                    TT[estat] = afegir(key, valor, (byte) depth_a, (byte)0);
                }
            }
            
            //Retorna el valor heuristic
            return valor;   
        }
        
        //Llista de moviments possibles
        ArrayList<Point> moves_minmax = gs.getMoves();
        long hashKey;
        //Maximitza 
        if (maximitza){       
            //Recorregut dels moviments possibles 
            for(int i=0;i<moves_minmax.size();i++){
                //Copia de l'estat del tauler
                GameStatus gs_max = new GameStatus(gs);
                //Fem un moviment
                gs_max.movePiece(moves_minmax.get(i));
                //Calculem el valor hash zobrist de l'estat actual del tauler
                hashKey = calculateHashTable(gs_max);
                //Si es un moviment guanyador, no fa falta seguint explorant
                if(gs_max.checkGameOver()){    return Integer.MAX_VALUE;}       
                //Seguim l'exploracio del node
                int min =  miniMax(gs_max, depth-1, depth_a, alpha, beta, !maximitza, hashKey);
                //Valor alpha
                alpha = Math.max(alpha, min);                    
                //Poda alpha-beta
                if (beta <= alpha){ 
                    //Guardem a la taula de transposicio el cut-off de la poda com alpha
                    if(TT[estat]== null || ((TT[estat].hashKey == key && TT[estat].profPendent <= depth))){
                        TT[estat] = afegir(key, alpha, (byte) depth, (byte)1);
                    }   
                    break;  
                }
            } 
            
            return alpha;
        } 
        //Minimitza
        else{
            //Recorregut dels moviments possibles 
            for(int i=0;i<moves_minmax.size();i++){
                //Copia de l'estat del tauler
                GameStatus gs_min = new GameStatus(gs);
                //Fem un moviment
                gs_min.movePiece(moves_minmax.get(i));
                //Si es un moviment guanyador, no fa falta seguint explorant
                if(gs_min.checkGameOver()){    return Integer.MIN_VALUE;}
                //Calculem el valor hash zobrist de l'estat actual del tauler
                hashKey = calculateHashTable(gs_min);
                //Seguim l'exploracio del node
                int max = miniMax(gs_min, depth-1, depth_a, alpha, beta, !maximitza, hashKey);
                //Valor beta
                beta = Math.min(beta, max);
                //Poda alpha-beta
                if (beta <= alpha){ 
                    //Guardem a la taula de transposicio el cut-off de la poda com beta
                    if(TT[estat]== null || ((TT[estat].hashKey == key && TT[estat].profPendent <= depth))){
                        TT[estat] = afegir(key, beta, (byte) depth, (byte)2);
                    }
                    break;  
                }  
            }
            
            return beta;
        }
    }
    
    /**
     * Calcula el valor de hash del tauler del joc, fent l'operacio XOR
     * de les caselles on es troben els dos tipus de jugadors.
     * 
     * @param gs Tauler i estat actual de joc.
     * @return valor hash del tauler de joc.
     */
    public long calculateHashTable(GameStatus gs){
        long key = 0;
        for (int i = 0; i < gs.getSize(); ++i) {
            for (int j = 0; j < gs.getSize(); ++j) {
                if(gs.getPos(i, j) == CellType.PLAYER1) 
                    key ^= zobristKeys[i][j][0];
                if(gs.getPos(i, j) == CellType.PLAYER2) 
                    key ^= zobristKeys[i][j][1];
            }
        }
        return Math.abs(key);
    }    
    
    /**
     * Genera les claus Zobrist a un vector tridimensional on els indexs son
     * claus[fila][columa][tipus de peça] i els dona un valor aleatori de 64 bits.
     * 
     * @return vector tridimensional de números aleatoris de 64 bits
     */
    public long[][][] creaZobristRandom() {
        Random rand = new Random();
        long[][][] keys = new long[8][8][2];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 2; k++) {
                    keys[i][j][k] = rand.nextLong();
                }
            }
        }
        return keys;
    }
    
    /**
     * Crea un InfoNode pero poder afegir-lo a la taula de transposicio.
     * 
     * @param key clau Zobrist de l'estat del tauler
     * @param valor valor heuristic del moviment
     * @param depth profunditat assolida
     * @param tipus tipus de node 0-> exacte, 1-> poda alpha, 2-> poda beta 
     * @return un nou InfoNode
     */
    public InfoNode afegir(long key, int valor, byte depth, byte tipus){       
        return new InfoNode(key, valor, depth, tipus);                                             
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        //System.out.println("Bah! You are so slow...");
    }
    
    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return nom;
    }
}