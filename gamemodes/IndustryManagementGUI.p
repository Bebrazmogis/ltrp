#include <YSI\y_hooks>

#define TruckerJob: 						TMILTRP

#define DIALOG_INDUSTRY_M_MAIN			7051
#define DIALOG_INDUSTRY_M_INPUT_ID		7052
#define DIALOG_INDUSTRY_M_DESTROY_CONF	7053
#define DIALOG_INDUSTRY_M_INPUT_PRICE	7054
#define DIALOG_INDUSTRY_M_INPUT_S_B_STA	7055
#define DIALOG_INDUSTRY_M_ADD_COMMODITY 7056
#define DIALOG_INDUSTRY_M_REMOVE_COMMMO	7057
#define DIALOG_INDUSTRY_M_NEW_CARGO		7058
#define DIALOG_INDUSTRY_M_REMOVE_CARGO	7059
#define DIALOG_I_MINDUSTRY_DESTR_IN_ID	7060
#define DIALOG_I_M_INDUSTRY_INPUT_NAME	7061
#define DIALOG_INDUSTRY_M_NEW_CARGO_NAM	7062
#define DIALOG_INDUSTRY_M_COMMODITY_PRI	7063
#define DIALOG_INDUSTRY_M_COMMODITY_PR2	7064
#define DIALOG_INDUSTRY_M_COMMODITY_STO	7065
#define DIALOG_INDUSTRY_M_COMMODITY_ST2 7066

enum E_INDUSTRY_MANAGEMENT_GUI_DATA {
	ActionList,
	NewIndustry,
	DestroyIndustry,
	AddTruckerCargo,
	AddTruckerCargo2,
	RemoveTruckerCargo,
	GetCurrentIndustryId,
	InputIndex,
	CommodityPriceChange,
	CommodityCurrentStockChange
};


// Kadangi yra keli GUI kuriems reikia gauti industrijos ID
// Bus naudojamas tik vienas kuris atitinkamai pagal tai
// KAM reikia to ID, ten ir perduos
// best comment ever
enum E_INDUSTRY_ID_INPUT_USE {
	IndustryDestroy,
	IndustryAddCommodity,
	IndustryRemoveCommodity,
	ChangeCommodityCurrentStock,
	ChangeCommodityPrice,
	IndustryNull
};

new E_INDUSTRY_ID_INPUT_USE:TruckerJob:InputIndexUse[ MAX_PLAYERS ], TruckerJob:CurrentIndustryIndex[ MAX_PLAYERS ];

