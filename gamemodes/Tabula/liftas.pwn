
/*
*   Example elevator system for the new LS building.
*
*	Zamaroht 2010
*
*   26/08/2011: Kye: added a sound effect for the elevator starting/stopping.
*/

#define ELEVATOR_SPEED      (4.0)   // Movement speed of the elevator.
#define DOORS_SPEED         (1.5)   // Movement speed of the doors.
#define ELEVATOR_WAIT_TIME  (5000)  // Time in ms that the elevator will wait in each floor before continuing with the queue.
									// Be sure to give enough time for doors to open.

#define DIALOG_ID           (101)

// Private:
#define X_DOOR_CLOSED       (1786.627685)
#define X_DOOR_R_OPENED     (1785.027685)
#define X_DOOR_L_OPENED     (1788.227685)
#define GROUND_Z_COORD      (14.511476)
#define ELEVATOR_OFFSET     (0.059523)

/* ------------------
// Constants:
-------------------*/
static FloorNames[21][] =
{
	"Pagrindinis áëjimas",
	"Pirmas Aukðtas",
	"Antras Aukðtas",
	"Treèias Aukðtas",
	"Ketvirtas Aukðtas",
	"Penktas Aukðtas",
	"Ðeðtas Aukðtas",
	"Septintas Aukðtas",
	"Aðtuntas Aukðtas",
	"Devintas Aukðtas",
	"Deðimtas Aukðtas",
	"Vienuoliktas Aukðtas",
	"Dvyliktas Aukðtas",
	"Tryliktas Aukðtas",
	"Keturioliktas Aukðtas",
	"Penkioliktas Aukðtas",
	"Ðeðioliktas Aukðtas",
	"Septinioliktas Aukðtas",
	"Aðtunioliktas Aukðtas",
	"Devinioliktas Aukðtas",
	"Palëpë"
};

static Float:FloorZOffsets[21] =
{
    0.0,		// 0.0,
    8.5479,		// 8.5479,
    13.99945,   // 8.5479 + (5.45155 * 1.0),
    19.45100,   // 8.5479 + (5.45155 * 2.0),
    24.90255,   // 8.5479 + (5.45155 * 3.0),
    30.35410,   // 8.5479 + (5.45155 * 4.0),
    35.80565,   // 8.5479 + (5.45155 * 5.0),
    41.25720,   // 8.5479 + (5.45155 * 6.0),
    46.70875,   // 8.5479 + (5.45155 * 7.0),
    52.16030,   // 8.5479 + (5.45155 * 8.0),
    57.61185,   // 8.5479 + (5.45155 * 9.0),
    63.06340,   // 8.5479 + (5.45155 * 10.0),
    68.51495,   // 8.5479 + (5.45155 * 11.0),
    73.96650,   // 8.5479 + (5.45155 * 12.0),
    79.41805,   // 8.5479 + (5.45155 * 13.0),
    84.86960,   // 8.5479 + (5.45155 * 14.0),
    90.32115,   // 8.5479 + (5.45155 * 15.0),
    95.77270,   // 8.5479 + (5.45155 * 16.0),
    101.22425,  // 8.5479 + (5.45155 * 17.0),
    106.67580,	// 8.5479 + (5.45155 * 18.0),
    112.12735	// 8.5479 + (5.45155 * 19.0)
};

/* ------------------
// Variables:
-------------------*/
new Obj_Elevator, Obj_ElevatorDoors[2],
	Obj_FloorDoors[21][2];

new Text3D:Label_Elevator, Text3D:Label_Floors[21];

#define ELEVATOR_STATE_IDLE     (0)
#define ELEVATOR_STATE_WAITING  (1)
#define ELEVATOR_STATE_MOVING   (2)

new ElevatorState,
	ElevatorFloor;  // If Idle or Waiting, this is the current floor. If Moving, the floor it's moving to.

#define INVALID_FLOOR           (-1)

new ElevatorQueue[21],  	// Floors in queue.
	FloorRequestedBy[21];   // FloorRequestedBy[floor_id] = playerid; - Points out who requested which floor.

