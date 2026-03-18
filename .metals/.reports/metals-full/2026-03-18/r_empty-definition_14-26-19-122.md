error id: file:///C:/Users/User/OneDrive/Desktop/Buteelt/s24-lab07/Java/src/main/java/frogger/Records.java:java/util/List#contains().
file:///C:/Users/User/OneDrive/Desktop/Buteelt/s24-lab07/Java/src/main/java/frogger/Records.java
empty definition using pc, found symbol in pc: java/util/List#contains().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 382
uri: file:///C:/Users/User/OneDrive/Desktop/Buteelt/s24-lab07/Java/src/main/java/frogger/Records.java
text:
```scala
package frogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Refactor Task 2.
 *
 * @author Zishen Wen (F22), Deyuan Chen (S22)
 */
public class Records {
    private final List<FroggerID> records;

    public Records() {
        this.records = new ArrayList<>();
    }

    public boolean addRecord(FroggerID froggerID) {
        if (this.records.@@contains(froggerID)) {
            return false;
        }
        this.records.add(froggerID);
        return true;
    }

    /**
     * Adds a frogger's record.
     *
     * @param firstName   first name of the frogger
     * @param lastName    last name of the frogger
     * @param phoneNumber phone number of the frogger
     * @param zipCode     zip code of the frogger
     * @param state       state of the frogger
     * @param gender      gender of the frogger
     * @return Return false if the record has existed. Else, return true.
     */
    public boolean addRecord(String firstName, String lastName, String phoneNumber,
                             String zipCode, String state, String gender) {
        return addRecord(new FroggerID(firstName, lastName, phoneNumber, zipCode, state, gender));
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#contains().