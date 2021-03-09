package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;
import java.util.Random;

public class DonkeyAI implements IBot {

    private String Donkey_AI = "Donkey_AI";
    private Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        System.out.println(state.getField());
        return availableMoves.get(rand.nextInt(availableMoves.size()-1));
    }

    @Override
    public String getBotName() {
        return Donkey_AI;
    }
}
