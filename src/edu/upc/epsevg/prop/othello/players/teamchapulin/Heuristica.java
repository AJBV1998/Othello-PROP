/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.teamchapulin;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;

/**
 * Clase Heuristica.
 * 
 * @author Antony Baño (antony.joel.bano@estudiantat.upc.edu)
 * @author Michael Zerpa (michael.alberto.zerpa@estudiantat.upc.edu)
 */
public class Heuristica {
    //Per guadar el jugador
    private static CellType jugador = CellType.EMPTY;
    //Per guadar el contrari
    private static CellType adversari = CellType.EMPTY;
    
    //Per fer les esquines adjacents positives 
    private static int X1 = 1, X2 = 1, X3 = 1, X4 = 1;
    //Matriu que descriu el pes en les diferents posicions en el tauler
    static int[][] stability_board = new int[][]{ //Tauler 8x8
        { 100, -20*X1,  10,  5,  5,  10, -20*X2,  100 },
        { -20*X1, -50,  -2, -2, -2,  -2, -50,  -20*X2 },
        {  10,  -2,  -1, -1, -1,  -1,  -2,   10 },
        {   5,  -2,  -1, -1, -1,  -1,  -2,    5 },
        {   5,  -2,  -1, -1, -1,  -1,  -2,    5 },
        {  10,  -2,  -1, -1, -1,  -1,  -2,   10 },
        { -20*X3, -50,  -2, -2, -2,  -2, -50,  -20*X4 },
        { 100, -20*X3,  10,  5,  5,  10, -20*X4,  100 } 
    };
    
    /**
     * Funció d'avaluació utilitzat en MiniMax limitat en profunditat i MiniMax amb Iterative Deepeing per calcular el valor d'utilitat que utilitzarà 
     * algorisme per determinar el següent millor moviment possible.
     * 
     * Recull una serie de valors de diferents funcions per tornar el valor heurístic corresponent, el resultat de cada funció se li assigna un pes.
     * Tenim la funció CoinParity que té assignat un pes de 25, la funció Mobility té un pes de 10,
     * la funció CornerCaptured té un pes de 100 i la funció Stability no té perque l'agafa de la matriu estatica.
     * 
     * @param gs Tauler i estat actual de joc.
     * 
     * @return El valor heuristic calculat a partir de la suma de les funcions descrites.
     */
    public static int evaluador(GameStatus gs){
        //Jugador actual 
        jugador = gs.getCurrentPlayer();
        //Jugador contrari
        adversari = CellType.opposite(jugador);
        
        //retornar la suma de les diferents funcions descrites.
        return 25*CoinParity(gs) + 100*CornerCaptured(gs) + (Stability(gs) + 10*Mobility(gs));
    }
    
    /**
     * Maximitzar la quantitat de fitxes del jugador a partir de la diferència relativa 
     * A partir del estat actual del tauler, calcular el total de fitxes colocades per part de cada jugador per 
     * després fer la diferència relativa. 
     * 
     * @param gs Tauler i estat actual de joc.
     * @return Diferència relativa del total fitxes de cada jugador, el valor retornat serà entre -100 i 100
     */
    public static int CoinParity(GameStatus gs){
        //Total de fitxes del jugador
        int coinsJugador1 = gs.getScore(jugador);
        //Total de fitxes del contrari
        int coinsJugador2 = gs.getScore(adversari);
        int coinsParity = 0;
        //Jugador actual 
        if(CellType.PLAYER1 == jugador)
            //Diferència relativa de les fitxes de cada jugador * 100
            coinsParity = 100*((coinsJugador1-coinsJugador2)/(coinsJugador1+coinsJugador2));
        //Jugador contrari
        if(CellType.PLAYER2 == jugador)
            //Diferència relativa de les fitxes de cada jugador * 100
            coinsParity = 100*((coinsJugador2-coinsJugador1)/(coinsJugador2+coinsJugador1));
        
        return coinsParity;
    }
    
    /**
     * Maximitzar la quantitat de fitxes del jugador 
     * A partir del estat actual del tauler, calcular el total de fitxes colocades per part de cada jugador per 
     * després fer la diferència. 
     * 
     * @param gs Tauler i estat actual de joc.
     * @return Diferència de les fitxes del jugador i el contrari
     */
    public static int CoinsCount(GameStatus gs){
        //Total de fitxes del jugador
        int coinsJugador1 = gs.getScore(jugador);
        //Total de fitxes del contrari
        int coinsJugador2 = gs.getScore(adversari);
        
        int coinsCount = 0;
        
        //Jugador actual 
        if(CellType.PLAYER1 == jugador)
            coinsCount = coinsJugador1-coinsJugador2;
        //Jugador contrari
        if(CellType.PLAYER2 == jugador)
            coinsCount = coinsJugador2-coinsJugador1;
        
        return coinsCount;
    }
    
