package com.example.combatcalculator;

import java.util.Map;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class OptionView extends LinearLayout {

    private Spinner selectionSpinner;
    private Spinner valueSpinner;
    private View deleteButton;
    

    public OptionView(Context context) {
        this(context, null);
    }

    public OptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadViews();
    }

    private void loadViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_special_rules, this, true);
        

        selectionSpinner = (Spinner) findViewById(R.id.options_selection_spinner);
        valueSpinner = (Spinner) findViewById(R.id.values_selection_spinner);
        deleteButton =  findViewById(R.id.delete_button);

    }

	
	
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, 
        		selectionSpinner.getSelectedItemPosition(), valueSpinner.getSelectedItemPosition(), 
        		isValueVisible()
        		);
    }
    
    
    private boolean isValueVisible(){
    	
    	Map<String, AttackProperty> mappedProperty = (Map<String, AttackProperty>) selectionSpinner.getSelectedItem();
    	AttackProperty property = mappedProperty.get("Name");
    	return property.hasValues();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        
        selectionSpinner.setSelection(savedState.getSelection());
        valueSpinner.setSelection(savedState.getValue());
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

        private final int selection;
        private final int value;
        private final boolean valueVisible;

        private SavedState(Parcelable superState, int selection, int value, boolean valueVisible) {
            super(superState);
            this.selection = selection;
            this.value = value;
            this.valueVisible = valueVisible;
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.readInt();
            value = in.readInt();
            boolean[] temp = new boolean[1];
            in.readBooleanArray(temp);
            valueVisible = temp[0];
        }

        public int getSelection() {
            return selection;
        }

        public int getValue() {
            return value;
        }

        public boolean getValueVisible() {
            return valueVisible;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(selection);
            destination.writeInt(value);
            boolean[] temp = {valueVisible};
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