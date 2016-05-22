package lt.ltrp;

import lt.ltrp.constant.ItemType;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.data.JobData;
import lt.ltrp.data.PlayerLicense;
import lt.ltrp.object.*;
import lt.ltrp.player.BankAccount;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.object.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Bebras
 *         2016.02.07.
 */
public class GettersSetters {

    public GettersSetters(AmxInstance amx) {
        amx.registerFunction("getPlayerSqlId", params-> {
            LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getUUID();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("showJobVehManagementDialog", params -> {
            throw new NotImplementedException();
            /*
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                new JobVehicleMenu(p, LtrpGamemode.get().getEventManager()).show();
            }
            return 0;
            */
        }, Integer.class);



        amx.registerFunction("getPlayerUsername", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                int len = (Integer)params[2];
                params[0] = p.getName().substring(0, len > p.getName().length() ? len : p.getName().length());
                return 1;
            }
            return 0;
        }, Integer.class);
        amx.registerFunction("setPlayerUsername", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setName((String) params[1]);

                return 1;
            }
            return 0;
        }, Integer.class, String.class, Integer.class);


        amx.registerFunction("getPlayerPassword", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                params[1] = p.getPassword ();
            }
            return 0;
        }, Integer.class);

        amx.registerFunction("setPlayerPassword", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setPassword((String)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, String.class, Integer.class);

        amx.registerFunction("getPlayerSecretQuestion", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.getSecretQuestion ();
                return 1;
            }
            return 0;
        }, Integer.class);

        amx.registerFunction("setPlayerSecretQuestion", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setSecretQuestion((String)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, String.class, Integer.class);

        amx.registerFunction("getPlayerSecretAnswer", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                params[1] = p.getSecretAnswer ();
                return 1;
            }
            return 0;
        }, Integer.class);

        amx.registerFunction("setPlayerSecretAnswer", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setSecretAnswer((String)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, String.class, Integer.class);

        amx.registerFunction("getPlayerLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLevel();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setLevel((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerAdminLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getAdminLevel();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerAdminLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setAdminLevel((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerConnectedTime", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getConnectedTime();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerConnectedTime", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setOnlineHours((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerBoxStyle", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getBoxingStyle();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerBoxStyle", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setBoxingStyle((Integer) params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);



        amx.registerFunction("getPlayerAge", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getAge();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerAge", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setAge((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("addJobExp", params -> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                JobData jobData = JobController.get().getJobData(p);
                if(jobData != null) {
                    jobData.addXp((Integer)params[1]);
                }
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerRespect", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getRespect();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerRespect", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setRespect((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerMoney", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getMoney();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerMoney", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setMoney((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);

        BankPlugin plugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
        amx.registerFunction("getPlayerBankMoney", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                BankAccount account = plugin.getBankController().getAccount(p);
                if(account != null)
                    return account.getMoney();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("addPlayerBankMoney", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                BankAccount account = plugin.getBankController().getAccount(p);
                if(account != null)
                    account.addMoney((Integer) params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerDeaths", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getDeaths();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerDeaths", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setDeaths((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerWantedLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getWantedLevel();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerWantedLevel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setWantedLevel((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerJob", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                JobData jobData = JobController.get().getJobData(p);
                return jobData != null ? jobData.getJob().getUUID() : 0;
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerJob", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            Job job = Job.get((Integer) params[1]);
            if(p != null && job != null) {
                JobController.get().setJob(p, job);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);




        amx.registerFunction("getPlayerRank", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                JobData jobData = JobController.get().getJobData(p);
                return jobData != null ? jobData.getJobRank().getNumber() : null;
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerRank", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            JobData jobData = JobController.get().getJobData(p);
            if(p != null && jobData != null) {
                Rank rank = jobData.getJob().getRank((Integer) params[1]);
                if(rank != null) {
                    JobController.get().setRank(p, rank);
                    return 1;
                }
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerSkin", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getSkin();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerSkin", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setSkin((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);


        amx.registerFunction("getPlayerPhonenumber", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                ItemPhone phone = (ItemPhone)p.getInventory().getItem(ItemType.Phone);
                if(phone != null) {
                    return phone.getPhonenumber();
                }
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("getPlayerCarlic", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLicenses().get(LicenseType.Car) == null ? 0 : 1;
            }
            return -1;
        }, Integer.class);



        amx.registerFunction("getPlayerFlylic", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLicenses().get(LicenseType.Aircraft) == null ? 0 : 1;
            }
            return -1;
        }, Integer.class);



        amx.registerFunction("getPlayerBoatlic", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLicenses().get(LicenseType.Ship) == null ? 0 : 1;
            }
            return -1;
        }, Integer.class);



        amx.registerFunction("getPlayerMotolic", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLicenses().get(LicenseType.Motorcycle) == null ? 0 : 1;
            }
            return -1;
        }, Integer.class);



        amx.registerFunction("getPlayerGunlic", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getLicenses().get(LicenseType.Gun) == null ? 0 : 1;
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("getPlayerRadioChannel", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                RadioItem item = (RadioItem)p.getInventory().getItem(ItemType.Radio);
                if(item != null) {
                    params[1] = item.getFrequency();
                    return 1;
                }
            }
            return 0;
        }, Integer.class, Float.class);


        amx.registerFunction("getPlayerDriverwarn", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                PlayerLicense license = p.getLicenses().get(LicenseType.Car);
                if(license != null) {
                    return license.getWarnings().length;
                }
                return 0;
            }
            return -1;
        }, Integer.class);


        amx.registerFunction("getPlayerHunger", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                return p.getHunger();
            }
            return -1;
        }, Integer.class);

        amx.registerFunction("setPlayerHunger", params-> {
           LtrpPlayer p = LtrpPlayer.get(Player.get((Integer) params[0]));
            if(p != null) {
                p.setHunger((Integer)params[1]);
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class);



    }

}
