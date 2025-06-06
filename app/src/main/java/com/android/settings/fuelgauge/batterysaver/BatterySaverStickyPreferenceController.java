package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.icu.text.NumberFormat;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.text.TextUtils;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;

public class BatterySaverStickyPreferenceController extends TogglePreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private Context mContext;

    public BatterySaverStickyPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mContext = context;
    }

    @Override
    public boolean isChecked() {
        return Global.getInt(mContext.getContentResolver(),
            Global.LOW_POWER_MODE_STICKY_AUTO_DISABLE_ENABLED, 1) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        Global.putInt(mContext.getContentResolver(),
            Global.LOW_POWER_MODE_STICKY_AUTO_DISABLE_ENABLED,
            isChecked ? 1 : 0);
        return true;
    }

    @Override
    protected void refreshSummary(Preference preference) {
        super.refreshSummary(preference);
        final int stickyShutoffLevel = Global.getInt(
            mContext.getContentResolver(), Global.LOW_POWER_MODE_STICKY_AUTO_DISABLE_LEVEL, 90);
        preference.setSummary(TextUtils.expandTemplate(
                mContext.getString(R.string.battery_saver_sticky_description_new),
                NumberFormat.getIntegerInstance().format(stickyShutoffLevel)));
    }

    @Override
    public void updateState(Preference preference) {
        int setting = Global.getInt(mContext.getContentResolver(),
            Global.LOW_POWER_MODE_STICKY_AUTO_DISABLE_ENABLED, 1);

        ((SwitchPreference) preference).setChecked(setting == 1);
        refreshSummary(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_battery;
    }
}
