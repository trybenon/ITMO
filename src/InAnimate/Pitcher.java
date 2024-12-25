package InAnimate;
import Exception.PitcherBrokenException;
public class Pitcher extends InAnimate {
    private boolean isBroken;
    private boolean isFilled;

    public Pitcher(String name) {
        super(name);

    }

    public void fill() throws PitcherBrokenException{
        if (!isBroken){
            isFilled = true;
            System.out.println(name + " наполнен водой");
        }
        else
        {
            throw new PitcherBrokenException(name + " сломан, его нельзя наполнить водой");

        }
    }

        public void breakPitcher(){
        isBroken = true;
        isFilled = false;
            System.out.println(name + " разбился");
        }

    @Override
    public void interact() {
        System.out.println(name + "будет использоваться для воды");

    }

    @Override
    public void describe() {
        System.out.println(name + (isBroken ? " сломан" : (isFilled ? " наполнен водой": " пустой")));
    }

    public boolean isBroken() {
        return isBroken;
    }

    public boolean isFilled() {
        return isFilled;
    }
}
