/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.teamchapulin;

import edu.upc.epsevg.prop.othello.CellType;
import static edu.upc.epsevg.prop.othello.CellType.opposite;
import edu.upc.epsevg.prop.othello.GameStatus;


/**
 *
 * @author maic1
 */
public class Heuristica {
    public static double evaluador(GameStatus s){
        double resultat;
        resultat = 25*CoinParity(s) + 5*Mobility(s) + 100*CornerCaptured(s); //+ 25*Stability(s);
        return resultat*Stability(s);
    }
    

    // Valor heurístico de paridad de moneda =
	//100 * (Piezas jugador1 - Piezas jugador2) / (Piezas jugador1 + Piezas jugador2)
    public static double CoinParity(GameStatus s){
        CellType jugador = s.getCurrentPlayer();
        CellType adversari = opposite(jugador);
        return 100 * ( ( s.getScore(jugador) - s.getScore(adversari) ) / ( s.getScore(jugador) + s.getScore(adversari) ) );
    }
    
    private static double Mobility(GameStatus s) {
        int jugador_moves = s.getMoves().size();
        s.skipTurn();
        int adversari_moves = s.getMoves().size();
        
        if((jugador_moves + adversari_moves) != 0  )
            return 100 * ( (jugador_moves - adversari_moves) / (jugador_moves + adversari_moves) );
        else
            return 0;
    }

    private static double CornerCaptured(GameStatus s) {
        int jugador_corners = 0;
        int adversari_corners = 0;
        
        if(s.getPos(0,0)==s.getCurrentPlayer()) jugador_corners++;
        else{if(s.getPos(0,0)!=CellType.EMPTY)   adversari_corners++;}
        
        if(s.getPos(0,7)==s.getCurrentPlayer()) jugador_corners++;
        else{if(s.getPos(0,7)!=CellType.EMPTY)   adversari_corners++;}
        
        if(s.getPos(7,0)==s.getCurrentPlayer()) jugador_corners++;
        else{if(s.getPos(7,0)!=CellType.EMPTY)   adversari_corners++;}
        
        if(s.getPos(7,7)==s.getCurrentPlayer()) jugador_corners++;
        else{if(s.getPos(7,7)!=CellType.EMPTY)   adversari_corners++;}
        
        if((jugador_corners + adversari_corners) != 0)
            return 100 * ( (jugador_corners - adversari_corners) / (jugador_corners + adversari_corners) );
        else return 0;
    }
    
    private static int Stability(GameStatus s) {
        int jugador_stability = 0;
        int adversari_stability = 0;
        
        int[][] stability_board = new int[][]{ //8x8
                    { 4, -3,  2,  2,  2,  2, -3,  4},
                    {-3, -4, -1, -1, -1, -1, -4, -3},
                    { 2, -1,  1,  0,  0,  1, -1,  2},
                    { 2, -1,  0,  1,  1,  0, -1,  2},
                    { 2, -1,  0,  1,  1,  0, -1,  2},
                    { 2, -1,  1,  0,  0,  1, -1,  2},
                    {-3, -4, -1, -1, -1, -1, -4, -3},
                    { 4, -3,  2,  2,  2,  2, -3,  4}
            };
        
        for(int i = 0; i < s.getSize(); ++i){
            for(int j = 0; j < s.getSize(); ++j){
                if(s.getPos(i,j) == s.getCurrentPlayer()) jugador_stability+=stability_board[i][j];
                else{ if(s.getPos(i,j)!=CellType.EMPTY) adversari_stability-=stability_board[i][j];}
            }
        }
        
        return jugador_stability - adversari_stability;
    }
    
}
