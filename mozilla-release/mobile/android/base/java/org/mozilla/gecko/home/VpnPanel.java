package org.mozilla.gecko.home;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mozilla.gecko.R;
import org.mozilla.gecko.preferences.PreferenceManager;
import org.mozilla.gecko.vpn.LaunchVPN;
import org.mozilla.gecko.vpn.core.ConnectionStatus;
import org.mozilla.gecko.vpn.core.LogItem;
import org.mozilla.gecko.vpn.core.OpenVPNService;
import org.mozilla.gecko.vpn.core.ProfileManager;
import org.mozilla.gecko.vpn.core.VpnStatus;

/**
 * Copyright © Cliqz 2018
 * This file is derived from @{@link TopSitesPanel}.java
 */
public class VpnPanel extends HomeFragment implements View.OnClickListener,
        VpnCountriesDialog.VpnDialogCallbacks, VpnStatus.LogListener {
    private static final String LOGTAG = "GeckoVpnPanel";
    private TextView selectedCountry;
    private View vpnConnectButton;
    private LaunchVPN launchVPN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_vpn_panel, container, false);
        selectedCountry = (TextView) view.findViewById(R.id.vpn_country);
        selectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
        selectedCountry.setOnClickListener(this);
        selectedCountry.setPaintFlags(selectedCountry.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        vpnConnectButton = view.findViewById(R.id.vpn_connect_button);
        vpnConnectButton.setOnClickListener(this);
        VpnStatus.addLogListener(this);
        return view;
    }

    @Override
    protected void load() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vpn_country) {
            VpnCountriesDialog.show(getContext(), this);
        } else if (v.getId() == R.id.vpn_connect_button) {
            ProfileManager m = ProfileManager.getInstance(getContext());
            launchVPN = new LaunchVPN(m.getProfileByName("us-vpn"), getActivity());
            launchVPN.launchVPN();
            final Intent pauseVPN = new Intent(getActivity(), OpenVPNService.class);
            pauseVPN.setAction(OpenVPNService.PAUSE_VPN);
            final Intent resumeVPN = new Intent(getActivity(), OpenVPNService.class);
            resumeVPN.setAction(OpenVPNService.RESUME_VPN);
            final PendingIntent pauseVPNPending = PendingIntent.getService(getActivity(), 0, pauseVPN, 0);
            final PendingIntent resumeVPNPending = PendingIntent.getService(getActivity(), 0, resumeVPN, 0);
            VpnStatus.addStateListener(new VpnStatus.StateListener() {
                @Override
                public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
                    Log.d(LOGTAG, "State: " + state);
                    Log.d(LOGTAG, "Msg: " + logmessage);
                    Log.d(LOGTAG, "Level: " + level.name());
                }

                @Override
                public void setConnectedVPN(String uuid) {
                    Log.d(LOGTAG, "Connected to " + uuid);
                }
            });
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        pauseVPNPending.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            },5000);
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        resumeVPNPending.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            },10000);


        }
    }

    @Override
    public void onCheckChanged() {
        selectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
    }

    @Override
    public void newLog(LogItem logItem) {
        Log.d(LOGTAG, logItem.toString());
    }

}
