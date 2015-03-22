


#include <a_npc>



public OnRecordingPlaybackEnd()
{
    StartRecordingPlayback(PLAYER_RECORDING_TYPE_DRIVER,"andromada_ls_to_lv");
}


public OnNPCEnterVehicle(vehicleid, seatid)
{
    StartRecordingPlayback(PLAYER_RECORDING_TYPE_DRIVER,"andromada_ls_to_lv");
}


public OnNPCExitVehicle()
{
    StopRecordingPlayback();
}