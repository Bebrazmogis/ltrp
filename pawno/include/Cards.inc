/*
                                                        ====================================
                                                        |               Kortø/Cards include                    |
                                                        |               Autorius/Author - Bebras        |
                                                        ====================================
*/
 
#define CARD_HEARTS_TWO                 1
#define CARD_DIAMONDS_TWO               2
#define CARD_CLUBS_TWO                  3
#define CARD_SPADES_TWO                 4
 
#define CARD_HEARTS_THREE               5
#define CARD_DIAMONDS_THREE             6
#define CARD_CLUBS_THREE                7
#define CARD_SPADES_THREE               8
 
#define CARD_HEARTS_FOUR                9
#define CARD_DIAMONDS_FOUR              10
#define CARD_CLUBS_FOUR                 11
#define CARD_SPADES_FOUR                12
 
#define CARD_HEARTS_FIVE                13
#define CARD_DIAMONDS_FIVE              14
#define CARD_CLUBS_FIVE                 15
#define CARD_SPADES_FIVE                16
 
#define CARD_HEARTS_SIX                 17
#define CARD_DIAMONDS_SIX               18
#define CARD_CLUBS_SIX                  19
#define CARD_SPADES_SIX                 20
 
#define CARD_HEARTS_SEVEN               21
#define CARD_DIAMONDS_SEVEN             22
#define CARD_CLUBS_SEVEN                23
#define CARD_SPADES_SEVEN               24
 
#define CARD_HEARTS_EIGHT               25
#define CARD_DIAMONDS_EIGHT             26
#define CARD_CLUBS_EIGHT                27
#define CARD_SPADES_EIGHT               28
 
#define CARD_HEARTS_NINE                29
#define CARD_DIAMONDS_NINE              30
#define CARD_CLUBS_NINE                 31
#define CARD_SPADES_NINE                32
 
#define CARD_HEARTS_TEN                 33
#define CARD_DIAMONDS_TEN               34
#define CARD_CLUBS_TEN                  35
#define CARD_SPADES_TEN                 36
 
#define CARD_HEARTS_JACK                37
#define CARD_DIAMONDS_JACK              38
#define CARD_CLUBS_JACK                 39
#define CARD_SPADES_JACK                40
 
#define CARD_HEARTS_QUEEN               41
#define CARD_DIAMONDS_QUEEN             42
#define CARD_CLUBS_QUEEN                43
#define CARD_SPADES_QUEEN               44
 
#define CARD_HEARTS_KING                45
#define CARD_DIAMONDS_KING              46
#define CARD_CLUBS_KING                 47
#define CARD_SPADES_KING                48
 
#define CARD_HEARTS_ACE                 49
#define CARD_DIAMONDS_ACE               50
#define CARD_CLUBS_ACE                  51
#define CARD_SPADES_ACE                 52
 
#define CARD_HIDDEN                     53
 
 
stock Text:CreateCard(cardtype,Float:x,Float:y,Float:width=30.0,Float:height=60.0)
{
        new Text:CardID = TextDrawCreate(x,y,GetTXDModel(cardtype));
        TextDrawFont(CardID,TEXT_DRAW_FONT_SPRITE_DRAW);
        TextDrawTextSize(CardID,width,height);
        return CardID;
}
stock ShowCardForPlayer(playerid,Text:cardid)
{
        TextDrawShowForPlayer(playerid,cardid);
        return 1;
}
stock HideCardForPlayer(playerid,Text:cardid)
{
        TextDrawHideForPlayer(playerid,cardid);
        return 1;
}
stock SetCardSelectable(Text:cardid,set)
{      
        TextDrawSetSelectable(cardid,set);
        return 1;
}

stock GetRandomCard()
{
        return random(52) + 1;
}

