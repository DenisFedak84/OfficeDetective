package criminalintent.android.bignerdranch.com.criminalintent.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import criminalintent.android.bignerdranch.com.criminalintent.R;

/**
 * Created by Denis on 03.12.2015.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
         // создаем объект Calendar для получения года, месяца и дня
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date,null);
        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Преобразование года, месяца и дня в объект Date
                mDate = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                //Обновление аргумента для сохранения выбранного значения при повороте
                getArguments().putSerializable(EXTRA_DATE,mDate);
            }
        });
        return
                new AlertDialog.Builder(getActivity())
                        .setView(v)
                        .setTitle(R.string.date_picker_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                        .create();
    }

    public static DatePickerFragment newInstance (Date date){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE,date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCod){
        if(getTargetFragment() == null)
        return;

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCod,i);
    }
}
