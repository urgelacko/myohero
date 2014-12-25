package com.urgesoft.gopro.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.myo.AssignableMyoPoseKeys;
import com.urgesoft.gopro.myo.MyoAssignments;

import java.util.ArrayList;
import java.util.List;

public class MyoAssignmentSettingsFragment extends PreferenceFragment {

    public static final String KEY_MYO_COMMAND_ASSIGNMENTS = "myo_command_assignments";

    public static final String PREF_KEY_SEPARATOR = "-";
    private PreferenceCategory mainCategory;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(com.urgesoft.gopro.R.xml.pref_myo_assignments);
        mainCategory = (PreferenceCategory) findPreference(KEY_MYO_COMMAND_ASSIGNMENTS);

        addCommandAssignmentsSetting();
    }

    @Override
    public void onResume() {

        super.onResume();

        addCommandAssignmentsSetting();
    }

    @Override
    public void onPause() {

        String assignmentsJson = MyoAssignments.get().toString();

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Preconditions.checkState(settings.edit().putString(KEY_MYO_COMMAND_ASSIGNMENTS, assignmentsJson).commit(), "Myo command assignment save error!");

        super.onPause();
    }

    private void addCommandAssignmentsSetting() {

        List<String> usablePoseNames = new ArrayList<String>();
        for (Object pose : getAssignablePoseKeys()) {
            int poseNameResourceId = this.getResources().getIdentifier("myo_pose_" + pose.toString().toLowerCase(), "string", this.getActivity().getPackageName());
            usablePoseNames.add(this.getResources().getString(poseNameResourceId));
        }
        List<String> poseKeyStrings = Lists.transform(getAssignablePoseKeys(), Functions.toStringFunction());

        mainCategory.removeAll();
        for (final GoProCommand command : GoProCommand.values()) {
            if (command.isAssignable()) {
                final ListPreference listPref = createAssignmentPreference(usablePoseNames, poseKeyStrings, command);
                mainCategory.addPreference(listPref);
            }
        }

    }


    private ListPreference createAssignmentPreference(final List<String> usablePoseNames, final List<String> usablePoseKeys, final GoProCommand command) {

        // PREFERENCE device
        final ListPreference listPref = new ListPreference(this.getActivity());
        listPref.setKey(buildPrefKeyForCommand(command));
        listPref.setDefaultValue(AssignableMyoPoseKeys.NONE);
        listPref.setEntries(usablePoseNames.toArray(new String[]{}));
        listPref.setEntryValues(usablePoseKeys.toArray(new String[]{}));
        listPref.setDialogTitle(command.getMessageKey());
        listPref.setTitle(command.getMessageKey());

        final String currentlyAssignedPoseKey = MyoAssignments.get().getPoseKey(command);
        updateCmdPrefSummary(listPref, currentlyAssignedPoseKey);

        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {

                clearCurrentAssignment(newValue.toString(), command);
                if (!AssignableMyoPoseKeys.NONE.equals(newValue.toString())) {
                    //newValue will be the entryValue for the entry selected
                    MyoAssignments.get().setAssignment(newValue.toString(), command);
                }
                updateCmdPrefSummary((ListPreference) preference, newValue.toString());

                return true;
            }

            private void clearCurrentAssignment(String poseKey, GoProCommand command) {

                GoProCommand currentCommandForPose = MyoAssignments.get().getCommand(poseKey);
                if (null != currentCommandForPose) {
                    PreferenceCategory mainCategory = (PreferenceCategory) findPreference(KEY_MYO_COMMAND_ASSIGNMENTS);
                    ListPreference currentCommandPreference = (ListPreference) mainCategory.getPreferenceManager().findPreference(buildPrefKeyForCommand(currentCommandForPose));
                    updateCmdPrefSummary(currentCommandPreference, AssignableMyoPoseKeys.NONE);
                }

                String currentPoseForCommand = MyoAssignments.get().getPoseKey(command);
                MyoAssignments.get().clearAssignment(currentPoseForCommand);
            }
        });

        return listPref;
    }

    private void updateCmdPrefSummary(ListPreference listPref, final String newPoseAssignment) {

        int poseNameResourceId = this.getResources().getIdentifier("myo_pose_" + newPoseAssignment.toString().toLowerCase(), "string", this.getActivity().getPackageName());

        listPref.setSummary(this.getResources().getString(poseNameResourceId));
        listPref.setValue(newPoseAssignment);
        listPref.setDefaultValue(newPoseAssignment);
    }

    private String buildPrefKeyForCommand(GoProCommand command) {
        return KEY_MYO_COMMAND_ASSIGNMENTS + PREF_KEY_SEPARATOR + command.toString().toLowerCase();
    }


    protected List<Object> getAssignablePoseKeys() {
        boolean comboSupported = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean(SettingsFragment.KEY_MYO_COMBO_ENABLED, true);
        return comboSupported ? AssignableMyoPoseKeys.withCombo() : AssignableMyoPoseKeys.noCombo();
    }

}
