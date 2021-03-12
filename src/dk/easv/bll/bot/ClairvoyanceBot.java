package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClairvoyanceBot implements IBot {

    private static final String BOTNAME = "Clairvoyance bot";
    private static int playerNumber;
    private Random rand = new Random();

    private static int[][] miniBoard = new int[3][3];
    private static int[][] macroBoard = new int[3][3];
    private static int[][] priorityBoard = new int[3][3];

    private static final int EMPTY = 0;
    private static final int MAJORITY_BOT = 1;
    private static final int MAJORITY_OPP = 2;
    private static final int BOT_WIN = 3;
    private static final int OPPONENT_WIN = 4;
    private static final int FREEFORM = 5;

    private static final int GENERIC_TILE = 0;
    private static final int BLOCK_TILE = 1;
    private static final int WIN_TILE = 2;
    private static final int TAKEN_TILE = 3;
    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();

        if(isFreeformMove(state)) System.out.println("freeform");
        setPlayerNumber(state);

        if (state.getMoveNumber() == 0 ){
            return starterMove();
        }
        if (moves.size() > 0) {
            return play(state);
        }
        return null;
        //start in the middle on the edge tiles
        //swing for the fences(Don't send the opponent to the middle unless you stand to win a board)
        //block enemy wins (avoid sending the opponent into a winning board)>(block his lines)
        //don't do moves that give your opponent a freeform move
    }
    @Override
    public String getBotName() {
        return BOTNAME;
    }

    private void setPlayerNumber(IGameState state){
        playerNumber = state.getMoveNumber()%2;
    }

    private IMove starterMove(){
        int x = rand.nextInt(3)+3;
        int y = rand.nextInt(3)+3;
        if(y == x && y == 5){
            y+=1;
        }
        return new Move(x,y);
    }

    private boolean isFreeformMove(IGameState state){
        int availableMacros = 0;

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                if (state.getField().getMacroboard()[i][k].equals(IField.AVAILABLE_FIELD)) availableMacros += 1;
            }
        }
        return availableMacros > 1;
    }

    private int macroState(int x,int y,IGameState state){
        if(state.getField().getMacroboard()[x][y].equals(IField.EMPTY_FIELD) || state.getField().getMacroboard()[x][y].equals(IField.AVAILABLE_FIELD)){
            return isPotentialWin(x, y, state);
        }
        return FREEFORM;
    }

    private void fillMacroBoard(IGameState state){
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                macroBoard[i][k] = macroState(i,k,state);
                System.out.println(macroState(i,k,state));
            }
        }
    }

    private int isPotentialWin(int xmacro,int ymacro,IGameState state){
        int winningMove = EMPTY;
        int botTiles = 0;
        int opponentTiles = 0;
        int totalBotTiles = 0;
        int totalOpponentTiles = 0;
        //column checker
        for (int x = 0; x < 3; x++){
            for (int y = 0; y < 3; y++){
                //count each player's tiles in a column
                if(state.getField().getPlayerId(x + xmacro*3,y + ymacro*3).contains(""+(playerNumber))){
                    botTiles+=1;
                }
                else if(!state.getField().getPlayerId(x + xmacro*3,y + ymacro*3).contains(IField.EMPTY_FIELD)){
                    opponentTiles+=1;
                }
            }
            if (botTiles == 2 && opponentTiles == 0 && winningMove != OPPONENT_WIN) winningMove = BOT_WIN;
            else if (botTiles == 0 && opponentTiles == 2) winningMove = OPPONENT_WIN;
            else {
                totalBotTiles+=botTiles;
                totalOpponentTiles+=opponentTiles;
            }
        }
        botTiles = 0;
        opponentTiles = 0;
        //row checker
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                //count each player's tiles in a row
                if(state.getField().getPlayerId(x + xmacro*3,y + ymacro*3).contains(""+(playerNumber))){
                    botTiles+=1;
                }
                else if(!state.getField().getPlayerId(x + xmacro*3,y + ymacro*3).contains(IField.EMPTY_FIELD)){
                    opponentTiles+=1;
                }
            }
            if (botTiles == 2 && opponentTiles == 0 && winningMove != OPPONENT_WIN) winningMove = BOT_WIN;
            else if (botTiles == 0 && opponentTiles == 2) winningMove = OPPONENT_WIN;
            else {
                totalBotTiles+=botTiles;
                totalOpponentTiles+=opponentTiles;
            }
        }
        botTiles = 0;
        opponentTiles = 0;
        //diagonal checker
        //first diagonal
        for(int i = 0; i < 3; i++) {
            if (state.getField().getPlayerId(i + xmacro * 3, i + ymacro * 3).contains("" + (playerNumber))) {
                botTiles += 1;
            } else if (!state.getField().getPlayerId(i + xmacro * 3, i + ymacro * 3).contains(IField.EMPTY_FIELD)) {
                opponentTiles += 1;
            }
        }
        if (botTiles == 2 && opponentTiles == 0 && winningMove != OPPONENT_WIN) winningMove = BOT_WIN;
        else if (botTiles == 0 && opponentTiles == 2) winningMove = OPPONENT_WIN;
        else {
            totalBotTiles+=botTiles;
            totalOpponentTiles+=opponentTiles;
        }
        botTiles = 0;
        opponentTiles = 0;
        //second diagonal
        for(int i = 0; i < 3; i++) {
            if (state.getField().getPlayerId(2-i + xmacro * 3, i + ymacro * 3).contains("" + (playerNumber))) {
                botTiles += 1;
            } else if (!state.getField().getPlayerId(2-i + xmacro * 3, i + ymacro * 3).contains(IField.EMPTY_FIELD)) {
                opponentTiles += 1;
            }
        }
        if (botTiles == 2 && opponentTiles == 0 && winningMove != OPPONENT_WIN) winningMove = BOT_WIN;
        else if (botTiles == 0 && opponentTiles == 2) winningMove = OPPONENT_WIN;
        else {
            totalBotTiles+=botTiles;
            totalOpponentTiles+=opponentTiles;
        }
        //Majority count if no one has potential win
        if(winningMove == EMPTY && totalBotTiles > totalOpponentTiles) winningMove = MAJORITY_BOT;
        else if(winningMove == EMPTY && totalOpponentTiles > 0) winningMove = MAJORITY_OPP;
        return winningMove;
    }

    private int miniState(int x,int y,int xmacro,int ymacro,IGameState state){
        if(state.getField().getPlayerId(x + xmacro*3,y + ymacro*3).contains(IField.EMPTY_FIELD)){
            return checkForWin(x, y, xmacro, ymacro, state);
        }
        return TAKEN_TILE;
    }

    private void fillMiniBoard(int xmacro,int ymacro,IGameState state){
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                miniBoard[i][k] = miniState(i,k,xmacro,ymacro,state);
            }
        }
    }

    private int checkForWin(int x,int y,int xmacro,int ymacro,IGameState state){
        int tileType = GENERIC_TILE;
        int botTiles = 0;
        int opponentTiles = 0;
        //row check
        for (int i = 0; i < 3; i++) {
            if(state.getField().getPlayerId(i + xmacro*3,y + ymacro*3).contains(""+(playerNumber))){
                botTiles+=1;
            }
            else if(!state.getField().getPlayerId(i + xmacro*3,y + ymacro*3).contains(IField.EMPTY_FIELD)){
                opponentTiles+=1;
            }
        }
        if (botTiles == 0 && opponentTiles == 2) tileType = BLOCK_TILE;
        else if (botTiles == 2 && opponentTiles == 0) tileType = WIN_TILE;
        botTiles = 0;
        opponentTiles = 0;
        //column check
        for (int i = 0; i < 3; i++) {
            if(state.getField().getPlayerId(x + xmacro*3,i + ymacro*3).contains(""+(playerNumber))){
                botTiles+=1;
            }
            else if(!state.getField().getPlayerId(x + xmacro*3,i + ymacro*3).contains(IField.EMPTY_FIELD)){
                opponentTiles+=1;
            }
        }
        if (botTiles == 0 && opponentTiles == 2 && tileType != WIN_TILE) tileType = BLOCK_TILE;
        else if (botTiles == 2 && opponentTiles == 0) tileType = WIN_TILE;
        botTiles = 0;
        opponentTiles = 0;
        //diagonal check
        if(!((x == 1 &&(y == 0 || y == 2))||(y == 1 &&(x == 0 || x == 2)))){ //exclude tiles that don't form diagonals
            if(x == y) {
                //first diagonal
                for (int i = 0; i < 3; i++) {
                    if (state.getField().getPlayerId(i + xmacro * 3, i + ymacro * 3).contains("" + (playerNumber))) {
                        botTiles += 1;
                    } else if (!state.getField().getPlayerId(i + xmacro * 3, i + ymacro * 3).contains(IField.EMPTY_FIELD)) {
                        opponentTiles += 1;
                    }
                }
                if (botTiles == 0 && opponentTiles == 2 && tileType != WIN_TILE) tileType = BLOCK_TILE;
                else if (botTiles == 2 && opponentTiles == 0) tileType = WIN_TILE;
            }
            else {
                //second diagonal
                for (int i = 0; i < 3; i++) {
                    if (state.getField().getPlayerId(2 - i + xmacro * 3, i + ymacro * 3).contains("" + (playerNumber))) {
                        botTiles += 1;
                    } else if (!state.getField().getPlayerId(2 - i + xmacro * 3, i + ymacro * 3).contains(IField.EMPTY_FIELD)) {
                        opponentTiles += 1;
                    }
                }
                if (botTiles == 0 && opponentTiles == 2 && tileType != WIN_TILE) tileType = BLOCK_TILE;
                else if (botTiles == 2 && opponentTiles == 0) tileType = WIN_TILE;
            }
        }
        return tileType;
    }

    private IMove play(IGameState state){
        fillMacroBoard(state);
        if(isFreeformMove(state)){
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    fillMiniBoard(i,k,state);
                    //go random
                }
            }
        }
        else {
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    if (state.getField().getMacroboard()[i][k].contains(IField.AVAILABLE_FIELD)){
                        System.out.println("selected board:"+ i +","+ k);
                        fillMiniBoard(i,k,state);
                        return priorityMove(i,k,state);
                    }
                }
            }
        }
        //Do the first available move by default
        System.out.println("firstAvailable");
        return state.getField().getAvailableMoves().get(rand.nextInt(state.getField().getAvailableMoves().size()));
    }

    private void fillPriorityBoard(IGameState state){
        for (int l = 0; l < 3; l++) {
            for (int m = 0; m < 3; m++) {
                System.out.println("checking" + l + "," + m);
                switch (miniBoard[l][m]) {
                    case WIN_TILE -> {
                        switch (macroBoard[l][m]) {
                            case EMPTY -> priorityBoard[l][m] = 18;
                            case MAJORITY_BOT -> priorityBoard[l][m] = 17;
                            case MAJORITY_OPP -> priorityBoard[l][m] = 16;
                            case BOT_WIN -> priorityBoard[l][m] = 9;
                            case OPPONENT_WIN -> priorityBoard[l][m] = 6;
                            case FREEFORM -> priorityBoard[l][m] = 5;
                        }
                        System.out.print(priorityBoard[l][m]);
                    }
                    case BLOCK_TILE -> {
                        switch (macroBoard[l][m]) {
                            case EMPTY -> priorityBoard[l][m] = 15;
                            case MAJORITY_BOT -> priorityBoard[l][m] = 14;
                            case MAJORITY_OPP -> priorityBoard[l][m] = 13;
                            case BOT_WIN -> priorityBoard[l][m] = 8;
                            case OPPONENT_WIN -> priorityBoard[l][m] = 4;
                            case FREEFORM -> priorityBoard[l][m] = 3;
                        }
                        System.out.print(priorityBoard[l][m]);
                    }
                    case GENERIC_TILE -> {
                        switch (macroBoard[l][m]) {
                            case EMPTY -> priorityBoard[l][m] = 12;
                            case MAJORITY_BOT -> priorityBoard[l][m] = 11;
                            case MAJORITY_OPP -> priorityBoard[l][m] = 10;
                            case BOT_WIN -> priorityBoard[l][m] = 7;
                            case OPPONENT_WIN -> priorityBoard[l][m] = 2;
                            case FREEFORM -> priorityBoard[l][m] = 1;
                        }
                        System.out.print(priorityBoard[l][m]);
                    }
                    case TAKEN_TILE -> priorityBoard[l][m] = 0;
                }
                System.out.println("");
            }
        }
    }

    private IMove priorityMove(int xMacro,int yMacro,IGameState state){
        fillPriorityBoard(state);
        for (int i = 18;i>0;i--){
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {

                    if (priorityBoard[x][y] == i) {
                        System.out.println("click " + x + "and" + y);
                        return new Move(x + xMacro * 3, y + yMacro * 3);
                    }
                }
            }
        }
        return state.getField().getAvailableMoves().get(rand.nextInt(state.getField().getAvailableMoves().size()));
    }
}
