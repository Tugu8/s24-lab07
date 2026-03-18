package frogger;

public class Road {

    private final boolean[] occupied;

    public Road(boolean[] occupied) {
        this.occupied = occupied;
    }


    public boolean isValidPosition(int position) {
        return position >= 0 && position < occupied.length;
    }


    public boolean isOccupied(int position) {
        return occupied[position];
    }

    public boolean canMoveTo(int position) {
        return isValidPosition(position) && !isOccupied(position);
    }
}