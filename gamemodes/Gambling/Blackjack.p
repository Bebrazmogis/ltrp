


enum E_PLAYER_BLACK_JACK_DATA
{
	bool:IsActive,
	Cards[ 5 ],
	Text:CardTDs[ 5 ],
	DealerCards[ 5 ],
	Text:DealerCardTDs[ 5 ],
};

static PlayerBlackJackSession[ MAX_PLAYERS ][ E_PLAYER_BLACK_JACK_DATA ];



#define BLACKJACK_CARD_START_X 		40.0
#define BLACKJACK_PLAYER_CARD_START_Y 	200.0
#define BLACKJACK_DEALER_CARD_START_Y	100.0



CMD:bjstart(playerid)
{
	if(PlayerBlackJackSession[ playerid ][ IsActive ])
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau þaidþiate blackjack.");
	else
	{
		new Float:x = 200.0, Float:y = 100.0;

		PlayerBlackJackSession[ playerid ][ DealerCards ][ 0 ] = GetRandomCard();
		while(IsCardInPlayerBlackJackSession(playerid, PlayerBlackJackSession[ playerid ][ DealerCards ][ 1 ]) || !PlayerBlackJackSession[ playerid ][ DealerCards ][ 1 ])
			PlayerBlackJackSession[ playerid ][ DealerCards ][ 1 ] = GetRandomCard();

		while(IsCardInPlayerBlackJackSession(playerid, PlayerBlackJackSession[ playerid ][ Cards ][ 0 ]) || !PlayerBlackJackSession[ playerid ][ Cards ][ 0 ])
			PlayerBlackJackSession[ playerid ][ Cards ][ 0 ] = GetRandomCard();

		while(IsCardInPlayerBlackJackSession(playerid, PlayerBlackJackSession[ playerid ][ Cards ][ 1 ]) || !PlayerBlackJackSession[ playerid ][ Cards ][ 1 ])
			PlayerBlackJackSession[ playerid ][ Cards ][ 1 ] = GetRandomCard();

		PlayerBlackJackSession[ playerid ][ DealerCardTDs][ 0 ] = CreateCard(CARD_HIDDEN, x, y);
		PlayerBlackJackSession[ playerid ][ DealerCardTDs][ 1 ] = CreateCard(PlayerBlackJackSession[ playerid ][ DealerCards][ 1 ], x + 40.0, BLACKJACK_DEALER_CARD_START_Y);

		y = 300;
		for(new i = 0; i < 2; i++)
			PlayerBlackJackSession[ playerid ][ CardTDs ][ i ] = CreateCard(PlayerBlackJackSession[ playerid ][ Cards ][ i ], x + x * 40.0, BLACKJACK_PLAYER_CARD_START_Y);

		PlayerBlackJackSession[ playerid ][ IsActive ] = true;

		CheckForBlackJackWinner(playerid);

	}
	return 1;
}


CMD:bjhit(playerid)
{
	if(!PlayerBlackJackSession[ playerid ][ IsActive ])
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neþaidþiate blackjack.");
	else if(GetPlayerCardCount(playerid) == 5)
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, galite turëti tik 5 kortas. Dabar galite tik /bjstay");
	else
	{
		new card, index = GetPlayerCardCount(playerid);
		while(!card || IsCardInPlayerBlackJackSession(playerid, card))
			card = GetRandomCard();

		PlayerBlackJackSession[ playerid ][ Cards ][ index ] = card;
		PlayerBlackJackSession[ playerid ][ CardTDs ][ index ] = CreateCard(card, BLACKJACK_CARD_START_X + BLACKJACK_CARD_START_X * index, BLACKJACK_PLAYER_CARD_START_Y);

		CheckForBlackJackWinner(playerid);
	}
	return 1;
}

CMD:bjstay(playerid)
{
	if(!PlayerBlackJackSession[ playerid ][ IsActive ])
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neþaidþiate blackjack.");
	else
	{
		// Dealeris masto.
	}
	return 1;
}


OnPlayerBlackjackWin(playerid, playercardcount, dealercardcount)
{

}
OnPlayerBlackjackLose(playerid, playercardcount, dealercardcount)
{

}
OnPlayerBlackjackDraw(playerid, playercardcount, dealercardcount)
{

}


IsCardInPlayerBlackJackSession(playerid, cardid)
{
	for(new i = 0; i < 5; i++)
		if(PlayerBlackJackSession[ playerid ][ Cards ] == cardid || PlayerBlackJackSession[ playerid ][ DealerCards ] == cardid)
			return true;
	return false;
}

GetPlayerCardValueCount(playerid)
{
	new sum = 0;
	for(new i = 0; i < 5; i++)
		sum += PlayerBlackJackSession[ playerid ][ Cards ][ i ];
	return sum;
}
GetPlayerDealerCardValueCount(playerid)
{
	new sum = 0;
	for(new i = 0; i < 5; i++)
		sum += PlayerBlackJackSession[ playerid ][ DealerCards ][ i ];
	return sum;
}
GetPlayerCardCount(playerid)
{
	new sum = 0;
	for(new i = 0; i < 5; i++)
		if(PlayerBlackJackSession[ playerid ][ Cards ][ i ])
			sum++;
	return sum;
}
GetPlayerDealerCardCount(playerid)
{
	new sum = 0;
	for(new i = 0; i < 5; i++)
		if(PlayerBlackJackSession[ playerid ][ DealerCards ][ i ])
			sum++;
	return sum;
}

CheckForBlackJackWinner(playerid)
{
	new playercount = GetPlayerCardValueCount(playerid),
		dealercount = GetPlayerDealerCardValueCount(playerid);

	if(playercount == 21 && dealercount != 21)
		OnPlayerBlackjackWin(playerid, playercount, dealercount);
	else if(dealercount == 21 && playercount != 21)
		OnPlayerBlackjackLose(playerid, playercount, dealercount);
	else
		OnPlayerBlackjackDraw(playerid, playercount, dealercount);
}