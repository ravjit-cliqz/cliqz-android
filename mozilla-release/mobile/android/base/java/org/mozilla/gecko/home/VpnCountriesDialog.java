package org.mozilla.gecko.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.mozilla.gecko.R;
import org.mozilla.gecko.preferences.PreferenceManager;

/**
 * Copyright © Cliqz 2018
 */
public class VpnCountriesDialog implements RadioGroup.OnCheckedChangeListener, DialogInterface.OnClickListener {

    private Context mContext;
    private VpnDialogCallbacks mVpnDialogCallbacks;

    private VpnCountriesDialog(Context context, VpnDialogCallbacks vpnDialogCallbacks) {
        mContext = context;
        mVpnDialogCallbacks = vpnDialogCallbacks;
    }

    public static void show(Context context, VpnDialogCallbacks vpnDialogCallbacks) {
        final PreferenceManager preferenceManager = PreferenceManager.getInstance(context);
        final String selectedCountry = preferenceManager.getVpnSelectedCountry();
        final VpnCountriesDialog vpnCountriesDialog = new VpnCountriesDialog(context, vpnDialogCallbacks);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.vpn_countries_dialog, null);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.countries_radio_group);
        radioGroup.setOnCheckedChangeListener(vpnCountriesDialog);
        final String [] availableCountries = context.getResources().getStringArray(R.array.vpn_available_countries);
        for (String country : availableCountries) {
            final RadioButton radioButton = new RadioButton(context);
            radioButton.setText(country);
            radioGroup.addView(radioButton);
            if (selectedCountry.equals(country)) {
                radioButton.setChecked(true);
            }
        }
        builder.setView(dialogView);
        builder.setNegativeButton(R.string.button_cancel, vpnCountriesDialog);
        builder.setPositiveButton(R.string.button_ok, vpnCountriesDialog);
        builder.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        PreferenceManager.getInstance(mContext).setVpnCountry(
        ((RadioButton)group.findViewById(checkedId)).getText().toString());
        mVpnDialogCallbacks.onCheckChanged();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    public interface VpnDialogCallbacks {
        void onCheckChanged();
    }

}