/* ------------------
*  Function forwards:
-------------------*/
// Public:
forward CallElevator(playerid, floorid);    // You can use INVALID_PLAYER_ID too.
forward ShowElevatorDialog(playerid);

// Private:
forward Elevator_TurnToIdle();

public OnDynamicObjectMoved(objectid)
{
	#if defined liftas_OnDynamicObjectMoved
		liftas_OnDynamicObjectMoved(objectid);
	#endif
    new Float:x, Float:y, Float:z;
	for(new i; i < sizeof(Obj_FloorDoors); i ++)
	{
		if(objectid == Obj_FloorDoors[i][0])
		{
		    GetDynamicObjectPos(Obj_FloorDoors[i][0], x, y, z);

		    if(x < X_DOOR_L_OPENED - 0.5)   // Some floor doors have shut, move the elevator to next floor in queue:
		    {
				Elevator_MoveToFloor(ElevatorQueue[0]);
				RemoveFirstQueueFloor();
			}
		}
	}

	if(objectid == Obj_Elevator)   // The elevator reached the specified floor.
	{
	    FloorRequestedBy[ElevatorFloor] = INVALID_PLAYER_ID;

		SetTimer("Elevator_Dors_Open", 1000, 0);

	    GetDynamicObjectPos(Obj_Elevator, x, y, z);
	    Label_Elevator	= CreateDynamic3DTextLabel("Spauskite 'F' kad pasirinktumëte aukðtà", 0xFFFFFFFF, 1784.9822, -1302.0426, z - 0.9, 4.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, true, -1, -1, -1, 15.0);

	    ElevatorState 	= ELEVATOR_STATE_WAITING;
	    SetTimer("Elevator_TurnToIdle", ELEVATOR_WAIT_TIME, 0);
	}

	return 1;
}
#if defined _ALS_OnDynamicObjectMoved
	#undef OnDynamicObjectMoved
#else 
	#define _ALS_OnDynamicObjectMoved
#endif
#define OnDynamicObjectMoved liftas_OnDynamicObjectMoved
#if defined liftas_OnDynamicObjectMoved
	forward liftas_OnDynamicObjectMoved(objectid);
#endif


forward Elevator_Dors_Open( floor );
public Elevator_Dors_Open( floor )
{
	Elevator_OpenDoors();
 	Floor_OpenDoors(ElevatorFloor);
 	return 1;
}

// ------------------------ Functions ------------------------
stock Elevator_Initialize()
{
	// Initializes the elevator.

	Obj_Elevator 			= CreateDynamicObject(18755, 1786.678100, -1303.459472, GROUND_Z_COORD + ELEVATOR_OFFSET, 0.000000, 0.000000, 270.000000);
	Obj_ElevatorDoors[0] 	= CreateDynamicObject(18757, X_DOOR_CLOSED, -1303.459472, GROUND_Z_COORD, 0.000000, 0.000000, 270.000000);
	Obj_ElevatorDoors[1] 	= CreateDynamicObject(18756, X_DOOR_CLOSED, -1303.459472, GROUND_Z_COORD, 0.000000, 0.000000, 270.000000);

	Label_Elevator          = CreateDynamic3DTextLabel("Spauskite 'F' kad pasirinktumëte aukðtà", 0xFFFFFFFF, 1784.9822, -1302.0426, 13.6491, 4.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, true, -1, -1, -1, 15.0);

	new string[128],
		Float:z;

	for(new i; i < sizeof(Obj_FloorDoors); i ++)
	{
	    Obj_FloorDoors[i][0] 	= CreateDynamicObject(18757, X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(i), 0.000000, 0.000000, 270.000000);
		Obj_FloorDoors[i][1] 	= CreateDynamicObject(18756, X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(i), 0.000000, 0.000000, 270.000000);

		format(string, sizeof(string), "%s\nSpauskite 'F' kad iðkviestumëte.", FloorNames[i]);

		if(i == 0)
		    z = 13.4713;
		else
		    z = 13.4713 + 8.7396 + ((i-1) * 5.45155);

		Label_Floors[i]         = CreateDynamic3DTextLabel(string, 0xFFFFFFFF, 1783.9799, -1300.7660, z, 10.5, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, true, -1, -1, -1, 15.0);
	}

	// Open ground floor doors:
	Floor_OpenDoors(0);
	Elevator_OpenDoors();

	return 1;
}

