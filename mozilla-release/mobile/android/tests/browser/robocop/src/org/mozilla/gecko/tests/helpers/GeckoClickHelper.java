package org.mozilla.gecko.tests.helpers;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.robotium.solo.Solo;

import org.mozilla.gecko.Driver;
import org.mozilla.gecko.tests.StringHelper;
import org.mozilla.gecko.tests.UITestContext;

/**
 * Provides helper functions for clicking elements rendered by the Gecko engine.
 */
public class GeckoClickHelper {
    private static Solo sSolo;
    private static Activity sActivity;
    private static Driver sDriver;
    private static StringHelper sStringHelper;
    protected static void init(final UITestContext context) {
        sSolo = context.getSolo();
        sActivity = context.getActivity();
        sDriver = context.getDriver();
        sStringHelper = context.getStringHelper();
    }

    private GeckoClickHelper() { /* To disallow instantiation. */ }

    /**
     * Long press the link and select "Open Link in New Tab" from the context menu.
     *
     * The link should be positioned at the top of the page, at least 60px high and
     * aligned to the middle.
     */
    public static void openCentralizedLinkInNewTab() {
        openLinkContextMenu();

        // Click on "Open Link in New Tab"
        sSolo.clickOnText(sStringHelper.CONTEXT_MENU_ITEMS_IN_NORMAL_TAB[0]);
    }

    /**
     * Long press the link and select "Open Link in New Private Tab" from the context menu.
     *
     * The link should be positioned at the top of the page, at least 60px high and
     * aligned to the middle.
     */
    public static void openCentralizedLinkInNewPrivateTab() {
        openLinkContextMenu();

        // Click on "Open Link in New Private Tab"
        sSolo.clickOnText(sStringHelper.CONTEXT_MENU_ITEMS_IN_NORMAL_TAB[1]);
    }

    private static void openLinkContextMenu() {
        DisplayMetrics dm = new DisplayMetrics();
        sActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        sSolo.clickLongOnScreen(
                sDriver.getGeckoLeft() + sDriver.getGeckoWidth() / 2,
                sDriver.getGeckoTop() + 30 * dm.density
        );
    }
}
