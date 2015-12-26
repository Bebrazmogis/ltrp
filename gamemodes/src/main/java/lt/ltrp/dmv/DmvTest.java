package lt.ltrp.dmv;


import lt.ltrp.player.LtrpPlayer;

public interface DmvTest {

	LtrpPlayer getPlayer();
	boolean isFinished();
	boolean isPassed();
	void stop();
	
}