stock Elevator_Destroy()
{
	// Destroys the elevator.

	DestroyDynamicObject(Obj_Elevator);
	DestroyDynamicObject(Obj_ElevatorDoors[0]);
	DestroyDynamicObject(Obj_ElevatorDoors[1]);
	DestroyDynamic3DTextLabel(Label_Elevator);

	for(new i; i < sizeof(Obj_FloorDoors); i ++)
	{
	    DestroyDynamicObject(Obj_FloorDoors[i][0]);
		DestroyDynamicObject(Obj_FloorDoors[i][1]);
		DestroyDynamic3DTextLabel(Label_Floors[i]);
	}

	return 1;
}

stock Elevator_OpenDoors()
{
	// Opens the elevator's doors.

	new Float:x, Float:y, Float:z;

	GetDynamicObjectPos(Obj_ElevatorDoors[0], x, y, z);
	MoveDynamicObject(Obj_ElevatorDoors[0], X_DOOR_L_OPENED, y, z, DOORS_SPEED);
	MoveDynamicObject(Obj_ElevatorDoors[1], X_DOOR_R_OPENED, y, z, DOORS_SPEED);

	return 1;
}

stock Elevator_CloseDoors()
{
    // Closes the elevator's doors.

    if(ElevatorState == ELEVATOR_STATE_MOVING)
	    return 0;

    new Float:x, Float:y, Float:z;

	GetDynamicObjectPos(Obj_ElevatorDoors[0], x, y, z);
	MoveDynamicObject(Obj_ElevatorDoors[0], X_DOOR_CLOSED, y, z, DOORS_SPEED);
	MoveDynamicObject(Obj_ElevatorDoors[1], X_DOOR_CLOSED, y, z, DOORS_SPEED);

	return 1;
}

stock Floor_OpenDoors(floorid)
{
    // Opens the doors at the specified floor.

    MoveDynamicObject(Obj_FloorDoors[floorid][0], X_DOOR_L_OPENED, -1303.171142, GetDoorsZCoordForFloor(floorid), DOORS_SPEED);
	MoveDynamicObject(Obj_FloorDoors[floorid][1], X_DOOR_R_OPENED, -1303.171142, GetDoorsZCoordForFloor(floorid), DOORS_SPEED);

	PlaySoundForPlayersInRange(6401, 50.0, X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(floorid) + 5.0);

	return 1;
}

stock Floor_CloseDoors(floorid)
{
    // Closes the doors at the specified floor.

    MoveDynamicObject(Obj_FloorDoors[floorid][0], X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(floorid), DOORS_SPEED);
	MoveDynamicObject(Obj_FloorDoors[floorid][1], X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(floorid), DOORS_SPEED);

	PlaySoundForPlayersInRange(6401, 50.0, X_DOOR_CLOSED, -1303.171142, GetDoorsZCoordForFloor(floorid) + 5.0);

	return 1;
}

stock Elevator_MoveToFloor(floorid)
{
	// Moves the elevator to specified floor (doors are meant to be already closed).

	ElevatorState = ELEVATOR_STATE_MOVING;
	ElevatorFloor = floorid;

	// Move the elevator slowly, to give time to clients to sync the object surfing. Then, boost it up:
	MoveDynamicObject(Obj_Elevator, 1786.678100, -1303.459472, GetElevatorZCoordForFloor(floorid), ELEVATOR_SPEED);
    MoveDynamicObject(Obj_ElevatorDoors[0], X_DOOR_CLOSED, -1303.459472, GetDoorsZCoordForFloor(floorid), ELEVATOR_SPEED);
    MoveDynamicObject(Obj_ElevatorDoors[1], X_DOOR_CLOSED, -1303.459472, GetDoorsZCoordForFloor(floorid), ELEVATOR_SPEED);
    DestroyDynamic3DTextLabel(Label_Elevator);
	return 1;
}

