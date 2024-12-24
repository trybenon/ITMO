package Story;

public class Main {
    public static void main(String[] args) {
        StoryManager storyManager = new StoryManager();

        storyManager.describeInitialState();
        storyManager.sonsFetchWater();
        storyManager.fatherReaction();
        storyManager.describeFinalState();
    }
}
