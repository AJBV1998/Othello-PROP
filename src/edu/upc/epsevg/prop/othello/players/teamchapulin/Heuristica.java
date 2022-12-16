/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.teamchapulin;

import edu.upc.epsevg.prop.othello.CellType;
import static edu.upc.epsevg.prop.othello.CellType.opposite;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author maic1
 */
public class Heuristica {

    static int evaluador;
    public static double evaluador(GameStatus s){
        double resultado;
        resultado= 64*(3*CoinParity(s)+ Mobility(s)+2*CornerCaptured(s))/600;
        return resultado;
    }
    

    // Valor heurístico de paridad de moneda =
	//100 * (Monedas máximas de jugador - Monedas mínimas de jugador) / (Monedas máximas de jugador + Monedas mínimas de jugador)
    public static double CoinParity(GameStatus s){
        CellType jugador = s.getCurrentPlayer();
        CellType contrario = opposite(jugador);
        return 100 * (s.getScore(jugador)-s.getScore(contrario)/s.getScore(jugador)+s.getScore(contrario));
    }
    
    private static double Mobility(GameStatus s) {
        int max_jugador = s.getMoves().size();
        s.skipTurn();
        int min_jugador = s.getMoves().size();
        
        if((max_jugador+min_jugador) != 0  )
            return 100*(max_jugador - min_jugador)/(max_jugador + min_jugador);
        else
            return 0;
    }

    private static double CornerCaptured(GameStatus s) {
        int maxCorners = 0;
        int minCorners = 0;
        
        if(s.getPos(0,0)==s.getCurrentPlayer()) maxCorners++;
        else minCorners++;
        
        if(s.getPos(0,7)==s.getCurrentPlayer()) maxCorners++;
        else minCorners++;
        
        if(s.getPos(7,0)==s.getCurrentPlayer()) maxCorners++;
        else minCorners++;
        
        if(s.getPos(7,7)==s.getCurrentPlayer()) maxCorners++;
        else minCorners++;
        
        if((maxCorners + minCorners) != 0)
            return 100 *(maxCorners - minCorners)/(maxCorners + minCorners);
        else return 0;
    }
    
}
