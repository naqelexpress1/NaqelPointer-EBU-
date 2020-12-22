package com.naqelexpress.naqelpointer.JSON;

import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;

import java.util.List;

public class RetrofitCallResponse {

    public boolean HasError;
    public String ErrorMessage;


    public class OnlineValidationResponse extends RetrofitCallResponse {

        private List<OnLineValidation> OnLineValidationData;

        public List<OnLineValidation> getOnLineValidationData() {
            return OnLineValidationData;
        }
    }


}
