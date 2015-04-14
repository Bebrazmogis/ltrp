#include <YSI\y_hooks>



#define DIALOG_FURNITURE_MAIN           115
#define DIALOG_FURNITURE_SELECT_CATEGOR 116
#define DIALOG_FURNITURE_CONFIRM_PURCHA 118
#define DIALOG_FURNITURE_OWNED_LIST     127
#define DIALOG_FURNITURE_EDIT_MAIN      119
#define DIALOG_FURNITURE_ITEM_INFO      120
#define DIALOG_FURNITURE_EDIT_TEXTURE   122
#define DIALOG_FURNITURE_TEXTURE_MAIN   121
#define DIALOG_FURNITURE_TEXTURE_SELECT 123
#define DIALOG_FURNITURE_TEXTURE_COLOR  124
#define DIALOG_FURNITURE_SELL           125
#define DIALOG_FURNITURE_CHANGE_NAME    126

#define TEXTURE_PREVIEW_FURNITURE       1

#define MODEL_SELECTION_FURNITURE       114

#define FURNITURE_PER_PAGE              30

#define FURNITURE_NEXT_PAGE_BUTTON      "Toliau ---->"
#define FURNITURE_PREVIOUS_PAGE_BUTTON  "<---- Atgal"

static PlayerFurniturePage[ MAX_PLAYERS ];


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






public OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz)
{
    #if defined prope_OnPlayerEditDynamicObject
        prope_OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz);
    #endif
    new Float:oldX, Float:oldY, Float:oldZ,
        Float:oldRotX, Float:oldRotY, Float:oldRotZ;
    GetDynamicObjectPos(objectid, oldX, oldY, oldZ);
    GetDynamicObjectRot(objectid, oldRotX, oldRotY, oldRotZ);

    if(!IsAnyHouseFurnitureObject(objectid) && !IsAnyBusinessFurnitureObject(objectid) && !IsAnyGarageFurnitureObject(objectid))
        return 0;

    switch(response)
    {
        case EDIT_RESPONSE_FINAL: // Paspaudþia Save migtukà
        {
            MoveDynamicObject(objectid, x, y, z, 15, rx, ry, rz);

        	new house_index = GetPlayerHouseIndex(playerid);
            if(house_index != -1)
            {
                // Jei perkëlë uþ interjero ribø, jau negerai.
                if(!IsPointInInterior(GetHouseInteriorID(house_index), x, y, z))
                {
                    EditDynamicObject(playerid, objectid);
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Objektas iðëjo ið namo ribø.");
                }

                new furnitureIndex = GetHouseFurnitureIndex(house_index, objectid);
                SaveHouseFurnitureObject(house_index, furnitureIndex, x, y, z, rx, ry, rz);
                SendClientMessage(playerid, COLOR_WHITE,"Objekto redagavimas sëkmingai baigtas." );
                return 1;
            }
            	
            new biz_index = GetPlayerBusinessIndex(playerid);
            if(biz_index != -1)
            {
                if(!IsPointInInterior(GetBusinessInteriorID(biz_index), x, y, z))
                {
                    EditDynamicObject(playerid, objectid);
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Objektas iðëjo ið namo ribø.");
                }
                
                new furnitureIndex = GetBusinessFurnitureIndex(biz_index, objectid);
                SaveBusinessFurnitureObject(biz_index, furnitureIndex, x, y, z, rx, ry, rz);
                SendClientMessage(playerid, COLOR_WHITE,"Objekto redagavimas sëkmingai baigtas." );
                return 1;
            }
            new garage_index = GetPlayerGarageIndex(playerid);
            if(garage_index != -1)
            {
                if(!IsPointInInterior(GetGarageInteriorID(garage_index), x, y, z))
                {
                    EditDynamicObject(playerid, objectid);
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Objektas iðëjo ið namo ribø.");
                }
                
                new furnitureIndex = GetGarageFurnitureIndex(garage_index, objectid);
                SaveGarageFurnitureObject(garage_index, furnitureIndex, x, y, z, rx, ry, rz);
                SendClientMessage(playerid, COLOR_WHITE,"Objekto redagavimas sëkmingai baigtas." );
                return 1;
            }

            
        }
        case EDIT_RESPONSE_CANCEL: // Atðaukia redagavimá .
        {
            SetDynamicObjectPos(objectid, oldX, oldY, oldZ);
            SetDynamicObjectRot(objectid, oldRotX, oldRotY, oldRotZ);
            SendClientMessage(playerid, COLOR_RED,"Redagavimas buvo atðauktas, objektas gràþintas á pradinæ vietà.");
            return 1;
        }
    }
    return 0;
}
#if defined _ALS_OnPlayerEditDynamicObject
    #undef OnPlayerEditDynamicObject
#else 
    #define _ALS_OnPlayerEditDynamicObject
#endif
#define OnPlayerEditDynamicObject       prope_OnPlayerEditDynamicObject
#if defined prope_OnPlayerEditDynamicObject
    forward prope_OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz);
#endif




public OnPlayerPickUpDynamicPickup(playerid, pickupid)
{
    
    new index = GetBusinessPickupIndex(pickupid);
    if(index != -1)
    {
        new string[256];

        format(string,sizeof(string),"%s~n~~w~Savininkas: ~g~%s~n~ ~w~Mokestis: ~g~ %d ~n~~p~ Noredami ieiti - Rasykite /enter",
            GetBusinessName(index), GetSqlIdName(GetBusinessOwner(index)), GetBusinessEntrancePrice(index));
        GameTextForPlayer(playerid, string, 4000, 7);
        return 1;
    }


    index = GetHousePickupHouseIndex(pickupid);
    if(index != -1)
    {
        new string[90];
        switch(GetHouseRent(index))
        {
            case 0:
                format(string,sizeof(string) ,"Namo numeris: %d",GetHouseID(index));
            default:
            format(string,sizeof(string),"Namo numeris: %d~n~Nuomos mokestis: %d~n~Nuomavimuisi - /rentroom",
                    GetHouseID(index),GetHouseRent(index));
        }
        ShowInfoText(playerid, string, 4000);
        return 1;
    }
    #if defined pro_OnPlayerPickUpDynamicPickup
        pro_OnPlayerPickUpDynamicPickup(playerid, pickupid);
    #endif
    return 1;
}
#if defined _ALS_OnPlayerPickUpDynamicPUp
    #undef OnPlayerPickUpDynamicPickup
#else 
    #define _ALS_OnPlayerPickUpDynamicPUp
#endif
#define OnPlayerPickUpDynamicPickup pro_OnPlayerPickUpDynamicPickup
#if defined pro_OnPlayerPickUpDynamicPickup 
    forward pro_OnPlayerPickUpDynamicPickup(playerid, pickupid);
#endif


hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    switch(dialogid)
    {
        case DIALOG_FURNITURE_MAIN:
        {
            if(!response)
                return true;

            switch(listitem)
            {
                case 0: // Baldø pirkimas
                {
                    ShowPlayerFurnitureShopCategori(playerid);
                }
                case 1: // Esami baldai
                {
                    ShowPlayerOwnedFurnitureList(playerid);
                }
            }
            return 1;
        }
        case DIALOG_FURNITURE_SELECT_CATEGOR:
        {
            if(!response)
                return 1;

            new furnitureObjectIDs[ MAX_FURNITURE_OBJECTS ], furnitureObjectCount;
            // Ðita funkcija pripildys pateiktos kategorijos objektø ir graþins kieká.
            GetCategoryFurnitureObjects(GetFurnitureCategoryId(listitem), furnitureObjectIDs, furnitureObjectCount, sizeof(furnitureObjectIDs));
            SetPVarInt(playerid, "FurnCategId", GetFurnitureCategoryId(listitem));
            ShowModelSelectionMenuEx(playerid, furnitureObjectIDs, furnitureObjectCount, GetFurnitureCategoryName(listitem), MODEL_SELECTION_FURNITURE, 45.0, 45.0, 45.0);
            return 1;
        }
        case DIALOG_FURNITURE_CONFIRM_PURCHA:
        {
            if(!response)
                return SendClientMessage(playerid, COLOR_WHITE,"{00FF00}* {FFFFFF}Pirkimas atðauktas!");

            if(GetPlayerHouseIndex(playerid) == -1 && GetPlayerBusinessIndex(playerid) == -1 && GetPlayerGarageIndex(playerid) == -1)
                return SendClientMessage( playerid, COLOR_WHITE,"{00FF00}* {FFFFFF}Turite bûti name, versle arba garaþe!");

            new furnitureIndex = GetPVarInt(playerid, "FurnitureIndex"),
                Float:pos[3],
                index,
                objectid;

            // Jau tikrinom ankðèiau, but just in case 
            if(GetFurniturePrice(furnitureIndex) > Tabu_GetPlayerMoney(playerid))
                return SendClientMessage(playerid, COLOR_WHITE,"Jums nepakanka pinigø.");

            GetPlayerPos(playerid, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            GetXYInFrontOfPlayer(playerid, pos[ 0 ], pos[ 1 ], 2.0);
            SendClientMessage( playerid, COLOR_WHITE,"Baldas nupirktas ir padëtas. Dabar galite já redaguoti !");
            GivePlayerMoney( playerid, -GetFurniturePrice(furnitureIndex));

            if((index = GetPlayerHouseIndex(playerid)) != -1)
                objectid = AddHouseFurniture(index, furnitureIndex, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            else if((index = GetPlayerBusinessIndex(playerid)) != -1)
                objectid = AddBusinessFurniture(index, furnitureIndex, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            else if((index = GetPlayerGarageIndex(playerid)) != -1)
                objectid = AddGarageFurniture(index, furnitureIndex, pos[ 0 ], pos[ 1 ], pos[ 2 ]);

            // Jei þaidëjas nepajudës, objekto nematys todël Streamer_Update
            Streamer_Update(playerid);
            // Ið karto duodam tà objektà judint.
            EditDynamicObject(playerid, objectid);
            return 1;
        }
        case DIALOG_FURNITURE_OWNED_LIST:
        {
            if(!response)
                return ShowPlayerFurnitureMain(playerid);

            // Yra puslapiai...
            if(!strcmp(inputtext, #FURNITURE_PREVIOUS_PAGE_BUTTON))
            {
                PlayerFurniturePage[ playerid ]--;
                ShowPlayerOwnedFurnitureList(playerid);
            }
            else if(!strcmp(inputtext, #FURNITURE_NEXT_PAGE_BUTTON))
            {
                PlayerFurniturePage[ playerid ]++;
                ShowPlayerOwnedFurnitureList(playerid);
            }
            else
            {
                // Èia taip pat turim "PropertyIndex" PVar su verslo/namo/garaþo indeksu
                new tmp[16], index;
                index = strfind(inputtext, "Baldas ")+7;
                strmid(tmp, inputtext, index, strfind(inputtext, ":", .pos=index+1));
                printf("General: DIALOG_FURNITURE_OWNED_LIST tmp:%s|", tmp);

                index = strval(tmp);
                SetPVarInt(playerid, "FurnitureIndex", index);
                ShowPlayerFurnitureEditOptions(playerid);
            }
            return 1;
        }
        case DIALOG_FURNITURE_EDIT_MAIN:
        {
            if(!response)
                return ShowPlayerOwnedFurnitureList(playerid);

            new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                furnitureIndex = GetPVarInt(playerid, "FurnitureIndex"),
                string[256],
                pKaina
            ;

            switch(listitem)
            {
                case 0: // Daikto informacija
                {
                    if(IsPlayerInHouse(playerid, propertyIndex))
                    {
                        pKaina = GetHouseFurniturePrice(propertyIndex, furnitureIndex) / 2; // Parduodant baldà kainà dviubai maþesnë

                        format( string, sizeof string, "{FFFFFF}\t\tID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Kaina: {ffd700}$%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}",
                            GetHouseFurnitureId(propertyIndex, furnitureIndex),
                            GetHouseFurnitureName(propertyIndex, furnitureIndex),
                            GetHouseFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetHouseFurniturePrice(propertyIndex, furnitureIndex),
                            pKaina);
                    }
                    
                    else if(IsPlayerInBusiness(playerid, propertyIndex))
                    {
                        pKaina = GetBusinessFurniturePrice(propertyIndex, furnitureIndex) / 2;

                        format( string, sizeof string, "{FFFFFF}\t\tID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Kaina: {ffd700}$%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}",
                            GetBusinessFurnitureId(propertyIndex, furnitureIndex),
                            GetBusinessFurnitureName(propertyIndex, furnitureIndex),
                            GetBusinessFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetBusinessFurniturePrice(propertyIndex, furnitureIndex),
                            pKaina);
                    }

                    else if(IsPlayerInGarage(playerid, propertyIndex))
                    {
                        pKaina = GetGarageFurniturePrice(propertyIndex, furnitureIndex) / 2;

                        format( string, sizeof string, "{FFFFFF}\t\tID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Kaina: {ffd700}$%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}",
                            GetGarageFurnitureId(propertyIndex, furnitureIndex),
                            GetGarageFurnitureName(propertyIndex, furnitureIndex),
                            GetGarageFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetGarageFurniturePrice(propertyIndex, furnitureIndex),
                            pKaina);
                    }
                    
                    ShowPlayerDialog(playerid, DIALOG_FURNITURE_ITEM_INFO, DIALOG_STYLE_MSGBOX,"Informacija", string, "Uþdaryti", "Atgal");
                }
                case 1: // Pozicija
                {
                    new objectid;

                    if(IsPlayerInHouse(playerid, propertyIndex) && !IsPlayerInRangeOfHouseFurniture(playerid, propertyIndex, furnitureIndex, 10.0))
                        return SendClientMessage(playerid, COLOR_WHITE,"Esate per toli nuo baldo !");

                    if(IsPlayerInBusiness(playerid, propertyIndex) && !IsPlayerInRangeOfBizFurniture(playerid, propertyIndex, furnitureIndex, 10.0))
                       return SendClientMessage(playerid, COLOR_WHITE,"Esate per toli nuo baldo !");

                    if(IsPlayerInGarage(playerid, propertyIndex) && !IsPlayerInRangeOfGarageFurnitur(playerid, propertyIndex, furnitureIndex, 10.0))
                        return SendClientMessage(playerid, COLOR_WHITE,"Esate per toli nuo baldo !");

                    if(IsPlayerInHouse(playerid, propertyIndex))
                        objectid = GetHouseFurnitureObjectId(propertyIndex, furnitureIndex);
                    else if(IsPlayerInBusiness(playerid, propertyIndex))
                        objectid = GetBusinessFurnitureObjectId(propertyIndex, furnitureIndex);
                    else if(IsPlayerInGarage(playerid, propertyIndex))
                        objectid = GetGarageFurnitureObjectId(propertyIndex, furnitureIndex);

                    if(EditDynamicObject(playerid, objectid))
                    {
                        SendClientMessage(playerid, COLOR_WHITE,"Pradëjote redaguoti baldà!");
                        SendClientMessage(playerid, COLOR_WHITE,"Spaudykite migtukus objekto valdymui.");
                        GameTextForPlayer(playerid, "Nuspaudus ~k~~PED_SPRINT~ galite sukti kamera.", 1, 1);

                    }
                    else
                        SendClientMessage(playerid, COLOR_GREY,"KLAIDA ! Nepavyko redaguoti objekto.");
                }
                case 2: // Tekstûros
                {
                    if(pInfo[ playerid ][ pDonator ] > 1)
                        SendClientMessage(playerid, COLOR_WHITE,"Ði funkcija prieinama tik turint Remëjo statusà!");
                    else 
                         ShowPlayerFurnitureTextureMain(playerid);
                }
                case 3: // Parduoti
                {
                    if(!response)
                        return true;

                    if(IsPlayerInHouse(playerid, propertyIndex))
                    {
                        format(string, sizeof(string), "{FFFFFF}\t\tBaldo ID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}\n\
                            Ar tikrai norite parduoti baldà ?",
                            GetHouseFurnitureId(propertyIndex, furnitureIndex),
                            GetHouseFurnitureName(propertyIndex, furnitureIndex),
                            GetHouseFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetHouseFurniturePrice(propertyIndex, furnitureIndex) / 2);
                    }
                    else if(IsPlayerInBusiness(playerid, propertyIndex))
                    {
                        format(string, sizeof(string), "{FFFFFF}\t\tBaldo ID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}\n\
                            Ar tikrai norite parduoti baldà ?",
                            GetBusinessFurnitureId(propertyIndex, furnitureIndex),
                            GetBusinessFurnitureName(propertyIndex, furnitureIndex),
                            GetBusinessFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetBusinessFurniturePrice(propertyIndex, furnitureIndex));
                    }
                    else if(IsPlayerInGarage(playerid, propertyIndex))
                    {
                        format(string, sizeof(string), "{FFFFFF}\t\tBaldo ID: {ffd700}%d\n\
                            {FFFFFF}Pavadinimas: {ffd700}%s{FFFFFF}\n\
                            Objekto ID: {ffd700}%d{FFFFFF}\n\
                            Pardavus bus gauta: {ffd700}$%d{FFFFFF}\n\
                            Ar tikrai norite parduoti baldà ?",
                            GetGarageFurnitureId(propertyIndex, furnitureIndex),
                            GetGarageFurnitureName(propertyIndex, furnitureIndex),
                            GetGarageFurnitureObjectId(propertyIndex, furnitureIndex),
                            GetGarageFurniturePrice(propertyIndex, furnitureIndex));
                    }
                    
                    ShowPlayerDialog(playerid, DIALOG_FURNITURE_SELL, DIALOG_STYLE_MSGBOX,"Baldo pardavimas", string, "Parduoti", "Atðaukti");
                }
                case 4: // Pervadinti
                {
                    ShowPlayerFurnitureNameChange(playerid);
                }
            }
            return 1;
        }
        case DIALOG_FURNITURE_ITEM_INFO:
        {
            if(!response)
                return ShowPlayerFurnitureEditOptions(playerid);
            else 
                return 1;
        }
        case DIALOG_FURNITURE_EDIT_TEXTURE:
        {

            if(!response)
                return ShowPlayerFurnitureEditOptions(playerid);

            new string[32];
            format(string, sizeof(string), "Pasirinkote %d slotà", listitem);
            SendClientMessage(playerid, COLOR_WHITE, string);
            SetPVarInt(playerid, "FurnitureTextIndex", listitem);
            ShowPlayerDialog(playerid, DIALOG_FURNITURE_TEXTURE_MAIN, DIALOG_STYLE_LIST,"Baldo iðvaizdos keitimas", "Keisti tekstûrà\nPaðalinti tekstûrà\nKeisti spalvà\nPaðalinti spalvà", "Pasirinkti", "<--");
            return 1;
        }
        case DIALOG_FURNITURE_TEXTURE_MAIN:
        {
            if( !response )
                return ShowPlayerFurnitureTextureMain(playerid);

            new string[1024];
            switch(listitem)
            {
                case 0:
                {
                    StartFurnitureTexturePreview(playerid, TEXTURE_PREVIEW_FURNITURE);
                    SendClientMessage(playerid, COLOR_FADE1, "Naudokite NUM 2 ir NUM 6 klaviðus eiti per tekstûras.");
                    SendClientMessage(playerid, COLOR_FADE1, "Pasirinkti tekstûrà galite su /ftexture select, atðaukti su /ftexture cancel");
                    SendClientMessage(playerid, COLOR_FADE2, "Pastaba. Jei nematote objektø, pabandykite sumaþint atstumà su /ftexture distance");
                }
                case 1: // Trinti tekstûrà
                {
                    new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                        furnitureIndex = GetPVarInt(playerid,"FurnitureIndex"),
                        textureSlot = GetPVarInt(playerid, "FurnitureTextIndex")
                    ;       

                    if(IsPlayerInHouse(playerid, propertyIndex))
                    {
                        RemoveHouseFurnitureTexture(propertyIndex, furnitureIndex, textureSlot);
                    }
                    
                    else if(IsPlayerInBusiness(playerid, propertyIndex))
                    {
                        RemoveBusinessFurnitureTexture(propertyIndex, furnitureIndex, textureSlot);
                    }
                    
                    else if(IsPlayerInGarage(playerid, propertyIndex))
                    {
                        RemoveGarageFurnitureTexture(propertyIndex, furnitureIndex, textureSlot);
                    }

                    SendClientMessage(playerid, COLOR_WHITE,"Tekstûra sëkmingai iðtrinta !");
                }
                case 2: // Pridëti spalvà 
                {
                    for(new i = 0; i < GetFurnitureTextureColorCount(); i++)
                    {
                        format(string, sizeof(string), "%s{%s}%s\n",
                            string,
                            GetFurnitureTextureColorRGB(i),
                            GetFurnitureTextureColorName(i));
                    }
                    ShowPlayerDialog(playerid, DIALOG_FURNITURE_TEXTURE_COLOR, DIALOG_STYLE_LIST, "Pasirinkite spalvà", string, "Pasirinkti", "Atðaukti" );
                }
                case 3: // Trinti spalvà 
                {
                    new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                        furnitureIndex = GetPVarInt(playerid,"FurnitureIndex"),
                        textureSlot = GetPVarInt(playerid, "FurnitureTextIndex")
                    ;   
                    if(IsPlayerInHouse(playerid, propertyIndex))
                    {
                        SetHouseFurnitureTextureColor(propertyIndex, furnitureIndex, textureSlot, 0);
                    }
                    
                    else if(IsPlayerInBusiness(playerid, propertyIndex))
                    {
                        SetBusinessFurnitureTextureColo(propertyIndex, furnitureIndex, textureSlot, 0);
                    }

                    else if(IsPlayerInGarage(playerid, propertyIndex))
                    {
                        SetGarageFurnitureTextureColor(propertyIndex, furnitureIndex, textureSlot, 0);
                    }
                    
                    else 
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, norëdami atlikti ðá veiksmà, privalote bûti namuose/biznyje, kuriame redaguojate baldus, daigtus.");
                    SendClientMessage(playerid, COLOR_WHITE, "Spalva sëkmingai paðalinta!");
                }
            }
            return 1;
        }
        /*
        // Pakeista á "texture preview"
        // 
        case DIALOG_FURNITURE_TEXTURE_SELECT:
        {
            if( !response )
                return true;

            new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                furnitureIndex = GetPVarInt(playerid,"FurnitureIndex"),
                textureSlot = GetPVarInt(playerid, "FurnitureTextIndex")
            ;
            if(IsPlayerInHouse(playerid, propertyIndex))
            {
                SetDynamicObjectMaterial(
                    GetHouseFurnitureObjectId(propertyIndex, furnitureIndex), 
                    textureSlot,
                    GetFurnitureTextureObjectModel(listitem),
                    GetFurnitureTextureTxdName(listitem),
                    GetFurnitureTextureName(listitem)
                );
                SetHouseFurnitureTexture(propertyIndex, furnitureIndex, textureSlot, GetFurnitureTextureObjectModel(listitem), GetFurnitureTextureTxdName(listitem), GetFurnitureTextureName(listitem));
            }
            else if(IsPlayerInBusiness(playerid, propertyIndex))
            {
                SetDynamicObjectMaterial(
                    GetBusinessFurnitureObjectId(propertyIndex, furnitureIndex), 
                    textureSlot,
                    GetFurnitureTextureObjectModel(listitem),
                    GetFurnitureTextureTxdName(listitem),
                    GetFurnitureTextureName(listitem)
                );
                SetBusinessFurnitureTexture(propertyIndex, furnitureIndex, textureSlot, GetFurnitureTextureObjectModel(listitem), GetFurnitureTextureTxdName(listitem), GetFurnitureTextureName(listitem));
            }
            
            SendClientMessage(playerid, COLOR_WHITE,"Tekstûra pakeista sëkmingai !");
            return 1;
        }  
        */ 
        case DIALOG_FURNITURE_TEXTURE_COLOR:
        {
            if(!response)
                return 1;

            new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                furnitureIndex = GetPVarInt(playerid,"FurnitureIndex"),
                textureSlot = GetPVarInt(playerid, "FurnitureTextIndex")
            ;

            if(IsPlayerInHouse(playerid, propertyIndex))
            {
                SetHouseFurnitureTextureColor(propertyIndex, furnitureIndex, textureSlot, GetFurnitureTextureColorRGBA(listitem));
            }
            else if(IsPlayerInBusiness(playerid, propertyIndex))
            {
                SetBusinessFurnitureTextureColo(propertyIndex, furnitureIndex, textureSlot, GetFurnitureTextureColorRGBA(listitem));
            }
            else if(IsPlayerInGarage(playerid, propertyIndex))
            {
                SetGarageFurnitureTextureColor(propertyIndex, furnitureIndex, textureSlot, GetFurnitureTextureColorRGBA(listitem));
            }
            else 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, norëdami atlikti ðá veiksmà, privalote bûti namuose/biznyje, kuriame redaguojate baldus, daigtus.");
            SendClientMessage(playerid, COLOR_WHITE,"Spalva pakeista sëkmingai!");
            return 1;
        }
        case DIALOG_FURNITURE_SELL:
        {
            if(!response)
                return ShowPlayerFurnitureEditOptions(playerid);

            new string[100],
                propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                furnitureIndex = GetPVarInt(playerid, "FurnitureIndex"),
                price;

            if(IsPlayerInHouse(playerid, propertyIndex))
            {
                price = GetHouseFurniturePrice(propertyIndex, furnitureIndex) / 2;
                format(string, sizeof(string), "Pardavëte baldà {ffd700}%s{FFFFFF} uþ {ffd700}$%d",
                    GetHouseFurnitureName(propertyIndex, furnitureIndex), price);

                GivePlayerMoney(playerid, price);
                DeleteHouseFurniture(propertyIndex, furnitureIndex);
            }
            else if(IsPlayerInBusiness(playerid, propertyIndex))
            {
                price = GetBusinessFurniturePrice(propertyIndex, furnitureIndex) / 2;
                format(string, sizeof(string), "Pardavëte baldà {ffd700}%s{FFFFFF} uþ {ffd700}$%d",
                    GetBusinessFurnitureName(propertyIndex, furnitureIndex), price);

                GivePlayerMoney(playerid, price);
                DeleteBusinessFurniture(propertyIndex, furnitureIndex);
            }
            else if(IsPlayerInGarage(playerid, propertyIndex))
            {
                price = GetGarageFurniturePrice(propertyIndex, furnitureIndex) / 2;
                format(string, sizeof(string), "Pardavëte baldà {ffd700}%s{FFFFFF} uþ {ffd700}$%d",
                    GetGarageFurnitureName(propertyIndex, furnitureIndex), price);

                GivePlayerMoney(playerid, price);
                DeleteGarageFurniture(propertyIndex, furnitureIndex);
            }
            
            SendClientMessage(playerid, COLOR_WHITE, string);
            return 1;
        }
        case DIALOG_FURNITURE_CHANGE_NAME:
        {
            if(!response)
                return ShowPlayerFurnitureEditOptions(playerid);

            if(strlen( inputtext ) >= MAX_FURNITURE_NAME || isnull(inputtext))
                return ShowPlayerFurnitureNameChange(playerid, "Pavadinimas negali bûti ilgesnis nei " #MAX_FURNITURE_NAME " simboliai ar tuðèias");
            
            new string[80],
                propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
                furnitureIndex = GetPVarInt(playerid, "FurnitureIndex");

            format(string, sizeof(string),"Naujas baldo pavadinimas '%s'", inputtext);
            SendClientMessage(playerid, COLOR_WHITE, string );


            if(IsPlayerInHouse(playerid, propertyIndex))
            {
                SetHouseFurnitureName(propertyIndex, furnitureIndex, inputtext);
            }
            
            else if(IsPlayerInBusiness(playerid, propertyIndex))
            {
                SetBusinessFurnitureName(propertyIndex, furnitureIndex, inputtext);
            }
            else if(IsPlayerInGarage(playerid, propertyIndex))
            {
                SetGarageFurnitureName(propertyIndex, furnitureIndex, inputtext);
            }
            return 1;
        }
    }
    return 0;
}


public OnPlayerModelSelectionEx(playerid, response, extraid, modelid)
{
    if(extraid == MODEL_SELECTION_FURNITURE)
    {
        if(!response)
            return ShowPlayerFurnitureShopCategori(playerid);

        new index,
            currentObjectCount, // Kiek ðiuo metu versle/name/garaþe yra objektø.
            categoryId = GetPVarInt(playerid, "FurnCategId"), // Pasirinktos kategorijos ID(ne indeksas)  
            furnitureIndex = GetObjectFurnitureIndex(modelid, categoryId), // Baldø Objektu masyve, objekto indeksas
            string[512]
        ;

        if((index = GetPlayerHouseIndex(playerid)) != -1)
        {
            currentObjectCount = GetHouseFurnitureCount(index);
        }
        if(!currentObjectCount && (index = GetPlayerBusinessIndex(playerid)) != -1)
        {
            currentObjectCount = GetBusinessFurnitureCount(index);
        }
        if(!currentObjectCount && (index = GetPlayerGarageIndex(playerid)) != -1)
        {
            currentObjectCount = GetGarageFurnitureCount(index);
        }

        if(currentObjectCount >= 100 && pInfo[ playerid ][ pDonator ] < 1)
            return SendClientMessage(playerid, COLOR_WHITE, "Baldø limitas 100 baldø.");
        else if(currentObjectCount >= 150 && pInfo[ playerid ][ pDonator ] == 1 )
            return SendClientMessage(playerid, COLOR_WHITE, "Baldø limitas 150 baldø.");
        else if(currentObjectCount >= 250 && pInfo[ playerid ][ pDonator ] == 2 )
            return SendClientMessage(playerid, COLOR_WHITE, "Baldø limitas 250 baldø.");
        else if(currentObjectCount >= 400 && pInfo[ playerid ][ pDonator ] == 3 )
            return SendClientMessage(playerid, COLOR_WHITE, "Baldø limitas 400 baldø.");


        if(Tabu_GetPlayerMoney(playerid) < GetFurniturePrice(furnitureIndex))
            return SendClientMessage( playerid, COLOR_WHITE,"{00FF00}* {FFFFFF}Jums nepakanka pinigø.");

        format(string, sizeof string, "{FFFFFF}\t\tBaldo pirkimas.\n\
            Vietoje, kurioje stovite bus sukurtas baldas ir já\n\
            iðkart galësite pradëti redaguoti, keisti vietà \n\
            Grupë: {00FF00} %s\n\
            {FFFFFF} Baldas: {00FF00}%s\n\
            {FFFFFF} Objekto Id: {00FF00}%d\n\
            {FFFFFF} Kaina: {00FF00}$%d{FFFFFF}\n\
            Spauskite {00FF00}Pirkti{FFFFFF} pirkimo patvirtinimui.\n\
            Spauskite {FF0000}Atðaukti{FFFFFF} pirkimo atðaukimui.",
            GetFurnitureCategoryName(GetFurnitureCategoryIndex(categoryId)),
            GetFurnitureName(furnitureIndex),
            GetFurnitureObjectId(furnitureIndex),
            GetFurniturePrice(furnitureIndex)
        );

        SetPVarInt(playerid, "FurnitureIndex", furnitureIndex);
        ShowPlayerDialog(playerid, DIALOG_FURNITURE_CONFIRM_PURCHA, DIALOG_STYLE_MSGBOX, "Baldo pirkimas", string, "Pirkti", "Atðaukti");
        return 1;
    }
    #if defined biz_OnPlayerModelSelectionEx
        biz_OnPlayerModelSelectionEx(playerid, response, extraid, modelid);
    #endif
    return 0;
}

#if defined _ALS_OnPlayerModelSelectionEx
    #undef OnPlayerModelSelectionEx
#else 
    #define _ALS_OnPlayerModelSelectionEx
#endif
#define OnPlayerModelSelectionEx biz_OnPlayerModelSelectionEx
#if defined biz_OnPlayerModelSelectionEx 
    forward OnPlayerModelSelectionEx(playerid, response, extraid, modelid);
#endif





public OnPlayerSelectTexture(playerid, modelid, txdname[], texturename[], extraid)
{
    if(extraid == TEXTURE_PREVIEW_FURNITURE)
    {
        new propertyIndex = GetPVarInt(playerid, "PropertyIndex"),
            furnitureIndex = GetPVarInt(playerid,"FurnitureIndex"),
            textureSlot = GetPVarInt(playerid, "FurnitureTextIndex")
        ;
        if(IsPlayerInHouse(playerid, propertyIndex))
        {
            SetDynamicObjectMaterial(GetHouseFurnitureObjectId(propertyIndex, furnitureIndex), textureSlot, modelid, txdname, texturename);
            SetHouseFurnitureTexture(propertyIndex, furnitureIndex, textureSlot, modelid, txdname, texturename);
        }
        else if(IsPlayerInBusiness(playerid, propertyIndex))
        {
            SetDynamicObjectMaterial(GetBusinessFurnitureObjectId(propertyIndex, furnitureIndex), textureSlot, modelid, txdname, texturename);
            SetBusinessFurnitureTexture(propertyIndex, furnitureIndex, textureSlot, modelid, txdname, texturename);
        }
        else if(IsPlayerInGarage(playerid, propertyIndex))
        {
            SetDynamicObjectMaterial(GetGarageFurnitureObjectId(propertyIndex, furnitureIndex), textureSlot, modelid, txdname, texturename);
            SetGarageFurnitureTexture(propertyIndex, furnitureIndex, textureSlot, modelid, txdname, texturename);
        }
        SendClientMessage(playerid, COLOR_WHITE,"Tekstûra pakeista sëkmingai!");
        return 1;
    }
    #if defined property_OnPlayerSelectTexture
        property_OnPlayerSelectTexture(playerid, modelid, txdname, texturename, extraid);
    #endif
    return 0;
}
#if defined _ALS_OnPlayerSelectTexture
    #undef OnPlayerSelectTexture
#else 
    #define _ALS_OnPlayerSelectTexture
#endif
#define OnPlayerSelectTexture property_OnPlayerSelectTexture
#if defined property_OnPlayerSelectTexture
    forward property_OnPlayerSelectTexture(playerid, modelid, txdname[], texturename[], extraid);
#endif

public OnPlayerCancelTexturePreview(playerid, extraid)
{
    if(extraid == TEXTURE_PREVIEW_FURNITURE)
    {
        ShowPlayerFurnitureEditOptions(playerid);
        return 1;
    }
    #if defined pr_OnPlayerCancelTexturePreview
        pr_OnPlayerCancelTexturePreview(playerid, extraid);
    #endif
    return 0;
}
#if defined _ALS_OnPlayerCancelTexturePW
    #undef OnPlayerCancelTexturePreview
#else 
    #define _ALS_OnPlayerCancelTexturePW
#endif
#define OnPlayerCancelTexturePreview pr_OnPlayerCancelTexturePreview
#if defined pr_OnPlayerCancelTexturePreview
    forward pr_OnPlayerCancelTexturePreview(playerid, extraid);
#endif


/*
                                                                                                                                                  
                           ,,             ,,                             ,...                                      ,,                             
            `7MM"""Yb.     db           `7MM                           .d' ""                               mm     db                             
              MM    `Yb.                  MM                           dM`                                  MM                                    
              MM     `Mb `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm      mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
              MM      MM   MM  8)   MM    MM 6W'   `Wb :MI  I8         MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
              MM     ,MP   MM   ,pm9MM    MM 8M     M8  WmmmP"         MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
              MM    ,dP'   MM  8M   MM    MM YA.   ,A9 8M              MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
            .JMMmmmdP'   .JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb      .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
                                                       6'     dP                                                                                  
                                                       Ybmmmd'                                                                                    

*/

// Bets function name EVER.
stock ShowPlayerFurnitureShopCategori(playerid)
{
    new string[512];
    for(new i = 0; i < GetFurnitureCategoryCount(); i++)
        format(string, sizeof(string),"%s%s\n",
            string, 
            GetFurnitureCategoryName(i));
    ShowPlayerDialog(playerid, DIALOG_FURNITURE_SELECT_CATEGOR, DIALOG_STYLE_LIST,"Baldø pirkimas", string, "Pasirinkti","Uþdaryti");
    return 1;
}

stock ShowPlayerOwnedFurnitureList(playerid)
{
    // Funkcija parodo lentelæ su turimais objektais nuosavybëje kurioje ðiuo metu yra.
    new count = 0,
        string[2048],
        index,
        furnitureStart = PlayerFurniturePage[ playerid ] * FURNITURE_PER_PAGE,
        furnitureEnd = furnitureStart + FURNITURE_PER_PAGE,
        furnitureCount;

    if(furnitureStart != 0)
        string = #FURNITURE_PREVIOUS_PAGE_BUTTON "\n";

    if(IsPlayerInAnyHouse(playerid))
    {
        index = GetPlayerHouseIndex(playerid);
        furnitureCount = GetHouseFurnitureCount(index);
        for(new i = furnitureStart; i < furnitureEnd; i++)
        {
            if(i == furnitureCount) 
                break;
            count++;
            format(string, sizeof(string), "%sBaldas %d: %s\n",
                string, i, GetHouseFurnitureName(index, i));
        }
    }
    else if(IsPlayerInAnyBusiness(playerid))
    {
        index = GetPlayerBusinessIndex(playerid);
        furnitureCount = GetBusinessFurnitureCount(index);
        for(new i = furnitureStart; i < furnitureEnd, i != furnitureCount; i++)
        {
            if(i == furnitureCount) 
                break;
            count++;
            format(string, sizeof(string), "%sBaldas %d: %s\n",
                string, i, GetBusinessFurnitureName(index, i));
        }
    }  
    else if(IsPlayerInAnyGarage(playerid))
    {
        index = GetPlayerGarageIndex(playerid);
        furnitureCount = GetGarageFurnitureCount(index);
        for(new i = furnitureStart; i < furnitureEnd, i != furnitureCount; i++)
        {
            if(i == furnitureCount) 
                break;
            count++;
            format(string, sizeof(string), "%sBaldas %d: %s\n",
                string, i, GetGarageFurnitureName(index, i));
        }
    }

    if(furnitureEnd < furnitureCount)
        strcat(string, #FURNITURE_NEXT_PAGE_BUTTON);

    if(!count)
        return SendClientMessage(playerid, COLOR_WHITE,"Jûs neturite baldø.");

    SetPVarInt(playerid, "PropertyIndex", index);
    ShowPlayerDialog(playerid, DIALOG_FURNITURE_OWNED_LIST, DIALOG_STYLE_LIST,"Baldø sàraðas", string, "Redaguoti", "<--"); 
    return 1;
}


stock ShowPlayerFurnitureMain(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_FURNITURE_MAIN, DIALOG_STYLE_LIST,"Interjero baldai", "Pirkti baldus\nTurimi baldai", "Pasirinkti","Uþdaryti");
    return 1;
}

stock ShowPlayerFurnitureEditOptions(playerid)
    return ShowPlayerDialog(playerid, DIALOG_FURNITURE_EDIT_MAIN, DIALOG_STYLE_LIST, "Pasirinktys", "Informacija\nPozicija\nBaldo iðvaizda\nParduoti\nPervadinti", "Pasirinkti","Atgal");

stock ShowPlayerFurnitureTextureMain(playerid)
    return ShowPlayerDialog(playerid, DIALOG_FURNITURE_EDIT_TEXTURE, DIALOG_STYLE_LIST,"Baldo iðvaizdos keitimas",
                            "{FFFFFF}Baldo tekstûrø slot {00FF00}0{FFFFFF}\nBaldo tekstûrø slot {00FF00}1{FFFFFF}\nBaldo tekstûrø slot {00FF00}2{FFFFFF}\n\
                            Baldo tekstûrø slot {00FF00}3{FFFFFF}\nBaldo tekstûrø slot {00FF00}4", "Pasirinkti", "<--");


stock ShowPlayerFurnitureNameChange(playerid, errostr[] = "")
{
    new string[256];
    string = "Áveskite naujà  baldo pavadinimà  ir spauskite Keisti\nBaldo pavadinimas negali bûti ilgesnis nei " #MAX_FURNITURE_NAME " simboliai\nPavadinimas:";
    if(!isnull(errostr))
        format(string, sizeof(string), "{AA2200}%s{FFFFFF}%s", errostr, string);
    ShowPlayerDialog(playerid, DIALOG_FURNITURE_CHANGE_NAME, DIALOG_STYLE_INPUT, "Baldo pavadinimas", string, "Keisti", "Atgal");
    return 1;
}

/*
					                                                                                              
					                                                                                  ,,          
					  .g8"""bgd                                                                     `7MM          
					.dP'     `M                                                                       MM          
					dM'       ` ,pW"Wq.`7MMpMMMb.pMMMb.  `7MMpMMMb.pMMMb.   ,6"Yb.  `7MMpMMMb.   ,M""bMM  ,pP"Ybd 
					MM         6W'   `Wb MM    MM    MM    MM    MM    MM  8)   MM    MM    MM ,AP    MM  8I   `" 
					MM.        8M     M8 MM    MM    MM    MM    MM    MM   ,pm9MM    MM    MM 8MI    MM  `YMMMa. 
					`Mb.     ,'YA.   ,A9 MM    MM    MM    MM    MM    MM  8M   MM    MM    MM `Mb    MM  L.   I8 
					  `"bmmmd'  `Ybmd9'.JMML  JMML  JMML..JMML  JMML  JMML.`Moo9^Yo..JMML  JMML.`Wbmd"MML.M9mmmP' 
					                                                                                              
					                                                                                              
*/


CMD:ram(playerid)
{
    if(UsePDCMD(playerid) != 1) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente.");
    if(pInfo[ playerid ][pRank] < 2)      
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda leidþiama naudotis 2 rango pareigûnams.");

    new index = GetPlayerHouseIndex(playerid, true);
    if(index != -1)
    {
        if(IsHouseLocked(index))
        {
            ShowInfoText(playerid, "~w~ Namo durys iðlauþtos", 5000);
            ToggleHouseLock(index);
        }
        else 
            ShowInfoText(playerid, "~w~ Namo durys atrakintos", 5000);
        return 1;
    }
    index = GetPlayerBusinessIndex(playerid);
    if(index != -1)
    {
        if(IsBusinessLocked(index))
        {
            ShowInfoText(playerid, "~w~ Verslo durys iðlauþtos", 5000);
            ToggleBusinessLock(index);
        }
        else 
            ShowInfoText(playerid, "~w~ Verslo durys atrakintos", 5000);
        return 1;
    }
    index = GetPlayerGarageIndex(playerid);
    if(index != -1)
    {
        if(IsGarageLocked(index))
        {
            ShowInfoText(playerid, "~w~ Garaþo vartai iðlauþti", 5000);
            ToggleBusinessLock(index);
        }
        else 
            ShowInfoText(playerid, "~w~ Garaþo vartai atrakinti", 5000);
        return 1;
    }
    return 1;
}



CMD:furniture(playerid, params[])
{
    if(IsPlayerInTexturePreview(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Negalite naudoti ðios komandos perþiûrinëdami tekstûras.");

    new index = -1;

    PlayerFurniturePage[ playerid ] = 0;

    if((index = GetPlayerHouseIndex(playerid)) != -1 && IsPlayerHouseOwner(playerid, index))
        ShowPlayerFurnitureMain(playerid);

    else if((index = GetPlayerBusinessIndex(playerid)) != -1 && IsPlayerBusinessOwner(playerid, index))
        ShowPlayerFurnitureMain(playerid);

    else if((index = GetPlayerGarageIndex(playerid)) != -1 && IsPlayerGarageOwner(playerid, index))
        ShowPlayerFurnitureMain(playerid);

    else 
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs negalite keisti/pridëti baldø nebûdami arba sau nepriklausanèioje nuosavybëje." );

    return 1;
}

CMD:clothes(playerid)
{
    new bizIndex = -1;
    if((bizIndex = GetPlayerBusinessIndex(playerid)) != -1 && IsPlayerInBusiness(playerid, bizIndex))
    {
        // BAD. Reikia kesit
        if(_:GetBusinessType(bizIndex) == 4)
        {
            ShowModelSelectionMenu ( playerid, skinlist, "Select Skin" ) ;
        }
    }
    else if(IsPlayerInRangeOfPoint(playerid, 20.0, 389.6868,339.0674,999.9752))
    {
        ShowModelSelectionMenu ( playerid, skinlist, "Select Skin" ) ;
    }
    else if(IsPlayerInAnyHouse(playerid))
    {
        ShowModelSelectionMenu(playerid, skinlist, "Select Skin");
    }
    else if(Data_IsPlayerInRangeOfCoords(playerid, 15.0, "md_clothes"))
    {
        ShowModelSelectionMenu(playerid, skinlist, "Pasirinkite apranga");
    }
    else 
        SendClientMessage(playerid, COLOR_LIGHTRED, "Drabuþius galite keisti tik namuose arba drabuþiø parduotuvëje.");
    return 1;
}

CMD:enter(playerid)
{
    #if defined property_cmd_enter
        property_cmd_enter(playerid);
    #endif

    new index = GetPlayerHouseIndex(playerid, true),
        Float:pos[3];
    if(index != -1)
    {
        if(IsPlayerInHouse(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau namo viduje.");

        if(!IsValidInterior(GetHouseInteriorID(index)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis namas neturi interjero.");

        if(IsHouseLocked(index))    
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);

        GetHouseExitPos(index, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
        SetPlayerPos(playerid, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);

        SetPlayerInterior(playerid, GetInteriorInteriorId(GetHouseInteriorID(index)));
        SetPlayerVirtualWorld(playerid,GetHouseVirtualWorld(index));
        if(Audio_IsClientConnected(playerid))
        {
            if(IsHouseRadioTurnedOn(index))
                SetPlayerRadio(playerid, GetHouseRadioStation(index));
        }
        return 1;
    }

    index = GetPlayerBusinessIndex(playerid);
    if(index != -1)
    {
        if(!IsValidInterior(GetBusinessInteriorID(index)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis verslas neturi interjero.");    

        if(IsPlayerInBusiness(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau verslo viduje.");

        if(IsBusinessLocked(index)) 
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);
        if(PlayerMoney[ playerid ] < GetBusinessEntrancePrice(index)) 
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);
        
        // Jei produktu nera ir ne savininkas, neieis :(
        if(_:GetBusinessType(index) != 0 && GetBusinessProductCount(index) <= 0 && !IsPlayerBusinessOwner(playerid, index) && GetBusinessEntrancePrice(index))
            return GameTextForPlayer(playerid, "~r~UZRAKINTA del prekiu stokos", 2000, 1);
        
        GetBusinessExitPos(index, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
        SetPlayerPos(playerid, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);

        SetPlayerInterior(playerid, GetInteriorInteriorId(GetBusinessInteriorID(index)));
        SetPlayerVirtualWorld(playerid, GetBusinessVirtualWorld(index));
        
        if(!IsPlayerBusinessOwner(playerid, index))
        {
            OnPlayerEnterBiz(playerid, index);
            if(_:GetBusinessType(index) != 0 && GetBusinessEntrancePrice(index))
                UpdateBusinessProducts(index, GetBusinessProductCount(index) -1);
        }
        return 1;
    }
    index = GetPlayerGarageIndex(playerid);
    if(index != -1)
    {
        if(!IsValidInterior(GetGarageInteriorID(index)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis garaþas neturi interjero.");

        if(!IsGarageVehicleEntrancePosSet(index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, garaþo transporto priemonës koordinatës lauke nëra nustatytos.");

        if(IsGarageLocked(index))
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);

        new Float:angle, 
            vehicleid = GetPlayerVehicleID(playerid);
        if(vehicleid)
        {
            GetGarageVehicleExitPos(index, pos[ 0 ], pos[ 1 ], pos[ 2 ], angle);
            SetVehicleZAngle(vehicleid, angle);
            SetVehiclePos(vehicleid, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            LinkVehicleToInterior(vehicleid, GetInteriorInteriorId(GetGarageInteriorID(index)));
            SetVehicleVirtualWorld(vehicleid, GetGarageVirtualWorld(index));
            foreach(new i : Player) 
                if(IsPlayerInVehicle(i, vehicleid))
                {
                    SetPlayerInterior(i, GetInteriorInteriorId(GetGarageInteriorID(index)));
                    SetPlayerVirtualWorld(i, GetGarageVirtualWorld(index));
                }
        }
        else
        {
            GetGarageExitPos(index, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            SetPlayerPos(playerid, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
            SetPlayerInterior(playerid, GetInteriorInteriorId(GetGarageInteriorID(index)));
            SetPlayerVirtualWorld(playerid, GetGarageVirtualWorld(index));
            SetPlayerFacingAngle(playerid, angle);
        }
        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);
        return 1;
    }
    return 1;
}
#if defined _ALS_cmd_enter
    #undef cmd_enter 
#else 
    #define _ALS_cmd_enter
#endif
#define cmd_enter property_cmd_enter
#if defined property_cmd_enter 
    forward property_cmd_enter(playerid);
#endif

CMD:exit(playerid)
{
    #if defined property_cmd_exit
        property_cmd_exit(playerid);
    #endif

    new index = GetPlayerHouseIndex(playerid),
        Float:x,
        Float:y, 
        Float:z;
    if(index != -1 && IsPlayerInHouse(playerid, index))
    {
        if(!IsPlayerInRangeOfHouseExit(playerid, index, 5.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðios komandos nebûdami prie iðëjimo.");

        if(IsHouseLocked(index)) 
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);

        GetHouseEntrancePos(index, x, y, z);
        SetPlayerPos(playerid, x, y, z);
        SetPlayerVirtualWorld(playerid, GetHouseEntranceVirtualWorld(index));
        SetPlayerInterior(playerid, GetHouseEntranceInteriorID(index));

        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);

        StopPlayerRadio(playerid);
        return 1;
    }   
    
    index = GetPlayerBusinessIndex(playerid);
    if(index != -1 && IsPlayerInBusiness(playerid, index))
    {
        if(!IsPlayerInRangeOfBusinessExit(playerid, index, 5.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðios komandos nebûdami prie iðëjimo.");

        if(IsBusinessLocked(index)) 
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);

        GetBusinessEntrancePos(index, x, y, z);
        SetPlayerPos(playerid, x, y, z);
        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);
        SetPlayerVirtualWorld(playerid, GetBusinessEntranceVirtualWorld(index));
        SetPlayerInterior(playerid, GetBusinessEntranceInteriorID(index));
        return 1;
    }
    index = GetPlayerGarageIndex(playerid);
    if(index != -1)
    {
        if(!IsPlayerInRangeOfGarageExit(playerid, index, 5.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðios komandos nebûdami prie iðëjimo.");
        if(IsGarageLocked(index)) 
            return GameTextForPlayer(playerid, "~r~UZRAKINTA", 2000, 1);

        new vehicleid = GetPlayerVehicleID(playerid), Float:angle;
        if(vehicleid)
        {
            GetGarageVehicleEntrancePos(index, x, y ,z, angle);
            SetVehicleZAngle(vehicleid, angle);
            SetVehiclePos(vehicleid, x, y ,z);
            LinkVehicleToInterior(vehicleid, GetGarageEntranceInteriorID(index));
            SetVehicleVirtualWorld(vehicleid, GetGarageEntranceVirtualWorld(index));
            foreach(new i : Player)
                if(IsPlayerInVehicle(i, vehicleid))
                {
                    SetPlayerVirtualWorld(i, GetGarageEntranceVirtualWorld(index));
                    SetPlayerInterior(i, GetGarageEntranceInteriorID(index));
                }
        }
        else 
        {
            GetGarageEntrancePos(index, x,y ,z);
            SetPlayerPos(playerid, x, y, z);
            Unfreeze[ playerid ] = 2;
            TogglePlayerControllable(playerid, false);
            SetPlayerVirtualWorld(playerid, GetGarageEntranceVirtualWorld(index));
            SetPlayerInterior(playerid, GetGarageEntranceInteriorID(index));
            SetPlayerFacingAngle(playerid, angle);
        }
        return 1;
    }
    return 1;
}
#if defined _ALS_cmd_exit
    #undef cmd_exit 
#else 
    #define _ALS_cmd_exit
#endif
#define cmd_exit property_cmd_exit
#if defined property_cmd_exit   
    forward property_cmd_exit(playerid);
#endif



CMD:ds(playerid, params[])
{
    new string[ 256 ],
        index,
        Float:x,
        Float:y,
        Float:z,
        interior,
        world;
    
    if(isnull(params))
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ds [Tekstas]");

    index = GetPlayerHouseIndex(playerid, true);
    if(index != -1)
    {
        if(IsPlayerInHouse(playerid, index))
        {
            GetHouseEntrancePos(index, x, y, z);
            world = GetHouseEntranceVirtualWorld(index);
            interior = GetHouseEntranceInteriorID(index);
        }
        else
        {
            GetHouseExitPos(index, x, y, z);
            world = GetHouseVirtualWorld(index);
            interior = GetInteriorInteriorId(GetHouseInteriorID(index));
        }

        format(string, sizeof(string), "%s ðaukia á duris: %s", GetPlayerNameEx(playerid), params);
        ProxDetector(30.0, playerid, string, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2);

        format(string, sizeof(string), "%s ðaukia pro duris: %s", GetPlayerNameEx(playerid), params);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2, world, interior);
        return 1;
    }

    index = GetPlayerBusinessIndex(playerid);
    if(index != -1)
    {
        if(IsPlayerInBusiness(playerid, index))
        {   
            GetBusinessEntrancePos(index, x, y, z);
            world = GetBusinessEntranceVirtualWorld(index);
            interior = GetBusinessEntranceInteriorID(index);
        }
        else 
        {
            GetBusinessExitPos(index, x, y, z);
            world = GetBusinessVirtualWorld(index);
            interior = GetInteriorInteriorId(GetBusinessInteriorID(index));
        }
        format(string, sizeof(string), "%s ðaukia á duris: %s", GetPlayerNameEx(playerid), params);
        ProxDetector(30.0, playerid, string, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2);

        format(string, sizeof(string), "%s ðaukia pro duris: %s", GetPlayerNameEx(playerid), params);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2, world, interior);
        return 1;
    }
    index = GetPlayerGarageIndex(playerid);
    if(index != -1)
    {
        if(IsPlayerInGarage(playerid, index))
        {
            GetGarageEntrancePos(index, x, y, z);
            world = GetGarageEntranceVirtualWorld(index);
            interior = GetGarageEntranceInteriorID(index);
        }
        else 
        {
            GetGarageExitPos(index, x, y, z);
            world = GetGarageVirtualWorld(index);
            interior = GetInteriorInteriorId(GetGarageInteriorID(index));
        }
        format(string, sizeof(string), "%s ðaukia á duris: %s", GetPlayerNameEx(playerid), string);
        ProxDetector(30.0,  playerid, string, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2);

        format(string, sizeof(string), "%s ðaukia pro duris: %s", GetPlayerNameEx(playerid), string);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_FADE1, COLOR_FADE2, world, interior);
        return 1;
    }
    return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate prie jokiø durø.");
}
CMD:knock(playerid)
{
    new string[ 126 ],
        index,
        Float:x, Float:y, Float:z;

    
    if((index = GetPlayerHouseIndex(playerid, true)) != -1)
    {
        if(IsPlayerInHouse(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Negalite belstis bûdami namie!");

        format(string, sizeof(string), "* %s beldþiasi á namo duris.", GetPlayerNameEx(playerid));
        ProxDetector(30.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

        format(string, sizeof(string), "* %s beldþiasi á duris.", GetPlayerNameEx(playerid));
        
        GetHouseExitPos(index, x, y, z);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, GetHouseEntranceVirtualWorld(index), GetInteriorInteriorId(GetHouseEntranceInteriorID(index)));
        return 1;
    }
    if((index = GetPlayerBusinessIndex(playerid)) != -1)
    {
        if(IsPlayerInBusiness(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Negalite belstis bûdami versle!");

        format(string, sizeof(string), "* %s beldþiasi á namo duris.", GetPlayerNameEx(playerid));
        ProxDetector(30.0,  playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

        format(string, sizeof(string), "* %s beldþiasi á duris.", GetPlayerNameEx(playerid));
        GetBusinessExitPos(index, x, y, z);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, GetBusinessEntranceVirtualWorld(index), GetInteriorInteriorId(GetBusinessEntranceInteriorID(index)));
        return 1;
    }
    if((index = GetPlayerGarageIndex(playerid)) != -1)
    {
        if(IsPlayerInGarage(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Negalite belstis bûdami garaþe!");

        format(string, sizeof(string), "* %s beldþiasi á namo duris.", GetPlayerNameEx(playerid));
        ProxDetector(30.0,  playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

        format(string, sizeof(string), "* %s beldþiasi á duris.", GetPlayerNameEx(playerid));
        GetGarageExitPos(index, x, y, z);
        ProxDetectorCords(30.0, string, x, y, z, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, GetGarageVirtualWorld(index), GetInteriorInteriorId(GetGarageInteriorID(index)));
        return 1;
    }
    return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate prie jokiø durø. ");
}

CMD:lock(playerid)
{
    /*
    foreach(Garazai,id)
    {
        if ( PlayerToPoint( 2.0, playerid, gInfo[ id ][ gSpawn ][ 0 ], gInfo[ id ][ gSpawn ][ 1 ], gInfo[ id ][ gSpawn ][ 2 ] ) ||
        PlayerToPoint( 2.0, playerid, gInfo[ id ][ gExit ][ 0 ], gInfo[ id ][ gExit ][ 1 ], gInfo[ id ][ gExit ][ 2 ] ) && GetPlayerVirtualWorld(playerid) == GARAGE_VIRTUAL_WORLD+gInfo[ id ][ gID ])
        {
            if ( gInfo[ id ][ gOwner ] ==  0 )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Garaþas nëra nupirktas. " );
            if ( gInfo[ id ][ gOwner ] == pInfo[ playerid ][ pMySQLID ] )
            {
                if ( gInfo[ id ][ gLock ] == 1 )
                {
                    gInfo[ id ][ gLock ] = 0;
                    ShowInfoText   ( playerid, "~w~GARAZAS ~g~ATRAKINTAS", 1000);
                    PlayerPlaySound( playerid, 1145, 0.0, 0.0, 0.0);
                    SaveGarage( id );
                    return 1;
                }
                else if ( gInfo[ id ][ gLock ] == 0 )
                {
                    gInfo[ id ][ gLock ] = 1;
                    ShowInfoText   ( playerid, "~w~GARAZAS ~r~UZRAKINTAS", 1000);
                    PlayerPlaySound( playerid, 1145, 0.0, 0.0, 0.0);
                    SaveGarage( id );
                    return 1;
                }
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Garaþas nepriklauso jums. " );
        }
    }
    */
    new index = GetPlayerHouseIndex(playerid, true);
    if(index != -1 && (IsPlayerHouseOwner(playerid, index) || IsPlayerRentingHouse(playerid, index)))
    {
        if(IsHouseLocked(index))
            ShowInfoText(playerid, "~w~DURYS ~g~ATRAKINTOS", 1000);
        else 
            ShowInfoText(playerid, "~w~DURYS ~r~UZRAKINTOS", 1000);

        PlayerPlaySound(playerid, 1145, 0.0, 0.0, 0.0);
        ToggleHouseLock(index);
        return 1;
    }
    if((index = GetPlayerGarageIndex(playerid)) != -1)
    {
        if(!IsPlayerGarageOwner(playerid, index))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Garaþas nepriklauso jums. ");   

        if(IsGarageLocked(index))
        {
            ShowInfoText(playerid, "~w~GARAZAS ~g~ATRAKINTAS", 1000);
        }
        else 
        {
            ShowInfoText(playerid, "~w~GARAZAS ~r~UZRAKINTAS", 1000);
        }
        PlayerPlaySound(playerid, 1145, 0.0, 0.0, 0.0);
        ToggleGarageLock(index);
        return 1;
    }

    
    #if defined property_cmd_lock
        property_cmd_lock(playerid);
    #endif
    return 1;
}
#if defined _ALS_cmd_lock
    #undef cmd_lock
#else 
    #define _ALS_cmd_lock
#endif
#define cmd_lock property_cmd_lock
#if defined property_cmd_lock
    forward property_cmd_lock(playerid);
#endif