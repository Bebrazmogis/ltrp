
#include <a_npc>


public OnNPCSpawn()
{
	StartPlayback();
}

public OnRecordingPlaybackEnd()
{
	StartPlayback();
}

StartPlayback()
{
	StartRecordingPlayback(PLAYER_RECORDING_TYPE_ONFOOT, "static_bank");
}