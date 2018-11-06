package org.mozilla.gecko.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import org.mozilla.gecko.R;
import org.mozilla.gecko.preferences.PreferenceManager;
import org.mozilla.gecko.vpn.DisconnectVPN;
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
        VpnCountriesDialog.VpnDialogCallbacks, VpnStatus.LogListener, VpnStatus.StateListener {
    private static final String LOGTAG = "GeckoVpnPanel";
    private TextView mSelectedCountry;
    private View mVpnConnectButton;
    private TextView mVpnButtonTitle;
    private TextView mVpnButtonDesc;
    private TextView mVpnStatusText;
    private ImageView mWorldMap;
    private Chronometer vpnTimer;
    private Handler mainHandler;
    private Boolean shouldAnimate = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_vpn_panel, container, false);
        mSelectedCountry = (TextView) view.findViewById(R.id.vpn_country);
        mSelectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
        mSelectedCountry.setOnClickListener(this);
        mSelectedCountry.setPaintFlags(mSelectedCountry.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        mVpnConnectButton = view.findViewById(R.id.vpn_connect_button);
        mVpnConnectButton.setOnClickListener(this);
        mVpnButtonTitle = (TextView) mVpnConnectButton.findViewById(R.id.vpn_button_text_title);
        mVpnButtonDesc = (TextView) mVpnConnectButton.findViewById(R.id.vpn_button_text_desc);
        mVpnStatusText = (TextView) view.findViewById(R.id.vpn_status_text);
        mWorldMap = (ImageView) view.findViewById(R.id.vpn_map);
        vpnTimer = (Chronometer) view.findViewById(R.id.vpn_timer);
        VpnStatus.addLogListener(this);
        VpnStatus.addStateListener(this);
        mainHandler = new Handler(getContext().getMainLooper());
        updateStateToConnect();
        return view;
    }

    @Override
    protected void load() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VpnStatus.isVPNAConnected()) {
            updateStateToConnected();
        } else {
            updateStateToConnect();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vpn_country) {
            VpnCountriesDialog.show(getContext(), this);
        } else if (v.getId() == R.id.vpn_connect_button) {
            if (VpnStatus.isVPNAConnected()) {
                Intent disconnectVPN = new Intent(getActivity(), DisconnectVPN.class);
                startActivity(disconnectVPN);
                updateStateToConnect();
            } else {
                final String vpnCountry = PreferenceManager.getInstance(getContext()).getVpnSelectedCountry();
                final ProfileManager m = ProfileManager.getInstance(getContext());
                final LaunchVPN launchVPN = new LaunchVPN(m.getProfileByName(
                        vpnCountry.equalsIgnoreCase("Germany") ? "germany-vpn" : "us-vpn"), getActivity());

                launchVPN.launchVPN();
                final Intent pauseVPN = new Intent(getActivity(), OpenVPNService.class);
                pauseVPN.setAction(OpenVPNService.PAUSE_VPN);
                final Intent resumeVPN = new Intent(getActivity(), OpenVPNService.class);
                resumeVPN.setAction(OpenVPNService.RESUME_VPN);
                final PendingIntent pauseVPNPending = PendingIntent.getService(getActivity(), 0, pauseVPN, 0);
                final PendingIntent resumeVPNPending = PendingIntent.getService(getActivity(), 0, resumeVPN, 0);
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pauseVPNPending.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                },3000);
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            resumeVPNPending.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                },6000);
            }
        }

    }

    @Override
    public void onCheckChanged() {
        mSelectedCountry.setText(PreferenceManager.getInstance(getContext()).getVpnSelectedCountry());
    }

    @Override
    public void newLog(LogItem logItem) {
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        Log.d(LOGTAG, "state: " + state);
        Log.d(LOGTAG, "level " + level.name());
        if (level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)) {
        } else if (level.equals(ConnectionStatus.LEVEL_START)) {
            if (mainHandler != null) {
                mainHandler.post(run1);
            }
        } else if (level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
            shouldAnimate = false;
            if (mainHandler != null) {
                mainHandler.post(run2);
            }
        } else if (level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)) {
            if (mainHandler != null) {
                mainHandler.post(run3);
            }
        } else if (level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)) {
            PreferenceManager preferenceManager = PreferenceManager.getInstance(getContext());
            preferenceManager.setVpnStartTime(System.currentTimeMillis());
        }

    }

    @Override
    public void setConnectedVPN(String uuid) {

    }

    Runnable run1 = new Runnable() {
        @Override
        public void run() {
            updateStateToConnecting();
        }
    };

    Runnable run2 = new Runnable() {
        @Override
        public void run() {
            updateStateToConnected();
        }
    };

    Runnable run3 = new Runnable() {
        @Override
        public void run() {
            updateStateToConnect();
        }
    };

    private void updateStateToConnecting() {
        mVpnButtonTitle.setText("");
        mVpnButtonDesc.setText(".....");
        mVpnButtonTitle.setTextColor(getResources().getColor(android.R.color.black));
        mVpnButtonDesc.setTextColor(getResources().getColor(android.R.color.black));
        mWorldMap.setImageResource(R.drawable.vpn_map_off);
        mVpnConnectButton.setBackground(getResources().getDrawable(R.drawable.vpn_button_connecting_animation));
        final RotateDrawable drawable = (RotateDrawable)mVpnConnectButton.getBackground();
        ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, 10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        //animator.setDuration(500);
        animator.start();
        mVpnButtonTitle.setTextColor(getResources().getColor(R.color.general_blue_color));
        mVpnButtonDesc.setTextColor(getResources().getColor(R.color.general_blue_color));
    }

    private void updateStateToConnected() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(getContext());
        mVpnButtonTitle.setVisibility(View.GONE);
        vpnTimer.setVisibility(View.VISIBLE);
        vpnTimer.setBase(SystemClock.elapsedRealtime() - (System.currentTimeMillis() - preferenceManager.getVpnStartTime()));
        vpnTimer.start();
        mVpnButtonDesc.setText("Disconnect");
        mVpnButtonTitle.setTextColor(getResources().getColor(R.color.general_blue_color));
        mVpnButtonDesc.setTextColor(getResources().getColor(R.color.general_blue_color));
        mVpnConnectButton.setBackground(getResources().getDrawable(R.drawable.vpn_button_background_on));
        mWorldMap.setImageResource(R.drawable.vpn_map_on);
        mVpnStatusText.setText(R.string.vpn_connected_message);
    }

    private void updateStateToConnect() {
        vpnTimer.stop();
        vpnTimer.setVisibility(View.GONE);
        mVpnButtonTitle.setVisibility(View.VISIBLE);
        mVpnButtonTitle.setText("Vpn");
        mVpnButtonDesc.setText("Connect");
        mVpnButtonTitle.setTextColor(getResources().getColor(android.R.color.black));
        mVpnButtonDesc.setTextColor(getResources().getColor(android.R.color.black));
        mVpnConnectButton.setBackground(getResources().getDrawable(R.drawable.vpn_button_background_off));
        mWorldMap.setImageResource(R.drawable.vpn_map_off);
        mVpnStatusText.setText(R.string.vpn_turn_on_message);
    }
}
