package lt.ltrp;


import lt.ltrp.object.LtrpPlayer;public interface DmvTest {

	LtrpPlayer getPlayer();
	boolean isFinished();
	boolean isPassed();
	void stop();
	
}