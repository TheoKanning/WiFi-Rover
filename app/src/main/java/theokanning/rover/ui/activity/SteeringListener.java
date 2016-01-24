package theokanning.rover.ui.activity;

public interface SteeringListener {
    public enum Direction{
        UP("UP"),
        DOWN("DOWN"),
        LEFT("LEFT"),
        RIGHT("RIGHT");

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