    /**
     * Potenciar la posició en les esquines per al jugador 
     * A partir del estat actual del tauler, compten les esquines ocupades per 
     * part de cada jugador, tener les esquines captures permet tenir millor estabilitat
     * 
     * @param s Tauler i estat actual de joc.
     * @return Diferència relativa de les esquines conquistades per part dels jugador, el valor retornat serà entre -100 i 100
     */
    private static int CornerCaptured(GameStatus s) {
        //Comptabilitzar del jugador
        int j_corners = 0;
        //Comptabilitzar del contrari
        int a_corners = 0;
        
        // La variable "x" serà utilitzada en la funció Stability en el cas que el jugador 
        // tingui una fitxa en una esquina per augmentar la captura de les posicions adjacents
        
        // Augmentar el comptador de jugador o contrari si tenen la fitxa posada en la esquina (0,0)
        if(s.getPos(0,0) == jugador){    j_corners++;  X1=-1;}
        if(s.getPos(0,0) == adversari)   a_corners++;
        
        // Augmentar el comptador de jugador o contrari si tenen la fitxa posada en la esquina (0,7)
        if(s.getPos(0,7) == jugador){   j_corners++;  X2=-1;}
        if(s.getPos(0,7) == adversari)   a_corners++;
        
        // Augmentar el comptador de jugador o contrari si tenen la fitxa posada en la esquina (7,0)
        if(s.getPos(7,0) == jugador){   j_corners++;  X3=-1;}
        if(s.getPos(7,0) == adversari)   a_corners++;
        
        // Augmentar el comptador de jugador o contrari si tenen la fitxa posada en la esquina (7,7)
        if(s.getPos(7,7) == jugador){   j_corners++;  X4=-1;}
        if(s.getPos(7,7) == adversari)   a_corners++;
        
        //Fer la diferència relativa de les esquines captures si ha ha alguna esquina capturada
        if(j_corners + a_corners != 0)
            return 100 * ( (j_corners-a_corners) / (j_corners + a_corners) );
        //Si ningú jugador té una esquina capturada, retornar un 0
        else    return 0;
    }
    
    /**
     * Augmentar la quantitat de moviments del jugador i disminuir la movilitat del contrari 
     * A partir del estat actual del tauler, comprovem la la quantitat de moviments disponibles de cada jugador, 
     * per després fer la diferència relativa a partir dels moviments calculats, potenciem el resultat si el jugador té alguna esquina capturada,
     * en cas que l'adversari tingi més esquines capturares fem més baix el valor resultat de diferència relativa.
     *  
     * @param s Tauler i estat actual de joc.
     * @return Diferència relativa de les esquines conquistades per part dels jugador
     */
    private static int Mobility(GameStatus s) {
        //Quantitat de moviments que pot fer el jugador en el estat actual del tauler
        int jugador_moves = s.getMoves().size();
        s.skipTurn();
        //Quantitat de moviments que pot fer el contrari en el estat actual del tauler
        int adversari_moves = s.getMoves().size();
        //Fer la diferència relativa si algún jugador te almanys un moviment
        if(jugador_moves + adversari_moves != 0)
            return 100*( (jugador_moves - adversari_moves) / (jugador_moves + adversari_moves) );
        //En cas que ningú juggador tingui moviments, reduir el resultat
        else return 0;
    }
    
    /**
     * Potenciar el posicionament del jugador 
     * A partir de la matriu stability_board que descriu el valors de les posicions del tauler, 
     * comptabilitzem les posicions de les fitxes del jugador descrites en la matriu, fent el mateix per al contrri.
     * La matriu té en compta les adjacències de les esquines en cas de ser capturares per part del jugador per maximitzar 
     * les captures de les poscions adjacents veïnes.
     * 
     * @param s Tauler i estat actual de joc.     
     * @return Retorna la diferència deprés d'haver calculat el pes de les posicions de cada jugador
     */
    private static int Stability(GameStatus s) {
        //Variable per guardar el pes de les posicions del jugador
        int stability_jugador = 0;
        //Variable per guardar el pes de les posicions del contrari
        int stability_adversari = 0;
        
        //Recorregut del tauler per fer la suma en funció del pes descrit en la mstriu stability_board
        for(int i = 0; i < s.getSize(); ++i){
            for(int j = 0; j < s.getSize(); ++j){
                //Comprovar si el jugador té posada una fitxa en la posició comparada
                if(s.getPos(i,j) == jugador) stability_jugador+=stability_board[i][j];
                //Comprovar si el contrari té posada una fitxa en la posició comparada
                if(s.getPos(i,j) == adversari) stability_adversari+=stability_board[i][j];
            }
        }
        
        return stability_jugador-stability_adversari;
    }
}