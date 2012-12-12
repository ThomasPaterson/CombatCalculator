package com.example.combatcalculator;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class WeaponView extends LinearLayout {

    private EditText powText;
    private EditText rofText;
    private CheckBox rangeBox;
    

    public WeaponView(Context context) {
        this(context, null);
    }

    public WeaponView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeaponView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadViews();
    }

    private void loadViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.weapon_item, this, true);
        

        powText = (EditText) findViewById(R.id.pow_entry);
        rofText = (EditText) findViewById(R.id.rof_entry);
        rangeBox = (CheckBox) findViewById(R.id.ranged_entry);

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, powText.getText().toString(), rofText.getText().toString(), rangeBox.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        
        powText.setText(savedState.getPow());
        rofText.setText(savedState.getROF());
        rangeBox.setChecked(savedState.getRanged());
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        // As we save our own instance state, ensure our children don't save and restore their state as well.
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        /** See comment in {@link #dispatchSaveInstanceState(android.util.SparseArray)} */
        super.dispatchThawSelfOnly(container);
    }

    /**
* Convenience class to save / restore the lock combination picker state. Looks clumsy
* but once created is easy to maintain and use.
*/
    protected static class SavedState extends BaseSavedState {

        private final String pow;
        private final String rof;
        private final boolean ranged;

        private SavedState(Parcelable superState, String pow, String rof, boolean ranged) {
            super(superState);
            this.pow = pow;
            this.rof = rof;
            this.ranged = ranged;
        }

        private SavedState(Parcel in) {
            super(in);
            pow = in.readString();
            rof = in.readString();
            boolean[] temp = new boolean[1];
            in.readBooleanArray(temp);
            ranged = temp[0];
        }

        public String getPow() {
            return pow;
        }

        public String getROF() {
            return rof;
        }

        public boolean getRanged() {
            return ranged;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeString(pow);
            destination.writeString(rof);
            boolean[] temp = {ranged};
            destination.writeBooleanArray(temp);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }

}