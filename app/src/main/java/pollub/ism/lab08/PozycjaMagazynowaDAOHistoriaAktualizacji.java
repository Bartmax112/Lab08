package pollub.ism.lab08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PozycjaMagazynowaDAOHistoriaAktualizacji {

    @Insert
    public void insert(PozycjaMagazynowaHistoriaAktualizacji item);

    @Update
    void update(PozycjaMagazynowaHistoriaAktualizacji item);

    @Query("SELECT * FROM WarzywniakHistoriaAktualizacji WHERE NAME= :wybraneWarzywoNazwa")
    List<PozycjaMagazynowaHistoriaAktualizacji> findUpdatesByItemName(String wybraneWarzywoNazwa);

    @Query("SELECT COUNT(*) FROM WarzywniakHistoriaAktualizacji")
    int size();
}
