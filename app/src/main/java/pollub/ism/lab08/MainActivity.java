package pollub.ism.lab08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import pollub.ism.lab08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;
    private String wybraneWarzywoNazwa = null;
    private Integer wybraneWarzywoIlosc = null;

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ};

    private BazaMagazynowa bazaDanych;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.spinner.setAdapter(adapter);

        bazaDanych = Room.databaseBuilder(getApplicationContext(), BazaMagazynowa.class, BazaMagazynowa.NAZWA_BAZY)
                .allowMainThreadQueries().build();

        if(bazaDanych.pozycjaMagazynowaDAO().size() == 0){
            String[] asortyment = getResources().getStringArray(R.array.Asortyment);
            for(String nazwa : asortyment){
                PozycjaMagazynowa pozycjaMagazynowa = new PozycjaMagazynowa();
                pozycjaMagazynowa.NAME = nazwa; pozycjaMagazynowa.QUANTITY = 0;
                bazaDanych.pozycjaMagazynowaDAO().insert(pozycjaMagazynowa);
            }
        }

        binding.przyciskSkladuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.SKLADUJ);
            }
        });

        binding.przyciskWydaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.WYDAJ);
            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybraneWarzywoNazwa = adapter.getItem(i).toString(); // <---
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nie będziemy implementować, ale musi być
            }
        });
    }

    private void aktualizuj(){
        wybraneWarzywoIlosc = bazaDanych.pozycjaMagazynowaDAO().findQuantityByName(wybraneWarzywoNazwa);
        binding.tekstStanMagazynu.setText("Stan magazynu dla " + wybraneWarzywoNazwa + " wynosi: " + wybraneWarzywoIlosc);
        StringBuilder listaAktualizacja = new StringBuilder();
        for (PozycjaMagazynowaHistoriaAktualizacji wynik : bazaDanych.pozycjaMagazynowaDAOHistoriaAktualizacji().findUpdatesByItemName(wybraneWarzywoNazwa)) {
            listaAktualizacja.append(String.format("%s, %s, %s\n", wynik.DATE, wynik.OLD_QUANTITY, wynik.NEW_QUANTITY));
        }
        binding.aktualizacjaInfo.setText(listaAktualizacja.toString());
    }

    private void zmienStan(OperacjaMagazynowa operacja){

        Integer zmianaIlosci = null;

        try {
            zmianaIlosci = Integer.parseInt(binding.edycjaIlosc.getText().toString());
        }catch(NumberFormatException ex){
            return;
        }finally {
            binding.edycjaIlosc.setText("");
        }
        int staraIlosc = wybraneWarzywoIlosc;

        switch (operacja){
            case SKLADUJ: wybraneWarzywoIlosc += zmianaIlosci; break;
            case WYDAJ:
                if(wybraneWarzywoIlosc - zmianaIlosci >= 0)
                    wybraneWarzywoIlosc -= zmianaIlosci;
                else
                    Toast.makeText(this, "Brak podanej ilosci warzywa w magazynie", Toast.LENGTH_SHORT).show();
                break;
        }


        bazaDanych.pozycjaMagazynowaDAO().updateQuantityByName(wybraneWarzywoNazwa,wybraneWarzywoIlosc);

        ZonedDateTime updateTime = ZonedDateTime.now(ZoneId.of("UTC+2"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        PozycjaMagazynowaHistoriaAktualizacji aktualizacja = new PozycjaMagazynowaHistoriaAktualizacji(updateTime.format(timeFormatter), wybraneWarzywoNazwa, staraIlosc, wybraneWarzywoIlosc);
        bazaDanych.pozycjaMagazynowaDAOHistoriaAktualizacji().insert(aktualizacja);

        aktualizuj();
    }
}