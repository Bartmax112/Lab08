package pollub.ism.lab08;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "WarzywniakHistoriaAktualizacji")
public class PozycjaMagazynowaHistoriaAktualizacji {

    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String DATE;
    public String NAME;
    public int OLD_QUANTITY;
    public int NEW_QUANTITY;

    public PozycjaMagazynowaHistoriaAktualizacji(String DATE, String NAME, int OLD_QUANTITY, Integer NEW_QUANTITY) {
        this.DATE = DATE;
        this.NAME = NAME;
        this.OLD_QUANTITY = OLD_QUANTITY;
        this.NEW_QUANTITY = NEW_QUANTITY;
    }
}

