package org.mozilla.gecko.home;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mozilla.gecko.R;
import org.mozilla.gecko.preferences.PreferenceManager;

/**
 * Copyright © Cliqz 2018
 * This file is derived from @{@link TopSitesPanel}.java
 */
public class VpnPanel extends HomeFragment implements View.OnClickListener, VpnCountriesDialog.VpnDialogCallbacks {
    private static final String LOGTAG = "GeckoVpnPanel";
    private TextView selectedCountry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_vpn_panel, container, false);
        selectedCountry = (TextView) view.findViewById(R.id.vpn_country);
        selectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
        selectedCountry.setOnClickListener(this);
        selectedCountry.setPaintFlags(selectedCountry.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    @Override
    protected void load() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vpn_country) {
            VpnCountriesDialog.show(getContext(), this);
        }
    }

    @Override
    public void onCheckChanged() {
        selectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
    }
}