stock TruckerJob:ShowPlayerDialog(playerid, E_INDUSTRY_MANAGEMENT_GUI_DATA:element)
{
	new string[1024];
	switch(element)
	{
		case ActionList:
			ShowPlayerDialog( playerid, DIALOG_INDUSTRY_M_MAIN, DIALOG_STYLE_LIST,"Industrijø/kroviniø meniu","- Kurti naujà industrijà \n\
							                                                                                    - Paðalinti\n\
							                                                                                   	- Pridëti industrijai prekiø\n\
							                                                                                   	- Paðalinti industrijos prekæ\n\
							                                                                                   	- Keisti industrijos prekës kainà\n\
							                                                                                   	- Keisti industrijos prekës kieká\n\
							                                                                                   	- Pridëti kroviniø\n\
							                                                                                   	- Paðalinti krovinius\n\
							                                                                                   	- Þiûrëti dabartinës industrijos ID", "Rinktis", "Atðaukti" );
		case NewIndustry:
			ShowPlayerDialog(playerid, DIALOG_I_M_INDUSTRY_INPUT_NAME, DIALOG_STYLE_INPUT, "Naujos industrijos kûrimas", "Áveskite industrijos pavadinimà.", "Kurti", "Iðeiti");

		case AddTruckerCargo:
		{
			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_NEW_CARGO_NAM, DIALOG_STYLE_INPUT, "Naujas krovinys", "Áveskite krovinio pavadinimà", "Tæsti", "Iðeiti");
		}
		case AddTruckerCargo2:
		{
			strcat(string, "Krovinio sukûrimas. Naudojimas tokiu formatu:\n{FFFFFF}limitas pagaminamas_kiekis sunaudojamas_kiekis uþimama_vieta(skaièius) tipas\n\n");
			strcat(string, "limitas - Kiek daugiausiai galës ðios prekës bûti industrijoje\npagaminamas_kiekis - kiek bus pagaminta ðios prekës per valandà\n");
			strcat(string, "sunaudojamas_kiekis - kiek bus sunaudota ðios prekës per valandà(antrinëse industrijose)\nuþimama_vieta - dëþe uþima 1 vietà. Plytø paletë 6. Mediena 18.\n");
			strcat(string, "tipas - 1. Malkos 2. Dëþë 3.Skystis 4. Nematomi objektai 5. Plytos");
			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_NEW_CARGO, DIALOG_STYLE_INPUT, "Naujas krovinys", string, "Kurti", "Iðeiti");
		}
		case RemoveTruckerCargo:
		{
			foreach(TruckerCargoIterator, i)
				format(string, sizeof(string), "%s%d. %s\n", string, TruckerCargo[ i ][ Id ], TruckerCargo[ i ][ Name ]);

			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_REMOVE_CARGO, DIALOG_STYLE_LIST, "Pasirinkite kroviná naikinimui", string, "Iðtrinti", "Iðeiti");
		}

		case GetCurrentIndustryId:
		{
			new index = GetPlayerIndustryInRange(playerid, 5.0);
			if(index != -1)
				format(string, sizeof(string),"Jûs stovite prie industrijos, kurios ID %d.",index);
			else
				string = "Prie jûsø nëra jokios industrijos.";
			SendClientMessage(playerid, COLOR_NEWS, string);
		}
		case CommodityPriceChange:
			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_COMMODITY_PR2, DIALOG_STYLE_INPUT, "Kainos keitimas", "Áveskite naujà kainos prekæ", "Iðsaugoti","Iðeiti");
		case CommodityCurrentStockChange:
			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_COMMODITY_ST2, DIALOG_STYLE_INPUT, "Kiekio keitimas", "Áveskite naujà ðios prekës esamà kieká", "Iðsaugoti" ,"Iðeiti");
		case InputIndex:
		{
			new index = GetPlayerIndustryInRange(playerid, 5.0);
			string = "Áveskite industrijos kurià norite sunaikinti ID.";
			if(index != -1)
				format(string, sizeof(string),"%s\nIndustrijos prie kuriose stovite ID yra %d",string, index);
			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_INPUT_ID, DIALOG_STYLE_INPUT, "Industrijos ID", string, "Tæsti", "Iðeiti");
		}
	}
	return 1;
}



hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_INDUSTRY_M_MAIN:
		{
			if(!response)
				return 1;

			switch(listitem)
			{
				case 0:
					TruckerJob:ShowPlayerDialog(playerid, NewIndustry);
				case 1:
				{
					TruckerJob:InputIndexUse[ playerid ] = IndustryDestroy;
					TruckerJob:ShowPlayerDialog(playerid, InputIndex);
				}
				case 2:
				{
					TruckerJob:InputIndexUse[ playerid ] = IndustryAddCommodity;
					TruckerJob:ShowPlayerDialog(playerid, InputIndex);
				}
				case 3:
				{
					TruckerJob:InputIndexUse[ playerid ] = IndustryRemoveCommodity;
					TruckerJob:ShowPlayerDialog(playerid, InputIndex);
				}
				case 4:
				{
					TruckerJob:InputIndexUse[ playerid] = ChangeCommodityPrice;
					TruckerJob:ShowPlayerDialog(playerid, InputIndex);
				}
				case 5:
				{
					TruckerJob:InputIndexUse[ playerid ] = ChangeCommodityCurrentStock;
					TruckerJob:ShowPlayerDialog(playerid, InputIndex);
				}
				case 6:
					TruckerJob:ShowPlayerDialog(playerid, AddTruckerCargo);
				case 7:
					TruckerJob:ShowPlayerDialog(playerid, RemoveTruckerCargo);
				case 8:
				{
					//TruckerJob:InputIndexUse[ playerid ] = IndustryNull;
					TruckerJob:ShowPlayerDialog(playerid, GetCurrentIndustryId);
				}
			}
			return 1;
		}
		case DIALOG_INDUSTRY_M_NEW_CARGO_NAM:
		{
			if(!response)
				return 1;
			SetPVarString(playerid, "NewCargo_Name", inputtext);
			TruckerJob:ShowPlayerDialog(playerid, AddTruckerCargo2);
			return 1;
		}
		case DIALOG_INDUSTRY_M_REMOVE_CARGO:
		{
			if(!response)
				return 1;

			new tmp[16], id, query[70];
			strmid(tmp, inputtext, 0, strfind(inputtext, "."));
			id = strval(tmp);

			format(query, sizeof(query), "DELETE FROM trucker_cargo WHERE id = %d LIMIT 1", id);
			mysql_pquery(DbHandle,query);

			SendClientMessage(playerid, COLOR_NEWS, "Krovinys sëkmingai sunaikintas.");
			return 1;
		}
		case DIALOG_INDUSTRY_M_NEW_CARGO:
		{
			if(!response)
				return 1;

			new cargo[E_CARGO_DATA];
			if(sscanf(inputtext, "e<-i-s[32]iiiii>", cargo) || !cargo[ Limit ] || !cargo[ Slot ] || cargo[ Type ] < 0 || cargo[ Type] > 5)
				return TruckerJob:ShowPlayerDialog(playerid, AddTruckerCargo2);

			new query[180];
			GetPVarString(playerid, "NewCargo_Name", cargo[ Name ], MAX_TRUCKER_CARGO_NAME);
			strcat(query, cargo[ Name ], 16); // Negaliu tiesiogiai duot pavadinimo mysql_real_escape_string nes jis neima sizeof parametro..
			mysql_real_escape_string(query, query);
			format(query, sizeof(query), "INSERT INTO trucker_cargo (name,`limit`,production, consumption, slot, type) VALUES('%s', %d, %d, %d, %d, %d)",
				query, cargo[ Limit ], cargo[ Production ], cargo[ Consumption ], cargo[ Slot ], cargo[ Type ]);
			new Cache:result = mysql_query(DbHandle,query);

			for(new i = 0; i < sizeof TruckerCargo; i++)
			{
				if(TruckerCargo[ i ][ Id ])
					continue;

				TruckerCargo[ i ] = cargo;
				TruckerCargo[ i ][ Id ] = cache_insert_id();
				Itter_Add(TruckerCargoIterator, i);
				break;
			}
			cache_delete(result);
			SendClientMessage(playerid, COLOR_NEWS, "Krovinys sëkmingai sukurtas");
			return 1;
		}
		case DIALOG_I_M_INDUSTRY_INPUT_NAME:
		{
			if(isnull(inputtext) || strlen(inputtext) > 64)
				return TruckerJob:ShowPlayerDialog(playerid, NewIndustry);

			new index, query[200], Float:x, Float:y,Float:z;
			GetPlayerPos(playerid, x, y, z);

			for(new i = 0; i < sizeof Industries; i++)
				if(!Industries[ i ][ Id ])
				{
					index = i;
					break;
				}

			mysql_escape_string(inputtext, query);
			format(query, sizeof(query), "INSERT INTO industries (name, x, y, z) VALUES('%s', %f, %f, %f)",
				query, x, y, z);
			new Cache:result = mysql_query(DbHandle,query);

			Industries[ index ][ Id ] = cache_insert_id();
			strcat(Industries[ index ][ Name ], inputtext, 64);
			Industries[ index ][ PosX ] = x;
			Industries[ index ][ PosY ] = y;
			Industries[ index ][ PosZ ] = z;
			Iter_Add(IndustryIterator, index);
			cache_delete(result);

			UpdateIndustryInfo(index);

			format(query, sizeof(query), "Industrija sëkmingai sukurta. Jos ID:%d MySQL ID:%d. Nepamirðkite jai pridëti prekiø.", index, Industries[ index ][ Id ]);
			SendClientMessage(playerid, COLOR_NEWS, query);
			return 1;
		}
		case DIALOG_INDUSTRY_M_DESTROY_CONF:
		{
			if(!response)
				return 1;

			new query[90];
			format(query, sizeof(query), "DELETE FROM commodities WHERE industry_id = %d AND type = 'Industry'", Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ]);
			mysql_pquery(DbHandle,query);
			format(query, sizeof(query), "DELETE FROM industries WHERE id = %d LIMIT 1", Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ]);
			mysql_pquery(DbHandle,query);

			// mysql cleanup baigtas, pereinam prie kintamuju.
			foreach(CommodityIterator, i)
				if(Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] == Commodities[ i ][ IndustryId ]
					&& !Commodities[ i ][ IsBusinessCommodity ])
				{
					new next;
					Iter_SafeRemove(CommodityIterator, i, next);
					i = next;
				}

			Itter_Remove(IndustryIterator, TruckerJob:CurrentIndustryIndex[ playerid ]);
			DestroyDynamic3DTextLabel(Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Label ]);
			DestroyDynamicPickup(Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Pickup ]);
			Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] = 0;

			SendClientMessage(playerid, COLOR_NEWS, "Industrija sëkmingai paðalinta.");
			return 1;
		}
		case DIALOG_INDUSTRY_M_ADD_COMMODITY:
		{
			// Yra trucke cargo ID. Truksta: kainos ir perka/parduoda
			if(!response)
				return 1;

			new tmp[16], id;
			strmid(tmp, inputtext, 0, strfind(inputtext, "."));
			id = strval(tmp);
			SetPVarInt(playerid, "Commodity_CargoId", id);

			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_INPUT_S_B_STA, DIALOG_STYLE_MSGBOX, "Pirkimas/pardavimas", "Kà ði industrija turi daryti su ðia preke?", "Pirkti", "Parduoti");
			return 1;
		}
		case DIALOG_INDUSTRY_M_INPUT_S_B_STA:
		{
			// Turim CargoID ir perka/parduoda statusa. Truksta: kainos.
			if(response)
				SetPVarInt(playerid,"Commodity_SellBuy",_:Buying);
			else
				SetPVarInt(playerid, "Commodity_SellBuy", _:Selling);

			ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_INPUT_PRICE, DIALOG_STYLE_INPUT, "Prekës kaina", "Áveskite prekës kainà", "Kurti", "Iðeiti");
			return 1;
		}
		case DIALOG_INDUSTRY_M_INPUT_PRICE:
		{
			if(!response)
				return 1;

			new price;
			if(sscanf(inputtext, "i", price) || price < 0)
				return ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_INPUT_PRICE, DIALOG_STYLE_INPUT, "Prekës kaina", "{FF0000}Kaina netinkama. Kaina turi bûti skaièius ir negali bûti maþesnë uþ 0.\n{FFFFFF}Áveskite prekës kainà", "Kurti", "Iðeiti");


			// Viska turim. Galima pagaliau pridët industrijai prekæ.
			new query[180];
			format(query, sizeof(query),"INSERT INTO commodities (industry_id, cargo_id, sell_buy_status, type, price) VALUES(%d, %d, '%s', 'Industry', %d)",
				Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ],
				GetPVarInt(playerid, "Commodity_CargoId"),
				(GetPVarInt(playerid, "Commodity_SellBuy") == _:Selling) ? ("Selling") : ("Buying"),
				price);
			mysql_pquery(DbHandle,query);

			for(new i = 0; i < sizeof(Commodities); i++)
            {
                if(Commodities[ i ][ IndustryId ] || Commodities[ i ][ CargoId ])
                    continue;
                Commodities[ i ][ IndustryId ] = Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ];
                Commodities[ i ][ CargoId ] = GetPVarInt(playerid, "Commodity_CargoId");
                Commodities[ i ][ CurrentStock ] = 0;
                Commodities[ i ][ SellBuyStatus ] = E_COMMODITY_SELL_BUY_STATUS: GetPVarInt(playerid, "Commodity_SellBuy");
                Commodities[ i ][ IsBusinessCommodity ] = false;
                Commodities[ i ][ Price ] = price;
                Itter_Add(CommodityIterator, i);
                break;
            }
            UpdateIndustryInfo(TruckerJob:CurrentIndustryIndex[ playerid ]);
            format(query, sizeof(query), "Industrijos prekës %s sëkmingai pridëtas. Kaina: %d.",
            	(GetPVarInt(playerid, "Commodity_SellBuy") == _:Selling) ? ("pardavimas") : ("pirkimas"),
            	price);
            SendClientMessage(playerid, COLOR_NEWS, query);
            DeletePVar(playerid, "Commodity_SellBuy");
            DeletePVar(playerid, "Commodity_CargoId");
            TruckerJob:CurrentIndustryIndex[ playerid ] = -1;
            return 1;
		}
		case DIALOG_INDUSTRY_M_REMOVE_COMMMO:
		{
			if(!response)
				return 1;

			new tmp[16], id, query[110];
			strmid(tmp, inputtext, 0, strfind(inputtext, "."));
			id = strval(tmp);

			format(query, sizeof(query), "DELETE FROM commodities WHERE industry_id = %d AND cargo_id = %d AND type 'Industry'",
				Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ], id);
			mysql_pquery(DbHandle,query);

			foreach(CommodityIterator, i)
				if(Commodities[ i ][ IndustryId ] == Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] && Commodities[ i ][ CargoId ] == id)
				{
					Commodities[ i ][ IndustryId ] = 0;
					Commodities[ i ][ CargoId ] = 0;
					Commodities[ i ][ CurrentStock ] = 0;
					Commodities[ i ][ Price ] = 0;
					id = i;
					break;
				}
			Iter_Remove(CommodityIterator, id);
			UpdateIndustryInfo(TruckerJob:CurrentIndustryIndex[ playerid ]);
			SendClientMessage(playerid, COLOR_NEWS, "Prekë sëkmingai paðalinta.");
			return 1;
		}
		case DIALOG_INDUSTRY_M_COMMODITY_PRI:
		{
			if(!response)
				return 1;

			new tmp[32];
			strmid(tmp, inputtext, 0, strfind(inputtext, "."));
			SetPVarInt(playerid, "Commodity_Index", strval(tmp));
			TruckerJob:ShowPlayerDialog(playerid, CommodityPriceChange);
			return 1;
		}
		case DIALOG_INDUSTRY_M_COMMODITY_PR2:
		{
			if(!response)
				return 1;

			new query[120],
				index = GetPVarInt(playerid, "Commodity_Index"),
				price;

			if(sscanf(inputtext, "i", price) || price <= 0)
				return TruckerJob:ShowPlayerDialog(playerid, CommodityPriceChange);

			Commodities[ index ][ Price ] = price;
			format(query, sizeof(query), "UPDATE commodities SET price = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
				Commodities[ index ][ Price ], Commodities[ index ][ CargoId ], Commodities[ index ][ IndustryId ]);
			mysql_pquery(DbHandle,query);

			SendClientMessage(playerid, COLOR_NEWS, "Kaina sëkmingai atnaujinta.");
			UpdateIndustryInfo(TruckerJob:CurrentIndustryIndex[ playerid ]);
			return 1;
		}
		case DIALOG_INDUSTRY_M_COMMODITY_STO:
		{
			if(!response)
				return 1;

			new tmp[32];
			strmid(tmp, inputtext, 0, strfind(inputtext, "."));
			SetPVarInt(playerid, "Commodity_Index", strval(tmp));
			TruckerJob:ShowPlayerDialog(playerid, CommodityCurrentStockChange);
			return 1;
		}
		case DIALOG_INDUSTRY_M_COMMODITY_ST2:
		{
			if(!response)
				return 1;

			new query[120],
				index = GetPVarInt(playerid, "Commodity_Index"),
				amount;

			if(sscanf(inputtext, "i", amount) || amount < 0)
				return TruckerJob:ShowPlayerDialog(playerid, CommodityPriceChange);
			if(amount > GetCargoLimit(Commodities[ index ][ CargoId ]))
			{
				SendClientMessage(playerid, GRAD, "Klaida, kiekis negali bûti didesnis uþ limità.");
				return TruckerJob:ShowPlayerDialog(playerid, CommodityPriceChange);
			}

			Commodities[ index ][ CurrentStock ] = amount;
			format(query, sizeof(query), "UPDATE commodities SET current_stock = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
				Commodities[ index ][ CurrentStock ], Commodities[ index ][ CargoId ], Commodities[ index ][ IndustryId ]);
			mysql_pquery(DbHandle, query);

			SendClientMessage(playerid, COLOR_NEWS, "Kiekis sëkmingai atnaujintas.");
			UpdateIndustryInfo(TruckerJob:CurrentIndustryIndex[ playerid ]);
			return 1;
		}
		case DIALOG_INDUSTRY_M_INPUT_ID:
		{
			if(!response)
				return 1;
			new industry_index, string[1024];
			if(sscanf(inputtext, "i", industry_index))
				return TruckerJob:ShowPlayerDialog(playerid, GetCurrentIndustryId);
			if(industry_index < 0 || industry_index >= GetIndustryCount())
				return TruckerJob:ShowPlayerDialog(playerid, GetCurrentIndustryId);

			TruckerJob:CurrentIndustryIndex[ playerid ] = industry_index;
			// Turim indeksa dabar galima testi toliau atitinkamus veiksmus.
			switch(TruckerJob:InputIndexUse[ playerid ])
			{
				// Jei indekso norejo kad sunaikinti industrija, dar paklausiam ar tikrai to nori...
				case IndustryDestroy:
				{
					format(string, sizeof(string),"\"%s\"\n{FFFFFF}Ar tikrai norite sunaikinti ðià industrijà?\nSugraþinti jos nebus ámanoma.",
						Industries[ industry_index ][ Name ]);
					ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_DESTROY_CONF, DIALOG_STYLE_MSGBOX, "Industrijos naikinimas", string, "Iðtrinti", "Iðeiti");
				}
				case IndustryAddCommodity:
				{
					foreach(TruckerCargoIterator, i)
						format(string, sizeof(string),"%s%d.\t%s\n", string, TruckerCargo[ i ][ Id ], TruckerCargo[ i ][ Name ]);

					ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_ADD_COMMODITY, DIALOG_STYLE_LIST, "Pasirinkite prekæ kurià norite pridëti", string, "Tæsti", "Iðeiti");
				}
				case IndustryRemoveCommodity:
				{
					foreach(CommodityIterator, i)
						if(Commodities[ i ][ IndustryId ] == Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] && !Commodities[ i ][ IsBusinessCommodity ])
							format(string, sizeof(string),"%s%d.\t%s\n", string, Commodities[ i ][ CargoId ], GetCargoName(Commodities[ i ][ CargoId ]));

					ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_REMOVE_COMMMO, DIALOG_STYLE_LIST, "Pasirinkite prekæ kurià norite paðalinti", string, "Tæsti", "Iðeiti");
				}
				case ChangeCommodityPrice:
				{
					foreach(CommodityIterator, i)
						if(Commodities[ i ][ IndustryId ] == Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] && !Commodities[ i ][ IsBusinessCommodity ])
							format(string, sizeof(string), "%s%d. [%s]%32s $%d\n",
								string,
								i,
								(Commodities[ i ][ SellBuyStatus ] == Selling)? ("Parduoda") : ("Perka"),
								GetCargoName(Commodities[ i ][ CargoId ]),
								Commodities[ i ][ Price ]);
					ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_COMMODITY_PRI, DIALOG_STYLE_LIST, "Pasirinkte prekæ", string, "Tæsti", "Iðeiti");
				}
				case ChangeCommodityCurrentStock:
				{
					foreach(CommodityIterator, i)
						if(Commodities[ i ][ IndustryId ] == Industries[ TruckerJob:CurrentIndustryIndex[ playerid ] ][ Id ] && !Commodities[ i ][ IsBusinessCommodity ])
							format(string, sizeof(string), "%s%d. [%s]%32s %d vienetai\n",
								string,
								i,
								(Commodities[ i ][ SellBuyStatus ] == Selling)? ("Parduoda") : ("Perka"),
								GetCargoName(Commodities[ i ][ CargoId ]),
								Commodities[ i ][ CurrentStock ]);
					ShowPlayerDialog(playerid, DIALOG_INDUSTRY_M_COMMODITY_STO, DIALOG_STYLE_LIST, "Pasirinkte prekæ", string, "Tæsti", "Iðeiti");
				}
			}
			return 1;
		}
	}
	return 0;
}
