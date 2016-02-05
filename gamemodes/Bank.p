#include <YSI\y_hooks>


#define DIALOG_BANK_MAIN 				1
#define DIALOG_BANK_WITHDRAW 			2
#define DIALOG_BANK_DEPOSIT 			3

static BankPickup, PaycheckPickup,
	bool:IsUsingBank[ MAX_PLAYERS ];



/*
	                      ,,    ,,  ,,
	  .g8"""bgd         `7MM  `7MM *MM                       `7MM
	.dP'     `M           MM    MM  MM                         MM
	dM'       ` ,6"Yb.    MM    MM  MM,dMMb.   ,6"Yb.  ,p6"bo  MM  ,MP',pP"Ybd
	MM         8)   MM    MM    MM  MM    `Mb 8)   MM 6M'  OO  MM ;Y   8I   `"
	MM.         ,pm9MM    MM    MM  MM     M8  ,pm9MM 8M       MM;Mm   `YMMMa.
	`Mb.     ,'8M   MM    MM    MM  MM.   ,M9 8M   MM YM.    , MM `Mb. L.   I8
	  `"bmmmd' `Moo9^Yo..JMML..JMML.P^YbmdP'  `Moo9^Yo.YMbmd'.JMML. YA.M9mmmP'

	                                                                            */



public OnGameModeInit()
{

	new Float:x, Float:y, Float:z;
	Data_GetCoordinates("bank", x, y, z);
	BankPickup = CreateDynamicPickup(1239, 1, x, y, z, Data_GetVirtualWorld("bank"), Data_GetInterior("bank"));

	Data_GetCoordinates("bank_paycheck", x, y, z);
	PaycheckPickup = CreateDynamicPickup(1210, 1, x, y, z, Data_GetVirtualWorld("bank_paycheck"), Data_GetInterior("bank_paycheck"));

	#if defined bank_OnGameModeInit
		bank_OnGameModeInit();
	#endif
}

#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit 					bank_OnGameModeInit
#if defined bank_OnGameModeInit
	forward bank_OnGameModeInit();
#endif


public OnPlayerPickUpDynamicPickup(playerid, pickupid)
{
	#if defined ban_OnPlayerPickUpDynamicPickup
		ban_OnPlayerPickUpDynamicPickup(playerid, pickupid);
	#endif

	if(pickupid == BankPickup)
    {
    	if(IsUsingBank[ playerid ])
    		return 1;

    	IsUsingBank[ playerid ] = true;

        if(GetPlayerSavings(playerid) > 0)
        {
        	SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs esate pasidëjæs indëlá, todël negalite naudotis savo banko sàskaita.");
        	PlayerBankPickupDelay(playerid);
        }
        else
        	ShowPlayerDialog(playerid, DIALOG_BANK_MAIN, DIALOG_STYLE_MSGBOX, "BANKAS", " Los Santos Bankas\n Norëdami naudotis bankobanko paslaugomis\n Paspauskite mygtukus esanèius þemiau.", "Nuimti", "Padëti" );
        return 1;
    }
    if(pickupid == PaycheckPickup)
    {
    	if(IsUsingBank[ playerid ])
    		return 1;

        if(!GetPlayerTotalPaycheck(playerid))
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate sukaupæs algos.");
        else
        {
        	GivePlayerMoney(playerid, GetPlayerTotalPaycheck(playerid));
	        SetPlayerTotalPaycheck(playerid, 0);
	        SaveAccount(playerid);
	        SendClientMessage(playerid, COLOR_NEWS, "Sëkmingai pasiemëte algà.");
        }
        IsUsingBank[ playerid ] = true;
        defer PlayerBankPickupDelay(playerid);
        return 1;
    }
    return 0;
}

#if defined _ALS_OnPlayerPickUpDynamicPUp
	#undef OnPlayerPickUpDynamicPickup
#else
	#define _ALS_OnPlayerPickUpDynamicPUp
#endif
#define OnPlayerPickUpDynamicPickup ban_OnPlayerPickUpDynamicPickup
#if defined ban_OnPlayerPickUpDynamicPickup
	forward ban_OnPlayerPickUpDynamicPickup(playerid, pickupid);
#endif



hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_BANK_MAIN:
	    {
	    	new string[128];
	        if(response)
	        {
	            format(string, sizeof(string)," Los Santos Bank\nJûsø banko sàskaitoje ðiuo metu yra: %d$\n Áraðykite sumà, kurià norite iðsiimti",pInfo[playerid][pBank]);
	            ShowPlayerDialog(playerid, DIALOG_BANK_WITHDRAW, DIALOG_STYLE_INPUT, "BANKAS", string, "Iðsimti", "Atðaukti");
	        }
	        else
	        {
	            format(string, sizeof(string)," Los Santos Bank\nSu savimi ðiuo metu turite %d$\n Kokià sumà norësite áneðti á bankà?",PlayerMoney[ playerid ]);
	            ShowPlayerDialog(playerid, DIALOG_BANK_DEPOSIT, DIALOG_STYLE_INPUT, "BANKAS", string, "Áneðti", "Atðaukti");
	        }
	        PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
	        return 1;
	    }
	   	case DIALOG_BANK_WITHDRAW:
	    {
	        if(response && !isnull(inputtext))
	        {
	        	if(GetPlayerSavings(playerid))
	        		SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Banku naudotis negalite, kol esate pasidëje terminuotà indëlá. ");
	        	else
	        	{
	        		new money = strval(inputtext), string[ 64 ];
		            if(GetPlayerBankMoney(playerid) >= money && money > 0)
		            {
		                GivePlayerMoney(playerid,money);
		                SendClientMessage(playerid, COLOR_GREEN, "|______ LOS SANTOS BANK ______|");
		                format(string, sizeof(string), "  Buvæs banko balansas: %d$", GetPlayerBankMoney(playerid));
		                SendClientMessage(playerid, COLOR_FADE1, string);
		                format(string, sizeof(string), "  Ið banko buvo iððimta: %d$",money);
		                SetPlayerBankMoney(playerid, GetPlayerBankMoney(playerid) - money);
		                SendClientMessage(playerid, COLOR_FADE2, string);
		                SendClientMessage(playerid, COLOR_GREEN, "|----------------------------------------|");
		                format(string, sizeof(string), "  Ðiuo metu banke yra %d$", GetPlayerBankMoney(playerid));
		                SendClientMessage(playerid, COLOR_FADE1, string);
		                PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
		                SaveAccount(playerid);
		            }
		            else SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs banke nëra tiek pinigø.");
	        	}
	        }
	        defer PlayerBankPickupDelay(playerid);
	   		return 1;
	    }
	    case DIALOG_BANK_DEPOSIT:
	    {
	        if(response && !isnull(inputtext))
	        {
	        	new money = strval(inputtext), string[ 64 ];
	            if(PlayerMoney[ playerid ] >= money && money > 0)
	            {
	                GivePlayerMoney(playerid,-money);
	                SendClientMessage(playerid, COLOR_GREEN, "|______ LOS SANTOS BANK ______|");
	                format(string, sizeof(string), "  Buvæs banko balansas: %d$", GetPlayerBankMoney(playerid));
	                SendClientMessage(playerid, COLOR_FADE1, string);
	                format(string, sizeof(string), "  Á bankà buvo áneðta: %d$",money);
	                SetPlayerBankMoney(playerid, GetPlayerBankMoney(playerid) + money);
	                SendClientMessage(playerid, COLOR_FADE2, string);
	                SendClientMessage(playerid, COLOR_GREEN, "|----------------------------------------|");
	                format(string, sizeof(string), "  Ðiuo metu banke yra %d$", GetPlayerBankMoney(playerid));
	                SendClientMessage(playerid, COLOR_FADE1, string);
	                PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
	                SaveAccount(playerid);
	            }
	             else SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neturite tiek pinigø.");
	        }
	      	defer PlayerBankPickupDelay(playerid);
	        return 1;
	    }
	}
	return 0;
}


hook OnPlayerDisconnect(playerid, reason)
{
	IsUsingBank[ playerid ] = false;
}

/*
	             ,,                                                      AW
	MMP""MM""YMM db                                                     ,M'       mm                   `7MM
	P'   MM   `7                                                        MV        MM                     MM
	     MM    `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd         AW       mmMMmm  ,6"Yb.  ,pP"Ybd  MM  ,MP',pP"Ybd
	     MM      MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `"        ,M'         MM   8)   MM  8I   `"  MM ;Y   8I   `"
	     MM      MM    MM    MM    MM 8M""""""  MM     `YMMMa.        MV          MM    ,pm9MM  `YMMMa.  MM;Mm   `YMMMa.
	     MM      MM    MM    MM    MM YM.    ,  MM     L.   I8       AW           MM   8M   MM  L.   I8  MM `Mb. L.   I8
	   .JMML.  .JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP'      ,M'           `Mbmo`Moo9^Yo.M9mmmP'.JMML. YA.M9mmmP'
	                                                                MV
	                                                               AW                                                     */




timer PlayerBankPickupDelay[ 1000 ](playerid)
{
	if(IsUsingBank[ playerid ])
		IsUsingBank[ playerid ] = false;
}