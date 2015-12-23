package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerLicense;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.checkpoint.CheckpointEnterEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvCar implements Dmv {

    private static final float MIN_HEALTH = 980.0f;
    private static final Logger logger = LoggerFactory.getLogger(DmvCar.class);
    private static final int MAX_SPEED = 60;

    private int id;
    private Location location;
    private String name;
    private List<DmvQuestion> theoryQuestions;
    private List<DmvCheckpoint> drivingTestCheckpoints;
    private int[] prices;
    private List<LtrpVehicle> vehicles;

    protected Map<LtrpPlayer, Map<DmvQuestion, DmvQuestion.DmvAnswer>> playerTheoryProgress;
    private Map<LtrpPlayer, DrivingTestSession> playerDrivingSessions;
    private EventManager eventManager;

    public DmvCar(String name) {
        this.name = name;
        this.playerTheoryProgress = new HashMap<>();
        this.theoryQuestions = new ArrayList<>();
        this.drivingTestCheckpoints = new ArrayList<>();
        this.playerDrivingSessions = new HashMap<>();
        this.vehicles = new ArrayList<>();

        //this.eventManager = DmvManager.getInstance().getEventManager().createChildNode();
        this.eventManager = LtrpGamemode.get().getEventManager().createChildNode();

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
           LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                if(playerTheoryProgress.containsKey(player)) {
                    playerTheoryProgress.remove(player);
                }
                if(playerDrivingSessions.containsKey(player)) {
                    playerDrivingSessions.remove(player);
                }
            }
        });

        eventManager.registerHandler(SpeedometerTickEvent.class, e -> {
           DrivingTestSession session = playerDrivingSessions.get(e.getPlayer());
            if(e.getSpeed() > session.maxSpeed) {
                session.maxSpeed = e.getSpeed();
            }
        });


        eventManager.registerHandler(CheckpointEnterEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            for(DmvCheckpoint checkpoint : drivingTestCheckpoints) {
                // If he entered the right checkpoint
                if(checkpoint.getCheckpoint().equals(e.getCheckpoint())) {
                    // If we have a registered session
                    if(playerDrivingSessions.containsKey(player)) {
                        DrivingTestSession session = playerDrivingSessions.get(player);
                        // If it is the last one
                        if(++session.cp == drivingTestCheckpoints.size()) {
                            onFinishDrivingTest(player);
                        } else {
                            session.lights = player.getVehicle().getState().getLights() == VehicleParam.PARAM_ON;
                            session.seatbelt = player.getSeatbelt();
                            showCheckpoint(player);
                        }
                    }
                }
            }
        });

    }


    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void startTest(LtrpPlayer player) {
        PlayerLicense playerLicense = player.getLicenses().get(LicenseType.Car);
        PlayerLicense bikeLicense = player.getLicenses().get(LicenseType.Motorcycle);
        // Pradedam teorij�
        if(playerLicense == null) {
            int price = getStagePrice(0);
            MsgboxDialog.create(player, DmvManager.getInstance().getEventManager())
                    .caption(name)
                    .buttonOk("T�sti")
                    .buttonCancel("At�aukti")
                    .message(name + "{FFFFFF}" +
                            "\n\nTeorijos egzamino bandymo kaina $" + price +
                            "\n" + (player.getMoney() < price ? "Jums tr�ksta $" + (price - player.getMoney()) : "") +
                            "\n\nTeorijos egzamin� sudaro " + theoryQuestions.size() + " klausimai." +
                            "\nKad i�laikytum�te turite atsakyti � 90%, t.y. " + (theoryQuestions.size() / 100 * 90) + " klausimus." +
                            "\n\n Ar norite prad�ti egzamin�?")
                    .onClickOk(dialog -> {
                        if (player.getMoney() >= price) {
                            player.giveMoney(-price);
                            if (playerTheoryProgress.containsKey(player)) {
                                playerTheoryProgress.remove(player);
                            } else {
                                showQuestionDialog(player);
                            }
                        } else {
                            player.sendErrorMessage("Teorijos egzamino bandymas kainuoja $" + price);
                        }
                    })
                    .build()
                    .show();

            // Pradedam praktik�
        } else if(playerLicense.getStage() == 1 || bikeLicense.getStage() == 1) {
            if(player.getVehicle() != null && vehicles.contains(player.getVehicle())) {
                int price = getStagePrice(1);
                MsgboxDialog.create(player, DmvManager.getInstance().getEventManager())
                        .caption(getName())
                        .buttonOk("T�sti")
                        .buttonCancel("At�aukti")
                        .message(name + "{FFFFFF}" +
                            "\n\nVairavimo egzamino kaina $" + price +
                            "\n\nMaksimalus leid�iamas greitis egzamino metu 60 km/h.")
                        .onClickCancel(dialog ->  player.removeFromVehicle())
                        .onClickOk(dialog -> {
                            showCheckpoint(player);
                        })
                        .build()
                        .show();
            } else {
                player.sendErrorMessage("J�s ne " + getName() + " transporto priemon�je.");
            }
        } else {
            player.sendErrorMessage("J�s jau turite lengvojo automobilio ir motociklo licenzijas.");
        }
    }

    private void showCheckpoint(LtrpPlayer player) {
        DmvCheckpoint checkpoint;
        if(!playerDrivingSessions.containsKey(player)) {
            playerDrivingSessions.put(player, new DrivingTestSession());
        }
        checkpoint = drivingTestCheckpoints.get(playerDrivingSessions.get(player).cp);
        checkpoint.getCheckpoint().set(player);
    }

    private void showQuestionDialog(LtrpPlayer player) {
        Map<DmvQuestion, DmvQuestion.DmvAnswer> playerQuestions = playerTheoryProgress.get(player);
        DmvQuestion nextQuestion;
        Random random = new Random();
        do {
            nextQuestion = theoryQuestions.get(random.nextInt(theoryQuestions.size()));
        } while(playerQuestions.containsKey(nextQuestion));
        InputDialog questionDialog = InputDialog.create(player, eventManager).build();
        questionDialog.setCaption("Klausimas " + playerQuestions.size() + "/" + theoryQuestions.size());
        questionDialog.setButtonOk("Atsakyti");
        questionDialog.setButtonCancel("I�eiti");
        questionDialog.setMessage(getName() + "" +
                "\n\n{FFFFFF}Klausimas" + nextQuestion.getQuestion() +
                "\n\nGalimi atsakymai:");
        int count = 0;
        for(DmvQuestion.DmvAnswer answer : nextQuestion.getAnswers()) {
            questionDialog.addLine(String.format("%d) %s\n", count+1, answer.getAnswer()));
        }
        questionDialog.addLine("\n�veskite teisingo atskaymo numer�.");

        final DmvQuestion question = nextQuestion;
        questionDialog.setClickOkHandler((dialog, input) -> {
            int number = -1;
            try {
                number = Integer.parseInt(input);
            } catch(NumberFormatException e) {}
            if(number > -1 && number < question.getAnswers().length) {
                playerQuestions.put(question, question.getAnswers()[number-1]);
                if(playerQuestions.size() == theoryQuestions.size()) {
                    onFinishTheoryTest(player);
                } else {
                    showQuestionDialog(player);
                }
            } else {
                player.sendErrorMessage("Netinkamas atsakymo numeris. Galimi numeriai 1 - " + question.getAnswers().length);
                questionDialog.show();
            }
        });
        questionDialog.show();
    }

    private void onFinishTheoryTest(LtrpPlayer player) {
        Map<DmvQuestion, DmvQuestion.DmvAnswer> playerQuestions = playerTheoryProgress.get(player);
        playerTheoryProgress.remove(player);
        int correctAnswers = 0;
        for(DmvQuestion.DmvAnswer answer : playerQuestions.values()) {
            if(answer.isCorrect())
                correctAnswers++;
        }
        int correctPercent = theoryQuestions.size() / 100 * correctAnswers;

        MsgboxDialog.create(player, eventManager)
                .caption("Testo pabaiga!")
                .buttonOk("Gerai")
                .message("I� teorijos egzamino surinkote " + correctPercent + "%(" + correctAnswers + "/" + theoryQuestions.size()+")" +
                        "\n\n" + (correctPercent >= 90 ? "{00BB00}Sveikiname i�laikius egzamin�\n{FFFFFF}Dabar galite laikyti praktin� vairavimo egzamin�." : "{BB0000}Egzamino nei�laik�te. Pra�ome bandyti dar kart�."))
                .build()
                .show();
        if(correctPercent >= 90) {
            PlayerLicense license = new PlayerLicense();
            license.setPlayer(player);
            license.setType(LicenseType.Car);
            license.setStage(1);
            license.setDateAquired(new Date());
            player.getLicenses().add(license);
            license = new PlayerLicense();
            license.setPlayer(player);
            license.setType(LicenseType.Motorcycle);
            license.setDateAquired(new Date());
            license.setStage(1);
            player.getLicenses().add(license);

            LtrpGamemode.getDao().getPlayerDao().updateLicenses(player.getLicenses());
        }
    }

    private void onFinishDrivingTest(LtrpPlayer player) {
        logger.debug("onFinishDrivingTest" );
        LtrpVehicle vehicle = player.getVehicle();
        DrivingTestSession session = playerDrivingSessions.get(player);

        boolean passed = true;
        if(session.maxSpeed > MAX_SPEED) {
            passed = false;
        } else if(!session.lights) {
            passed = false;
        } else if(!session.seatbelt) {
            passed = false;
        } else if(vehicle.getHealth() < 980.0f) {
            passed = false;
        }

        player.removeFromVehicle();
        vehicle.respawn();

        MsgboxDialog.create(player, eventManager)
                .caption("Testo pabaiga!")
                .buttonOk("Gerai")
                .message("Pabaig�te testo vairavimo etap�! " +
                        "\n\n{BA931B}" + (passed ? "Test� j�s i�laik�te!" : "Testo j�s nei�laik�te!") +
                        "\n\n{FFFFFF}Testo suvestin�:" +
                        "\nDid�iausias greitis: " + (passed ? "{1BD61F}" : "{FF464A}") + session.maxSpeed +
                        "\n{FFFFFF}Saugos dir�ai: " + (session.seatbelt ? "{1BD61F}Taip" : "{FF464A}Ne") +
                        "\n{FFFFFF}�viesos: " + (session.lights ? "{1BD61F}Taip" : "{FF464A}Ne") +
                        "\n{FFFFFF}Apgadintas automobilis:" + (vehicle.getHealth() < MIN_HEALTH ? "{1BD61F}Ne" : "{FF464A}Taip"))
                .build()
                .show();

        if(passed) {
            if (LtrpVehicleModel.isBike(vehicle.getModelId()))
                player.getLicenses().get(LicenseType.Motorcycle).setStage(2);
            else
                player.getLicenses().get(LicenseType.Car).setStage(2);
            LtrpGamemode.getDao().getPlayerDao().updateLicenses(player.getLicenses());
        }
    }


    @Override
    public int getStagePrice(int stage) {
        return prices[stage];
    }


    @Override
    public String getName() {
        return name;
    }

    public void addPrice(int stage, int price) {
        if(stage >= prices.length) {
            this.prices = new int[stage+1];
        }
        prices[ stage ] = price;
    }

    public void addQuestion(DmvQuestion question) {
        this.theoryQuestions.add(question);
    }

    public void addCheckpoint(DmvCheckpoint checkpoint) {
        this.drivingTestCheckpoints.add(checkpoint);
    }

    @Override
    public List<LtrpVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<LtrpVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    private class DrivingTestSession {
        protected float maxSpeed;
        protected int cp;
        protected boolean lights, seatbelt;

        public DrivingTestSession() {

        }
    }

}
