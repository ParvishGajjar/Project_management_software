package achievements;

import entities.Message;
import entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AchievementTracker {
    private AchievementLibrary library = AchievementLibrary.getInstance();
    private HashMap<String, Integer> tracker;
    private User user;

    public AchievementTracker(User user){
        tracker = new HashMap<>();
        this.user = user;
    }

    public int addPoints(String achievementName, int point){
        int total = tracker.containsKey(achievementName) ? tracker.get(achievementName) : 0;
        total += point;
        tracker.put(achievementName, total);
        //send a message if user earned an achievement
        int progress = tracker.get(achievementName) - (getCurrentTier(achievementName)* library.getAchievementRequirement(achievementName));
        int required = library.getAchievementRequirement(achievementName)-progress;

        if(required == library.getAchievementRequirement(achievementName) &&
           getCurrentTier(achievementName) <= library.getAchievementMaxTier(achievementName) ){
            sendCongratsMessage(achievementName);
            user.addExp(library.getAchievementRequirement(achievementName));
        }


        return total;
    }


    private int getCurrentTier(String achievement){
        int currentPoints = tracker.get(achievement);
        int requiredPoints = library.getAchievement(achievement).getRequiredPoints();
        return currentPoints/requiredPoints;
    }

    public ArrayList<String> getUserAchievements(){
        ArrayList<String> accomplishedOnes = new ArrayList<>();
        for(String achievementName : tracker.keySet()){

            //if user have enough points to have this achievement, add name to return list;
            if(tracker.get(achievementName) >= library.getAchievementRequirement(achievementName)){
                accomplishedOnes.add(achievementName);
            }
        }
        return accomplishedOnes;
    }

    public int getTotalTiers(){
        int total = 0;
        ArrayList<String> accomplishedOnes = getUserAchievements();
        for(String achievement : accomplishedOnes){
            total += getCurrentTier(achievement);
        }
        return total;
    }

    public int getNumOfUserAchievements(){
        int totalAchievements=0;
        for(String achievementName : tracker.keySet()){

            //if user have enough points to have this achievement, add name to return list;
            if(tracker.get(achievementName) >= library.getAchievementRequirement(achievementName)){
                totalAchievements++;
            }
        }
        return totalAchievements;
    }



    private String getAchievementStatus(String achievementName){
        if(!tracker.containsKey(achievementName)){
            return "";
        }
        int currentTier = getCurrentTier(achievementName);
        String tier;
        if(currentTier >= library.getAchievementMaxTier(achievementName)){
            tier = "Tier Max";
        } else {
            tier = "Tier " + currentTier;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(library.getAchievementTitle(achievementName)+ " - " + tier);
        builder.append(System.getProperty("line.separator"));
        builder.append(library.getAchievementDescription(achievementName));
        builder.append(System.getProperty("line.separator"));
        if(!tier.equalsIgnoreCase("Tier Max")){
            int progress = tracker.get(achievementName) - (currentTier* library.getAchievementRequirement(achievementName));
            int required = library.getAchievementRequirement(achievementName)-progress;
            builder.append(required + " more points required to achieve next tier");
        }
        //just the new line
        builder.append(System.getProperty("line.separator"));
        return  builder.toString();

    }

    public void printUserAchievementsWithDetails(){
        ArrayList<String> accomplishedOnes = getUserAchievements();
        for(String achievement : accomplishedOnes){
            System.out.println(getAchievementStatus(achievement));

        }
    }

    public void printUserAchievements(){
        ArrayList<String> accomplishedOnes = getUserAchievements();


        for(String achievement : accomplishedOnes){
            int currentTier = getCurrentTier(achievement);
            String tier;
            if(currentTier >= library.getAchievementMaxTier(achievement)){
                tier = "Tier Max";
            } else {
                tier = "Tier " + currentTier;
            }
            System.out.println(library.getAchievementTitle(achievement)+ " - " + tier);
        }
        System.out.println("---------------------------------------------------");
    }

    private void sendNotification(User userToNotify, String message) {
        userToNotify.getInbox().add(new Message("System", userToNotify.getUserName(), message));
    }

    private void sendCongratsMessage(String achievementName){
        StringBuilder builder = new StringBuilder();
        builder.append("Congratulations");
        builder.append(System.getProperty("line.separator"));
        builder.append("Dear " + user.getUserName() + ",");
        builder.append(System.getProperty("line.separator"));
        builder.append("We are happy to report that you have earned a new achievement.");
        builder.append(System.getProperty("line.separator"));
        builder.append(getAchievementStatus(achievementName));
        sendNotification(this.user, builder.toString());
    }





}