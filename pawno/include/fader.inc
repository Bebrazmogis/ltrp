



#define FADER_FRAMERATE					100


#if defined _inc_y_timers
	#define YSI_Y_TIMERS 
#endif


enum E_FADER_COLOR_DATA {
	Float:ChangePerFrame,
	Float:Current
};

enum E_FADER_DATA {
	Red[ E_FADER_COLOR_DATA ],
	Green[ E_FADER_COLOR_DATA ],
	Blue[ E_FADER_COLOR_DATA ],
	Alpha[ E_FADER_COLOR_DATA ],
	FramesLeft,
	FramesTotal,
	bool:FadeBack
};

new PlayerFadeData[ MAX_PLAYERS ][ E_FADER_DATA ], Text:FaderTextdraw;


public OnGameModeInit() 
{
	FaderTextdraw = TextDrawCreate(0.0,0.0,"~r~");
	TextDrawTextSize(FaderTextdraw,640,480);
	TextDrawLetterSize(FaderTextdraw,0.0,50.0);
	TextDrawUseBox(FaderTextdraw,1);

	#if !defined YSI_Y_TIMERS
		SetTimer("Fader_Update", FADER_FRAMERATE, true);
	#endif
	#if defined fader_OnGameModeInit
		fader_OnGameModeInit();
	#endif
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit fader_OnGameModeInit
#if defined fader_OnGameModeInit
	forward fader_OnGameModeInit();
#endif

public OnPlayerDisconnect(playerid, reason)
{
	PlayerFadeData[ playerid ][ FramesLeft ] = 0;
	#if defined fader_OnPlayerDisconnect
		fader_OnPlayerDisconnect(playerid, reason);
	#endif
}
#if defined _ALS_OnPlayerDisconnect
	#undef OnPlayerDisconnect
#else 
	#define _ALS_OnPlayerDisconnect
#endif
#define OnPlayerDisconnect fader_OnPlayerDisconnect
#if defined fader_OnPlayerDisconnect
	forward fader_OnPlayerDisconnect(playerid, reason);
#endif

#if !defined YSI_Y_TIMERS
	forward Fader_Update();
	public Fader_Update()
	{
		foreach(Player, i)
			ProcessPlayerFading(i);
	}
#else 
	task Fade_Update[FADER_FRAMERATE]()
	{
		foreach(Player,i)
			ProcessPlayerFading(i);
	}	

#endif

stock FadeColorForPlayer(playerid, start_red, start_green, start_blue, start_alpha, end_red, end_green, end_blue, end_alpha, frames, bool:fade_back = false)
{
	PlayerFadeData[ playerid ][ Red ][ Current ] = start_red;
	PlayerFadeData[ playerid ][ Red ][ ChangePerFrame ] = (end_red - start_red) / float(frames);

	PlayerFadeData[ playerid ][ Green ][ Current ] = start_green;
	PlayerFadeData[ playerid ][ Green ][ ChangePerFrame ] = (end_green - start_green) / float(frames);

	PlayerFadeData[ playerid ][ Blue ][ Current ] = start_blue;
	PlayerFadeData[ playerid ][ Blue ][ ChangePerFrame ] = (end_blue - start_blue) / float(frames);

	PlayerFadeData[ playerid ][ Alpha ][ Current ] = start_alpha;
	PlayerFadeData[ playerid ][ Alpha ][ ChangePerFrame ] = (end_alpha - start_alpha) / float(frames);

	if(fade_back)
	{
		if(frames % 2 != 0)
			frames--;

		PlayerFadeData[ playerid ][ Red ][ ChangePerFrame ] *= 2;
		PlayerFadeData[ playerid ][ Green ][ ChangePerFrame ] *= 2;
		PlayerFadeData[ playerid ][ Blue ][ ChangePerFrame ] *= 2;
		PlayerFadeData[ playerid ][ Alpha ][ ChangePerFrame ] *= 2;
	}

	PlayerFadeData[ playerid ][ FramesTotal ] = PlayerFadeData[ playerid ][ FramesLeft ] = frames;
	PlayerFadeData[ playerid ][ FadeBack ] = fade_back;
}









stock RGBToHex(red, green, blue, alpha)
{
    new color = red << 24 | green << 16 | blue << 8 | alpha;
    return color;
}



ProcessPlayerFading(playerid)
{
	if(!PlayerFadeData[ playerid ][ FramesLeft ])
		return;
	PlayerFadeData[ playerid ][ FramesLeft ]--;


	if(PlayerFadeData[ playerid ][ FadeBack ] && PlayerFadeData[ playerid ][ FramesTotal ] / 2 > PlayerFadeData[ playerid ][ FramesLeft ])
	{
		PlayerFadeData[ playerid ][ Red ][ Current ] -= PlayerFadeData[ playerid ][ Red ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Green ][ Current ] -= PlayerFadeData[ playerid ][ Green ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Blue ][ Current ] -= PlayerFadeData[ playerid ][ Blue ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Alpha ][ Current ] -= PlayerFadeData[ playerid ][ Alpha ][ ChangePerFrame ];
	}
	else 
	{
		PlayerFadeData[ playerid ][ Red ][ Current ] += PlayerFadeData[ playerid ][ Red ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Green ][ Current ] += PlayerFadeData[ playerid ][ Green ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Blue ][ Current ] += PlayerFadeData[ playerid ][ Blue ][ ChangePerFrame ];
		PlayerFadeData[ playerid ][ Alpha ][ Current ] += PlayerFadeData[ playerid ][ Alpha ][ ChangePerFrame ];
	}

	new color = RGBToHex(
		floatround(PlayerFadeData[ playerid ][ Red ][ Current ]),
		floatround(PlayerFadeData[ playerid ][ Green ][ Current ]),
		floatround(PlayerFadeData[ playerid ][ Blue ][ Current ]),
		floatround(PlayerFadeData[ playerid ][ Alpha ][ Current ])
	);
	TextDrawBoxColor(FaderTextdraw, color);
	TextDrawShowForPlayer(playerid, FaderTextdraw);

	if(PlayerFadeData[ playerid ][ FramesLeft ] == 0)
	{
		TextDrawHideForPlayer(playerid, FaderTextdraw);
		#if defined OnPlayerFadeComplete
			OnPlayerFadeComplete(playerid);
		#endif
		return;
	}
}


#if defined OnPlayerFadeComplete
	forward OnPlayerFadeComplete(playerid);
#endif