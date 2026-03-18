package frogger;

/**
 * Refactor Task 1 & 2: Frogger
 *
 * @author Zishen Wen (F22), Deyuan Chen (S22), Duan Liang (F23)
 */
public class Frogger {

    // Field for task 1.
    private final Road road;
    private int position;
    
    private final Records records;
    private final FroggerID froggerID; // Task 2: replace six separate profile fields with one ID object.

    public Frogger(Road road, int position, Records records, FroggerID froggerID) {
        this.road = road;
        this.position = position;
        this.records = records;
        this.froggerID = froggerID;
    }

    public Frogger(Road road, int position, Records records, String firstName, String lastName, String phoneNumber,
    String zipCode, String state, String gender) {
        this(
                road,
                position,
                records,
                new FroggerID(firstName, lastName, phoneNumber, zipCode, state, gender) // Task 2: keep old constructor, but convert inputs into FroggerID.
        );
    }

    /**
     * Moves Frogger.
     *
     * @param forward true is move forward, else false.
     * @return true if move successful, else false.
     */
    public boolean move(boolean forward) {
        int nextPosition = this.position + (forward ? 1 : -1);
        if (!this.road.canMoveTo(nextPosition)) { // Task 1: Road now decides whether a target position is valid and free.
            return false;
        }
        this.position = nextPosition;
        return true;
    }

    public boolean isOccupied(int position) {
        return this.road.isOccupied(position); // Task 1: occupancy logic moved out of Frogger.
    }
    
    public boolean isValid(int position) {
        return this.road.isValidPosition(position); // Task 1: bounds checking is now Road's responsibility.
    }

    /**
     * Records Frogger to the list of records.
     * 
     * @return true if record successful, else false.
     */
    public boolean recordMyself() {
        return this.records.addRecord(this.froggerID); // Task 2: Records receives one object instead of six String values.
    }

}