public Elevator_TurnToIdle()
{
	ElevatorState = ELEVATOR_STATE_IDLE;
	ReadNextFloorInQueue();

	return 1;
}

stock RemoveFirstQueueFloor()
{
	// Removes the data in ElevatorQueue[0], and reorders the queue accordingly.

	for(new i; i < sizeof(ElevatorQueue) - 1; i ++)
	    ElevatorQueue[i] = ElevatorQueue[i + 1];

	ElevatorQueue[sizeof(ElevatorQueue) - 1] = INVALID_FLOOR;

	return 1;
}

stock AddFloorToQueue(floorid)
{
 	// Adds 'floorid' at the end of the queue.

	// Scan for the first empty space:
	new slot = -1;
	for(new i; i < sizeof(ElevatorQueue); i ++)
	{
	    if(ElevatorQueue[i] == INVALID_FLOOR)
	    {
	        slot = i;
	        break;
	    }
	}

	if(slot != -1)
	{
	    ElevatorQueue[slot] = floorid;

     	// If needed, move the elevator.
	    if(ElevatorState == ELEVATOR_STATE_IDLE)
	        ReadNextFloorInQueue();

	    return 1;
	}

	return 0;
}

stock ResetElevatorQueue()
{
	// Resets the queue.

	for(new i; i < sizeof(ElevatorQueue); i ++)
	{
	    ElevatorQueue[i] 	= INVALID_FLOOR;
	    FloorRequestedBy[i] = INVALID_PLAYER_ID;
	}

	return 1;
}

stock IsFloorInQueue(floorid)
{
	// Checks if the specified floor is currently part of the queue.

	for(new i; i < sizeof(ElevatorQueue); i ++)
	    if(ElevatorQueue[i] == floorid)
	        return 1;

	return 0;
}

stock ReadNextFloorInQueue()
{
	// Reads the next floor in the queue, closes doors, and goes to it.

	if(ElevatorState != ELEVATOR_STATE_IDLE || ElevatorQueue[0] == INVALID_FLOOR)
	    return 0;

	Elevator_CloseDoors();
	Floor_CloseDoors(ElevatorFloor);

	return 1;
}

stock DidPlayerRequestElevator(playerid)
{
	for(new i; i < sizeof(FloorRequestedBy); i ++)
	    if(FloorRequestedBy[i] == playerid)
	        return 1;

	return 0;
}

stock ShowElevatorDialog(playerid)
{
	new string[512];
	for(new i; i < sizeof(ElevatorQueue); i ++)
	{
	    if(FloorRequestedBy[i] != INVALID_PLAYER_ID)
	        strcat(string, "{FF0000}");

	    strcat(string, FloorNames[i]);
	    strcat(string, "\n");
	}

	ShowPlayerDialog(playerid, DIALOG_ID, DIALOG_STYLE_LIST, "Liftas", string, "Spausti", "Atðaukti");

	return 1;
}

stock CallElevator(playerid, floorid)
{
	// Calls the elevator (also used with the elevator dialog).

	if(FloorRequestedBy[floorid] != INVALID_PLAYER_ID || IsFloorInQueue(floorid))
	    return 0;

	FloorRequestedBy[floorid] = playerid;
	AddFloorToQueue(floorid);

	return 1;
}
forward Float:GetElevatorZCoordForFloor(floorid);
forward Float:GetDoorsZCoordForFloor(floorid);
stock Float:GetElevatorZCoordForFloor(floorid)
    return (GROUND_Z_COORD + FloorZOffsets[floorid] + ELEVATOR_OFFSET); // A small offset for the elevator object itself.

stock Float:GetDoorsZCoordForFloor(floorid)
	return (GROUND_Z_COORD + FloorZOffsets[floorid]);

stock PlaySoundForPlayersInRange(soundid, Float:range, Float:x, Float:y, Float:z)
{
	foreach(Player,i)
	{
	    if(IsPlayerInRangeOfPoint(i,range,x,y,z))
		    PlayerPlaySound(i, soundid, x, y, z);
	}
	return 1;
}
