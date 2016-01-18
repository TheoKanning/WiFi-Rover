package theokanning.rover.ui.activity;

public interface SteeringListener {
    public enum Direction{
        UP("up"),
        DOWN("down"),
        LEFT("left"),
        RIGHT("right");

        private String text;

        Direction(String text){
            this.text = text;
        }

        public String asText(){
            return text;
        }
    }

    void sendCommand(Direction direction);
}
