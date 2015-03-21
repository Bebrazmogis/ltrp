


#include <a_npc>



public OnRecordingPlaybackEnd()
{
    StartRecordingPlayback(PLAYER_RECORDING_TYPE_DRIVER,"cargo_train_loop_slow");
}


public OnNPCEnterVehicle(vehicleid, seatid)
{
    StartRecordingPlayback(PLAYER_RECORDING_TYPE_DRIVER,"cargo_train_loop_slow");
}


public OnNPCExitVehicle()
{
    StopRecordingPlayback();
}