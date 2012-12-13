package com.example.combatcalculator;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class WeaponView extends LinearLayout {

    private Spinner powText;
    private Spinner rofText;
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
        

        powText = (Spinner) findViewById(R.id.pow_entry);
        rofText = (Spinner) findViewById(R.id.rof_entry);
        rangeBox = (CheckBox) findViewById(R.id.ranged_entry);

    }

	
	
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, powText.getSelectedItemPosition(), rofText.getSelectedItemPosition(), rangeBox.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        
        powText.setSelection(savedState.getPow());
        rofText.setSelection(savedState.getROF());
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

        private final int pow;
        private final int rof;
        private final boolean ranged;

        private SavedState(Parcelable superState, int pow, int rof, boolean ranged) {
            super(superState);
            this.pow = pow;
            this.rof = rof;
            this.ranged = ranged;
        }

        private SavedState(Parcel in) {
            super(in);
            pow = in.readInt();
            rof = in.readInt();
            boolean[] temp = new boolean[1];
            in.readBooleanArray(temp);
            ranged = temp[0];
        }

        public int getPow() {
            return pow;
        }

        public int getROF() {
            return rof;
        }

        public boolean getRanged() {
            return ranged;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(pow);
            destination.writeInt(rof);
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