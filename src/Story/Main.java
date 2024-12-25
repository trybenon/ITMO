package Story;
import Exception.PitcherBrokenException;
public class Main {
    public static void main(String[] args) {
        StoryManager storyManager = new StoryManager();


        storyManager.describeInitialState();
        try {
            storyManager.sonsFetchWater();
        } catch (PitcherBrokenException e) {
            System.out.println("Произошла ошибка в истории: " + e.getMessage());
        }
        storyManager.fatherReaction();
        storyManager.describeFinalState();
    }
}
