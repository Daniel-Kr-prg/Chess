package chess.model;

import chess.view.Window;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

public class MyTimer extends Thread {
    private Model mod;
    private Window view;
    private int secondsOnMove;
    private boolean counting = true;

    /**
     * initialize the timer
     * @param mod it needs Model to notify it about the timer
     * @param secondsOnMove seconds for time out
     */
    public MyTimer(Model mod, int secondsOnMove)
    {
        this.mod = mod;
        view = mod.getView();
        this.secondsOnMove = secondsOnMove;
    }
    /**
     * stop counting
     */
    public void stopIt()
    {
        counting = false;
    }
    /**
     * start counting
     */
    public void run() {
        try {
            counting = true;
            LocalTime now = LocalTime.now();
            LocalTime moveTime = now.plusSeconds(secondsOnMove);
            while (counting) {
                now = LocalTime.now();
                if (view != null) {
                    Long minutes = ChronoUnit.MINUTES.between(now, moveTime);
                    now = now.plusMinutes(minutes);
                    Long seconds = ChronoUnit.SECONDS.between(now, moveTime);
                    view.showTime(minutes, seconds);
                }
                if (now.isAfter(moveTime)) {
                    mod.notifyGameState(GameState.TimeLeft);
                    return;
                }
                sleep(50);
            }
        }
        catch (InterruptedException e)
        {

        }
    }
}
