package com.example.pruebabasededatos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    private EditText reserva;
    private EditText reservaFecha;
    private EditText etHora;
    private Button butGuardar;
    private Date fecha;
    private String Hora;

    //Accedemos a la base de datos (Firestore)
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reserva = findViewById(R.id.etReserva);
        reservaFecha =  findViewById(R.id.etdFechaReserva);
        etHora =  findViewById(R.id.etHora);
        butGuardar = findViewById(R.id.button);










        reservaFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showDatePickerDialog();

            }
        });

        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obtenerHora();


            }
        });


        butGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaString = reservaFecha.getText().toString();
                fecha = ParseFecha(fechaString);
                Timestamp myDate = new Timestamp(fecha);
                Hora = etHora.getText().toString();

                Map<String, Timestamp> updateMap = new HashMap();
                updateMap.put("Fecha", myDate);

                db.collection("Reservas")
                        .document(reserva.getText().toString())
                        .set(updateMap);
               // db.collection("Reservas").document("Tipo_Reserva").set(Hora);
            }
        });






    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                reservaFecha.setText(selectedDate);
            }
        });

        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }


    public static Date ParseFecha(String fecha)
    {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);

        }
        catch ( ParseException ex)
        {
            Log.e(Values.LOG_TAG, "Error creando fecha: " + ex.getMessage());
            System.out.println(ex);
        }
        return fechaDate;
    }


}