stock static GetTXDModel(cardtype)
{
        new str[16] = "_";
        switch(cardtype)
        {
                case CARD_HEARTS_TWO:           str = "LD_CARD:cd2h";
                case CARD_DIAMONDS_TWO:         str = "LD_CARD:cd2d";  
                case CARD_CLUBS_TWO:            str = "LD_CARD:cd2c";  
                case CARD_SPADES_TWO:           str = "LD_CARD:cd2s";          
                case CARD_HEARTS_THREE:         str = "LD_CARD:cd3h";
                case CARD_DIAMONDS_THREE:       str = "LD_CARD:cd3d";  
                case CARD_CLUBS_THREE:          str = "LD_CARD:cd3c";  
                case CARD_SPADES_THREE:         str = "LD_CARD:cd3s";          
                case CARD_HEARTS_FOUR:          str = "LD_CARD:cd4h";
                case CARD_DIAMONDS_FOUR:        str = "LD_CARD:cd4d";  
                case CARD_CLUBS_FOUR:           str = "LD_CARD:cd4c";  
                case CARD_SPADES_FOUR:          str = "LD_CARD:cd4s";          
                case CARD_HEARTS_FIVE:          str = "LD_CARD:cd5h";
                case CARD_DIAMONDS_FIVE:        str = "LD_CARD:cd5d";  
                case CARD_CLUBS_FIVE:           str = "LD_CARD:cd5c";  
                case CARD_SPADES_FIVE:          str = "LD_CARD:cd5s";          
                case CARD_HEARTS_SIX:           str = "LD_CARD:cd6h";
                case CARD_DIAMONDS_SIX:         str = "LD_CARD:cd6d";  
                case CARD_CLUBS_SIX:            str = "LD_CARD:cd6c";  
                case CARD_SPADES_SIX:           str = "LD_CARD:cd6s";          
                case CARD_HEARTS_SEVEN:         str = "LD_CARD:cd7h";
                case CARD_DIAMONDS_SEVEN:       str = "LD_CARD:cd7d";  
                case CARD_CLUBS_SEVEN:          str = "LD_CARD:cd7c";  
                case CARD_SPADES_SEVEN:         str = "LD_CARD:cd7s";          
                case CARD_HEARTS_EIGHT:         str = "LD_CARD:cd8h";
                case CARD_DIAMONDS_EIGHT:       str = "LD_CARD:cd8d";  
                case CARD_CLUBS_EIGHT:          str = "LD_CARD:cd8c";  
                case CARD_SPADES_EIGHT:         str = "LD_CARD:cd8s";          
                case CARD_HEARTS_NINE:          str = "LD_CARD:cd9h";
                case CARD_DIAMONDS_NINE:        str = "LD_CARD:cd9d";  
                case CARD_CLUBS_NINE:           str = "LD_CARD:cd9c";  
                case CARD_SPADES_NINE:          str = "LD_CARD:cd9s";          
                case CARD_HEARTS_TEN:           str = "LD_CARD:cd10h";
                case CARD_DIAMONDS_TEN:         str = "LD_CARD:cd10d"; 
                case CARD_CLUBS_TEN:            str = "LD_CARD:cd10c"; 
                case CARD_SPADES_TEN:           str = "LD_CARD:cd10s";                 
                case CARD_HEARTS_JACK:          str = "LD_CARD:cd11h";
                case CARD_DIAMONDS_JACK:        str = "LD_CARD:cd11d"; 
                case CARD_CLUBS_JACK:           str = "LD_CARD:cd11c"; 
                case CARD_SPADES_JACK:          str = "LD_CARD:cd11s";         
                case CARD_HEARTS_QUEEN:         str = "LD_CARD:cd12h";
                case CARD_DIAMONDS_QUEEN:       str = "LD_CARD:cd12d"; 
                case CARD_CLUBS_QUEEN:          str = "LD_CARD:cd12c"; 
                case CARD_SPADES_QUEEN:         str = "LD_CARD:cd12s"; 
                case CARD_HEARTS_KING:          str = "LD_CARD:cd13h";
                case CARD_DIAMONDS_KING:        str = "LD_CARD:cd13d"; 
                case CARD_CLUBS_KING:           str = "LD_CARD:cd13c"; 
                case CARD_SPADES_KING:          str = "LD_CARD:cd13s"; 
                case CARD_HEARTS_ACE:           str = "LD_CARD:cd1h";
                case CARD_DIAMONDS_ACE:         str = "LD_CARD:cd1d";  
                case CARD_CLUBS_ACE:            str = "LD_CARD:cd1c";  
                case CARD_SPADES_ACE:           str = "LD_CARD:cd1s";  
                case CARD_HIDDEN:                       str = "LD_CARD:cdback";
        }
        return str;
}