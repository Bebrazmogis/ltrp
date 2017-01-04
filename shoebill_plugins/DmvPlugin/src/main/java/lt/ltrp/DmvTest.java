package lt.ltrp;


import lt.ltrp.player.object.LtrpPlayer;public interface DmvTest {

	LtrpPlayer getPlayer();
	boolean isFinished();
	boolean isPassed();
	void stop();
	
}