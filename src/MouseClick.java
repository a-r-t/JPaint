public enum MouseClick {
    RIGHT_CLICK, LEFT_CLICK, MIDDLE_CLICK;

    static MouseClick convertToMouseClick(int button) {
        switch (button) {
            case 1:
                return LEFT_CLICK;
            case 2:
                return MIDDLE_CLICK;
            case 3:
                return RIGHT_CLICK;
            default:
                return null;
        }
    }

}
