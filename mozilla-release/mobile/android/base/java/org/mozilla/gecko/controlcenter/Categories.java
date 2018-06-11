package org.mozilla.gecko.controlcenter;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import org.mozilla.gecko.R;

/**
 * Copyright © Cliqz 2018
 */
public enum Categories {

    advertising(R.color.advertising, R.color.advertising_disabled, R.drawable.ic_category_advertising),
    audio_video_player(R.color.audio_video_player, R.color.audio_video_player_disabled, R.drawable.ic_category_audio_video),
    comments(R.color.comments, R.color.comments_disabled, R.drawable.ic_category_comments),
    customer_interaction(R.color.customer_interaction, R.color.customer_interaction_disabled, R.drawable.ic_category_customer_interaction),
    essential(R.color.essential, R.color.essential_disabled, R.drawable.ic_category_essential),
    pornvertising(R.color.pornvertising, R.color.pornvertising_disabled, R.drawable.ic_category_pornvertising),
    site_analytics(R.color.site_analytics, R.color.site_analytics_disabled, R.drawable.ic_category_site_analytics),
    social_media(R.color.social_media, R.color.social_media_disabled, R.drawable.ic_category_social_media),
    unknown(R.color.default_category, R.color.default_category_disabled, R.drawable.ic_category_pornvertising); //TODO get icon for unknown

    public static Categories safeValueOf(String value) {
        try {
            return Categories.valueOf(value);
        } catch (IllegalArgumentException e) {
            return unknown;
        }
    }

    public final @ColorRes int categoryColor;
    public final @ColorRes int categoryColorDisabled;
    public final @DrawableRes int categoryIcon;

    Categories(@ColorRes int color, @ColorRes int colorDisabled, @DrawableRes int icon) {
        categoryColor = color;
        categoryColorDisabled = colorDisabled;
        categoryIcon = icon;
    }
}
