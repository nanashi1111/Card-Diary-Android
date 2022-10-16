package com.github.dewinjm.monthyearpicker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import java.text.DateFormatSymbols;
import java.util.Locale;

public class MonthYearPickerDialog extends AlertDialog implements DialogInterface
        .OnClickListener, Presenter.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";

    private Presenter presenter;
    private OnDateSetListener onDateSetListener;
    private String title;

    /**
     * @param context     The context the dialog is to run in.
     * @param year        Current year selected.
     * @param monthOfYear Current month selected.
     * @param listener    MonthYearPicker callback
     */
    MonthYearPickerDialog(Context context,
                          int year,
                          int monthOfYear,
                          OnDateSetListener listener) {
        this(context, 0, year, monthOfYear, MonthFormat.SHORT, listener);
    }

    /**
     * @param context     The context the dialog is to run in.
     * @param year        Current year selected.
     * @param monthOfYear Current month selected.
     * @param monthFormat Set month format strings.
     * @param listener    MonthYearPicker callback
     */
    MonthYearPickerDialog(Context context,
                          int year,
                          int monthOfYear,
                          MonthFormat monthFormat,
                          OnDateSetListener listener) {
        this(context, 0, year, monthOfYear, monthFormat, listener);
    }

    /**
     * @param context  The context the dialog is to run in.
     * @param listener MonthYearPicker callback
     */
    MonthYearPickerDialog(Context context, OnDateSetListener listener) {
        this(context, 0, 0, 0, MonthFormat.SHORT, listener);
    }

    /**
     * @param context     The context the dialog is to run in.
     * @param theme       The theme to apply to this dialog.
     * @param year        Current year selected.
     * @param monthOfYear Current month selected.
     * @param monthFormat Set month format strings.
     * @param listener    MonthYearPicker callback
     */
    private MonthYearPickerDialog(Context context,
                                  int theme,
                                  int year,
                                  int monthOfYear,
                                  MonthFormat monthFormat,
                                  OnDateSetListener listener) {
        this(context, theme, year, monthOfYear, true, monthFormat, listener);
    }

    /**
     * @param context               The context the dialog is to run in.
     * @param theme                 The theme to apply to this dialog.
     * @param year                  Current year selected.
     * @param monthOfYear           Current month selected.
     * @param showSelectedDateTitle Set true or false if you want to show the selected date as a title.
     * @param monthFormat           Set month format strings.
     * @param listener              MonthYearPicker callback
     */
    @SuppressLint("InflateParams")
    private MonthYearPickerDialog(Context context,
                                  int theme,
                                  int year,
                                  int monthOfYear,
                                  boolean showSelectedDateTitle,
                                  MonthFormat monthFormat,
                                  OnDateSetListener listener) {
        super(context, theme);

        onDateSetListener = listener;

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        View view = inflater.inflate(R.layout.month_year_picker, null);
        setView(view);

        if (showSelectedDateTitle)
            createTitle(year, monthOfYear);

        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        presenter = new Presenter(new PickerView(view), monthFormat);
        presenter.init(year, monthOfYear, this);
    }

    private void createTitle(int year, int monthOfYear) {
        Locale locale = Locale.getDefault();
        String month = new DateFormatSymbols().getMonths()[monthOfYear].toUpperCase(locale);
        createTitle(String.format(locale, "%s - %s", month, year));
    }

    void createTitle(String title) {
        this.title = title;
        this.setTitle(title);
    }

    String getTitle() {
        return title;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (onDateSetListener != null) {
                    onDateSetListener.onDateSet(
                            presenter.getYear(),
                            presenter.getMonth());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onDateChanged(int year, int monthOfYear) {
        // Stub - do nothing
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, presenter.getYear());
        state.putInt(MONTH, presenter.getMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        presenter.init(year, month, this);
    }

    void setMinDate(long minDate) {
        presenter.setMinDate(minDate);
    }

    void setMaxDate(long maxDate) {
        presenter.setMaxDate(maxDate);
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link
         *                    java.util.Calendar}.
         */
        void onDateSet(int year, int monthOfYear);
    }
}
