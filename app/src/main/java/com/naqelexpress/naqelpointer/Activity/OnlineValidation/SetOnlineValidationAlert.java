package com.naqelexpress.naqelpointer.Activity.OnlineValidation;

import android.app.Activity;

import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;

public class SetOnlineValidationAlert {


    public OnLineValidation setOnlineValidationalert(OnLineValidation onLineValidation, Activity activity, String keyName) {


        if (onLineValidation.isNoValidation()) {
            onLineValidation = NoValidataion(onLineValidation);
            return onLineValidation;
        }

        if (onLineValidation.getReasonID() == 7 && keyName.equals("ArrivedAt")) { // Shipment Arrival in Terminal Group ArrivedAt com.naqelexpress.naqelpointer.TerminalHandling.ThirdFragment
            onLineValidation.setClassName("ArrivedAt");
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.ArrivedAt_isMultiPiecealert));
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.ArrivedAt_isMultiPiecePopup));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.ArrivedAt_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.ArrivedAt_isDLalert));

        } else if (onLineValidation.getReasonID() == 5 && keyName.equals("DeliverySheet")) {
            onLineValidation.setClassName("DeliverySheet");
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.DeliverySheet_isMultiPiecePopup));
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.DeliverySheet_isMultiPiecealert));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.DeliverySheet_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isDLalert));
            onLineValidation.setConflictalert(activity.getResources().getBoolean(R.bool.DeliverySheet_isConflictalert));
        } else if (onLineValidation.getReasonID() == 44 && keyName.equals("INV")) {
            onLineValidation.setClassName("INV");
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.INV_isMultiPiecePopup));
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.INV_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.INV_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.INV_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.INV_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.INV_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.INV_isMultiPiecealert));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.INV_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.INV_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.INV_isDLalert));
            onLineValidation.setConflictalert(activity.getResources().getBoolean(R.bool.INV_isConflictalert));
        } else if (onLineValidation.getReasonID() == 999 && keyName.equals("VDS")) {
            onLineValidation.setClassName("VDS");
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.VDS_isMultiPiecePopup));
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.VDS_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.VDS_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.VDS_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.VDS_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.VDS_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.VDS_isMultiPiecealert));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.VDS_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.VDS_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.VDS_isDLalert));
            onLineValidation.setConflictalert(activity.getResources().getBoolean(R.bool.VDS_isConflictalert));
        } else if (onLineValidation.getReasonID() == 44 && keyName.equals("INV_byNCL")) {
            onLineValidation.setClassName("INV_byNCL");
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.INV_byNCL_isMultiPiecePopup));
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.INV_byNCL_isMultiPiecealert));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.INV_byNCL_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isDLalert));
            onLineValidation.setConflictalert(activity.getResources().getBoolean(R.bool.INV_byNCL_isConflictalert));
        } else if (onLineValidation.getReasonID() == 44 && keyName.equals("INV_byPiece")) {
            onLineValidation.setClassName("INV_byPiece");
            onLineValidation.setMultipiecePopup(activity.getResources().getBoolean(R.bool.INV_byPiece_isMultiPiecePopup));
            onLineValidation.setNoofAttemptsalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isNoofAttemptsalert));
            onLineValidation.setWrongDestalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isWrongDestalert));
            onLineValidation.setCITCalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isCITCalert));
            onLineValidation.setStoppedalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isStoppedalert));
            onLineValidation.setManifestedalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isManifestedalert));
            onLineValidation.setMultiPiecealert(activity.getResources().getBoolean(R.bool.INV_byPiece_isMultiPiecealert));
            onLineValidation.setCAFlert(activity.getResources().getBoolean(R.bool.INV_byPiece_isCAFCalert));
            onLineValidation.setRTOalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isRtoCalert));
            onLineValidation.setDLalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isDLalert));
            onLineValidation.setConflictalert(activity.getResources().getBoolean(R.bool.INV_byPiece_isConflictalert));
        }

        return onLineValidation;

    }


    private OnLineValidation NoValidataion(OnLineValidation onLineValidation) {
        onLineValidation.setNoofAttemptsalert(false);
        onLineValidation.setWrongDestalert(false);
        onLineValidation.setCITCalert(false);
        onLineValidation.setStoppedalert(false);
        onLineValidation.setManifestedalert(false);
        onLineValidation.setMultiPiecealert(false);
        onLineValidation.setMultipiecePopup(false);
        onLineValidation.setCITCalert(false);
        onLineValidation.setCAFlert(false);
        onLineValidation.setRTOalert(false);
        onLineValidation.setDLalert(false);
        return onLineValidation;
    }

}
